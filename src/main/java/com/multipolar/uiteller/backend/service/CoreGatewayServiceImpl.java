package com.multipolar.uiteller.backend.service;

import com.multipolar.uiteller.backend.resource.Constants;
import com.multipolar.uiteller.backend.security.model.AccountResponse;
import com.multipolar.uiteller.backend.security.model.AuthResponse;
import com.multipolar.uiteller.backend.security.model.CheckResponse;
import com.multipolar.uiteller.backend.security.model.DetailResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Map;

@Service
public class CoreGatewayServiceImpl {
    private static Logger logger = LoggerFactory.getLogger(CoreGatewayServiceImpl.class);

    private static final String HMAC_SHA1_ALGORITHM = "HmacSHA1";

    @Value("${sso.server.url}")
    private String url;
    @Value("${sso.server.authReqId}")
    private String authReqId;
    @Value("${sso.server.accountReqId}")
    private String accountReqId;
    @Value("${sso.server.userGtw}")
    private String userGtw;
    @Value("${sso.server.channelId}")
    private int channelId;
    @Value("${sso.default-gateway-ip}")
    private String defaultGatewayIp;
    @Autowired
    private RestTemplate restTemplate;

    public CoreGatewayServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    private String getIp() throws UnknownHostException {
        return InetAddress.getLocalHost().getHostAddress();
    }

    private String toHexString(byte[] bytes) {
        try (Formatter formatter = new Formatter()) {
            for (byte b : bytes) {
                formatter.format("%02x", b);
            }
            return formatter.toString();
        }
    }

        private String calculateHMAC(String data, String key) throws SignatureException, NoSuchAlgorithmException, InvalidKeyException {
            SecretKeySpec signingKey = new SecretKeySpec(key.getBytes(), HMAC_SHA1_ALGORITHM);
            Mac mac = Mac.getInstance(HMAC_SHA1_ALGORITHM);
            mac.init(signingKey);
            return toHexString(mac.doFinal(data.getBytes()));
        }

        private String getHMAC(String reqid, Date trxDate) {
            String hmac = null;

            try {
                StringBuilder builder = new StringBuilder();
                builder.append(reqid);
//                builder.append(getIp());
//                builder.append(Constants.DEFAULT_GATEWAY_IP);
                builder.append(defaultGatewayIp);
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-ddHH:mm:ss");
                builder.append(formatter.format(trxDate));
                String data = builder.toString();
                hmac = calculateHMAC(data, Constants.HMAC_KEY);
            } catch (Exception ex) {
                logger.error("Exception when creating message to SSO Server", ex);
            }

        return hmac;
    }

            /*
            private CoreUser buildFromResponse(JsonNode node) throws AuthenticationException {
                // check core authentication is success or fail
                int statusId = node.get("statusId").asInt();
                if (statusId == 0)
                    throw new UsernameNotFoundException("SSO return failed response: " + node.get("message") + " (" + node.get("rCode") + " )");

                CoreUser user = new CoreUser();
                user.setStatusId(statusId);

                JsonNode userMap = node.get("result").get("USERMAP");
                user.setNik(userMap.get("NIK").asText());
                user.setUserId(userMap.get("USERID").asText());
                user.setUsername(userMap.get("USERNM").asText());

                user.setUserStatus(userMap.get("STATUS").asInt());
                user.setPassword(userMap.get("PWD").asText());

                return user;
            }
            */

