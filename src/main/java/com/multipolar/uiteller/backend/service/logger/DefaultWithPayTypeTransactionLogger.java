package com.multipolar.uiteller.backend.service.logger;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.multipolar.uiteller.backend.model.TransactionLog;

@Service
public class DefaultWithPayTypeTransactionLogger extends DefaultTransactionLogger {
	private static final String SUPPORTED_URL_PREPAID = "token";
	private static final String SUPPORTED_URL_NONTAGLIST = "nontaglist";
	
	@Override
	public boolean supports(String s) {
		return StringUtils.equals(s, SUPPORTED_URL_PREPAID) || StringUtils.equals(s, SUPPORTED_URL_NONTAGLIST);
	}
	
	@Override
	public List<TransactionLog> addLog(Map<String, Object> map) {
		List<TransactionLog> logs = super.addLog(map);
        
		String payType = map.get("payType") == null ? "MISSING_PAY_TYPE" : map.get("payType").toString();
		TransactionLog payTypeLog = new TransactionLog("payType", payType);
		logs.add(payTypeLog);
        
        return logs;
	}
}
