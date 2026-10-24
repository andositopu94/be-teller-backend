package com.multipolar.uiteller.backend.service.iso;

import com.multipolar.uiteller.backend.model.ISOMsgLogEntry;
import com.multipolar.uiteller.backend.resource.Constants;
import com.multipolar.uiteller.backend.security.model.UserDetailImpl;
import com.multipolar.uiteller.backend.service.TraceNumberGenerator;
import com.multipolar.uiteller.backend.servletfilter.FeatureContextHolder;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.jpos.core.ConfigurationException;
import org.jpos.iso.*;
import org.jpos.iso.packager.GenericPackager;
import org.jpos.util.NameRegistrar;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@Service
public class SiskoGateway {
    private static final long DEFAULT_TIMEOUT = 62000L;
    private static final long DEFAULT_WAIT_TIMEOUT = 12000L;
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MMddkkmmss");
    private static final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");
    private static final SimpleDateFormat LOCAL_DATE = new SimpleDateFormat("yyMMdd");
    private Logger logIso = LogManager.getLogger("siskohat-log");
    @Autowired
    private TraceNumberGenerator stan;

    @Value("${SiskoController.kode.cabang}")
    private String SiskoControllerKodeCabang;

    public ISOMsg sendToHost(ISOMsg m) throws ConnectException, ISOException {
        UserDetailImpl userDetail = (UserDetailImpl) SecurityContextHolder.getContext().getAuthentication().getDetails();
        m.set(63, StringUtils.leftPad(userDetail.getBranchCode(), 3, Constants.ZERO));
        if(!SiskoControllerKodeCabang.equals("null")) {
            m.set(63, StringUtils.leftPad(SiskoControllerKodeCabang, 3, Constants.ZERO));
        }
        return sendToHost(m, DEFAULT_TIMEOUT);
    }

    public ISOMsg sentForwardingToHost(String s) throws ConnectException, ISOException, IOException {
        ISOMsg isoMsg = new ISOMsg();
        isoMsg.setPackager(new GenericPackager(new ClassPathResource("packager/" + Constants.EBCDIC).getInputStream()));
        isoMsg.unpack(ISOUtil.asciiToEbcdic(s));
        isoMsg.set(11, StringUtils.leftPad(stan.getTraceNumber(), 6, Constants.ZERO));
        setSettlementDate(isoMsg, new Date());
        FeatureContextHolder.getContext().setRequest(isoMsg);
        isoMsg.set(15, new SimpleDateFormat("yyMMdd").format(new Date()));
        logIso.info("Forwarding log for message with trace number: " + isoMsg.getString(11));
        return sendToHost(isoMsg, DEFAULT_TIMEOUT);
    }

    protected void setSettlementDate(ISOMsg msg, Date date) throws ISOException {
        Calendar calendar = Calendar.getInstance();
        if (date.after(date())) {
            calendar.setTime(date);
            calendar.add(Calendar.DATE, Constants.MPN_INCREMENT_DATE);
            date = calendar.getTime();
        }
        FeatureContextHolder.getContext().setSettlementDate(date);
        msg.set(15, LOCAL_DATE.format(date));
    }

    public Date date() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.HOUR_OF_DAY, Constants.MPN_SETTLEMENT_DATE);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    public ISOMsg sendToHost(final ISOMsg m, long timeout) throws ConnectException, ISOException {
        BaseChannel baseChannel;
        String muxName = "mux.siskomux";
        MUX mux = (MUX) NameRegistrar.getIfExists(muxName);
        if (mux == null)
            throw new ConfigurationException("mux is not configured");
        baseChannel = (BaseChannel) NameRegistrar.getIfExists("channel.siskochannel");
        baseChannel.setHeader(ISOUtil.asciiToEbcdic("V117     117        "));
        Chronometer chronometer = new Chronometer();
        if (isConnected(mux)) {
            long t = Math.max(timeout - chronometer.elapsed(), 1000L);
            try {
                final ISOMsgLogEntry requestEntry = new ISOMsgLogEntry(m, new Date());
                ISOMsg resp = mux.request(m, t);
                final ISOMsgLogEntry responseEntry = new ISOMsgLogEntry(resp, new Date());

                if (resp == null) {
                    logIso.info("No response for message with trace number: " + m.getString(11) + ". Log request for trace number: " + m.getString(11));
                    logRequest(requestEntry);
                    return resp;
                }

                Thread thread = new Thread(new Runnable() {

                    @Override
                    public void run() {
                        log(requestEntry, responseEntry);

                    }
                });
                thread.start();
                try {
                    baseChannel.disconnect();
                    baseChannel.connect();
                }
                catch (IOException e){
                    logIso.info(e.getMessage());
                }
                return resp;
            } catch (ISOException e) {
                throw new ISOException(e);
            }
//            catch (IOException e) {
//                throw new ConnectException("Failed when disconecting socket");
//            }
        } else {
            throw new ConnectException("not connected to host");
        }
    }

    public boolean isConnected(MUX mux) {
        if (mux.isConnected())
            return true;
        long timeout = System.currentTimeMillis() + DEFAULT_WAIT_TIMEOUT;
        while (System.currentTimeMillis() < timeout) {
            if (mux.isConnected())
                return true;
            ISOUtil.sleep(500);
        }
        return false;
    }

    protected void log(ISOMsgLogEntry req, ISOMsgLogEntry resp) {
        try {
            logIso.info(dumpMessage(req));
            logIso.info(dumpMessage(resp));
        } catch (ISOException ex) {
            logIso.error("Something happen when log the message");
            logIso.error(ex.getMessage());
            ex.printStackTrace();
        }
    }

    protected void logRequest(ISOMsgLogEntry req) {
        try {
            logIso.info(dumpMessage(req));
        } catch (ISOException ex) {
            logIso.error("Something happen when log the message");
            logIso.error(ex.getMessage());
            ex.printStackTrace();
        }
    }

    private String dumpMessage(ISOMsgLogEntry entry) throws ISOException {
        ISOMsg msg = entry.getMessage();
        Date date = entry.getDate();
        StringBuilder builder = new StringBuilder();
        builder.append("\n");
        builder.append("ISO Message MTI is " + msg.getMTI() + "\n");
        if (msg.isIncoming()) {
            builder.append("Incoming Message\n");
            builder.append("Message coming at " + format.format(date) + "\n");
        }
        if (msg.isOutgoing()) {
            builder.append("Outgoing Message\n");
            builder.append("Message going at " + format.format(date) + "\n");
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps;
        try {
            ps = new PrintStream(baos, true, StandardCharsets.UTF_8.name());
            msg.dump(ps, "");
            String content = new String(baos.toByteArray(), StandardCharsets.UTF_8);
            ps.close();

            builder.append(content);
        } catch (UnsupportedEncodingException e) {
            builder.append("Something happen when dump iso message");
            builder.append(msg + "\n");
            builder.append(e.getMessage());
        }

        return builder.toString();
    }

    public boolean state(ISOMsg isoMsg) {
        return isoMsg == null || !StringUtils.equals(isoMsg.getString(39), Constants.SUCCESS_CODE);
    }
}
