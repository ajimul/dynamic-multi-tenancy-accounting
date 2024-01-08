package com.accounting.rest.multitenant.dto;

import java.io.Serializable;

/**
 * @author Md. Amran Hossain
 */
public class AuthResponse implements Serializable {

//    private final String userName;
    private final String jwt;


//    public AuthResponse(String userName, String jwt) {
//    	
//    	this.userName = userName;
//    	this.jwt = jwt;
//    }
    public AuthResponse(String jwt) {
		this.jwt = jwt;
	}



	public String getJwt() {
		return jwt;
	}

	
}
