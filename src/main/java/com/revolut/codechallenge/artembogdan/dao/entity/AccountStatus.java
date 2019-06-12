package com.revolut.codechallenge.artembogdan.dao.entity;

public enum AccountStatus {
	ACTIVE(1),
	SUSPENDED(2),
	TERMINATED(3);
	
	private final int statusCode;

	private AccountStatus(int statusCode) {
		this.statusCode = statusCode;
	}

	public int getStatusCode() {
		return statusCode;
	}
	
	public static AccountStatus getByCode( int code ) {
		for ( AccountStatus value : AccountStatus.values() ) {
			if ( code == value.statusCode ) {
				return value;
			}
		}
		
		return null;
	}
	
}
