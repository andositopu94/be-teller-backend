package com.multipolar.uiteller.backend.service.logger;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.jpos.iso.ISOMsg;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.multipolar.uiteller.backend.model.TransactionLog;
import com.multipolar.uiteller.backend.service.MpnSettlementDateUtil;

@Service
public class MPNTransactionLogger extends DefaultTransactionLogger {
	private static final String SUPPORTED_URL_DJP = "djp";
	private static final String SUPPORTED_URL_DJPG3 = "djpg3";
	private static final String SUPPORTED_URL_DJBC = "djbc";
	private static final String SUPPORTED_URL_DJBCG3 = "djbcg3";
	private static final String SUPPORTED_URL_DJA = "dja";
	private static final String SUPPORTED_URL_DJAG3 = "djag3";
	private static final String SUPPORTED_URL_DJP_REINQUIRY = "djpReinquiry";
	private static final String SUPPORTED_URL_DJPG3_REINQUIRY = "djpg3Reinquiry";
	private static final String SUPPORTED_URL_DJBC_REINQUIRY = "djbcReinquiry";
	private static final String SUPPORTED_URL_DJBCG3_REINQUIRY = "djbcg3Reinquiry";
	private static final String SUPPORTED_URL_DJA_REINQUIRY = "djaReinquiry";
	private static final String SUPPORTED_URL_DJAG3_REINQUIRY = "djag3Reinquiry";

	private static final SimpleDateFormat ISO_FORMAT = new SimpleDateFormat("MMdd");
	private static final SimpleDateFormat DB_FORMAT = new SimpleDateFormat("dd/MM/yyyy");
	
	@Autowired
	private MpnSettlementDateUtil settlementDateUtil;

	@Override
	public List<TransactionLog> addLog(Map<String, Object> map) {
		List<TransactionLog> logs = super.addLog(map);

		String settlementDate = map.get("settlementDate") == null ? "MISSING_SETTLEMENT_DATE"
				: map.get("settlementDate").toString();
		TransactionLog settlementLog = new TransactionLog("settlementDate", settlementDate);
		logs.add(settlementLog);
		if (map.get("checkNumber") != null) {
			TransactionLog checkNumberLog = new TransactionLog("checkNumber", map.get("checkNumber").toString());
			logs.add(checkNumberLog);
		}
		if (map.get("checkPageNumber") != null) {
			TransactionLog checkPageNumberLog = new TransactionLog("checkPageNumber",
					map.get("checkPageNumber").toString());
			logs.add(checkPageNumberLog);
		}

		return logs;
	}

	public void setSettlementDate(Map<String, Object> reportData, ISOMsg response) {
		// cek response bit 39 == 00
		String bit39 = response != null ? response.getString(39) : "001";
		if (StringUtils.equalsAny(bit39, "00", "90")) {
			Calendar current = Calendar.getInstance();
			int currentMonth = current.get(Calendar.MONTH);
			int currentYear = current.get(Calendar.YEAR);
			
			String bit15 = response.getString(15);
			int responseMonth = Integer.parseInt(StringUtils.substring(bit15, 0, 2));
			
			// cek sekarang bulan desember dan settlement date bulan januari, maka telah berganti tahun
			if (currentMonth == 11 && responseMonth == 1)
				currentYear += 1;

			
			
			try {
				Date isoDate = ISO_FORMAT.parse(bit15);
				Calendar cal = Calendar.getInstance();
				cal.setTime(isoDate);
				cal.set(Calendar.YEAR, currentYear);

				bit15 = DB_FORMAT.format(cal.getTime());
			} catch (ParseException ex) {
				ex.printStackTrace();
			}
			for (Map.Entry<String, Object> entry : reportData.entrySet()) {
				if (StringUtils.equals(entry.getKey(), "settlementDate")) {
					reportData.put("settlementDate", bit15);
					break;
				}
			}
		} else {
			Date settlementDate = settlementDateUtil.getSettlementDate(new Date());
			String dbSettlementDate = DB_FORMAT.format(settlementDate);
			for (Map.Entry<String, Object> entry : reportData.entrySet()) {
				if (StringUtils.equals(entry.getKey(), "settlementDate")) {
					reportData.put("settlementDate", dbSettlementDate);
					break;
				}
			}
		}
	}
	

	@Override
	public boolean supports(String s) {
		String[] supported = new String[] { SUPPORTED_URL_DJP, SUPPORTED_URL_DJPG3, SUPPORTED_URL_DJBC, SUPPORTED_URL_DJBCG3,
				SUPPORTED_URL_DJA, SUPPORTED_URL_DJAG3, SUPPORTED_URL_DJP_REINQUIRY, SUPPORTED_URL_DJPG3_REINQUIRY,
				SUPPORTED_URL_DJBC_REINQUIRY, SUPPORTED_URL_DJBCG3_REINQUIRY, SUPPORTED_URL_DJA_REINQUIRY,
				SUPPORTED_URL_DJAG3_REINQUIRY };
		return StringUtils.equalsAny(s, supported);
	}

	public static void main(String[] args) throws ParseException {
		SimpleDateFormat ISO_FORMAT = new SimpleDateFormat("MMdd");
		SimpleDateFormat DB_FORMAT = new SimpleDateFormat("dd/MM/yyyy");

		String bit15 = "1231";
		Date date = ISO_FORMAT.parse(bit15);
		System.out.println(DB_FORMAT.format(date));
	}

}
