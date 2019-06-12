package com.revolut.codechallenge.artembogdan.service;

import java.util.Date;
import java.util.List;

public class AccountResponse {
	private Integer accountNumber;
	private String customerName;
	private float amount;
	private String status;
	private Date createWhen;
	private String createdBy;
	private List<TransactionResponse> transactions;
	
	public Integer getAccountNumber() {
		return accountNumber;
	}
	public void setAccountNumber(Integer accountNumber) {
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
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
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
	public List<TransactionResponse> getTransactions() {
		return transactions;
	}
	public void setTransactions(List<TransactionResponse> transactions) {
		this.transactions = transactions;
	}
}
