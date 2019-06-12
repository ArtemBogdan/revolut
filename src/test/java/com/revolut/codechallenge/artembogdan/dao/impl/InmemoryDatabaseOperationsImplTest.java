package com.revolut.codechallenge.artembogdan.dao.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.revolut.codechallenge.artembogdan.dao.DatabaseException;
import com.revolut.codechallenge.artembogdan.dao.entity.AccountBean;
import com.revolut.codechallenge.artembogdan.dao.entity.AccountStatus;

public class InmemoryDatabaseOperationsImplTest {

	private InmemoryPostgressDatabase database;
	private InmemoryDatabaseOperationsImpl dbOperations;

	@Before
	public void setUp() throws Exception {
		this.database = new InmemoryPostgressDatabase();
		this.dbOperations = new InmemoryDatabaseOperationsImpl(this.database);
	}

	@After
	public void tearDown() throws Exception {
		this.dbOperations = null;
		this.database = null;
	}

	@Test
	public void test() {
		AccountBean acb;
		try {
			acb = database.<AccountBean, DatabaseException>readOperation( () -> {
				return dbOperations.findAccount(1, "user");
			} );
			assertNull(acb);
			
			acb = database.<AccountBean, DatabaseException>transactionalOperation( () -> {
				return dbOperations.createAccount("TestAccount", "user");
			} );
			assertNotNull(acb);
			assertEquals("TestAccount", acb.getCustomerName());
			assertEquals(AccountStatus.ACTIVE, acb.getStatus());
		} catch (DatabaseException e) {
			fail("Something wrong: " + e.getMessage());
		}
	}

}
