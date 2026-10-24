package com.multipolar.uiteller.backend.service;

import com.multipolar.uiteller.backend.model.SysParam;
import com.multipolar.uiteller.backend.repository.SysParamRepository;
import com.multipolar.uiteller.backend.resource.Constants;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccNbrService {

	@Autowired
	private SysParamRepository sysParamRepository;

	public String getAccount(String acc){
		Integer integer = Integer.parseInt(StringUtils.substring(acc, 0, 1));
		SysParam sysParam = integer < 6 ? sysParamRepository.findByPropertyName(Constants.CONVENTIONAL_TELLER_ACCOUNT_NUMBER) : sysParamRepository.findByPropertyName(Constants.SYARIAH_TELLER_ACCOUNT_NUMBER);
		String s = sysParam == null ? Constants.BLANK : sysParam.getPropertyValue();
		return StringUtils.substring(acc, 0, 5) + s + StringUtils.substring(acc, 12, acc.length());
	}
}
