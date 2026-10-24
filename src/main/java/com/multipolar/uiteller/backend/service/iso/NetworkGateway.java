package com.multipolar.uiteller.backend.service.iso;

import com.multipolar.uiteller.backend.model.ISOMsgLogEntry;
import com.multipolar.uiteller.backend.resource.Constants;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.jpos.core.ConfigurationException;
import org.jpos.iso.*;
import org.jpos.util.NameRegistrar;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;

@Service
public class NetworkGateway {
	private static final long DEFAULT_TIMEOUT = 62000L;
	private static final long DEFAULT_WAIT_TIMEOUT = 12000L;
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MMddHHmmss");
	private static final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");
	private Logger logIso = LogManager.getLogger("network-log");

	public ISOMsg sendSchedToHost(ISOMsg m) throws ConnectException, ISOException {
		return sendToHost(m, DEFAULT_TIMEOUT);
	}

	public ISOMsg sendToHost (final ISOMsg m, long timeout) throws ConnectException, ISOException, ConfigurationException {
		BaseChannel baseChannel;
		String muxName = "mux.siskomux";
		MUX mux =  (MUX) NameRegistrar.getIfExists (muxName);
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

				return resp;
			} catch (ISOException e) {
				throw new ISOException(e);
			}
		} else {
			throw new ConnectException("not connected to host");
		}
	}

	protected boolean isConnected (MUX mux) {
		if (mux.isConnected())
			return true;
		long timeout = System.currentTimeMillis() + DEFAULT_WAIT_TIMEOUT;
		while (System.currentTimeMillis() < timeout) {
			if (mux.isConnected())
				return true;
			ISOUtil.sleep (500);
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
		} if (msg.isOutgoing()) {
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

	public boolean state(ISOMsg isoMsg){
		return isoMsg == null || !StringUtils.equals(isoMsg.getString(39), Constants.SUCCESS_CODE);
	}
}
