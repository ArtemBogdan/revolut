package com.revolut.codechallenge.artembogdan.service;

public class ServiceException extends Exception {
	private static final long serialVersionUID = -8193311964889184596L;
	
	private final ErrorCatalogue errorCode;
	private final String extendedDescription;
	
	public ServiceException( ErrorCatalogue errorCode ) {
		super(errorCode.getErrorDescription());
		this.errorCode = errorCode;
		this.extendedDescription = null;
	}
	
	public ServiceException( ErrorCatalogue errorCode, Throwable ex ) {
		super(errorCode.getErrorDescription(), ex);
		this.errorCode = errorCode;
		this.extendedDescription = null;
	}
	
	public ServiceException( ErrorCatalogue errorCode, String description, Throwable ex ) {
		super(errorCode.getErrorDescription(), ex);
		this.errorCode = errorCode;
		this.extendedDescription = description;
	}
	
	public ServiceException( ErrorCatalogue errorCode, String description) {
		super(errorCode.getErrorDescription());
		this.errorCode = errorCode;
		this.extendedDescription = description;
	}

	public ErrorCatalogue getErrorCode() {
		return errorCode;
	}

	public String getExtendedDescription() {
		return extendedDescription;
	}
	
}
