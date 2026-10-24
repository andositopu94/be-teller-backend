package com.multipolar.uiteller.backend.service.iso;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.multipolar.uiteller.backend.model.DataSetoran;
import com.multipolar.uiteller.backend.model.Pendidikan;
import com.multipolar.uiteller.backend.model.converter.ConverterRule;
import com.multipolar.uiteller.backend.model.converter.RequestRule;
import com.multipolar.uiteller.backend.model.converter.Rule;
import com.multipolar.uiteller.backend.repository.PendidikanRepository;
import com.multipolar.uiteller.backend.security.model.UserDetailImpl;
import com.multipolar.uiteller.backend.service.KonversiMataUangKeTerbilang;
import com.multipolar.uiteller.backend.servletfilter.FeatureContextHolder;

@Service
public class EdupayAtmConverterHandler extends DefaultConverterHandler {
	private static Logger logger = LoggerFactory.getLogger(EdupayAtmConverterHandler.class);
	
	
	private static final String EDUCATION_PAYMENT_INQ = "edupayAtmInquiry";
	
	private static final String DELIMITER = "|";
	private static final String DOUBLE_DELIMITER = "||";
	private static final String STRIP = "|-|";
	
	@Autowired
	private PendidikanRepository pendidikanRepository;
	
	@Override
	public boolean supports(String s) {
		return StringUtils.equals(EDUCATION_PAYMENT_INQ, s);
	}
	
	protected Rule getRule() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();

        Resource resource = new ClassPathResource("rules/" + EDUCATION_PAYMENT_INQ + ".rule.json");

        // convert json string to object
        return objectMapper.readValue(resource.getInputStream(), Rule.class);
    }
	
	@Override
	protected Map<String, Object> translatePrivateMessage(ISOMsg msg, boolean fl) throws ISOException {
		UserDetailImpl userDetail = (UserDetailImpl) SecurityContextHolder.getContext().getAuthentication().getDetails();
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			Rule rule = getRule();

			String errorKey = rule.getErrorKey();
			if (exception(msg, map, errorKey))
				return map;

			RequestRule[] responseRules = rule.getResponse();
			for (RequestRule responseRule : responseRules) {
				int bit = responseRule.getBit();
				String valueFromBit = msg.getString(bit);
				if (valueFromBit == null) {
					throw new ISOException("The rule said get value from bit but empty bit is found. bit " + bit);
				}

				ConverterRule[] values = responseRule.getValue();
				setValueForMap(map, valueFromBit, values);
			}
		} catch (FileNotFoundException fnfe) {
			fnfe.printStackTrace();
			return generalFail();
		} catch (JsonMappingException jme) {
			logger.error("Can't parsing rule file, please validate the json format");
			jme.printStackTrace();
			return generalFail();
		} catch (IOException iox) {
			logger.error(iox.getMessage());
			iox.printStackTrace();
			return generalFail();
		}

		// we need put source account number in map also
		String sourceName = FeatureContextHolder.getContext().getSourceName();
		map.put("sourceName", sourceName);
		map.put("userId", userDetail.getUsername());
		map.put("branchCode", userDetail.getBranchCode());
		map.put("branchName", userDetail.getBranchName());
		
		String trxDate = msg.getString(13);
		String trxTime = msg.getString(12).replaceAll("..(?!$)", "$0:"); // insert : between two char
		map.put("transactionDate", trxDate + " " + trxTime);
		
		String archiveNumber = msg.getString(37);
		map.put("archiveNumber", archiveNumber);
		//String msg41 = msg.getString(41);
		map.put("terminalId", msg.getString(41));

		// map.put("universityName", requestData.get("universityName"));
		String kodeBilling = map.get("kodeBilling").toString();
		map.put("customerIdAtm", kodeBilling);
		map.put("transactionDateAtm", trxDate);
		map.put("transactionTimeAtm", trxTime);
		map.put("atmNameAtm", msg.getString(43));
		map.put("traceNumberAtm", msg.getString(11));
		map.put("refAtm", archiveNumber);
 		// map.put("kodeBilling", kodeBilling);
		
		String kodeInstansi = map.get("kodeInstansi").toString();
		Pendidikan pendidikan = pendidikanRepository.findByKodeInstansi(kodeInstansi);
		map.put("universityName", pendidikan.getNamaInstansi());
		
		Object chargeObject = map.get("adminCharge");
		String chargeString = chargeObject != null && !StringUtils.isEmpty(chargeObject.toString()) ? chargeObject.toString() : "0";
		BigDecimal charge = new BigDecimal(chargeString);
		BigDecimal nominal = new BigDecimal(map.get("nominal").toString());
		BigDecimal total = charge.add(nominal);
		String terbilang = new KonversiMataUangKeTerbilang(total).toString();
		map.put("terbilang", terbilang);
		String qrString = kodeBilling + "+" + archiveNumber + "+" + total.toString();
		 map.put("qrString", qrString);

		Map<String, String> statusMap = new HashMap<String, String>();
		statusMap.put("status", "00");
		statusMap.put("code", "00");
		statusMap.put("message", "Request Success");
		map.put("status", statusMap);

		return map;
	}
	
	protected void setValueForMap(Map<String, Object> map, String bitValue, ConverterRule[] rules) {
		int index = 0;
		bitValue = StringUtils.replace(bitValue, DOUBLE_DELIMITER, STRIP);
		String[] data = StringUtils.split(bitValue, DELIMITER);
		for (ConverterRule rule : rules) {
			String key = rule.getKey();

			if (StringUtils.equals("loopbit61", key)) {
				// loop status tampil, param dan isi param
				List<DataSetoran> list = new ArrayList<>();
				while (index < data.length) {
					DataSetoran setoran = new DataSetoran();
					setoran.setStatusTampil(data[index++]);
					setoran.setParam(data[index++]);
					setoran.setValue(data[index++]);
					list.add(setoran);
				}

				map.put(key, list);
			} else if (StringUtils.equals("loopbit48", key)) {
				List<DataSetoran> list = new ArrayList<>();
				while (index < data.length) {
					DataSetoran setoran = new DataSetoran();
					setoran.setStatusTampil(data[index++]);
					setoran.setParam(data[index++]);
					setoran.setValue(data[index++]);
					setoran.setAdditionalValue(data[index++]);
					list.add(setoran);
				}

				map.put(key, list);
			} else {
				String leftpad = rule.getLeftpad();
				String rightpad = rule.getRightpad();

				String value = data[index];
				if (leftpad != null)
					value = StringUtils.stripStart(value, leftpad);
				else if (rightpad != null)
					value = StringUtils.stripEnd(value, rightpad);

				map.put(key, value);
				index++;
			}
		}

	}

}
