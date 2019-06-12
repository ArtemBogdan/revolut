package com.revolut.codechallenge.artembogdan.service;

public class MakeTransferRequest {
	private Integer accountFrom;
	private Integer accountTo;
	private Float amount;
	
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
	public Float getAmount() {
		return amount;
	}
	public void setAmount(Float amount) {
		this.amount = amount;
	}
}
