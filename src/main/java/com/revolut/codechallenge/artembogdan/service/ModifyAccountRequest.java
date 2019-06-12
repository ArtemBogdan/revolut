package com.revolut.codechallenge.artembogdan.service;

import com.revolut.codechallenge.artembogdan.dao.entity.AccountStatus;

public class ModifyAccountRequest {
	private String accountNumber;
	private String customerName;
	private AccountStatus status;
	
	public String getAccountNumber() {
		return accountNumber;
	}
	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}
	public String getCustomerName() {
		return customerName;
	}
	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}
	public AccountStatus getStatus() {
		return status;
	}
	public void setStatus(AccountStatus status) {
		this.status = status;
	}
}
