package com.multipolar.uiteller.backend.security;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.multipolar.uiteller.backend.model.ApiComponent;
import com.multipolar.uiteller.backend.model.ApiRole;
import com.multipolar.uiteller.backend.model.SessionToken;
import com.multipolar.uiteller.backend.repository.ApiComponentRepository;
import com.multipolar.uiteller.backend.repository.ApiRoleRepository;
import com.multipolar.uiteller.backend.repository.SessionTokenRepository;
import com.multipolar.uiteller.backend.security.model.AuthLimit;
import com.multipolar.uiteller.backend.security.model.UserDetailImpl;
import com.multipolar.uiteller.backend.security.model.UserLimit;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.CompressionCodecs;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public class JwtAuthorizationFilter extends BasicAuthenticationFilter {
	private static Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);	

    private SessionTokenRepository sessionTokenRepository;
    private ApiRoleRepository apiRoleRepository;
    private ApiComponentRepository apiCompRepository;
    // private AdminChargeRepository chargeRepository;

    public JwtAuthorizationFilter(
    		AuthenticationManager authManager, 
    		SessionTokenRepository sessionTokenRepository,
    		ApiRoleRepository apiRoleRepository,
    		ApiComponentRepository apiCompRepository) {
        super(authManager);
        this.sessionTokenRepository = sessionTokenRepository;
        this.apiRoleRepository = apiRoleRepository;
        this.apiCompRepository = apiCompRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        String header = req.getHeader(SecurityConstants.HEADER_STRING);

        if (header == null || (!header.startsWith(SecurityConstants.TOKEN_PREFIX)
                && !header.startsWith(SecurityConstants.REFRESH_PREFIX))) {
            chain.doFilter(req, res);
            return;
        }

        UsernamePasswordAuthenticationToken authentication = null;

        if (header.startsWith(SecurityConstants.TOKEN_PREFIX)) {
            authentication = getAuthentication(req, header, SecurityConstants.TOKEN_PREFIX);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            // update session token
            String username = authentication.getPrincipal().toString();
            List<SessionToken> sessionTokens = sessionTokenRepository.findByUsername(username);
            if (sessionTokens.size() <= 0) {
            	res.sendError(403);
            	chain.doFilter(req, res);
            	return;
            }
            
            //String remoteAddr = req.getRemoteAddr();
			String remoteAddr = extractClientIp(req);
            boolean sameAddr = false;
            for (SessionToken token : sessionTokens)
            	sameAddr = sameAddr || StringUtils.equals(remoteAddr, token.getIpAddress());
            	
            if (sameAddr) {
            	for (SessionToken token : sessionTokens) {
            		token.setUpdatedAt(new Date());
            		sessionTokenRepository.save(token);
            	}
            } else {
            	res.sendError(403, "User yang sama sedang login pada IP Address: " + remoteAddr);
            }
        } else {
            authentication = getAuthentication(req, header, SecurityConstants.REFRESH_PREFIX);
            String username = authentication.getPrincipal().toString();
            List<SessionToken> sessionTokens = sessionTokenRepository.findByUsername(username);
            if (sessionTokens == null)
            	throw new IllegalArgumentException("refresh token not exists");
            if (!StringUtils.equals(header.replace(SecurityConstants.REFRESH_PREFIX, ""), sessionTokens.get(0).getRefreshToken()))
                throw new IllegalArgumentException("Refresh token is invalid");

            Map<String, Object> map = new HashMap<>();
            map.put(SecurityConstants.USER_DETAILS, authentication.getDetails());

            Collection<? extends GrantedAuthority> grantedAuthorities = authentication.getAuthorities();
    		String roles = "";
    		for (GrantedAuthority ga : grantedAuthorities) {
    			roles += (ga.getAuthority() + ",");
    		}
    		StringUtils.removeEnd(roles, ",");
            
    		// TELLER ACCOUNT
    		UserDetailImpl details = (UserDetailImpl)authentication.getDetails();
    		String branchCode = details.getBranchCode();
    		String account = details.getAccountNumber();
    		String ccyCode = SecurityConstants.CURRENCY_CODE;
    		
    		// TRANSACTION LIMIT
    		List<UserLimit> userLimits = details.getUserLimits();
    		
    		List<AuthLimit> authLimits = details.getAuthLimits();

            String token = Jwts.builder()
                    .setSubject(authentication.getPrincipal().toString())
                    .setClaims(map)
                    .compressWith(CompressionCodecs.DEFLATE)
                    .setExpiration(new Date(System.currentTimeMillis() + SecurityConstants.EXPIRATION_TIME))
                    .signWith(SignatureAlgorithm.HS512, SecurityConstants.SECRET.getBytes()).compact();

            StringWriter stringWriter = new StringWriter();
            JsonFactory jFactory = new JsonFactory();
            JsonGenerator jsonGenerator = jFactory.createGenerator(stringWriter);
            jsonGenerator.writeStartObject();
            jsonGenerator.writeStringField("token", token);
    		jsonGenerator.writeStringField("tellerAccount", branchCode + account + ccyCode);
    		
    		// generateCharge(jsonGenerator);
    		jsonGenerator.writeFieldName("trxLimits");
    		generateUserLimit(jsonGenerator, userLimits);
    		jsonGenerator.writeFieldName("supervisor");
    		generateAuthLimit(jsonGenerator, authLimits);
            jsonGenerator.writeEndObject();
            
            jsonGenerator.close();
            
            String responseString = prettyJSONUtility(stringWriter.toString());
    		res.getWriter().write(responseString);
    		
            return;
        }
        chain.doFilter(req, res);
    }

    @SuppressWarnings("unchecked")
    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request, String token, String prefix) 
    		throws ExpiredJwtException, AccessDeniedException, JsonProcessingException, IOException {
        if (token != null) {
            Claims claims = Jwts.parser().setSigningKey(SecurityConstants.SECRET.getBytes())
                    .parseClaimsJws(token.replace(prefix, "")).getBody();
            
			Map<String, Object> map = (Map<String, Object>) claims.get(SecurityConstants.USER_DETAILS);
			ObjectMapper mapper = new ObjectMapper();
			String jsonResult = mapper.writeValueAsString(map);
			UserDetailImpl userDetail = mapper.readValue(jsonResult, UserDetailImpl.class);
            String roleName = userDetail.getRoles();
            String user = userDetail.getUsername();
            
            /*
            // cek roll user terhadap url yang akan diakses
            boolean authorized = isApiAuthorized(request, roleName);
            if (!authorized) {
            	logger.warn("Deny access for user " + user + "when accessing api " + request.getRequestURI());
            	throw new AccessDeniedException("Access in not allowed for calling the secured api");
            }
            */
            
            if (user != null) {
            	// create authority listPstn
            	List<SimpleGrantedAuthority> grantedAuthorities = new ArrayList<>();
            	String[] roles = StringUtils.split(roleName, ",");
            	for (String role : roles) {
            		SimpleGrantedAuthority authority = new SimpleGrantedAuthority(role);
            		grantedAuthorities.add(authority);
            	}
                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(user, null, grantedAuthorities);
                auth.setDetails(userDetail);
                return auth;
            }
            return null;
        }
        return null;
    }
    
    private boolean isApiAuthorized(HttpServletRequest request, String roleName) {
    	// if superuser always return true
    	if (StringUtils.equals(SecurityConstants.SUPERUSER_ROLE, roleName))
    		return true;
    	
    	String requestUri = request.getRequestURI();
    	ApiComponent component = apiCompRepository.findByApi(requestUri);
    	
    	// unregistered user, only superuser can access
    	if (component == null)
    		return false;
    	
    	String[] userRoles = StringUtils.split(roleName, ",");
    	for (String role : userRoles) {
	    	ApiRole apiRole = apiRoleRepository.findByRoleName(role);
	    	Set<ApiComponent> components = apiRole.getApis();
	    	if (components.contains(component))
	    		return true;
    	}
    	
    	// access is denied
    	return false;
    }
    
    private String prettyJSONUtility(String simpleJSON) {
		ObjectMapper mapper = new ObjectMapper();
		try {
			Object json = mapper.readValue(simpleJSON, Object.class);
			return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(json);
		} catch (IOException ex){
			return simpleJSON;
		}
	}
    
    private void generateAuthLimit(JsonGenerator gen, List<AuthLimit> authLimits) throws IOException {
		gen.writeStartArray();
		for (AuthLimit limit : authLimits) {
			gen.writeStartObject();
			gen.writeStringField("userId", limit.getUserId());
			gen.writeStringField("supervisorName", limit.getUserName());
			gen.writeStringField("approvalLimit", limit.getMaxAuthDebitOverbook());
			gen.writeStringField("branchId", limit.getBRANCHID());
			gen.writeEndObject();
		}
		gen.writeEndArray();
	}
    
    private void generateUserLimit(JsonGenerator gen, List<UserLimit> userLimits) throws IOException {
		gen.writeStartArray();
		for (UserLimit limit : userLimits) {
			gen.writeStartObject();
			gen.writeStringField("currency", limit.getCurrency());
			gen.writeStringField("obTrxLimit", limit.getMaxDebitOverbook());
			gen.writeStringField("cashTrxLimit", limit.getMaxCashTransaction());
			gen.writeEndObject();
		}
		gen.writeEndArray();
	}

	public String extractClientIp(HttpServletRequest request) {
		String xfHeader = request.getHeader("X-Forwarded-For");
		if (xfHeader != null) {
			return xfHeader.split(",")[0].trim();
		}
		return request.getRemoteAddr();
	}
    
    /*
    private void generateCharge(JsonGenerator gen) throws IOException {
		List<AdminCharge> charges = chargeRepository.findAll();
		gen.writeFieldName("charge");
		gen.writeStartObject();
		for (AdminCharge charge : charges) {
			gen.writeStringField(charge.getTransaction(), String.valueOf(charge.getCharge()));
		}
		gen.writeEndObject();
	}
	*/
}
