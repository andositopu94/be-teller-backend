package com.multipolar.uiteller.backend.service.iso;

import com.multipolar.uiteller.backend.model.ResourceMessage;
import com.multipolar.uiteller.backend.repository.ResourceMessageRepository;
import com.multipolar.uiteller.backend.resource.Constants;
import com.multipolar.uiteller.backend.servletfilter.FeatureContextHolder;
import org.apache.commons.lang3.StringUtils;
import org.jpos.iso.ISOMsg;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class SamsatConverterHandler extends DefaultConverterHandler {
	private static final String SAMSAT = "samsat";

	@Autowired
	private ResourceMessageRepository messageRepository;

	@Override
	public boolean supports(String s) {
		return StringUtils.equals(SAMSAT, s);
	}

	protected boolean exception(ISOMsg msg, Map<String, Object> map, String errorKey) {
		String rc = msg.getString(39);
		if (StringUtils.equals(rc, Constants.SUCCESS_CODE))
			return false;
		String id = StringUtils.substring(msg.getString(61), 465, 495);
		if(StringUtils.equals(rc, Constants.TIMEOUT_CODE))
			map.put("ktp", id);
		String errorMsg;
		ResourceMessage message = messageRepository.findByCodeAndMessage(errorKey, rc);
		errorMsg = message == null ? "Unmapping Error Number (" + rc + ")" : message.getValue();
		Map<String, Object> reportData = FeatureContextHolder.getContext().getReportData();
		map.putAll(reportData);
		Map<String, String> stringMap = new HashMap<>();
		stringMap.put("status", Constants.TIMEOUT_CODE);
		stringMap.put("code", rc);
		stringMap.put("message", errorMsg);
		map.put("status", stringMap);
		return true;
	}
}
