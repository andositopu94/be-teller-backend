package com.multipolar.uiteller.backend.service.iso;

import com.multipolar.uiteller.backend.resource.Constants;
import com.multipolar.uiteller.backend.service.RequestService;
import org.apache.commons.lang3.StringUtils;
import org.jpos.iso.ISOMsg;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class Network {
    @Autowired
    private NetworkGateway networkGateway;
    @Autowired
    private RequestService requestService;
    private ISOMsg isoMsg;
    private long milis = Constants.NETWORK_MILIS;

    @Scheduled(fixedDelay = Constants.NETWORK_MILIS / 2,initialDelay=300000)
    private void sisko() {
        try {
            isoMsg = networkGateway.sendSchedToHost(requestService.generateNetwork(Constants.SIGN));
            if(fl(isoMsg))
                return;
            while (!fl(isoMsg)) {
                isoMsg = networkGateway.sendSchedToHost(requestService.generateNetwork(Constants.ECHO));
                Thread.sleep(milis);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private boolean fl(ISOMsg isoMsg) {
        return isoMsg == null || !StringUtils.equals(isoMsg.getString(39), Constants.SUCCESS_CODE);
    }
}
