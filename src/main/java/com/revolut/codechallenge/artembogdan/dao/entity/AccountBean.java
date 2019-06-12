package com.revolut.codechallenge.artembogdan.dao.entity;

import java.util.Date;

public class AccountBean {

	private final Integer accountNumber;
	private String customerName;
	private float amount = 1000;
	private AccountStatus status;
	private Date createWhen;
	private String createdBy;
	
	public AccountBean(Integer accountNumber) {
		this.accountNumber = accountNumber;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public float getAmount() {
		return amount;
	}

	public void setAmount(float amount) {
		this.amount = amount;
	}

	public Integer getAccountNumber() {
		return accountNumber;
	}

	public AccountStatus getStatus() {
		return status;
	}

	public void setStatus(AccountStatus status) {
		this.status = status;
	}

	public Date getCreateWhen() {
		return createWhen;
	}

	public void setCreateWhen(Date createWhen) {
		this.createWhen = createWhen;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}
	
}
