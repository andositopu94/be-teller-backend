package com.multipolar.uiteller.backend.model;

import lombok.Data;

@Data
public class ApplicationUser {
	private String username;
	private String password;
	
	public ApplicationUser(String username, String password) {
		super();
		this.username = username;
		this.password = password;
	}
	
	public ApplicationUser() {}
}
