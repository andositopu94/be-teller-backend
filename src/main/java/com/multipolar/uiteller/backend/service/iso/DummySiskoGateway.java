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
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.ISOUtil;
import org.jpos.iso.MUX;
import org.jpos.iso.packager.GenericPackager;
import org.springframework.beans.factory.annotation.Autowired;
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
public class DummySiskoGateway {
    private static final long DEFAULT_TIMEOUT = 62000L;
    private static final long DEFAULT_WAIT_TIMEOUT = 12000L;
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MMddkkmmss");
    private static final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");
    private static final SimpleDateFormat LOCAL_DATE = new SimpleDateFormat("yyMMdd");
    private Logger logIso = LogManager.getLogger("siskohat-log");
    @Autowired
    private TraceNumberGenerator stan;

    public ISOMsg sendToHost(ISOMsg m) throws ConnectException, ISOException {
        UserDetailImpl userDetail = (UserDetailImpl) SecurityContextHolder.getContext().getAuthentication().getDetails();
        m.set(63, StringUtils.leftPad(userDetail.getBranchCode(), 3, Constants.ZERO));
        return sendToHost(m, DEFAULT_TIMEOUT);
    }

    public ISOMsg sentForwardingToHost(String s) throws ConnectException, ISOException, IOException {
        ISOMsg isoMsg = new ISOMsg();
        isoMsg.setPackager(new GenericPackager(new ClassPathResource("packager/" + Constants.EBCDIC).getInputStream()));
        isoMsg.unpack(ISOUtil.asciiToEbcdic(s));
        isoMsg.set(11, StringUtils.leftPad(stan.getTraceNumber(), 6, Constants.ZERO));
        setSettlementDate(isoMsg, new Date());
        FeatureContextHolder.getContext().setRequest(isoMsg);
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

    public ISOMsg sendToHost(final ISOMsg m, long timeout) throws ConnectException, ISOException, ConfigurationException {
        String pcode = m.getString(3);
        ISOMsg responseMsg = (ISOMsg) m.clone();
        if (StringUtils.equals("320000", pcode)) {
            // create isomsg dummy
            responseMsg.set(0, "0210");
            responseMsg.set(39, "00");
            responseMsg.set(61, "JUMIYAH                                 B1208231909500002    PEMATANG SEI BARU        311219652KAPIAS BATU VIII         21352Desa Kapias Batu Viii    Tanjung Balai            Kabupaten Asahan         0216412SYAHBULLAH                              ");
            responseMsg.set(62, "11721062201266471510JUMIYAH                                 1208231909500002    KAPIAS BATU VIII                   .........................");
            responseMsg.set(63, "632");
        } else if (StringUtils.equals("340000", pcode)) {
            responseMsg.set(0, "0210");
            responseMsg.set(39, "00");
            responseMsg.set(61, "B2206202111721062201266471510");
            responseMsg.set(62, "JUMIYAH                                 1208231909500002    PEMATANG SEI BARU        31121965KAPIAS BATU VIII         21352Desa Kapias Batu Viii    Tanjung Balai            Kabupaten Asahan         SUMATERA UTARA           TANI / NELAYAN SD             205500BPD SUMUT      CABANG PEMBANTU SYARJL. IMAM BONJOL NO.80 ASA63202030027410     3600002500000000                         ");
            responseMsg.set(63, "632");
        } else if (StringUtils.equals("312000", pcode)) {
            responseMsg.set(0, "0210");
            responseMsg.set(39, "00");
            //Lunas Tunda
            responseMsg.set(61, "0200125567");
            responseMsg.set(62, "ANDRIAN RAMADHAN FEBRIANA               0023600002500000000000250000000025000000000005000000000000250000000012050B8462ANDRIAN RAMADHAN FEBRIANA          ");

            //Non Lunas Tunda
            responseMsg.set(61, "0200125568");
            responseMsg.set(62, "ANDRIAN RAMADHAN FEBRIANA               0023600005000000000000000000000000000000000005000000000000000000000022050B8462ANDRIAN RAMADHAN FEBRIANA          ");

        }
        else if (StringUtils.equals("322000", pcode)) {
            // create isomsg dummy
            responseMsg.set(0, "0210");
            responseMsg.set(39, "00");
            //Lunas Tunda
            responseMsg.set(61, "0200125567ANDRIAN RAMADHAN FEBRIANA               0022600002500000000000250000000000000001000005000000000000510000000012022B846211111111111111111111111111111111111");
            responseMsg.set(62, "ANDRIAN RAMADHAN FEBRIANA               2HERLINA SARIFAH          BANDUNG                  02021995MES3600005000000000000500000000000025000000002000014430000555555555555555555555555555555555556666666666666666666666666SUKAMAJU                 CIBEUNYING KIDUL         CIBEUNYING KIDUL         11111111111111111111111114012111111111111111122222222222222222222333333333333333333333333305611444455555555555555555555555555555555");


            //Non Lunas Tunda
            responseMsg.set(61, "0200125568ANDRIAN RAMADHAN FEBRIANA               0022600005000000000000000000000000000001000005000000000000510000000022022B846211111111111111111111111111111111111");
            responseMsg.set(62, "ANDRIAN RAMADHAN FEBRIANA               2HERLINA SARIFAH          BANDUNG                  02021995MES3600005000000000000500000000000000000000002000014430000555555555555555555555555555555555556666666666666666666666666SUKAMAJU                 CIBEUNYING KIDUL         CIBEUNYING KIDUL         11111111111111111111111114012111111111111111122222222222222222222333333333333333333333333305611444455555555555555555555555555555555");

            responseMsg.set(63, "632");
        }
        else if (StringUtils.equals("342000", pcode)) {
            // create isomsg dummy
            responseMsg.set(0, "0210");
            responseMsg.set(39, "00");
            //Lunas Tunda
            responseMsg.set(61, "0200125567");
            responseMsg.set(62, "ANDRIAN RAMADHAN FEBRIANA               2HERLINA SARIFAH          BANDUNG                  02021995MES3600005000000000000500000000000025000000001000014430000                                   JL.WIRANTA NO.61A        SUKAMAJU                 CIBEUNYING KIDUL         CIBEUNYING KIDUL         SUMATERA UTARA           20856BPD SUMUT      BANK PUSAT          JL. IMAM BONJOL NO. 18 ME06706202231203C868FFB982FDAD515873C791A3E1205202200025000000000000000100");

            //Non Lunas Tunda
            responseMsg.set(61, "0200125568");
            responseMsg.set(62, "ANDRIAN RAMADHAN FEBRIANA               2HERLINA SARIFAH          BANDUNG                  02021995MES3600005000000000000500000000000000000000002000014430000                                   JL.WIRANTA NO.61A        SUKAMAJU                 CIBEUNYING KIDUL         CIBEUNYING KIDUL         SUMATERA UTARA           20856BPD SUMUT      BANK PUSAT          JL. IMAM BONJOL NO. 18 ME06706202231203C868FFB982FDAD515873C791A3E1205202200050000000000000000100");
            responseMsg.set(63, "632");
        }

        return responseMsg;
    }

    protected boolean isConnected(MUX mux) {
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
