package com.multipolar.uiteller.backend.service;

import java.io.Serializable;

import org.jpos.core.Configurable;
import org.jpos.core.Configuration;
import org.jpos.core.ConfigurationException;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.ISOSource;
import org.jpos.transaction.AbortParticipant;
import org.jpos.transaction.Context;
import org.jpos.util.Log;

public class SendHostResponse extends Log implements AbortParticipant, Configurable {
	private String source;
    private String response;

    public int prepare (long id, Serializable context) {
        Context ctx = (Context) context;
        ISOSource source = (ISOSource) ctx.get ("source");
        if (source == null || !source.isConnected())
            return ABORTED | READONLY | NO_JOIN;

        return PREPARED | READONLY;
    }
    public void commit (long id, Serializable context) { 
        sendResponse(id, (Context) context);
    }
    public void abort (long id, Serializable context) { 
        sendResponse(id, (Context) context);
    }
    private void sendResponse (long id, Context ctx) {
        ISOSource src = (ISOSource) ctx.get (source);
        ISOMsg resp = (ISOMsg) ctx.get (response);
        try {
            if (resp == null) {
                ctx.log (response + " not present");
            } else if (src == null) {
                ctx.log (source + " not present");
            } else if (!src.isConnected())
                ctx.log (source + " is no longer connected");
            else {
                src.send (resp);
            }
        } catch (Throwable t) {
            ctx.log(t);
        }
    }

    @Override
    public void setConfiguration(Configuration cfg) throws ConfigurationException {
//        source = cfg.get ("source", ContextConstants.SOURCE.toString());
//        response = cfg.get ("response", ContextConstants.RESPONSE.toString());
    }
	@Override
	public int prepareForAbort(long id, Serializable context) {
		return ABORTED | READONLY | NO_JOIN;
	}
}
