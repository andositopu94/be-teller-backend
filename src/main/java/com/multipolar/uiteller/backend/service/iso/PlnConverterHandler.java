    package com.multipolar.uiteller.backend.service.iso;

    import com.multipolar.uiteller.backend.model.ResourceMessage;
    import com.multipolar.uiteller.backend.repository.ResourceMessageRepository;
    import com.multipolar.uiteller.backend.servletfilter.FeatureContextHolder;
    import org.apache.commons.lang3.StringUtils;
    import org.jpos.iso.ISOMsg;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.stereotype.Service;

    import java.util.HashMap;
    import java.util.Map;

    @Service
    public class PlnConverterHandler extends DefaultConverterHandler {
        private static final String NONTAGLIS_INQUIRY = "nontaglistInquiry";
        private static final String NONTAGLIS = "nontaglist";
        private static final String PLN = "pln";
        private static final String PLN_INQUIRY = "plnInquiry";
        private static final String TOKEN = "token";
        private static final String TOKEN_INQUIRY = "tokenInquiry";

        @Autowired
        private ResourceMessageRepository messageRepository;

        @Override
        public boolean supports(String s) {
            return StringUtils.equals(NONTAGLIS, s) || StringUtils.equals(NONTAGLIS_INQUIRY, s)
                    || StringUtils.equals(PLN, s) || StringUtils.equals(PLN_INQUIRY, s) || StringUtils.equals(TOKEN, s)
                    || StringUtils.equals(TOKEN_INQUIRY, s);
        }

        protected boolean exception(ISOMsg msg, Map<String, Object> map, String errorKey) {
            String resultCode = msg.getString(39);
            if (StringUtils.equals(resultCode, "00"))
                return false;

            String pcode = msg.getString(3);
            String messageData = msg.getString(61);
            String messageDesc = msg.getString(62);
            String productCode = msg.getString(63);
            int beginIndex = 0;
            if (StringUtils.equals(pcode, "380000")) {
                beginIndex = 44;
            }
            if (StringUtils.equals(pcode, "500000")) {
                if (StringUtils.equals(productCode, "03015")) {
                    beginIndex = 130;
                }
                if (StringUtils.equals(productCode, "03016")) {
                    beginIndex = 144;
                }
                if (StringUtils.equals(productCode, "04004")) {
                    beginIndex = 120;
                }
            }

            if (messageDesc == null) {
                ResourceMessage message = messageRepository.findByCodeAndMessage(errorKey, resultCode);
                messageDesc = message == null ? "Unmapping Error Number (" + resultCode + ")" : message.getValue();
            }

            String errorMsg;
                if (StringUtils.equals(resultCode, "08") || StringUtils.equals(resultCode, "51")) {
    			ResourceMessage message = messageRepository.findByCodeAndMessage(errorKey, resultCode);
    			errorMsg = message == null ? "Unmapping Error Number (" + resultCode + ")" : message.getValue();
                } else if ((StringUtils.equals(resultCode, "74") && (messageData.length()>beginIndex) && StringUtils.equals(pcode, "380000")) ||(StringUtils.equals(pcode, "380000") && StringUtils.equals(productCode, "03015") && StringUtils.equals(resultCode, "55") && messageData.length()>beginIndex) ) {
                    String errorMessageTemp = messageData.substring(beginIndex).replace("|00"," ");
                    errorMsg = errorMessageTemp.replace("|"," ");
                }
                else if (StringUtils.equals(resultCode, "999")) {
                errorMsg = "TRANSAKSI GAGAL";
            } else if (StringUtils.equals(resultCode, "888")) {
                // ini untuk kondisi postpaid dan nontaglis
                Map<String, Object> reportData = FeatureContextHolder.getContext().getReportData();
                map.putAll(reportData);
                errorMsg = "TRANSAKSI SEDANG DALAM PROSES";
            } else if (StringUtils.equals(resultCode, "777")) {
                // ini untuk kondisi postpaid dan nontaglis
                Map<String, Object> reportData = FeatureContextHolder.getContext().getReportData();
                map.putAll(reportData);
                errorMsg = "TRANSAKSI ANDA SEDANG DIPROSES. MOHON DILAKUKAN MANUAL ADVICE DENGAN MEMASUKKAN REFNUM: " + msg.getString(37);
            } else {
            	ResourceMessage message = messageRepository.findByCodeAndMessage(errorKey, resultCode);
            	errorMsg = message == null ? "Unmapping Error Number (" + resultCode + ")" : message.getValue();
            }

            Map<String, String> statusMap = new HashMap<String, String>();
            statusMap.put("status", "01");
            statusMap.put("code", resultCode);
            statusMap.put("message", errorMsg);
            map.put("status", statusMap);

            return true;
        }
    }
