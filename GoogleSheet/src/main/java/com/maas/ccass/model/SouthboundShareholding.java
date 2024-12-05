package com.maas.ccass.model;

import java.io.Serializable;

import com.maas.util.GeneralHelper;

public class SouthboundShareholding implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 4047308428016859705L;
	private int id;
	private String txnDate;
	private String stockCode;
	private String stockName;
	private long shareholding;
	private String percentOfTotalIssuedShares;
	private long amount;
	
	private long dailyDelta;
	private double dailyDeltaGrowth;
	private long amountDelta;
		

	public SouthboundShareholding(String stockCode, String stockName, String txnDate, long shareholding, String percentOfTotalIssuedShares) {
		this.stockCode = stockCode;
		this.stockName = stockName;
		this.txnDate = txnDate;
		this.shareholding = shareholding;
		this.percentOfTotalIssuedShares = percentOfTotalIssuedShares;
	}
	
	
	public String getStockCode() {
		return stockCode;
	}
	public void setStockCode(String stockCode) {
		this.stockCode = stockCode;
	}
	
	public long getShareholding() {
		return shareholding;
	}
	public void setShareholding(long shareholding) {
		this.shareholding = shareholding;
	}

	public String getPercentOfTotalIssuedShares() {
		return percentOfTotalIssuedShares;
	}

	public void setPercentOfTotalIssuedShares(String percentOfTotalIssuedShares) {
		this.percentOfTotalIssuedShares = percentOfTotalIssuedShares;
	}


	public String getTxnDate() {
		return txnDate;
	}


	public void setTxnDate(String txnDate) {
		this.txnDate = txnDate;
	}


	public long getDailyDelta() {
		return dailyDelta;
	}


	public void setDailyDelta(long dailyDelta) {
		this.dailyDelta = dailyDelta;
	}

	
	public long getAmount() {
		return amount;
	}


	public void setAmount(long amount) {
		this.amount = amount;
	}

	

	public long getAmountDelta() {
		return amountDelta;
	}

	public void setAmountDelta(long amountDelta) {
		this.amountDelta = amountDelta;
	}


	public String getStockName() {
		return stockName;
	}


	public void setStockName(String stockName) {
		this.stockName = stockName;
	}

	

	public double getDailyDeltaGrowth() {
		return dailyDeltaGrowth;
	}


	public void setDailyDeltaGrowth(double dailyDeltaGrowth) {
		this.dailyDeltaGrowth = dailyDeltaGrowth;
	}


	public String toString() {
		return stockCode + "\t"+this.txnDate+"\t"+this.shareholding+"\t"+this.dailyDelta +"\t"+ GeneralHelper.dfPercent.format(this.dailyDeltaGrowth);
	}
	
}
