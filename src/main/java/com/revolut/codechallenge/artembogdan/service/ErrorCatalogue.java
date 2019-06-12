package com.revolut.codechallenge.artembogdan.service;

public enum ErrorCatalogue {
	
	ACCESS_DENIED("001", "Access denied", 403),
	UNKNOWN_ERROR("002", "Unknown backend error", 500),
	DATABASE_ERROR("003", "Database error", 500),
	INVALID_INPUT_PARAMETER("004", "Invalid input parameter", 400),
	NOT_FOUND("005", "Not found", 404),
	LOCK_TIMEOUT("006", "Unable to lock object", 400),
	ACOUNT_MUST_BE_ACTIVE("007", "Account must be active", 400),
	LOW_ACCOUNT_BALANCE("008", "Account has low balance to complete operation", 400);

	
	private final String errorCode;
	private final String errorDescription;
	private final int httpCode;
	
	private ErrorCatalogue(String errorCode, String errorDescription, int httpCode) {
		this.errorCode = errorCode;
		this.errorDescription = errorDescription;
		this.httpCode = httpCode;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public String getErrorDescription() {
		return errorDescription;
	}

	public int getHttpCode() {
		return httpCode;
	}
	
}
