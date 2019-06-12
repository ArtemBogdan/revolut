package com.revolut.codechallenge.artembogdan.dao;

import java.sql.Connection;

public interface Database {
	
	@FunctionalInterface
	 interface DatabaseOperation<R, E extends Exception> {
		public R exec() throws DatabaseException, E;
	}

	Connection getConnection() throws DatabaseException;
	<R, E extends Exception> R transactionalOperation(DatabaseOperation<R, E> operation) 
			throws DatabaseException, E;
	<R, E extends Exception> R readOperation( DatabaseOperation<R, E> operation ) 
			throws DatabaseException, E;
}
