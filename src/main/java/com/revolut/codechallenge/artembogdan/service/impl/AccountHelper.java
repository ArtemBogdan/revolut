package com.revolut.codechallenge.artembogdan.service.impl;

import com.revolut.codechallenge.artembogdan.dao.DatabaseException;
import com.revolut.codechallenge.artembogdan.dao.DatabaseOperations;
import com.revolut.codechallenge.artembogdan.dao.entity.AccountBean;
import com.revolut.codechallenge.artembogdan.dao.entity.AccountStatus;
import com.revolut.codechallenge.artembogdan.service.ErrorCatalogue;
import com.revolut.codechallenge.artembogdan.service.ServiceException;

public class AccountHelper {
	
	private final DatabaseOperations databaseOperations;

	public AccountHelper(DatabaseOperations databaseOperations) {
		this.databaseOperations = databaseOperations;
	}

	public AccountBean safeFindAccount(String accountNumber, boolean allowNull, String messagePrefix, String user) throws ServiceException {
		if ( accountNumber == null ) {
			throw new ServiceException(ErrorCatalogue.INVALID_INPUT_PARAMETER, 
					messagePrefix + ": must not be null");
		}
		
		Integer accNum;
		
		try {
			accNum = Integer.valueOf(accountNumber);
		} catch ( NumberFormatException nfe ) {
			throw new ServiceException(ErrorCatalogue.INVALID_INPUT_PARAMETER, 
					messagePrefix + ": must be an integer");
		}
		
		AccountBean acc;

		try {
			acc = databaseOperations.findAccount(accNum, user);
		} catch (DatabaseException e) {
			throw new ServiceException(ErrorCatalogue.DATABASE_ERROR, e.getMessage());
		}
		
		if (acc == null && !allowNull) {
			throw new ServiceException(ErrorCatalogue.NOT_FOUND, 
					"Account number [" + accountNumber + "] not found");
		}
		
		return acc;
	}

	public AccountBean safeFindAccount(String accountNumber, boolean allowNull, String user) throws ServiceException {
		return safeFindAccount(accountNumber, allowNull, "Invalid account number", user);
	}
	
	public void checkActiveStatus( AccountBean account ) throws ServiceException {
		if ( account.getStatus() != AccountStatus.ACTIVE ) {
			throw new ServiceException(ErrorCatalogue.ACOUNT_MUST_BE_ACTIVE, 
					"Account [" + account.getAccountNumber() + "] is not active");
		}
	}
	
}
