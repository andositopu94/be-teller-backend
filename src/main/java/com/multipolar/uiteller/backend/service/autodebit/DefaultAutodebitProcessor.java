package com.multipolar.uiteller.backend.service.autodebit;

import java.util.Map;

public class DefaultAutodebitProcessor extends AbstractAutodebitProcessor implements AutoDebitAware {
	@Override
	public boolean supports(String feature) {
		return false;
	}

	@Override
	public String getMessageRouting() {
		return "00000";
	}

	@Override
	public String getMessageRouting(String s) {
		return null;
	}

	@Override
	protected void setErrorMappingKey(Map<String, Object> map) {
		// nothing to do here		
	}

}
