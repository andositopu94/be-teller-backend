package com.multipolar.uiteller.backend.service.sms;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.multipolar.uiteller.backend.service.CoreGatewayServiceImpl;

@Service
public class SMSSender {
    private static Logger logger = LoggerFactory.getLogger(CoreGatewayServiceImpl.class);
    // 081282767997
    @Value("${smsgateway.server.url}")
    private String url;

    private RestTemplate restTemplate;

    @Autowired
    private DESedeEncryption encryptor;

    public SMSSender(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public SMSResponse sendSms(String phoneNo, String message) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            String encMessage = encryptor.encode(message);
            SMSModel model = new SMSModel(phoneNo, encMessage);

            HttpEntity<SMSModel> entity = new HttpEntity<SMSModel>(model, headers);
            ResponseEntity<SMSResponse> response = restTemplate.exchange(url, HttpMethod.POST, entity, SMSResponse.class);

            return response.getBody();
        } catch (Exception ex) {
            logger.error("Exception happen when contacting sms gateway server", ex);
            throw new RuntimeException(ex);
        }
    }
}
