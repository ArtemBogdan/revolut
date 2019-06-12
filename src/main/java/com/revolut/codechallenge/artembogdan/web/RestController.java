package com.revolut.codechallenge.artembogdan.web;

import static com.revolut.codechallenge.artembogdan.web.JsonUtils.safeGsonParse;
import static com.revolut.codechallenge.artembogdan.web.JsonUtils.toJsonString;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
import com.revolut.codechallenge.artembogdan.web.messages.ErrorMessage;

import spark.Request;
import spark.Response;
import spark.Spark;

public class RestController {
	
	private final Logger LOG = LoggerFactory.getLogger(RestController.class);
	private static final String MIME_TYPE_JSON = "application/json";

	private final AccountService accountService;
	private final ThreadLocal<String> user = new ThreadLocal<String>();

	public RestController(AccountService accountService) {
		this.accountService = accountService;
		start();
	}
	
	private void start() {
		Spark.exception(ServiceException.class, (ex, request, response) -> {
			LOG.error("Exception while handling request", ex);
			ServiceException exception = (ServiceException) ex;
			response.status(exception.getErrorCode().getHttpCode());
			response.type(MIME_TYPE_JSON);
			response.body( toJsonString(new ErrorMessage(exception)) );
		} );
		
		Spark.before("/account/*",
				MIME_TYPE_JSON,
				this::authorise);
		Spark.after("/account/*", 
				MIME_TYPE_JSON, 
				(request, response) -> {
					response.type(MIME_TYPE_JSON);
					response.status(200);
				});
		
		Spark.get("/account/:id", 
				MIME_TYPE_JSON,
				this::getAccount,
				JsonUtils::toJsonString);
		Spark.get("/account/:id/transactions/", 
				MIME_TYPE_JSON,
				this::getAccountTransactions,
				JsonUtils::toJsonString);
		Spark.post("/account/", 
				MIME_TYPE_JSON,
				this::createAccount,
				JsonUtils::toJsonString);
		Spark.put("/account/", 
				MIME_TYPE_JSON,
				this::modifyAccount,
				JsonUtils::toJsonString);
		Spark.post("/account/transfer/", 
				MIME_TYPE_JSON,
				this::makeTransfer,
				JsonUtils::toJsonString);
		Spark.post("/account/deposite/", 
				MIME_TYPE_JSON,
				this::deposite,
				JsonUtils::toJsonString);
		Spark.post("/account/withdraw/", 
				MIME_TYPE_JSON,
				this::withdraw,
				JsonUtils::toJsonString);
	}
	
	private void authorise(Request request, Response response) throws ServiceException  {
		String oAuthHeader = request.headers("Authorization");
		String user = null;
		if ( oAuthHeader != null && oAuthHeader.startsWith("Bearer ") ) {
			user = oAuthHeader.substring(7, 26);
		}
		
		if ( user == null ) {
			response.status(403);
			throw new ServiceException(ErrorCatalogue.ACCESS_DENIED);
		} else {
			this.user.set(user);
		}
	}
	
	private AccountResponse getAccount( Request request, Response response ) throws ServiceException {
		AccountResponse acc = accountService.getAccount(request.params(":id"), user.get());
		
		if ( acc == null ) {
			throw new ServiceException(ErrorCatalogue.NOT_FOUND, "Requested account is not found");
		}
		
		return acc;
	}
	
	private AccountResponse getAccountTransactions( Request request, Response response ) throws ServiceException {
		return accountService.getAccountTransactions(request.params(":id"), user.get());
	}
	
	private AccountResponse createAccount( Request request, Response response ) throws ServiceException {
		CreateAccountRequest requestBody = safeGsonParse(request.body(), CreateAccountRequest.class); 
		
		if ( requestBody == null ) {
			throw new ServiceException(ErrorCatalogue.INVALID_INPUT_PARAMETER, "Request must not be empty");
		}

		return accountService.createAccount(requestBody, user.get());
	}

	private AccountResponse modifyAccount( Request request, Response response ) throws ServiceException {
		ModifyAccountRequest requestBody = safeGsonParse(request.body(), ModifyAccountRequest.class);
		
		if ( requestBody == null ) {
			throw new ServiceException(ErrorCatalogue.INVALID_INPUT_PARAMETER, "Request must not be empty");
		}
		
		if ( requestBody.getAccountNumber() == null ) {
			throw new ServiceException(ErrorCatalogue.INVALID_INPUT_PARAMETER, "Account number field must be populated");
		}

		return accountService.modifyAccount(requestBody, user.get());
	}

	private TransactionResponse makeTransfer( Request request, Response response ) throws ServiceException {
		MakeTransferRequest requestBody = safeGsonParse(request.body(), MakeTransferRequest.class);
		
		if ( requestBody == null ) {
			throw new ServiceException(ErrorCatalogue.INVALID_INPUT_PARAMETER, "Request must not be empty");
		}
		
		if ( requestBody.getAmount() == null || requestBody.getAmount() <= 0 ) {
			throw new ServiceException(ErrorCatalogue.INVALID_INPUT_PARAMETER, "Amount must be positive");
		}
		
		if ( requestBody.getAccountFrom() == null || requestBody.getAccountFrom() <= 0 ) {
			throw new ServiceException(ErrorCatalogue.INVALID_INPUT_PARAMETER, "Account from must be positive");
		}
		
		if ( requestBody.getAccountTo() == null || requestBody.getAccountTo() <= 0 ) {
			throw new ServiceException(ErrorCatalogue.INVALID_INPUT_PARAMETER, "Account to must be positive");
		}
		
		if ( requestBody.getAccountFrom().equals(requestBody.getAccountTo()) ) {
			throw new ServiceException(ErrorCatalogue.INVALID_INPUT_PARAMETER, "Account from and to must be different");
		}
		
		return accountService.makeTransfer(requestBody, user.get());
	}

	private TransactionResponse deposite( Request request, Response response ) throws ServiceException {
		DepositeRequest requestBody = safeGsonParse(request.body(), DepositeRequest.class);
		
		if ( requestBody == null ) {
			throw new ServiceException(ErrorCatalogue.INVALID_INPUT_PARAMETER, "Request must not be empty");
		}
		
		if ( requestBody.getAmount() == null || requestBody.getAmount() <= 0 ) {
			throw new ServiceException(ErrorCatalogue.INVALID_INPUT_PARAMETER, "Amount must be positive");
		}
		
		if ( requestBody.getAccountTo() == null || requestBody.getAccountTo() <= 0 ) {
			throw new ServiceException(ErrorCatalogue.INVALID_INPUT_PARAMETER, "Account to must be positive");
		}
		
		return accountService.deposite(requestBody, user.get());
	}

	private TransactionResponse withdraw( Request request, Response response ) throws ServiceException {
		WithdrawRequest requestBody = safeGsonParse(request.body(), WithdrawRequest.class);
		
		if ( requestBody == null ) {
			throw new ServiceException(ErrorCatalogue.INVALID_INPUT_PARAMETER, "Request must not be empty");
		}
		
		if ( requestBody.getAmount() == null || requestBody.getAmount() <= 0 ) {
			throw new ServiceException(ErrorCatalogue.INVALID_INPUT_PARAMETER, "Amount must be positive");
		}
		
		if ( requestBody.getAccountFrom() == null || requestBody.getAccountFrom() <= 0 ) {
			throw new ServiceException(ErrorCatalogue.INVALID_INPUT_PARAMETER, "Account from must be positive");
		}
		
		return accountService.withdraw(requestBody, user.get());
	}
	
}
