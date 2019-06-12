package com.revolut.codechallenge.artembogdan.service;

import java.util.Date;

public class TransactionResponse {
	private Long transactionNumber;
	private Integer accountFrom;
	private Integer accountTo;
	private Date timmestamp;
	private float amount;
	private String user;

	public Long getTransactionNumber() {
		return transactionNumber;
	}
	public void setTransactionNumber(Long transactionNumber) {
		this.transactionNumber = transactionNumber;
	}
	public Integer getAccountFrom() {
		return accountFrom;
	}
	public void setAccountFrom(Integer accountFrom) {
		this.accountFrom = accountFrom;
	}
	public Integer getAccountTo() {
		return accountTo;
	}
	public void setAccountTo(Integer accountTo) {
		this.accountTo = accountTo;
	}
	public Date getTimmestamp() {
		return timmestamp;
	}
	public void setTimmestamp(Date timmestamp) {
		this.timmestamp = timmestamp;
	}
	public float getAmount() {
		return amount;
	}
	public void setAmount(float amount) {
		this.amount = amount;
	}
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
}
