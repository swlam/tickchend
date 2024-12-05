package com.maas.ccass.model;

public class MasterStockInfo {

	private String stockCode;
	private String stockName;
	private long shareholding;
	private double totalShareholdingAmount;
	private double latestDailyShareholdingDeltaAmount;
	private long latestDailyShareholdingDelta;
	private double latestClosePrice;
	private double closePriceAvg50;
	private String txnDate;
	private long marketCap;
	
	public MasterStockInfo() {
		// TODO Auto-generated constructor stub
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

	
	public double getTotalShareholdingAmount() {
		return totalShareholdingAmount;
	}

	public void setTotalShareholdingAmount(double amount) {
		this.totalShareholdingAmount = amount;
	}

	public String getTxnDate() {
		return txnDate;
	}

	public void setTxnDate(String txnDate) {
		this.txnDate = txnDate;
	}

	public String getStockName() {
		return stockName;
	}

	public void setStockName(String stockName) {
		this.stockName = stockName;
	}

	public long getMarketCap() {
		return marketCap;
	}

	public void setMarketCap(long marketCap) {
		this.marketCap = marketCap;
	}

	public double getLatestDailyShareholdingDeltaAmount() {
		return latestDailyShareholdingDeltaAmount;
	}

	public void setLatestDailyShareholdingDeltaAmount(double dailyShareholdingDeltaAmount) {
		this.latestDailyShareholdingDeltaAmount = dailyShareholdingDeltaAmount;
	}

	public long getLatestDailyShareholdingDelta() {
		return latestDailyShareholdingDelta;
	}

	public void setLatestDailyShareholdingDelta(long dailyShareholdingDelta) {
		this.latestDailyShareholdingDelta = dailyShareholdingDelta;
	}

	public double getLatestClosePrice() {
		return latestClosePrice;
	}

	public void setLatestClosePrice(double latestClosePrice) {
		this.latestClosePrice = latestClosePrice;
	}

	public double getClosePriceAvg50() {
		return closePriceAvg50;
	}

	public void setClosePriceAvg50(double closePriceAvg50) {
		this.closePriceAvg50 = closePriceAvg50;
	}
	
	
	
}
