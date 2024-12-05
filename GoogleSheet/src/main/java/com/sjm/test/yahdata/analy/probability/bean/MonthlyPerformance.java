package com.sjm.test.yahdata.analy.probability.bean;

public class MonthlyPerformance {

	private String stockCode;
	private String direction ; //up/down/steady
	private double percentageClose;
	private double percentageLow;
	private double percentageHigh;
	
	private String yyyy;
	private String mm;
	private String highestDate;
	private String lowestDate;
	private double diffHighLow;
	
	public MonthlyPerformance() {
	}

	

	public String getDirection() {
		return direction;
	}

	public String getYyyy() {
		return yyyy;
	}

	public void setYyyy(String yyyy) {
		this.yyyy = yyyy;
	}

	public String getMm() {
		return mm;
	}

	public void setMm(String mm) {
		this.mm = mm;
	}

	public void setDirection(String direction) {
		this.direction = direction;
	}

	public double getPercentageClose() {
		return percentageClose;
	}

	public void setPercentageClose(double percentage) {
		this.percentageClose = percentage;
	}
	
	public double getPercentageLow() {
		return percentageLow;
	}

	public void setPercentageLow(double percentageLow) {
		this.percentageLow = percentageLow;
	}

	public double getPercentageHigh() {
		return percentageHigh;
	}

	public void setPercentageHigh(double percentageHigh) {
		this.percentageHigh = percentageHigh;
	}
	

	public String getHighestDate() {
		return highestDate;
	}

	public void setHighestDate(String highestDate) {
		this.highestDate = highestDate;
	}

	public String getLowestDate() {
		return lowestDate;
	}

	public void setLowestDate(String lowestDate) {
		this.lowestDate = lowestDate;
	}

	public String getStockCode() {
		return stockCode;
	}

	public void setStockCode(String stockCode) {
		this.stockCode = stockCode;
	}

	public double getDiffHighLow() {
		return diffHighLow;
	}



	public void setDiffHighLow(double diffHighLow) {
		this.diffHighLow = diffHighLow;
	}



	public String toString() {
		return this.getYyyy()+"-"+this.getMm() +" "+ this.getDirection()+"\t"+this.getPercentageClose()*100+"%";
	}
	
}
