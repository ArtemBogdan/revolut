package com.revolut.codechallenge.artembogdan.dao.impl;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opentable.db.postgres.embedded.EmbeddedPostgres;
import com.revolut.codechallenge.artembogdan.dao.Database;
import com.revolut.codechallenge.artembogdan.dao.DatabaseException;

public class InmemoryPostgressDatabase implements Database {

	private final Logger LOG = LoggerFactory.getLogger(InmemoryPostgressDatabase.class);

	private final EmbeddedPostgres pg;
	private final DataSource database;
	private ThreadLocal<Connection> connection = new ThreadLocal<Connection>();
	
	public InmemoryPostgressDatabase() throws IOException, SQLException {
		this.pg = EmbeddedPostgres.start();
		this.database = pg.getPostgresDatabase();
		createTables();
	}
	
	private void createTables() throws SQLException {
		try (Connection conn = database.getConnection()) {
			Statement statement = conn.createStatement();
			statement.execute("CREATE TABLE account( " + 
	        		"   account_id serial PRIMARY KEY, " + 
	        		"   customer_name VARCHAR(50), " + 
	        		"   amount NUMERIC(10, 2) NOT NULL DEFAULT 0, " + 
	        		"   status_code integer NOT NULL DEFAULT 1, " + 
	        		"   created_when TIMESTAMP not null default CURRENT_DATE, " + 
	        		"   user_name VARCHAR(20)" + 
	        		")");

			statement.execute("CREATE TABLE transaction( " + 
	        		"   transaction_id serial PRIMARY KEY, " + 
	        		"   account_from integer REFERENCES account, " + 
	        		"   account_to integer REFERENCES account, " + 
	        		"   amount NUMERIC(10, 2) NOT NULL, " + 
	        		"   timestamp TIMESTAMP not null default CURRENT_DATE, " + 
	        		"   user_name VARCHAR(20)" + 
	        		")");
		} 
	}
	
	public void close() throws IOException {
		pg.close();
	}

	@Override
	public Connection getConnection() throws DatabaseException {
		Connection conn = connection.get();
		if ( conn == null ) {
			throw new DatabaseException("Connection is missing. Operation needs to be wrapped in transaction");
		}
		return conn;
	}
	
	private void setupConnection(Connection conn) throws SQLException {
		conn.setAutoCommit(false);
		conn.createStatement().execute("SET LOCAL lock_timeout = '4s'");
	}
	
	@Override
	public <R, E extends Exception> R readOperation( DatabaseOperation<R, E> operation ) 
			throws DatabaseException, E {
		R result;
		try ( Connection conn = database.getConnection() ) {
			setupConnection(conn);
			connection.set(conn);
			result = operation.exec();
		} catch (SQLException e2) {
			throw new DatabaseException(e2);
		} finally {
			connection.set(null);
		}
		return result;
	}

	@Override
	public <R, E extends Exception> R transactionalOperation(DatabaseOperation<R, E> operation) 
			throws DatabaseException, E {
		R result;
		try ( Connection conn = database.getConnection() ) {
			setupConnection(conn);
			connection.set(conn);
			try {
				result = operation.exec();
				conn.commit();
			} catch (Exception e) {
				try {
					conn.rollback();
				} catch (SQLException e1) {
					throw new DatabaseException(e1);
				}
				throw new DatabaseException(e);
			}
		} catch (SQLException e2) {
			throw new DatabaseException(e2);
		} finally {
			connection.set(null);
		}
		return result;
	}

}
