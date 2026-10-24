package com.multipolar.uiteller.backend.security.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class AuthResult  implements UserDetails{

	private static final long serialVersionUID = -7922232634384040332L;

	private static Logger logger = LoggerFactory.getLogger(AuthResult.class);

	@JsonProperty("LIMIT_USER")
	private LimitUser limitUser;

	@JsonProperty("CFG_PARM")
    private CfgParam cfgParam;

    @JsonProperty("ROLESMAP")
    private List<RoleMap> rolesMap;

    @JsonProperty("CFG_SYS")
    private CfgSys cfgSys;

    @JsonProperty("USERMAP")
    private UserMap usermap;

    @JsonProperty("BRANCHMAP")
    private BranchMap BRANCHMAP;

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getPassword() {
		return usermap.getPWD();
	}

	@Override
	public String getUsername() {
		return usermap.getUSERID();
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		if (usermap == null)
			return false;
		
		String expiredString = usermap.getExpiryDate();
		if (expiredString == null)
			return true;
		try {
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-DD");
			Date expiredDate = format.parse(expiredString);
			if (expiredDate.compareTo(new Date()) < 0)
				return false;
			else
				return true;
		} catch (ParseException ex) {
			logger.warn("USERMAP - EXPDT cant parsed to date, which is an specification contract violation");
			return true;
		}
	}

	@Override
	public boolean isEnabled() {
		if (usermap == null) return false;
		return StringUtils.equals(usermap.getStatus(), "1");
	}
}
