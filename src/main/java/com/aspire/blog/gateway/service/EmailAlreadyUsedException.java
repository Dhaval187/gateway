package com.aspire.blog.gateway.service;

public class EmailAlreadyUsedException extends RuntimeException {

	public EmailAlreadyUsedException() {
		super("Email is already in use!");
	}

}
