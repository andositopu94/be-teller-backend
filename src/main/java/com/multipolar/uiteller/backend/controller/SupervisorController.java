package com.multipolar.uiteller.backend.controller;

import java.nio.charset.Charset;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;

import javax.crypto.Cipher;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.multipolar.uiteller.backend.model.ApplicationUser;
import com.multipolar.uiteller.backend.resource.Constants;
import com.multipolar.uiteller.backend.security.model.AuthResponse;
import com.multipolar.uiteller.backend.service.CoreGatewayServiceImpl;

@RestController
@RequestMapping("/apiv2")
public class SupervisorController {
	
	@Autowired
	private CoreGatewayServiceImpl authenticationService;
	
	@PostMapping("/sup-login")
	public ResponseEntity<?> supervisorLogin(@RequestBody ApplicationUser user) {
		// read from authentication server
		String password = "";
		try {
			password = decryptMessage(user.getPassword());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if (StringUtils.isEmpty(password))
			throw new BadCredentialsException("Username atau Password salah");
		
		Authentication auth = new UsernamePasswordAuthenticationToken(user.getUsername(), password);
		AuthResponse response = authenticationService.getAuthResponse(auth);
		String statusId = response.getStatusId();
		if (StringUtils.equals(statusId, "0")) 
			throw new BadCredentialsException(response.getRCode() + ":" + response.getMessage());
		else
			return ResponseEntity.ok("true");			
	}
	
	private String decryptMessage(String encMessage) throws Exception {
		String pk = StringUtils.replace(Constants.PRIVATE_KEY, "-----BEGIN RSA PRIVATE KEY-----", "");
		pk = StringUtils.replace(pk, "-----END RSA PRIVATE KEY-----", "");

		PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(Base64.decodeBase64(pk));

		KeyFactory kf = KeyFactory.getInstance("RSA");
		PrivateKey privKey = kf.generatePrivate(keySpec);

		Cipher cipher = Cipher.getInstance("RSA/None/PKCS1Padding");
		cipher.init(Cipher.DECRYPT_MODE, privKey);
		byte[] cipherText = cipher.doFinal(Base64.decodeBase64(encMessage));
		return new String(cipherText, Charset.defaultCharset());
	}
}
