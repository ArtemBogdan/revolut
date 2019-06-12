package com.revolut.codechallenge.artembogdan.service;

public interface AccountService {
	AccountResponse createAccount( String customerName, String user ) throws ServiceException;
	AccountResponse createAccount( CreateAccountRequest request, String user ) throws ServiceException;
	AccountResponse getAccount( String accountNumber, String user ) throws ServiceException;
	AccountResponse modifyAccount( ModifyAccountRequest request, String user ) throws ServiceException;
	AccountResponse getAccountTransactions( String accountNumber, String user ) throws ServiceException;
	TransactionResponse makeTransfer(MakeTransferRequest request, String user) throws ServiceException;
	TransactionResponse deposite(DepositeRequest request, String user) throws ServiceException;
	TransactionResponse withdraw(WithdrawRequest request, String user) throws ServiceException;
}
