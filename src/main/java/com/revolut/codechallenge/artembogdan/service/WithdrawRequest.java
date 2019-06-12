package com.revolut.codechallenge.artembogdan.service;

public class WithdrawRequest {
	private Integer accountFrom;
	private Float amount;

	public Integer getAccountFrom() {
		return accountFrom;
	}
	public void setAccountFrom(Integer accountFrom) {
		this.accountFrom = accountFrom;
	}
	public Float getAmount() {
		return amount;
	}
	public void setAmount(Float amount) {
		this.amount = amount;
	}
}
