package com.aspire.blog.gateway.service;

public class UsernameAlreadyUsedException extends RuntimeException {

	public UsernameAlreadyUsedException() {
		super("Login name already used!");
	}

}
