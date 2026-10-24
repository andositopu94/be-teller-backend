package com.multipolar.uiteller.backend.security.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class RoleMap {
	@JsonProperty("ROLENM")
	private String roleName;

	@JsonProperty("ROLEID")
    private String roleId;

	@JsonProperty("DFLTROLE")
    private String defaultRole;
}
