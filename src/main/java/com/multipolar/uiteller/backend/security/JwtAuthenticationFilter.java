package com.multipolar.uiteller.backend.security;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.multipolar.uiteller.backend.model.ApplicationUser;
import com.multipolar.uiteller.backend.model.Role;
import com.multipolar.uiteller.backend.model.SessionToken;
import com.multipolar.uiteller.backend.repository.RoleRepository;
import com.multipolar.uiteller.backend.repository.SessionTokenRepository;
import com.multipolar.uiteller.backend.resource.Constants;
import com.multipolar.uiteller.backend.security.model.AuthLimit;
import com.multipolar.uiteller.backend.security.model.AuthResult;
import com.multipolar.uiteller.backend.security.model.UserDetailImpl;
import com.multipolar.uiteller.backend.security.model.UserLimit;
import com.multipolar.uiteller.backend.service.AccNbrService;
import com.multipolar.uiteller.backend.servletfilter.RequestLoggerUtil;
import freemarker.template.SimpleDate;
import io.jsonwebtoken.CompressionCodecs;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.DefaultClaims;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.crypto.Cipher;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.text.SimpleDateFormat;
import java.util.*;

public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private static Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    private static final SimpleDateFormat CURRENT_DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");

    private AuthenticationManager authenticationManager;
    private SessionTokenRepository sessionTokenRepository;
    // private AdminChargeRepository chargeRepository;
    private RequestLoggerUtil loggerUtil;
    private AccNbrService accNbrService;
    private RoleRepository roleRepository;

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager,
                                   SessionTokenRepository sessionTokenRepository, RoleRepository roleRepository,
                                   RequestLoggerUtil loggerUtil, AccNbrService accNbrService) {
        this.authenticationManager = authenticationManager;
        this.sessionTokenRepository = sessionTokenRepository;
        // this.chargeRepository = chargeRepository;
        this.loggerUtil = loggerUtil;
        this.accNbrService = accNbrService;
        this.roleRepository = roleRepository;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest req, HttpServletResponse res) throws AuthenticationException {
        try {
            ApplicationUser creds = new ObjectMapper().readValue(req.getInputStream(), ApplicationUser.class);

            String password = decryptMessage(creds.getPassword());

            // read from authentication server
            Authentication auth = new UsernamePasswordAuthenticationToken(creds.getUsername(), password);


            return authenticationManager.authenticate(auth);
        } catch (BadCredentialsException ex) {
            throw ex;
        } catch (IOException e) {
            // something happen when attempt to read username and password from request
            logger.error("Can't read username/password from request", e);
            throw new BadCredentialsException("Can't read username/password from request");
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadCredentialsException("Terjadi Kesalahan Pada System, Hubungi Administrator");
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest req, HttpServletResponse res, FilterChain chain,
                                            Authentication auth) throws IOException, ServletException {
        String username = ((UserDetails) auth.getPrincipal()).getUsername();
        UserDetailImpl userDetails = new UserDetailImpl();
        userDetails.setUsername(username);

        List<SessionToken> sessionToken = sessionTokenRepository.findByUsername(username);
        // cek session token sudah ada dan waktunya < SecurityConstants.SESSION_TOKEN_EXPIRY
        if (sessionToken.size() > 0) {
            SessionToken token = sessionToken.get(sessionToken.size() - 1);
            Date lastUpdate = token.getUpdatedAt();
            if (System.currentTimeMillis() - lastUpdate.getTime() < SecurityConstants.SESSION_TOKEN_EXPIRY) {
                res.sendError(418, "User telah login pada IP Address: " + token.getIpAddress());
                return;
            } else {
                sessionTokenRepository.delete(sessionToken);
            }
        }

        // ROLE USER
        Collection<? extends GrantedAuthority> grantedAuthorities = auth.getAuthorities();
        List<String> roles = new ArrayList<>();
        for (GrantedAuthority authority : grantedAuthorities) {
            roles.add(authority.getAuthority());
        }
        userDetails.setRoles(StringUtils.join(roles, ","));

        // TELLER ACCOUNT
        AuthResult details = (AuthResult) auth.getDetails();
        String branchCode = details.getBRANCHMAP().getBranchId();
        String branchName = details.getBRANCHMAP().getBranchName();
        String account = details.getLimitUser().getAccountNumber();
        String ccyCode = SecurityConstants.CURRENCY_CODE;
        String accNbr = branchCode + account + ccyCode;
        userDetails.setAccountNumber(account);
        userDetails.setBranchCode(branchCode);
        userDetails.setBranchName(branchName);

        // TRANSACTION LIMIT
        List<UserLimit> userLimits = details.getLimitUser().getUserLimits();
        userDetails.setUserLimits(userLimits);
        // map.put(SecurityConstants.TRANSACTION_LIMIT, trxLimit);

        // SUPERVISOR
        List<AuthLimit> authLimits = details.getLimitUser().getAUTH_LIMIT();
        userDetails.setAuthLimits(authLimits);

        DefaultClaims claims = new DefaultClaims();
        claims.put(SecurityConstants.USER_DETAILS, userDetails);

        String token = Jwts.builder().setSubject(username).setClaims(claims).compressWith(CompressionCodecs.DEFLATE)
                .setExpiration(new Date(System.currentTimeMillis() + SecurityConstants.EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS512, SecurityConstants.SECRET.getBytes()).compact();
        String refreshToken = Jwts.builder().setSubject(username)
                // .setClaims(map)
                .compressWith(CompressionCodecs.DEFLATE)
                .setExpiration(new Date(System.currentTimeMillis() + SecurityConstants.REFRESH_EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS512, SecurityConstants.SECRET.getBytes()).compact();
        res.addHeader(SecurityConstants.HEADER_STRING, SecurityConstants.TOKEN_PREFIX + token);
        res.addHeader(Constants.REQUEST_ID_HEADER, String.valueOf(System.currentTimeMillis()));

        SessionToken newToken = new SessionToken(username, refreshToken);
        //newToken.setIpAddress(req.getRemoteAddr());
        newToken.setIpAddress(extractClientIp(req));
        sessionTokenRepository.save(newToken);

        // res.getWriter().write("{\"token\":\"" + token + "\"}");

        StringWriter stringWriter = new StringWriter();
        JsonFactory jFactory = new JsonFactory();
        JsonGenerator jsonGenerator = jFactory.createGenerator(stringWriter);
        jsonGenerator.writeStartObject();
        jsonGenerator.writeStringField("token", token);
        jsonGenerator.writeStringField("refresh-token", refreshToken);
        jsonGenerator.writeStringField("tellerAccount", accNbrService.getAccount(accNbr));
        jsonGenerator.writeStringField("branchCode", branchCode);
        jsonGenerator.writeStringField("branchName", branchName);

        // current date
        String currentDate = CURRENT_DATE_FORMAT.format(new Date());
        jsonGenerator.writeStringField("currentDate", currentDate);

        generateRoles(jsonGenerator, roles);

        // charge
        // generateCharge(jsonGenerator);

        // trx limit for teller
        jsonGenerator.writeFieldName("trxLimits");
        generateUserLimit(jsonGenerator, userLimits);

        // supervisor
        jsonGenerator.writeFieldName("supervisor");
        generateAuthLimit(jsonGenerator, authLimits);
        jsonGenerator.writeEndObject();

        jsonGenerator.close();

        String responseString = prettyJSONUtility(stringWriter.toString());
        res.getWriter().write(responseString);

        loggerUtil.log(req, res);
        loggerUtil.logActivity(req, res, username);
    }

    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                              AuthenticationException failed) throws IOException, ServletException {

        SecurityContextHolder.clearContext();

        if (failed instanceof BadCredentialsException)
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, failed.getMessage());
        else if (failed instanceof LockedException || failed instanceof DisabledException)
            response.sendError(HttpServletResponse.SC_FORBIDDEN, failed.getMessage());
        else
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, failed.getMessage());
    }

    private String prettyJSONUtility(String simpleJSON) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            Object json = mapper.readValue(simpleJSON, Object.class);
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(json);
        } catch (IOException ex) {
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

    private void generateRoles(JsonGenerator gen, List<String> roleIds) throws IOException {
        List<Role> roles = roleRepository.findByRoleIdIn(roleIds);
        Set<String> unique = new HashSet<>();
        Boolean teller = false;
        Boolean supervisor = false;
        Boolean admin = false;
        for (Role role : roles) {
            String menuid = role.getMenuId();
            String[] menu = menuid.split(",");
            unique.addAll(Arrays.asList(menu));
            if (StringUtils.startsWithIgnoreCase(role.getName(), Constants.TELLER_ROLE_DEFINE))
                teller = true;
            if (StringUtils.startsWithIgnoreCase(role.getName(), Constants.SUPERVISOR_ROLE_DEFINE))
                supervisor = true;
            if (StringUtils.startsWithIgnoreCase(role.getName(), Constants.ADMIN_ROLE_DEFINE))
                admin = true;
        }
        gen.writeStringField("menus", StringUtils.join(unique, ","));
        gen.writeBooleanField("teller", teller);
        gen.writeBooleanField("supervisi", supervisor);
        gen.writeBooleanField("admin", admin);
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

    public String extractClientIp(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader != null) {
            return xfHeader.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }


}
