	package com.multipolar.uiteller.backend.service.logger;

	import com.multipolar.uiteller.backend.model.TransactionLog;
	import com.multipolar.uiteller.backend.model.TransactionSiskoRequestLog;
	import com.multipolar.uiteller.backend.repository.CommonTransactionSiskoRequestLogRepository;
	import com.multipolar.uiteller.backend.repository.ResourceMessageRepository;
	import com.multipolar.uiteller.backend.repository.TransactionLogRepository;
	import com.multipolar.uiteller.backend.repository.TransactionSiskoRequestLogRepository;
	import com.multipolar.uiteller.backend.resource.BusinessException;
	import com.multipolar.uiteller.backend.resource.Constants;
	import org.apache.commons.lang3.StringUtils;
	import org.jpos.iso.ISOMsg;
	import org.slf4j.Logger;
	import org.slf4j.LoggerFactory;
	import org.springframework.beans.factory.annotation.Autowired;
	import org.springframework.stereotype.Service;
	import org.springframework.transaction.annotation.Transactional;

	@Service
	@Transactional(readOnly = true)
	public class SiskoForwardingLogService {
		private static Logger logger = LoggerFactory.getLogger(SiskoForwardingLogService.class);

		@Autowired
		private CommonTransactionSiskoRequestLogRepository commonLogRepository;

		@Autowired
		private TransactionSiskoRequestLogRepository transactionRequestLogRepository;

		@Autowired
		private ResourceMessageRepository messageRepository;

		@Autowired
		private TransactionLogRepository transactionLogRepository;

		public TransactionSiskoRequestLog getRequestSiskoTransactionLog(Long id, String s) throws BusinessException{
			TransactionSiskoRequestLog transactionRequestLog = transactionRequestLogRepository.findByCommonTransactionSiskoRequestLogIdAndFieldName(id, s);
			if (transactionRequestLog == null)
				throw new BusinessException("Data Not Found");
			return transactionRequestLog;
		}

		public TransactionLog getTransactionLog(Long id, String s) throws BusinessException{
			TransactionLog transactionLog = transactionLogRepository.findByCommonTransactionIdAndAndFieldName(id, s);
			if (transactionLog == null)
				throw new BusinessException("Data Not Found");
			return transactionLog;
		}

		@Transactional
		public TransactionSiskoRequestLog setFlag(TransactionSiskoRequestLog transactionLog, ISOMsg isoMsg, String s){
			if(!StringUtils.equals(isoMsg.getString(39), Constants.SUCCESS_CODE))
				return null;
			TransactionSiskoRequestLog log = new TransactionSiskoRequestLog();
			if (StringUtils.equals(s, Constants.ZERO)){
				log.setFieldName(Constants.FORWARDING_FIELD_NAME);
				log.setFieldValue(Constants.FORWARDING_FIELD_VALUE);
			}
			else {
				log.setFieldName(Constants.REVERSAL_FIELD_NAME);
				log.setFieldValue(Constants.REVERSAL_FIELD_VALUE);
			}
			log.setTraceNumber(transactionLog.getTraceNumber());
			log.setTrxType(transactionLog.getTrxType());
			log.setCommonTransactionSiskoRequestLog(transactionLog.getCommonTransactionSiskoRequestLog());
			return transactionRequestLogRepository.save(log);
		}
	}
