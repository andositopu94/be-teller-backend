package com.multipolar.uiteller.backend.resource;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.InetAddress;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Formatter;

public class HMAC {
    private final String HMAC_SHA1_ALGORITHM = "HmacSHA1";
    private String ip;

    private String getIp() {
        try {
            ip = InetAddress.getLocalHost().getHostAddress();
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return ip;
    }

    private String toHexString(byte[] bytes) {
    	try (Formatter formatter = new Formatter()) {
    		for (byte b : bytes) {
                formatter.format("%02x", b);
            }
            return formatter.toString();
    	}        
    }

    public String calculateHMAC(String data, String key) throws SignatureException, NoSuchAlgorithmException, InvalidKeyException {
        SecretKeySpec signingKey = new SecretKeySpec(key.getBytes(), HMAC_SHA1_ALGORITHM);
        Mac mac = Mac.getInstance(HMAC_SHA1_ALGORITHM);
        mac.init(signingKey);
        return toHexString(mac.doFinal(data.getBytes()));
    }

    public String getHMAC(String reqid) {
    	Date date = new Date();
        String hmac = null;
        try {
            StringBuilder builder = new StringBuilder();
            builder.append(reqid);
            builder.append(getIp());
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-ddhh:mm:ss");
            builder.append(formatter.format(date));
            String data = builder.toString();
            hmac = calculateHMAC(data, Constants.HMAC_KEY);
            
            System.out.println(formatter.format(date));
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return hmac;
    }
    
    public static void main(String[] args) {
    	HMAC inst = new HMAC();
    	String hmac = inst.getHMAC("00015");
    	
    	System.out.println(hmac);
    }
}
