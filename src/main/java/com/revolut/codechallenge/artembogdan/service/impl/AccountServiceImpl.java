package com.revolut.codechallenge.artembogdan.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.revolut.codechallenge.artembogdan.dao.Database;
import com.revolut.codechallenge.artembogdan.dao.DatabaseException;
import com.revolut.codechallenge.artembogdan.dao.DatabaseOperations;
import com.revolut.codechallenge.artembogdan.dao.entity.AccountBean;
import com.revolut.codechallenge.artembogdan.dao.entity.TransactionBean;
import com.revolut.codechallenge.artembogdan.service.AccountResponse;
import com.revolut.codechallenge.artembogdan.service.AccountService;
import com.revolut.codechallenge.artembogdan.service.CreateAccountRequest;
import com.revolut.codechallenge.artembogdan.service.DepositeRequest;
import com.revolut.codechallenge.artembogdan.service.ErrorCatalogue;
import com.revolut.codechallenge.artembogdan.service.MakeTransferRequest;
import com.revolut.codechallenge.artembogdan.service.ModifyAccountRequest;
import com.revolut.codechallenge.artembogdan.service.ServiceException;
import com.revolut.codechallenge.artembogdan.service.TransactionResponse;
import com.revolut.codechallenge.artembogdan.service.WithdrawRequest;

public class AccountServiceImpl implements AccountService {

	private final Logger LOG = LoggerFactory.getLogger(AccountServiceImpl.class);
	
	private final Database database;
	private final DatabaseOperations databaseOperations;
	private AccountHelper accountHelper;
	
	public AccountServiceImpl( Database database, 
			DatabaseOperations databaseOperations) {
		this.database = database;
		this.databaseOperations = databaseOperations;
		this.accountHelper = new AccountHelper(databaseOperations);
	}
	
	private AccountResponse transform( AccountBean account ) {
		if ( account == null ) {
			return null;
		}
		
		AccountResponse response = new AccountResponse();
		response.setAccountNumber(account.getAccountNumber());
		response.setAmount(account.getAmount());
		response.setCustomerName(account.getCustomerName());
		response.setStatus(String.valueOf(account.getStatus()));
		response.setCreateWhen(account.getCreateWhen());
		response.setCreatedBy(account.getCreatedBy());
		
		return response;
	}

	@Override
	public AccountResponse createAccount(String customerName, String user) throws ServiceException {
		try {
			return database.transactionalOperation(()->{
				AccountBean bean = databaseOperations.createAccount(customerName, user);
				return transform(bean);
			});
		} catch (DatabaseException e) {
			LOG.error("Error while creating account", e);
			throw new ServiceException(ErrorCatalogue.DATABASE_ERROR, e.getMessage());
		}
	}
	
	@Override
	public AccountResponse createAccount( CreateAccountRequest request, String user ) throws ServiceException {
		return createAccount(request.getCustomerName(), user);
	}
	
	@Override
	public AccountResponse getAccount( String accountNumber, String user ) throws ServiceException {
		try {
			return database.<AccountResponse, ServiceException>readOperation(() -> {
				AccountBean account = accountHelper.safeFindAccount(accountNumber, true, user);
				if ( account == null ) {
					return null;
				}
				return transform(account);
			});
		} catch (DatabaseException e) {
			LOG.error("Error while reading account", e);
			throw new ServiceException(ErrorCatalogue.DATABASE_ERROR, e.getMessage());
		}
	}

	@Override
	public AccountResponse getAccountTransactions(String accountNumber, String user) throws ServiceException {
		try {
			return database.<AccountResponse, ServiceException>readOperation(() -> {
				AccountBean accountBean = accountHelper.safeFindAccount(accountNumber, false, user);
				List<TransactionBean> transactionBeans = 
						databaseOperations.getTransactionsForAccount(accountBean, user);
				AccountResponse response = transform(accountBean);
				List<TransactionResponse> transactions = new ArrayList<>(transactionBeans.size());
				for ( TransactionBean tb : transactionBeans ) {
					transactions.add( transformTransaction(tb) );
				}
				response.setTransactions(transactions);
				return response;
			});
		} catch (DatabaseException e) {
			LOG.error("Error while getting account transactions", e);
			throw new ServiceException(ErrorCatalogue.DATABASE_ERROR, e.getMessage());
		}
	}

