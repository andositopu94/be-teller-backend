package com.multipolar.uiteller.backend.service.iso;

import org.jpos.iso.*;
import org.jpos.iso.channel.BCDChannel;
import org.jpos.util.LogEvent;
import org.jpos.util.Logger;

import java.io.IOException;
import java.io.Serializable;

public class Ebcdic extends BCDChannel implements Serializable {

    private static final long serialVersionUID = 1397974468722702363L;

    @Override
    protected void sendMessageLength(int len) throws IOException {
        try {
            serverOut.write (
                    ISOUtil.str2bcd (
                            ISOUtil.zeropad (Integer.toString (len % 0xFFFF,16), 4), true
                    )
            );
        }
        catch (ISOException e) {
            Logger.log (new LogEvent(this, "send-message-length", e));
        }
    }

    @Override
    protected int getMessageLength() throws IOException, ISOException {
        byte[] b = new byte[2];
        serverIn.readFully(b,0,2);
        return Integer.parseInt (
                new String(ISOUtil.bcd2str (b, 0, 4, true)), 16
        );
    }

    @Override
    public void send(ISOMsg m) throws IOException, ISOException {
        LogEvent evt = new LogEvent (this, "send");
        try {
            if (!isConnected())
                throw new IOException ("unconnected ISOChannel");
            m.setDirection(ISOMsg.OUTGOING);
            ISOPackager p = getDynamicPackager(m);
            m.setPackager (p);
            m = applyOutgoingFilters (m, evt);
            evt.addMessage (m);
            m.setDirection(ISOMsg.OUTGOING);
            m.setPackager (p);
            byte[] b = m.pack();
            synchronized (serverOutLock) {
                sendMessageLength(b.length + getHeaderLength(m));
                sendMessageHeader(m, b.length);
                sendMessage (b, 0, b.length);
                sendMessageTrailer(m, b);
                serverOut.flush ();
            }
            cnt[TX]++;
            setChanged();
            notifyObservers(m);
        } catch (ISOFilter.VetoException e) {
            evt.addMessage (m);
            evt.addMessage (e);
            throw e;
        } catch (ISOException e) {
            evt.addMessage (e);
            throw e;
        } catch (IOException e) {
            evt.addMessage (e);
            throw e;
        } catch (Exception e) {
            evt.addMessage (e);
            throw new IOException ("unexpected exception", e);
        } finally {
            Logger.log (evt);
        }
    }
}
