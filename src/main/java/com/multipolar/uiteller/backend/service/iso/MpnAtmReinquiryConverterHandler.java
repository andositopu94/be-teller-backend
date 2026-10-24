package com.multipolar.uiteller.backend.service.iso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Map;

import com.multipolar.uiteller.backend.resource.Constants;
import org.apache.commons.lang3.StringUtils;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.multipolar.uiteller.backend.service.TraceNumberGenerator;
import com.multipolar.uiteller.backend.servletfilter.FeatureContextHolder;


@Service
public class MpnAtmReinquiryConverterHandler extends DefaultConverterHandler {
	
	private static final String DJP_REINQUIRY = "djpAtmReinquiry";
	private static final String DJBC_REINQUIRY = "djbcAtmReinquiry";
	private static final String DJA_REINQUIRY = "djaAtmReinquiry";
	private static final String DJPG3_REINQUIRY = "djpg3AtmReinquiry";
	private static final String DJBCG3_REINQUIRY = "djbcg3AtmReinquiry";
	private static final String DJAG3_REINQUIRY = "djag3AtmReinquiry";
	
	private static final SimpleDateFormat SDATE = new SimpleDateFormat("dd/MM/yyyy");
	
	@Autowired
	private TraceNumberGenerator stanGenerator;
	
	@Override
	public boolean supports(String s) {
		return StringUtils.equals(DJP_REINQUIRY, s) ||
				StringUtils.equals(DJBC_REINQUIRY, s) ||
				StringUtils.equals(DJA_REINQUIRY, s) ||
				StringUtils.equals(DJPG3_REINQUIRY, s) ||
				StringUtils.equals(DJBCG3_REINQUIRY, s) ||
				StringUtils.equals(DJAG3_REINQUIRY, s);
	}
	
	public ISOMsg doConvertToISO(Map<String, Object> map) throws InvalidMessageException {
		ISOMsg msg = super.doConvertToISO(map);
		FeatureContextHolder.getContext().setReportData(map);
		
		try {
			String settlementDate = map.get("settlementDate") != null ? map.get("settlementDate").toString() : "";
			FeatureContextHolder.getContext().setSettlementDate(SDATE.parse(settlementDate));
			// set bit 11 sama dengan trx sebelumnya
			String traceNumber = (String)map.get("traceNumber");
			if (StringUtils.isEmpty(traceNumber))
				traceNumber = stanGenerator.getTraceNumber();
			
			msg.set(11, traceNumber);
			if(map.get("payType").equals("CASH")) {
				setMerchantType(msg, Constants.MERCHANT_TYPE);
			} else {
				setMerchantType(msg, Constants.OVERBOOKING_MERCHANT_TYPE);
			}
			return msg;
		} catch (ParseException ex) {
			throw new InvalidMessageException("Gagal melakukan parsing settlement date");
		} catch (ISOException ex) {
			throw new InvalidMessageException("Gagal melakukan setting STAN");
		}
	}
}
