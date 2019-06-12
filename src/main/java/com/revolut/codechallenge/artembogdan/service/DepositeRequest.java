package com.revolut.codechallenge.artembogdan.service;

public class DepositeRequest {
	private Integer accountTo;
	private Float amount;

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
