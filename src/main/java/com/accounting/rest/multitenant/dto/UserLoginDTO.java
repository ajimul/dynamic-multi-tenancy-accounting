package com.accounting.rest.multitenant.dto;

import java.io.Serializable;

public class UserLoginDTO implements Serializable {

	private String username;
	private String password;
	private Integer tenantOrClientId;

	public UserLoginDTO() {
	}

	public UserLoginDTO(String username, String password, Integer tenantOrClientId) {
		this.username = username;
		this.password = password;
		this.tenantOrClientId = tenantOrClientId;
	}

	public String getusername() {
		return username;
	}

	public UserLoginDTO setusername(String username) {
		this.username = username;
		return this;
	}

	public String getPassword() {
		return password;
	}

	public UserLoginDTO setPassword(String password) {
		this.password = password;
		return this;
	}

	public Integer getTenantOrClientId() {
		return tenantOrClientId;
	}

	public UserLoginDTO setTenantOrClientId(Integer tenantOrClientId) {
		this.tenantOrClientId = tenantOrClientId;
		return this;
	}
}
