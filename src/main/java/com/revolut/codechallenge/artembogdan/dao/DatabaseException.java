package com.revolut.codechallenge.artembogdan.dao;

public class DatabaseException extends Exception {
	private static final long serialVersionUID = 9106075209542251435L;

	public DatabaseException() {
		super();
	}

	public DatabaseException(String arg0, Throwable arg1, boolean arg2, boolean arg3) {
		super(arg0, arg1, arg2, arg3);
	}

	public DatabaseException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public DatabaseException(String arg0) {
		super(arg0);
	}

	public DatabaseException(Throwable arg0) {
		super(arg0);
	}

}
