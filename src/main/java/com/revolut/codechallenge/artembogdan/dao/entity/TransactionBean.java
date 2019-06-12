package com.revolut.codechallenge.artembogdan.dao.entity;

import java.util.Date;

public class TransactionBean {
	private final Long transactionNumber;
	private Date timestamp;
	private Integer from;
	private Integer to;
	private float amount;
	private String user;
	
	public TransactionBean(Long transactionNumber) {
		this.transactionNumber = transactionNumber;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	public Integer getFrom() {
		return from;
	}

	public void setFrom(Integer from) {
		this.from = from;
	}

	public Integer getTo() {
		return to;
	}

	public void setTo(Integer to) {
		this.to = to;
	}

	public float getAmount() {
		return amount;
	}

	public void setAmount(float amount) {
		this.amount = amount;
	}

	public Long getTransactionNumber() {
		return transactionNumber;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}
}
