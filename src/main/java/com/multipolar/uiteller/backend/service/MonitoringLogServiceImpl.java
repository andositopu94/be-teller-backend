package com.multipolar.uiteller.backend.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.jpos.iso.ISOMsg;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.multipolar.uiteller.backend.model.CommonTransactionLog;
import com.multipolar.uiteller.backend.model.CommonTransactionRequestLog;
import com.multipolar.uiteller.backend.model.FailedLogMonitoring;
import com.multipolar.uiteller.backend.model.MonitoringLog;
import com.multipolar.uiteller.backend.model.SuspectLogMonitoring;
import com.multipolar.uiteller.backend.model.TimeoutResource;
import com.multipolar.uiteller.backend.model.converter.Rule;
import com.multipolar.uiteller.backend.repository.CommonTransactionLogRepository;
import com.multipolar.uiteller.backend.repository.FailedLogRepository;
import com.multipolar.uiteller.backend.repository.MonitoringLogRepository;
import com.multipolar.uiteller.backend.repository.SuspectLogMonitoringRepository;
import com.multipolar.uiteller.backend.repository.TimeoutResourceRepository;
import com.multipolar.uiteller.backend.resource.BusinessException;
import com.multipolar.uiteller.backend.resource.Constants;
import com.multipolar.uiteller.backend.resource.Troubleshoot;
import com.multipolar.uiteller.backend.security.model.UserDetailImpl;
import com.multipolar.uiteller.backend.security.model.ValidationResponse;
import com.multipolar.uiteller.backend.servletfilter.FeatureContextHolder;

@Service
public class MonitoringLogServiceImpl implements MonitoringLogService {
	@Autowired
	private MiddlewareGatewayServiceImpl middlewareGatewayService;

	@Autowired
	private FailedLogRepository failedLogRepository;

	@Autowired
	private SuspectLogMonitoringRepository suspectLogRepository;

	@Autowired
	private MonitoringLogRepository monitoringLogRepository;

	@Autowired
	private CommonTransactionLogRepository commonTransactionLogRepository;

	@Autowired
	private TimeoutResourceRepository timeoutResourceRepository;

	private static final String RULE_EXTENSION = ".rule.json";

	private static Map<String, String> responseMap = new HashMap<String, String>();

	private static final String UNMAPPED_DESCRIPTION = "Unmapped description for response code ";

	static {
		responseMap.put("01", "Gagal/Timeout di Biller, trx boleh dikoreksi");
		responseMap.put("02", "Gagal/Timeout di Biller, trx tidak boleh dikoreksi");
		responseMap.put("03", "Reversal sedang berjalan");
		responseMap.put("04", "Transaksi gagal");
		responseMap.put("06", "Timeout di teller, transaksi berhasil");
		responseMap.put("07", "Transaksi Timeout di core");
		responseMap.put("08", "Transaksi Timeout di biller");
		responseMap.put("99", "Transaksi gagal");
	}

	protected Rule getRule() throws IOException {
		// default implementasion is read the rule file base on feature -->
		// feature.rule.json
		String feature = FeatureContextHolder.getContext().getFeatureName();
		String filename = feature + RULE_EXTENSION;

		ObjectMapper objectMapper = new ObjectMapper();

		Resource resource = new ClassPathResource("rules/" + filename);

		// convert json string to object
		return objectMapper.readValue(resource.getInputStream(), Rule.class);
	}

