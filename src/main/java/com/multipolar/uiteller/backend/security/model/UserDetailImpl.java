package com.multipolar.uiteller.backend.security.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnore;


public class UserDetailImpl implements UserDetails {
	
	private static final long serialVersionUID = 2087922601290488432L;
	
	private String username;
	private String branchCode;
	private String branchName;
	//private String trxLimit;
	private String roles;
	private String accountNumber;
	
	private List<AuthLimit> authLimits = new ArrayList<>();
	private List<UserLimit> userLimits = new ArrayList<>();

	@Override
	@JsonIgnore
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	@JsonIgnore
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	@JsonIgnore
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	@JsonIgnore
	public boolean isEnabled() {
		return true;
	}

	@Override
	@JsonIgnore
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return null;
	}

	@Override
	@JsonIgnore
	public String getPassword() {
		return null;
	}

	@Override
	public String getUsername() {
		return username;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}

	public String getBranchCode() {
		return branchCode;
	}

	public void setBranchCode(String branchCode) {
		this.branchCode = branchCode;
	}

	public List<UserLimit> getUserLimits() {
		return userLimits;
	}

	public void setUserLimits(List<UserLimit> userLimits) {
		this.userLimits = userLimits;
	}

	public String getRoles() {
		return roles;
	}

	public void setRoles(String roles) {
		this.roles = roles;
	}

	public List<AuthLimit> getAuthLimits() {
		return authLimits;
	}

	public void setAuthLimits(List<AuthLimit> authLimits) {
		this.authLimits = authLimits;
	}
	
	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}
	
	public String getAccountNumber() {
		return accountNumber;
	}
	
	public String getBranchName() {
		return branchName;
	}
	
	public void setBranchName(String branchName) {
		this.branchName = branchName;
	}
}