	@Override
	public AccountResponse modifyAccount(ModifyAccountRequest request, String user) throws ServiceException {
		AccountBean acc = accountHelper.safeFindAccount(request.getAccountNumber(), false, user);

		if ( request.getCustomerName() != null ) {
			acc.setCustomerName( request.getCustomerName() );
		}
		if ( request.getStatus() != null ) {
			acc.setStatus( request.getStatus() );
		}
		
		try {
			acc = databaseOperations.modifyAccount(acc, user);
		} catch (DatabaseException e) {
			throw new ServiceException(ErrorCatalogue.DATABASE_ERROR, e.getMessage());
		}
		return transform(acc);
	}

	@Override
	public TransactionResponse makeTransfer(MakeTransferRequest request, String user) throws ServiceException {
		try {
			return database.<TransactionResponse, ServiceException>transactionalOperation(()->{
				AccountBean accountFrom = databaseOperations.lockAccountForUpdate(request.getAccountFrom(), user); 
				if ( accountFrom.getAmount() < request.getAmount() ) {
					throw new ServiceException(ErrorCatalogue.LOW_ACCOUNT_BALANCE);
				}
				AccountBean accountTo = databaseOperations.lockAccountForUpdate(request.getAccountTo(), user); 
				TransactionBean transactionBean = databaseOperations.createTransaction(accountFrom, accountTo, request.getAmount(), user);
				return transformTransaction(transactionBean);
			});
		} catch (DatabaseException e) {
			LOG.error("Error while making transfer", e);
			throw new ServiceException(ErrorCatalogue.DATABASE_ERROR, e.getMessage());
		}
	}

	@Override
	public TransactionResponse deposite(DepositeRequest request, String user) throws ServiceException {
		try {
			return database.<TransactionResponse, ServiceException>transactionalOperation(()->{
				AccountBean accountTo = databaseOperations.lockAccountForUpdate(request.getAccountTo(), user); 
				TransactionBean transactionBean = databaseOperations.createTransaction(null, accountTo, request.getAmount(), user);
				return transformTransaction(transactionBean);
			});
		} catch (DatabaseException e) {
			LOG.error("Error while making deposite", e);
			throw new ServiceException(ErrorCatalogue.DATABASE_ERROR, e.getMessage());
		}
	}

	@Override
	public TransactionResponse withdraw(WithdrawRequest request, String user) throws ServiceException {
		try {
			return database.<TransactionResponse, ServiceException>transactionalOperation(()->{
				AccountBean accountFrom = databaseOperations.lockAccountForUpdate(request.getAccountFrom(), user); 
				if ( accountFrom.getAmount() < request.getAmount() ) {
					throw new ServiceException(ErrorCatalogue.LOW_ACCOUNT_BALANCE);
				}
				TransactionBean transactionBean = databaseOperations.createTransaction(accountFrom, null, request.getAmount(), user);
				return transformTransaction(transactionBean);
			});
		} catch (DatabaseException e) {
			LOG.error("Error while making withdrawal", e);
			throw new ServiceException(ErrorCatalogue.DATABASE_ERROR, e.getMessage());
		}
	}
	
	static TransactionResponse transformTransaction(TransactionBean bean) {
		if ( bean == null ) {
			return null;
		}
		
		TransactionResponse response = new TransactionResponse();
		response.setAccountFrom(bean.getFrom());
		response.setAccountTo(bean.getTo());
		response.setAmount(bean.getAmount());
		response.setTimmestamp(bean.getTimestamp());
		response.setTransactionNumber(bean.getTransactionNumber());
		response.setUser(bean.getUser());
		
		return response;
	}
}
