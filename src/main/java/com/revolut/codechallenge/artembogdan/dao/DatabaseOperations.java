package com.revolut.codechallenge.artembogdan.dao;

import java.util.List;

import com.revolut.codechallenge.artembogdan.dao.entity.AccountBean;
import com.revolut.codechallenge.artembogdan.dao.entity.TransactionBean;

public interface DatabaseOperations {
	AccountBean createAccount(String customerName, String user) throws DatabaseException;
	AccountBean findAccount(Integer accountNumber, String user) throws DatabaseException;
	AccountBean lockAccountForUpdate(Integer accountNumber, String user) throws DatabaseException;
	AccountBean modifyAccount(AccountBean account, String user) throws DatabaseException;
	TransactionBean createTransaction(AccountBean from, AccountBean to, 
			float amount, String user) throws DatabaseException;
	TransactionBean getTransaction(Integer transactionId, String user) throws DatabaseException;
	List<TransactionBean> getTransactionsForAccount(AccountBean from, String user) throws DatabaseException;
}
