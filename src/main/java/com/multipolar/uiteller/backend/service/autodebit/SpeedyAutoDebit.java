package com.multipolar.uiteller.backend.service.autodebit;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.springframework.stereotype.Service;

import com.multipolar.uiteller.backend.model.Autodebit;

@Service
public class SpeedyAutoDebit extends AbstractAutodebitProcessor {

	private static final String SPEEDY = "speedy";
	
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
		return StringUtils.equals(feature, SPEEDY);
	}

	@Override
	public String getMessageRouting() {
		return "03002";
	}

	@Override
	public String getMessageRouting(String s) {
		return null;
	}

	@Override
	protected void setErrorMappingKey(Map<String, Object> map) {
		map.put("errorKey", "paymentSpeedy");
		map.put("errorInquiryKey", "inquirySpeedy");		
	}

}