    public AuthResponse getAuthResponse(Authentication user) throws AuthenticationException {
//        ignoreCertificates();
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, String> map = buildJsonRequestBody(user);
            HttpEntity<Map<String, String>> entity = new HttpEntity<Map<String, String>>(map, headers);
            ResponseEntity<AuthResponse> response = restTemplate.exchange(url, HttpMethod.POST, entity, AuthResponse.class);

            return response.getBody();
        } catch (Exception ex) {
            logger.error("Exception happen when contacting authentication server", ex);
            throw new UsernameNotFoundException("Exception happen when contacting authentication server", ex);
        }
    }

    public AccountResponse getAccountResponse(String accountNumber) throws AuthenticationException {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, String> map = buildJsonAccountRequestBody(accountNumber);
            HttpEntity<Map<String, String>> entity = new HttpEntity<Map<String, String>>(map, headers);
            ResponseEntity<AccountResponse> response = restTemplate.exchange(url, HttpMethod.POST, entity, AccountResponse.class);

            return response.getBody();
        } catch (Exception ex) {
            logger.error("Exception happen when contacting authentication server", ex);
            throw new UsernameNotFoundException("Exception happen when contacting authentication server", ex);
        }
    }

    public DetailResponse getDetailResponse(String accountNumber) throws AuthenticationException {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, String> map = buildJsonDetailRequestBody(accountNumber);
            HttpEntity<Map<String, String>> entity = new HttpEntity<Map<String, String>>(map, headers);
            ResponseEntity<DetailResponse> response = restTemplate.exchange(url, HttpMethod.POST, entity, DetailResponse.class);

            return response.getBody();
        } catch (Exception ex) {
            logger.error("Exception happen when contacting authentication server", ex);
            throw new UsernameNotFoundException("Exception happen when contacting authentication server", ex);
        }
    }

    public CheckResponse getCheckResponse(String accountNumber, String chq, String pg) throws AuthenticationException {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, String> map = buildJsonCheckRequestBody(accountNumber, chq, pg);
            HttpEntity<Map<String, String>> entity = new HttpEntity<Map<String, String>>(map, headers);
            ResponseEntity<CheckResponse> response = restTemplate.exchange(url, HttpMethod.POST, entity, CheckResponse.class);

            return response.getBody();
        } catch (Exception ex) {
            logger.error("Exception happen when contacting server", ex);
            throw new UsernameNotFoundException("Exception happen when contacting server", ex);
        }
    }

    private Map<String, String> buildJsonRequestBody(Authentication user) throws AuthenticationException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        SimpleDateFormat timeFormat = new SimpleDateFormat("HHmmss");
        Date trxDate = new Date();
        // AuthenticationRequest request = new AuthenticationRequest();

        String hmac = getHMAC(authReqId, trxDate);
        Map<String, String> map = new HashMap<>();
        map.put("authKey", hmac);
        map.put("reqId", authReqId);
        map.put("txDate", dateFormat.format(trxDate));
        map.put("txHour", timeFormat.format(trxDate));
        map.put("userGtw", userGtw);
        map.put("channelId", String.valueOf(channelId));
        map.put("userId", user.getPrincipal().toString());
        map.put("password", user.getCredentials().toString());

        return map;
    }

    private Map<String, String> buildJsonAccountRequestBody(String accountNumber) throws AuthenticationException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        SimpleDateFormat timeFormat = new SimpleDateFormat("HHmmss");
        Date trxDate = new Date();
        // AuthenticationRequest request = new AuthenticationRequest();

        String hmac = getHMAC(accountReqId, trxDate);
        Map<String, String> map = new HashMap<>();
        map.put("authKey", hmac);
        map.put("reqId", accountReqId);
        map.put("txDate", dateFormat.format(trxDate));
        map.put("txHour", timeFormat.format(trxDate));
        map.put("userGtw", userGtw);
        map.put("channelId", String.valueOf(channelId));
        map.put("accnbr", accountNumber);

        return map;
    }

    private Map<String, String> buildJsonDetailRequestBody(String accountNumber) throws AuthenticationException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        SimpleDateFormat timeFormat = new SimpleDateFormat("HHmmss");
        Date trxDate = new Date();
        // AuthenticationRequest request = new AuthenticationRequest();

        String hmac = getHMAC(Constants.CIF_ACCOUNT, trxDate);
        Map<String, String> map = new HashMap<>();
        map.put("authKey", hmac);
        map.put("reqId", Constants.CIF_ACCOUNT);
        map.put("txDate", dateFormat.format(trxDate));
        map.put("txHour", timeFormat.format(trxDate));
        map.put("userGtw", userGtw);
        map.put("channelId", String.valueOf(channelId));
        map.put("accnbr", accountNumber);

        return map;
    }

    private Map<String, String> buildJsonCheckRequestBody(String accountNumber, String chq, String pg) throws AuthenticationException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        SimpleDateFormat timeFormat = new SimpleDateFormat("HHmmss");
        Date trxDate = new Date();
        String hmac = getHMAC(Constants.CHECK_ACCOUNT, trxDate);
        Map<String, String> map = new HashMap<>();
        map.put("authKey", hmac);
        map.put("reqId", Constants.CHECK_ACCOUNT);
        map.put("txDate", dateFormat.format(trxDate));
        map.put("txHour", timeFormat.format(trxDate));
        map.put("userGtw", userGtw);
        map.put("channelId", String.valueOf(channelId));
        map.put("accnbr", accountNumber);
        map.put("chqbknbr", chq);
        map.put("pagenbr", pg);
        return map;
    }
}
