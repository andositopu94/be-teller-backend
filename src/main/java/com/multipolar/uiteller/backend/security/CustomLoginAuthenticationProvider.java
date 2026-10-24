package com.multipolar.uiteller.backend.security;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserCache;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsChecker;
import org.springframework.security.core.userdetails.cache.NullUserCache;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import com.multipolar.uiteller.backend.security.model.AuthResponse;
import com.multipolar.uiteller.backend.security.model.RoleMap;
import com.multipolar.uiteller.backend.service.CoreGatewayServiceImpl;

@Component("authenticationProvider")
public class CustomLoginAuthenticationProvider extends AbstractUserDetailsAuthenticationProvider {
	
	private static Logger logger = LoggerFactory.getLogger(CustomLoginAuthenticationProvider.class);
	
	@Autowired
	private CoreGatewayServiceImpl authenticationService;
	
	private UserCache userCache = new NullUserCache();
	private UserDetailsChecker preAuthenticationChecks = new DefaultPreAuthenticationChecks();
	
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		Assert.isInstanceOf(UsernamePasswordAuthenticationToken.class, authentication,
				messages.getMessage(
						"AbstractUserDetailsAuthenticationProvider.onlySupports",
						"Only UsernamePasswordAuthenticationToken is supported"));

		// Determine username
		String username = (authentication.getPrincipal() == null) ? "NONE_PROVIDED" : authentication.getName();

		boolean cacheWasUsed = true;
		UserDetails user = this.userCache.getUserFromCache(username);
		
		List<GrantedAuthority> grantedAuthorities = new ArrayList<>();

		if (user == null) {
			cacheWasUsed = false;
			AuthResponse response = authenticationService.getAuthResponse(authentication);
			
			String statusId = response.getStatusId();
			if (!StringUtils.equals(statusId, "1")) 
				throw new BadCredentialsException(response.getRCode() + ":" + response.getMessage());
			
			// get user role
			List<RoleMap> roleMap = response.getResult().getRolesMap();
			for (RoleMap role : roleMap) {
				GrantedAuthority authority = new SimpleGrantedAuthority(role.getRoleId());
				grantedAuthorities.add(authority);
			}
			
			((UsernamePasswordAuthenticationToken)authentication).setDetails(response.getResult());
			user = retrieveUser(username, (UsernamePasswordAuthenticationToken) authentication);
		}

		try {
			preAuthenticationChecks.check(user);
			additionalAuthenticationChecks(user, (UsernamePasswordAuthenticationToken) authentication);
		}
		catch (AuthenticationException exception) {
			if (cacheWasUsed) {
				// There was a problem, so try again after checking
				// we're using latest data (i.e. not from the cache)
				cacheWasUsed = false;
				user = retrieveUser(username,
						(UsernamePasswordAuthenticationToken) authentication);
				preAuthenticationChecks.check(user);
				additionalAuthenticationChecks(user,
						(UsernamePasswordAuthenticationToken) authentication);
			}
			else {
				throw exception;
			}
		}

		if (!cacheWasUsed) {
			this.userCache.putUserInCache(user);
		}
		
		// recreate authentication object
		authentication = new UsernamePasswordAuthenticationToken(user, "", grantedAuthorities);
		
		return authentication;
	}

	@Override
	protected void additionalAuthenticationChecks(UserDetails userDetails,
			UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
		// do nothing
		return;		
	}

	@Override
	protected UserDetails retrieveUser(String username, UsernamePasswordAuthenticationToken authentication)
			throws AuthenticationException {
		return (UserDetails)authentication.getDetails();
	}
	
	private class DefaultPreAuthenticationChecks implements UserDetailsChecker {
		public void check(UserDetails user) {
			if (!user.isAccountNonLocked()) {
				logger.debug("User account is locked");

				throw new LockedException(messages.getMessage(
						"AbstractUserDetailsAuthenticationProvider.locked",
						"User account is locked"));
			}

			if (!user.isEnabled()) {
				logger.debug("User account is disabled");

				throw new DisabledException(messages.getMessage(
						"AbstractUserDetailsAuthenticationProvider.disabled",
						"User is disabled"));
			}

			if (!user.isAccountNonExpired()) {
				logger.debug("User account is expired");

				throw new AccountExpiredException(messages.getMessage(
						"AbstractUserDetailsAuthenticationProvider.expired",
						"User account has expired"));
			}
		}
	}

}
