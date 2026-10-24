    package com.multipolar.uiteller.backend.service.iso;

    import com.multipolar.uiteller.backend.model.ReprintTimeoutResource;
    import com.multipolar.uiteller.backend.model.ResourceMessage;
    import com.multipolar.uiteller.backend.model.TransactionLog;
    import com.multipolar.uiteller.backend.repository.ReprintTimeoutResourceRepository;
    import com.multipolar.uiteller.backend.repository.ResourceMessageRepository;
    import com.multipolar.uiteller.backend.repository.TransactionLogRepository;
    import com.multipolar.uiteller.backend.resource.Constants;
    import com.multipolar.uiteller.backend.servletfilter.FeatureContextHolder;
    import org.apache.commons.lang3.StringUtils;
    import org.jpos.iso.ISOException;
    import org.jpos.iso.ISOMsg;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.stereotype.Service;

    import java.util.HashMap;
    import java.util.List;
    import java.util.Map;

    @Service
    public class PbbConverterHandler extends DefaultConverterHandler {
        private static final String PBB = "pbb";

        @Autowired
        private TransactionLogRepository transactionLogRepository;

        @Autowired
        private ReprintTimeoutResourceRepository reprintTimeoutResourceRepository;

        @Autowired
        private ResourceMessageRepository messageRepository;

        @Override
        public boolean supports(String s) {
            return StringUtils.equals(PBB, s);
        }

        @Override
        protected Map<String, Object> translatePrivateMessage(ISOMsg msg, boolean fl) throws ISOException {
            Map<String, Object> stringObjectMap = super.translatePrivateMessage(msg, fl);
            if (msg.getString(76) != null) {
                List<TransactionLog> transactionLogs = transactionLogRepository.findByCommonTransactionId(transactionLogRepository.findFirstByFieldNameAndFieldValueAndTrxTypeOrderByIdDesc(Constants.REFFERENCE_FIELD_NAME, StringUtils.stripStart(msg.getString(37), Constants.ZERO), FeatureContextHolder.getContext().getFeatureName()).getCommonTransaction().getId());
                Map<String, String> objectMap = new HashMap<>();
                for (TransactionLog log : transactionLogs)
                    objectMap.put(log.getFieldName(), log.getFieldValue());
                stringObjectMap.put("noWp", objectMap.get("noWp") == null ? "" : objectMap.get("noWp"));
                stringObjectMap.put("namaWp", objectMap.get("namaWp") == null ? "" : objectMap.get("namaWp"));
                stringObjectMap.put("alamat", objectMap.get("alamat") == null ? "" : objectMap.get("alamat"));
                stringObjectMap.put("mataAnggaran", objectMap.get("mataAnggaran") == null ? "" : objectMap.get("mataAnggaran"));
                stringObjectMap.put("uraian", objectMap.get("uraian") == null ? "" : objectMap.get("uraian"));
                stringObjectMap.put("masaPajak", objectMap.get("masaPajak") == null ? "" : objectMap.get("masaPajak"));
                stringObjectMap.put("noSKP", objectMap.get("noSKP") == null ? "" : objectMap.get("noSKP"));
                stringObjectMap.put("jumlahPokok", objectMap.get("jumlahPokok") == null ? "" : objectMap.get("jumlahPokok"));
                stringObjectMap.put("denda", objectMap.get("denda") == null ? "" : objectMap.get("denda"));
                stringObjectMap.put("noArsip", objectMap.get("noArsip") == null ? "" : objectMap.get("noArsip"));
                stringObjectMap.put("archiveNumber", objectMap.get("archiveNumber") == null ? "" : objectMap.get("archiveNumber"));
            }
            return stringObjectMap;
        }

        @Override
        protected void setPrivateMessage(ISOMsg msg, Map<String, Object> map) throws ISOException {
            super.setPrivateMessage(msg, map);
        }

        protected boolean exception(ISOMsg msg, Map<String, Object> map, String errorKey) {
            String resultCode = msg.getString(39);
            String trxType = FeatureContextHolder.getContext().getFeatureName();

            // TODO cuma buat sementara
            Map<String, Object> contextData = FeatureContextHolder.getContext().getReportData();
            String noSts = contextData.get("noSts") != null ? contextData.get("noSts").toString() : "";
//            boolean filterBillerFlag = StringUtils.startsWithAny(noSts, "1294");

            ReprintTimeoutResource print = reprintTimeoutResourceRepository.findFirstByTrxTypeAndCode(trxType,resultCode);
//            if (StringUtils.equals(resultCode, "00") || (print != null) && filterBillerFlag == false)
//                return false;
//            //Remove auto reprint prefix temporary
            if (StringUtils.equals(resultCode, "00")|| (print != null))
                return false;

            ResourceMessage message = messageRepository.findByCodeAndMessage(errorKey, resultCode);
            String messageDesc = message == null ? "Unmapping Error Number (" + resultCode + ")" : message.getValue();
            Map<String, String> statusMap = new HashMap<>();
            statusMap.put("status", "01");
            statusMap.put("code", resultCode);
            statusMap.put("message", messageDesc);
            map.put("status", statusMap);

            return true;
        }
    }
