package com.revolut.codechallenge.artembogdan;

import java.io.IOException;
import java.sql.SQLException;

import com.revolut.codechallenge.artembogdan.dao.Database;
import com.revolut.codechallenge.artembogdan.dao.impl.InmemoryDatabaseOperationsImpl;
import com.revolut.codechallenge.artembogdan.dao.impl.InmemoryPostgressDatabase;
import com.revolut.codechallenge.artembogdan.service.AccountService;
import com.revolut.codechallenge.artembogdan.service.ServiceException;
import com.revolut.codechallenge.artembogdan.service.impl.AccountServiceImpl;
import com.revolut.codechallenge.artembogdan.web.RestController;

/**
 * @author bogdan
 *
 */
public final class Application {

	public static void main(String[] args) throws ServiceException, IOException, SQLException {
		System.setProperty(org.slf4j.impl.SimpleLogger.DEFAULT_LOG_LEVEL_KEY, "info");
		System.setProperty(org.slf4j.impl.SimpleLogger.LOG_FILE_KEY, "System.out");
		
		Database database = new InmemoryPostgressDatabase();
		InmemoryDatabaseOperationsImpl databaseOperations = new InmemoryDatabaseOperationsImpl(database);
		
		// Starting services
		AccountService accountService = new AccountServiceImpl(database, databaseOperations);
		
		RestController restController = new RestController(accountService);
	}

}
