package com.multipolar.uiteller.backend.service.iso;

import com.multipolar.uiteller.backend.resource.Constants;
import org.jpos.iso.ISOMsg;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class Reversal {
	@Autowired
	private MessageGateway messageGateway;
	private ISOMsg msg;
	private final static long milis = Constants.REVERSAL_MILIS;
	private final static int c = Constants.REVERSAL;

	public void sendToHost(ISOMsg isoMsg) {
		try {
			msg = isoMsg;
			new Thread(new Rev()).start();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	private class Rev implements Runnable
	{
		@Override
		public void run() {
			try {
				int i = 0;
				while (i < c) {
					ISOMsg isoMsg;
					isoMsg = messageGateway.sendToHost(msg);
					if(!messageGateway.state(isoMsg))
						break;
					i++;
					Thread.sleep(milis);
				}
			}
			catch (Exception e){
				e.printStackTrace();
			}
		}
	}
}