	public void delayTransactionStatusCall(CommonTransactionLog commonLog, ISOMsg request, ISOMsg response) {
		String bit39 = response != null ? response.getString(39) : null;
		Rule rule = null;
		try {
			rule = getRule();
			System.out.println("Rule is " + rule);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		String errorKey = rule.getErrorKey();
		// RC Sukses, tidak perlu disimpan
		if (StringUtils.equals(bit39, "00"))
			return;

		List<TimeoutResource> suspectRc = new ArrayList<TimeoutResource>();
		if (StringUtils.isNotBlank(bit39))
			suspectRc = timeoutResourceRepository.findByTrxTypeAndCode(errorKey, bit39);
//		SuspectLogMonitoring monitoringLog;
//		// System.out.println("Error key is "+errorKey);
//		// System.out.println("Suspect RC is "+suspectRc);

		// jika response code tidak ada dalam daftar suspectRc
		if (!suspectRc.isEmpty()) {
			SuspectLogMonitoring monitoringLog = new SuspectLogMonitoring("03",
					"Waiting Transaction Status Collected by CMS", commonLog);
			suspectLogRepository.save(monitoringLog);
		}
	}

	@Override
	public void saveSuspectTransaction(CommonTransactionLog commonLog, ISOMsg request, ISOMsg response) {
		String bit39 = response != null ? response.getString(39) : null;
		Rule rule = null;
		try {
			rule = getRule();
			System.out.println("Rule is " + rule);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		String errorKey = rule.getErrorKey();
		// RC Sukses, tidak perlu disimpan
		if (StringUtils.equals(bit39, "00"))
			return;

		List<TimeoutResource> suspectRc = new ArrayList<TimeoutResource>();
		if (StringUtils.isNotBlank(bit39))
			suspectRc = timeoutResourceRepository.findByTrxTypeAndCode(errorKey, bit39);
		SuspectLogMonitoring monitoringLog;
		System.out.println("Error key is " + errorKey);
		System.out.println("Suspect RC is " + suspectRc);
		// jika response code tidak ada dalam daftar suspectRc
		if (suspectRc.isEmpty() && StringUtils.isNotBlank(bit39)) {
			monitoringLog = new SuspectLogMonitoring("-99", "", commonLog);
		} else {
			String trace = StringUtils.isNotBlank(bit39) ? response.getString(11) : request.getString(11);
			String ref = StringUtils.isNotBlank(bit39) ? response.getString(37) : request.getString(37);

			ValidationResponse validationResponse;
			try {
				validationResponse = middlewareGatewayService.getStatusResponse(commonLog, trace, ref);
				String rcode = validationResponse.getRCode();
				String message = responseMap.get(rcode) != null ? responseMap.get(rcode) : UNMAPPED_DESCRIPTION + rcode;
				monitoringLog = new SuspectLogMonitoring(validationResponse.getRCode(), message, commonLog);
			} catch (BusinessException e) {
				monitoringLog = new SuspectLogMonitoring("3", "CMS Service failed - " + e.getMessage(), commonLog);
			}
		}

		suspectLogRepository.save(monitoringLog);
	}

	@Override
	public void failedTransactionLog(CommonTransactionLog commonTransactionLog,
			CommonTransactionRequestLog commonTransactionRequestLog, String trace, String ref, String status) {
		if (StringUtils.equals(status, Constants.SUCCESS_CODE))
			return;
		ValidationResponse validationResponse;
		TimeoutResource timeoutResource = timeoutResourceRepository
				.findFirstByTrxTypeAndCode(FeatureContextHolder.getContext().getFeatureName(), status);
		FailedLogMonitoring failedLogMonitoring = new FailedLogMonitoring(Troubleshoot.NONE, commonTransactionLog,
				commonTransactionRequestLog, timeoutResource, Troubleshoot.NONE);
		try {
			if (timeoutResource == null) {
				failedLogMonitoring.setStatus(Constants.OTHER_ERROR_RCODE);
				failedLogMonitoring.setDescription(Constants.OTHER_ERROR_DESCRIPTION);
			} else {
				validationResponse = middlewareGatewayService.getStatusResponse(commonTransactionLog, trace, ref);
				failedLogMonitoring.setStatus(validationResponse.getRCode());
				failedLogMonitoring.setDescription(validationResponse.getMessage());
			}
		} catch (BusinessException e) {
			e.printStackTrace();
			failedLogMonitoring.setStatus(Constants.STRIP_CODE);
			failedLogMonitoring.setDescription(Constants.STRIP_CODE);
		} finally {
			failedLogRepository.save(failedLogMonitoring);
		}
	}

	@Override
	public List<MonitoringLog> userMonitoringLog(String user) {
		return monitoringLogRepository.findTimeoutResourceIdAndTroubleshootAndTroubleshootAndTrubleshootFlagAndUser(
				Troubleshoot.NONE, Troubleshoot.NONE, user);
	}

	@Override
	public List<MonitoringLog> adminMonitoringLog() {
		return monitoringLogRepository.findAll();
	}

	@Override
	public void waitingStatus(Long id) {
		MonitoringLog monitoringLog = monitoringLogRepository.findOne(id);
		CommonTransactionLog commonTransactionLog = commonTransactionLogRepository.findOne(monitoringLog.getCommonId());
		ValidationResponse validationResponse;
		try {
			validationResponse = middlewareGatewayService.getStatusResponse(commonTransactionLog,
					monitoringLog.getTraceNumber(), monitoringLog.getReference());
			if (StringUtils.equals(monitoringLog.getMiddlewareStatus(), validationResponse.getRCode()))
				return;
			FailedLogMonitoring failedLogMonitoring = failedLogRepository.findOne(monitoringLog.getId());
			failedLogMonitoring.setStatus(validationResponse.getRCode());
			failedLogMonitoring.setDescription(validationResponse.getMessage());
			failedLogRepository.save(failedLogMonitoring);
		} catch (BusinessException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void updateByCommonTransactionRequestLog(CommonTransactionRequestLog commonTransactionRequestLog,
			Troubleshoot troubleshoot) {
		UserDetailImpl userDetail = (UserDetailImpl) SecurityContextHolder.getContext().getAuthentication()
				.getDetails();
		FailedLogMonitoring failedLogMonitoring = failedLogRepository
				.findByCommonTransactionRequestLog(commonTransactionRequestLog);
		if (failedLogMonitoring != null) {
			failedLogMonitoring.setTroubleshoot(troubleshoot);
			failedLogMonitoring.setTroubleshootDate(new Date());
			failedLogMonitoring.setTroubleshootUserId(userDetail.getUsername());
			failedLogRepository.save(failedLogMonitoring);
		}
	}
}
