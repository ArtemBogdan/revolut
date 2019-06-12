package com.revolut.codechallenge.artembogdan.web.messages;

import com.revolut.codechallenge.artembogdan.service.ServiceException;

public class ErrorMessage {
	
	private final String errorCode;
	private final String errorDescription;
	private final String errorExtendedDescription;
	
	public ErrorMessage( ServiceException ex ) {
		this.errorCode = ex.getErrorCode().getErrorCode();
		this.errorDescription = ex.getMessage();
		this.errorExtendedDescription = ex.getExtendedDescription();
	}

	public String getErrorCode() {
		return errorCode;
	}

	public String getErrorDescription() {
		return errorDescription;
	}

	public String getErrorExtendedDescription() {
		return errorExtendedDescription;
	}
	
}
