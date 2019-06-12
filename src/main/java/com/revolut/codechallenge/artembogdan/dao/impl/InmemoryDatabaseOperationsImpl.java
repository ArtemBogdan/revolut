package com.revolut.codechallenge.artembogdan.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.revolut.codechallenge.artembogdan.dao.Database;
import com.revolut.codechallenge.artembogdan.dao.DatabaseException;
import com.revolut.codechallenge.artembogdan.dao.DatabaseOperations;
import com.revolut.codechallenge.artembogdan.dao.entity.AccountBean;
import com.revolut.codechallenge.artembogdan.dao.entity.AccountStatus;
import com.revolut.codechallenge.artembogdan.dao.entity.TransactionBean;

public class InmemoryDatabaseOperationsImpl implements DatabaseOperations {

	private final Database database;
	private final Logger LOG = LoggerFactory.getLogger(InmemoryDatabaseOperationsImpl.class);
	
	public InmemoryDatabaseOperationsImpl(Database database) {
		this.database = database;
	}
	
	@Override
	public AccountBean createAccount(String customerName, String user) throws DatabaseException {
		try {
			Connection conn = database.getConnection();
			PreparedStatement ps = conn.prepareStatement(
					"INSERT INTO account(customer_name, user_name) VALUES(?, ?)", 
					Statement.RETURN_GENERATED_KEYS);
			ps.setString(1, customerName);
			ps.setString(2, user);
			ps.execute();
			ResultSet rs = ps.getGeneratedKeys();
			if ( !rs.next() ) {
				throw new DatabaseException("Unable to create account");
			}
			Integer accountNumber = rs.getInt(1);
			return findAccount(accountNumber, user);
		} catch ( SQLException ex ) {
			LOG.error("Error while creating account", ex);
			throw new DatabaseException("Unable to create account", ex);
		}
	}

	@Override
	public AccountBean findAccount(Integer accountNumber, String user) throws DatabaseException {
		try {
			Connection conn = database.getConnection();
			PreparedStatement ps = conn.prepareStatement(
					"SELECT * "+
					"FROM account "+
					"WHERE account_id=?", 
					Statement.RETURN_GENERATED_KEYS);
			ps.setInt(1, accountNumber);
			ResultSet rs = ps.executeQuery();
			if ( !rs.next() ) {
				return null;
			}
			return getAccountBean(rs);
		} catch (SQLException ex) {
			LOG.error("Unable to find account", ex);
			throw new DatabaseException("Unable to read account", ex);
		}
	}

	@Override
	public AccountBean lockAccountForUpdate(Integer accountNumber, String user) throws DatabaseException {
		try {
			Connection conn = database.getConnection();
			PreparedStatement ps = conn.prepareStatement(
					"SELECT * "+
					"FROM account "+
					"WHERE account_id=? FOR UPDATE", 
					Statement.RETURN_GENERATED_KEYS);
			ps.setInt(1, accountNumber);
			ResultSet rs = ps.executeQuery();
			if ( !rs.next() ) {
				throw new DatabaseException("Account " + accountNumber + " is missing");
			}
			return getAccountBean(rs);
		} catch (SQLException ex) {
			LOG.error("Unable to lock account", ex);
			throw new DatabaseException("Unable to read account", ex);
		}
	}

	@Override
	public AccountBean modifyAccount(AccountBean account, String user) throws DatabaseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TransactionBean getTransaction(Integer transactionId, String user) throws DatabaseException {
		try {
			Connection conn = database.getConnection();

			PreparedStatement ps = conn.prepareStatement("SELECT * FROM transaction WHERE transaction_id=?");
			ps.setInt(1, transactionId);
			ResultSet rs = ps.executeQuery();
			if ( !rs.next() ) {
				return null;
			}
			return getTransactionBean(rs);
		} catch (SQLException e) {
			LOG.error("Unable to read transaction", e);
			throw new DatabaseException("Error reading transaction: " + e.getMessage());
		}
	}

	@Override
	public List<TransactionBean> getTransactionsForAccount(AccountBean from, String user) throws DatabaseException {
		Connection conn = database.getConnection();

		try {
			PreparedStatement ps = conn.prepareStatement("SELECT * FROM transaction "+
					"WHERE account_from=? OR account_to=?");
			ps.setInt(1, from.getAccountNumber());
			ps.setInt(2, from.getAccountNumber());
			ResultSet rs = ps.executeQuery();
			
			List<TransactionBean> response = new ArrayList<>(); 
			while ( rs.next() ) {
				response.add( getTransactionBean(rs) );
			}
			return response;
		} catch (SQLException e) {
			LOG.error("Unable to read account transactions", e);
			throw new DatabaseException("Error reading transaction: " + e.getMessage());
		}
	}

	@Override
	public TransactionBean createTransaction(AccountBean from, AccountBean to, 
			float amount, String user) throws DatabaseException {
		try {
			Connection conn = database.getConnection();

			PreparedStatement ps;
			if ( from != null ) {
				ps = conn.prepareStatement("UPDATE account SET amount=amount-? WHERE account_id=?");
				ps.setFloat(1, amount);
				ps.setInt(2, from.getAccountNumber());
				ps.execute();
			}
		
			if ( to != null ) {
				ps = conn.prepareStatement("UPDATE account SET amount=amount+? WHERE account_id=?");
				ps.setFloat(1, amount);
				ps.setInt(2, to.getAccountNumber());
				ps.execute();
			}
			
			ps = conn.prepareStatement("INSERT INTO transaction(account_from, account_to, amount, user_name) "+
					"VALUES(?, ?, ?, ?)",
					Statement.RETURN_GENERATED_KEYS);
			ps.setObject(1, from == null ? null : from.getAccountNumber());
			ps.setObject(2, to == null ? null : to.getAccountNumber());
			ps.setFloat(3, amount);
			ps.setString(4, user);
			ps.execute();
			ResultSet rs = ps.getGeneratedKeys();
			if ( !rs.next() ) {
				throw new DatabaseException("Failed to create transaction");
			}
			Integer transactionId = rs.getInt(1);
			return getTransaction(transactionId, user);
		} catch (SQLException e) {
			LOG.error("Unable to make money transfer", e);
			throw new DatabaseException("Error while creating transaction: " + e.getMessage());
		}
	}
	
	private AccountBean getAccountBean(ResultSet rs) throws SQLException {
		AccountBean result = new AccountBean(rs.getInt("account_id"));
		result.setCustomerName(rs.getString("customer_name"));
		result.setAmount(rs.getFloat("amount"));
		result.setStatus( AccountStatus.getByCode(rs.getInt("status_code")) );
		result.setCreateWhen( rs.getDate("created_when") );
		result.setCreatedBy( rs.getString("user_name") );
		
		return result;
	}
	
	private TransactionBean getTransactionBean(ResultSet rs) throws SQLException {
		TransactionBean result = new TransactionBean(rs.getLong("transaction_id"));
		result.setFrom(rs.getObject("account_from", Integer.class));
		result.setTo(rs.getObject("account_to", Integer.class));
		result.setAmount(rs.getFloat("amount"));
		result.setTimestamp(rs.getDate("timestamp"));
		result.setUser(rs.getString("user_name"));
		
		return result;
	}

}
