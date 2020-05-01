package com.aspire.blog.gateway.service;

public class InvalidPasswordException extends RuntimeException {

	public InvalidPasswordException() {
		super("Incorrect password");
	}

}
