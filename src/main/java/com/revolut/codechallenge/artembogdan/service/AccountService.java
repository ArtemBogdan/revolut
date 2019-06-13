package com.revolut.codechallenge.artembogdan.service;

/**
 * The service layer interface defining all operations for bank accounts.
 * 
 * @author bogdan
 *
 */
public interface AccountService {
	/**
	 * Creates bank account. In case of error operation throws exception.
	 * @param customerName - Name of the account's owner
	 * @param user - Name of a system user calling the operation
	 * @return {@link AccountResponse} with created account in case of success. Never returns null
	 * @throws ServiceException
	 */
	AccountResponse createAccount( String customerName, String user ) throws ServiceException;
	
	/**
	 * Overloaded version of {@link #createAccount(CreateAccountRequest, String)}. 
	 * Creates bank account. In case of error operation throws exception.
	 * @param request - {@link CreateAccountRequest}
	 * @param user - Name of a system user calling the operation
	 * @return {@link AccountResponse} with created account in case of success. Never returns null
	 * @throws ServiceException
	 */
	AccountResponse createAccount( CreateAccountRequest request, String user ) throws ServiceException;
	
	/**
	 * Finds and returns account for a given <i>accountNumber</i>. 
	 * @param accountNumber number of bank account created by {@linkplain #createAccount(CreateAccountRequest, String)} operation.
	 * @param user - Name of a system user calling the operation
	 * @return {@link AccountResponse} or null if account does not exist
	 * @throws ServiceException
	 */
	AccountResponse getAccount( String accountNumber, String user ) throws ServiceException;
	
	AccountResponse modifyAccount( ModifyAccountRequest request, String user ) throws ServiceException;
	
	/**
	 * Returns financial transactions associated with the account.  
	 * @param accountNumber number of bank account created by {@linkplain #createAccount(CreateAccountRequest, String)} operation.
	 * @param user - Name of a system user calling the operation
	 * @return If account is found returns {@link AccountResponse} with populated {@link AccountResponse#getTransactions()} section. 
	 * If account is missing throws an exception
	 * @throws ServiceException
	 */
	AccountResponse getAccountTransactions( String accountNumber, String user ) throws ServiceException;
	
	/**
	 * Transfer money between two accounts.
	 * Operation checks if both accounts exist and also validates if accountFrom has enough balance
	 * @param request {@link MakeTransferRequest}
	 * @param user - Name of a system user calling the operation
	 * @return {@link TransactionResponse}. Never returns null.
	 * @throws ServiceException
	 */
	TransactionResponse makeTransfer(MakeTransferRequest request, String user) throws ServiceException;
	
	/** 
	 * Deposits money to the account
	 * @param request {@link DepositeRequest}
	 * @param user - Name of a system user calling the operation
	 * @return {@link TransactionResponse}. Never returns null.
	 * @throws ServiceException
	 */
	TransactionResponse deposite(DepositeRequest request, String user) throws ServiceException;
	
	/**
	 * Withdraws money from the account. Operation checks if there is enough funds before withdrawal.  
	 * @param request {@link WithdrawRequest}
	 * @param user - Name of a system user calling the operation
	 * @return {@link TransactionResponse}. Never returns null.
	 * @throws ServiceException
	 */
	TransactionResponse withdraw(WithdrawRequest request, String user) throws ServiceException;
}
