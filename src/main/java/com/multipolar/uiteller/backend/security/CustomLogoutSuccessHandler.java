package com.multipolar.uiteller.backend.security;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.multipolar.uiteller.backend.model.ApplicationUser;
import com.multipolar.uiteller.backend.model.SessionToken;
import com.multipolar.uiteller.backend.repository.SessionTokenRepository;
import com.multipolar.uiteller.backend.security.model.UserDetailImpl;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;

@Component
public class CustomLogoutSuccessHandler implements LogoutSuccessHandler {
	
	@Autowired
	private SessionTokenRepository sessionTokenRepository;
	
	@Override
	public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
			throws IOException, ServletException {
		
		ApplicationUser user = getAuthentication(request);
		if (user != null) { 
			List<SessionToken> tokens = sessionTokenRepository.findByUsername(user.getUsername());
			sessionTokenRepository.delete(tokens);
		} 

		response.setStatus(HttpServletResponse.SC_OK);
	}
	
	@SuppressWarnings("unchecked")
	private ApplicationUser getAuthentication(HttpServletRequest request) throws JsonProcessingException, IOException {
        String token = request.getHeader(SecurityConstants.HEADER_STRING);
        if (token != null) {
        	Claims claims = null;
        	
        	try {
	        	claims = Jwts.parser()
	                    .setSigningKey(SecurityConstants.SECRET.getBytes())
	                    .parseClaimsJws(token.replace(SecurityConstants.TOKEN_PREFIX, ""))
	                    .getBody();
        	} catch (ExpiredJwtException ex) {
        		claims = ex.getClaims();
        	} catch (Exception ex) {
        		return null;
        	}
        	
        	if (claims != null) {
	        	Map<String, Object> map = (Map<String, Object>) claims.get(SecurityConstants.USER_DETAILS);
				ObjectMapper mapper = new ObjectMapper();
				String jsonResult = mapper.writeValueAsString(map);
				UserDetailImpl userDetail = mapper.readValue(jsonResult, UserDetailImpl.class);
	            String user = userDetail.getUsername();
	
	            if (user != null) {
	                return new ApplicationUser(user, null);
	            }
        	}
            return null;
        }
        return null;
    }

}
