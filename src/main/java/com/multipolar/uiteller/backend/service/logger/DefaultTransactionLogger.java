	package com.multipolar.uiteller.backend.service.logger;

	import com.multipolar.uiteller.backend.model.TransactionLog;
	import com.multipolar.uiteller.backend.model.TransactionRequestLog;
	import com.multipolar.uiteller.backend.model.TransactionSiskoRequestLog;
	import com.multipolar.uiteller.backend.security.model.UserDetailImpl;
	import com.multipolar.uiteller.backend.servletfilter.FeatureContextHolder;
	import org.springframework.security.core.context.SecurityContextHolder;
	import org.springframework.stereotype.Service;

	import java.util.ArrayList;
	import java.util.List;
	import java.util.Map;

	@Service
	public class DefaultTransactionLogger implements TransactionLogger {

		@Override
		public List<TransactionLog> addLog(Map<String, Object> map) {
			List<TransactionLog> logs = new ArrayList<>();
			UserDetailImpl userDetail = (UserDetailImpl) SecurityContextHolder.getContext().getAuthentication().getDetails();
			String archive = userDetail.getUsername() + FeatureContextHolder.getContext().getTraceNumber();

			String archiveName = archive == null ? "MISSING_ARCHIVE_NAME" : archive;
			TransactionLog archiveLog = new TransactionLog("archiveNumber", archiveName);
			logs.add(archiveLog);

			String branchName = map.get("branchName") == null ? "MISSING_BRANCH_NAME" : map.get("branchName").toString();
			TransactionLog branchLog = new TransactionLog("branchName", branchName);
			logs.add(branchLog);

			String sourceName = map.get("sourceName") == null ? "MISSING_SOURCE_NAME" : map.get("sourceName").toString();
			TransactionLog sourceLog = new TransactionLog("sourceName", sourceName);
			logs.add(sourceLog);

			return logs;
		}

		@Override
		public List<TransactionRequestLog> addRequestLog(Map<String, Object> map) {
			List<TransactionRequestLog> logs = new ArrayList<>();
			UserDetailImpl userDetail = (UserDetailImpl) SecurityContextHolder.getContext().getAuthentication().getDetails();
			String archive = userDetail.getUsername() + FeatureContextHolder.getContext().getTraceNumber();

			String archiveName = archive == null ? "MISSING_ARCHIVE_NAME" : archive;
			TransactionRequestLog archiveLog = new TransactionRequestLog("archiveNumber", archiveName);
			logs.add(archiveLog);

			String branchName = map.get("branchName") == null ? "MISSING_BRANCH_NAME" : map.get("branchName").toString();
			TransactionRequestLog branchLog = new TransactionRequestLog("branchName", branchName);
			logs.add(branchLog);

			String sourceName = map.get("sourceName") == null ? "MISSING_SOURCE_NAME" : map.get("sourceName").toString();
			TransactionRequestLog sourceLog = new TransactionRequestLog("sourceName", sourceName);
			logs.add(sourceLog);

			return logs;
		}

		@Override
		public List<TransactionSiskoRequestLog> addSiskoRequestLog(Map<String, Object> map) {
			List<TransactionSiskoRequestLog> logs = new ArrayList<>();
			UserDetailImpl userDetail = (UserDetailImpl) SecurityContextHolder.getContext().getAuthentication().getDetails();
			String archive = userDetail.getUsername() + FeatureContextHolder.getContext().getTraceNumber();

			String archiveName = archive == null ? "MISSING_ARCHIVE_NAME" : archive;
			TransactionSiskoRequestLog archiveLog = new TransactionSiskoRequestLog("archiveNumber", archiveName);
			logs.add(archiveLog);

			String branchName = map.get("branchName") == null ? "MISSING_BRANCH_NAME" : map.get("branchName").toString();
			TransactionSiskoRequestLog branchLog = new TransactionSiskoRequestLog("branchName", branchName);
			logs.add(branchLog);

			String sourceName = map.get("sourceName") == null ? "MISSING_SOURCE_NAME" : map.get("sourceName").toString();
			TransactionSiskoRequestLog sourceLog = new TransactionSiskoRequestLog("sourceName", sourceName);
			logs.add(sourceLog);

			return logs;
		}

		@Override
		public boolean supports(String s) {
			return false;
		}
	}
