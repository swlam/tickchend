package com.maas.ccass.model;

public class ParticipantShareholding {

	private int id;
	private String stockCode;
	private String participantId;
	private String txnDate;
	private long shareholding;
	private String percentOfTotalIssuedShares;
	
	private int dailyDelta;
	private int weekNumber;
	private int monthNumber;
	
	private String participantName;// NOT a DB table field
	
	public ParticipantShareholding() {
		// TODO Auto-generated constructor stub
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getStockCode() {
		return stockCode;
	}

	public void setStockCode(String stockCode) {
		this.stockCode = stockCode;
	}

	public String getTxnDate() {
		return txnDate;
	}

	public void setTxnDate(String txnDate) {
		this.txnDate = txnDate;
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

	public int getWeekNumber() {
		return weekNumber;
	}

	public void setWeekNumber(int weekNumber) {
		this.weekNumber = weekNumber;
	}

	public int getMonthNumber() {
		return monthNumber;
	}

	public void setMonthNumber(int monthNumber) {
		this.monthNumber = monthNumber;
	}

	public String getParticipantId() {
		return participantId;
	}

	public void setParticipantId(String participantId) {
		this.participantId = participantId;
	}

	public int getDailyDelta() {
		return dailyDelta;
	}

	public void setDailyDelta(int dailyDelta) {
		this.dailyDelta = dailyDelta;
	}
	
	
	
	public String getParticipantName() {
		return participantName;
	}

	public void setParticipantName(String participantName) {
		this.participantName = participantName;
	}

	public String toString() {
		return stockCode + "\t"+this.participantId + "\t"+this.txnDate + "\t"+this.shareholding+"\t"+this.dailyDelta;
	}
}
