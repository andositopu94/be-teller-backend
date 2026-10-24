package com.multipolar.uiteller.backend.security;

public class SecurityConstants {
	//public static final String SECRET = "SecretKeyToGenJWTs";
    public static final String SECRET = "Z6Z/LApVBPgvHNDtJ5eCPBPwLQ9WlnyNCuHOlRoVzbCc9gR/hGsqS6ncsp+cVXcyiaARozOt/7vvO7pB3YpVVg==";
    public static final long EXPIRATION_TIME = 7 * 24 * 60 * 60 * 1000; // 1 min
    public static final long REFRESH_EXPIRATION_TIME = 7 * 24 * 60 * 60 * 1000; // 12 hours
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String REFRESH_PREFIX = "Refresh ";
    public static final String HEADER_STRING = "Authorization";
    public static final String SIGN_UP_URL = "/api/sign-up";
    public static final String LOGOUT_URL = "/logout";
    public static final String USER_DETAILS = "USER_DETAILS";
    public static final String USERNAME = "USERNAME";
    
    public static final String ROLE = "ROLE";
    public static final String SUPERUSER_ROLE = "ROLE_SUPER";
    public static final String TELLER_ACCOUNT = "TELLER_ACCOUNT";
    public static final String BRANCH_ID = "BRANCH_ID";
    public static final String CURRENCY_CODE = "360";
    public static final String TRANSACTION_LIMIT = "TRANSACTION_LIMIT";
    public static final String ANONYMOUS_USER = "Anonymous user";
    public static final String BRANCH_CODE = "BRANCH_CODE";
    

    public static final long SESSION_TOKEN_EXPIRY = 5 * 60 * 1000; // 30 menit

}
