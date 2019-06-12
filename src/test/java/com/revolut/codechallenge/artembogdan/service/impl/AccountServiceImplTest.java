package com.revolut.codechallenge.artembogdan.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import javax.transaction.TransactionRequiredException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.revolut.codechallenge.artembogdan.dao.impl.InmemoryDatabaseOperationsImpl;
import com.revolut.codechallenge.artembogdan.dao.impl.InmemoryPostgressDatabase;
import com.revolut.codechallenge.artembogdan.service.AccountResponse;
import com.revolut.codechallenge.artembogdan.service.DepositeRequest;
import com.revolut.codechallenge.artembogdan.service.MakeTransferRequest;
import com.revolut.codechallenge.artembogdan.service.ServiceException;
import com.revolut.codechallenge.artembogdan.service.TransactionResponse;
import com.revolut.codechallenge.artembogdan.service.WithdrawRequest;

public class AccountServiceImplTest {

	private AccountServiceImpl service;
	private final static String USER = "some_user";
	
	@Before
	public void setUp() throws Exception {
		InmemoryPostgressDatabase database = new InmemoryPostgressDatabase();
		InmemoryDatabaseOperationsImpl databaseOperations = new InmemoryDatabaseOperationsImpl(database);
		this.service = new AccountServiceImpl(database, databaseOperations);
	}

	@After
	public void tearDown() throws Exception {
		this.service = null;
	}

	@Test
	public void test() throws ServiceException {
		AccountResponse acc = service.getAccount("1", USER);
		assertNull(acc);
		
		acc = service.createAccount("Some customer", USER);
		assertNotNull(acc);
		assertEquals(Float.valueOf(acc.getAmount()), Float.valueOf(.0f));
		
		try {
			WithdrawRequest wr = new WithdrawRequest();
			wr.setAccountFrom(acc.getAccountNumber());
			wr.setAmount(10f);
			service.withdraw(wr, USER);
			fail();
		} catch (ServiceException se) {
			// low balance
		} 
		
		acc = service.getAccountTransactions(String.valueOf(acc.getAccountNumber()), USER);
		assertEquals(Integer.valueOf(acc.getTransactions().size()), Integer.valueOf(0));
		
		DepositeRequest dr = new DepositeRequest();
		dr.setAccountTo(acc.getAccountNumber());
		dr.setAmount(101.20f);
		service.deposite(dr, USER);
		service.deposite(dr, USER);
		acc = service.getAccountTransactions(String.valueOf(acc.getAccountNumber()), USER);
		assertEquals(Float.valueOf(acc.getAmount()), Float.valueOf(202.4f));
		assertNotNull(acc.getTransactions());
		assertEquals(Integer.valueOf(acc.getTransactions().size()), Integer.valueOf(2));
		
		AccountResponse acc2 = service.createAccount("Some customer 2", USER);
		assertNotNull(acc2);
		
		MakeTransferRequest mtr = new MakeTransferRequest();
		mtr.setAccountFrom(acc.getAccountNumber());
		mtr.setAccountTo(acc2.getAccountNumber());
		mtr.setAmount(200f);
		service.makeTransfer(mtr, USER);
		acc = service.getAccount(acc.getAccountNumber().toString(), USER);
		acc2 = service.getAccount(acc2.getAccountNumber().toString(), USER);
		assertEquals(Float.valueOf(acc.getAmount()), Float.valueOf(2.4f));
		assertEquals(Float.valueOf(acc2.getAmount()), Float.valueOf(200f));
		
		try {
			service.makeTransfer(mtr, USER);
			fail();
		} catch (ServiceException e) {
			// Low balance
		}
	}

}
