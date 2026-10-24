package com.multipolar.uiteller.backend.service.report;

import com.multipolar.uiteller.backend.resource.Constants;
import com.multipolar.uiteller.backend.security.model.AccountResult;
import com.multipolar.uiteller.backend.security.model.UserDetailImpl;
import com.multipolar.uiteller.backend.security.model.ValidationResponse;
import com.multipolar.uiteller.backend.service.MiddlewareGatewayServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    @Autowired
    private MiddlewareGatewayServiceImpl middlewareGatewayService;

    public ISOMsg transactionException(AccountResult accountResult, ISOMsg msg) throws ISOException {
        UserDetailImpl userDetail = (UserDetailImpl) SecurityContextHolder.getContext().getAuthentication().getDetails();
        ISOMsg isoMsg = new ISOMsg();
        if (accountResult == null) {
            isoMsg.set(39, Constants.ACCOUNT_NOT_FOUND);
            return isoMsg;
        }
        if (StringUtils.equals(accountResult.getTXSTS(), Constants.ZERO) && StringUtils.equals(msg.getString(57), Constants.OVERBOOKING_PAY_TYPE)) {
            isoMsg.set(39, Constants.ACCOUNT_NOT_PERMIT);
            return isoMsg;
        }
        if (StringUtils.equals(accountResult.getTXSTS(), Constants.ONE) && StringUtils.equals(msg.getString(57), Constants.OVERBOOKING_PAY_TYPE) && !StringUtils.equals(accountResult.getBRANCHID(), userDetail.getBranchCode())) {
            isoMsg.set(39, Constants.ACCOUNT_OTHER_BRANCH);
            return isoMsg;
        }
        return null;
    }

    public ISOMsg validationException(String id) throws ISOException {
        ISOMsg isoMsg = new ISOMsg();
        ValidationResponse validationResponse = middlewareGatewayService.getValidationResponse(id);
        if (validationResponse == null) {
            isoMsg.set(39, Constants.OTHER_ERROR);
            return isoMsg;
        }
        if (StringUtils.equals(validationResponse.getRCode(), Constants.ZERO)) {
            isoMsg.set(39, Constants.FORWARDING_NOT_PERMIT);
            return isoMsg;
        }
        return null;
    }
}
