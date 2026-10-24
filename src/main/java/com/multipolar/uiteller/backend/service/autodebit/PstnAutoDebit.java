package com.multipolar.uiteller.backend.service.autodebit;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.springframework.stereotype.Service;

import com.multipolar.uiteller.backend.model.Autodebit;

@Service
public class PstnAutoDebit extends AbstractAutodebitProcessor {

	private static final String PSTN = "pstn";

	@Override
	public Map<String, Object> construcRequestMap(Autodebit data) {
		Map<String, Object> map = super.construcRequestMap(data);		

		String customerId = data.getCustomerId();
		map.put("areaCode", StringUtils.substring(customerId, 0, 4));
		map.put("phoneNumber", StringUtils.substring(customerId, 4));

		return map;
	}

	@Override
	public ISOMsg createPaymentRequest(ISOMsg msg, Autodebit data) throws ISOException {
		ISOMsg request = super.createPaymentRequest(msg, data);
		
		String bit61 = request.getString("61");
		int npwpCustomerNameLength = 45;
		String paymentBit61 = StringUtils.substring(bit61, 0, bit61.length() - npwpCustomerNameLength);
		request.set(61, paymentBit61);
		return request;
	}
	
	@Override
	public void addReportDataFromInquiry(Map<String,Object> map, ISOMsg response) {
		String bit61 = response.getString(61);
		int length = bit61.length();
		int npwpLength = 15;
		int customerNameLength = 30;
		int index = length - npwpLength;
		String npwp = StringUtils.substring(bit61, index, length);
		String customerName = StringUtils.substring(bit61, index - customerNameLength, index);
		map.put("taxNumber", npwp.trim());
		map.put("customerName", customerName);
		map.put("payType", "PINDAH BUKU");
	};

	@Override
	public boolean supports(String feature) {
		return StringUtils.equals(feature, PSTN);
	}

	@Override
	public String getMessageRouting() {
		return "03001";
	}

	@Override
	public String getMessageRouting(String s) {
		return null;
	}

	@Override
	protected void setErrorMappingKey(Map<String, Object> map) {
		map.put("errorKey", "paymentPSTN");
		map.put("errorInquiryKey", "inquiryPSTN");		
	}

}
