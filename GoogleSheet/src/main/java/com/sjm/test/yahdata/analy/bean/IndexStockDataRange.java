package com.sjm.test.yahdata.analy.bean;

import com.maas.util.GeneralHelper;

public class IndexStockDataRange {

	private String year;

	private String startDate;
	private String endDate;

	private double stockPriceRaiseRatio; // e.g. 0.5 => 50%
	
	
	
	private int numOfTxDays;

	private double startingCalcPrice;
	private double endingCalcPrice;
	
	
	private double periodHightPrice;

	private double periodHightRatio;
	
	private double periodLowPrice;

	private double periodLowRatio;
	
	private String indexDatePeriod;

	public IndexStockDataRange(String year) {
		this.year = year;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public double getStockPriceRaiseRatio() {
		return stockPriceRaiseRatio;
	}

	public void setStockPriceRaiseRatio(double stockPriceRaiseRatio) {
		this.stockPriceRaiseRatio = stockPriceRaiseRatio;
	}


	public int getNumOfTxDays() {
		return numOfTxDays;
	}

	public void setNumOfTxDays(int numOfTxDays) {
		this.numOfTxDays = numOfTxDays;
	}

	public String getIndexDatePeriod() {
		return indexDatePeriod;
	}

	public void setIndexDatePeriod(String indexDatePeriod) {
		this.indexDatePeriod = indexDatePeriod;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public double getStartingCalcPrice() {
		return startingCalcPrice;
	}

	public void setStartingCalcPrice(double startingCalcPrice) {
		this.startingCalcPrice = startingCalcPrice;
	}

	public double getEndingCalcPrice() {
		return endingCalcPrice;
	}

	public void setEndingCalcPrice(double endingCalcPrice) {
		this.endingCalcPrice = endingCalcPrice;
	}

	public double getPeriodHightPrice() {
		return periodHightPrice;
	}

	public void setPeriodHightPrice(double periodHightPrice) {
		this.periodHightPrice = periodHightPrice;
	}

	public double getPeriodHightRatio() {
		return periodHightRatio;
	}

	public void setPeriodHightRatio(double periodHightRatio) {
		this.periodHightRatio = periodHightRatio;
	}

	public double getPeriodLowRatio() {
		return periodLowRatio;
	}

	public void setPeriodLowRatio(double periodLowRatio) {
		this.periodLowRatio = periodLowRatio;
	}

	public double getPeriodLowPrice() {
		return periodLowPrice;
	}

	public void setPeriodLowPrice(double periodLowPrice) {
		this.periodLowPrice = periodLowPrice;
	}

	

	public void buildIndexDatePeriod() {

		String index = this.getStartDate().substring(5, this.getStartDate().length()) + "_" + this.getEndDate().substring(5, this.getEndDate().length());
		setIndexDatePeriod(index);
	}

	
	
//	public double getStockPriceFallRatio() {
//		return stockPriceFallRatio;
//	}
//
//	public void setStockPriceFallRatio(double stockPriceFallRatio) {
//		this.stockPriceFallRatio = stockPriceFallRatio;
//	}
//	public double getStartingCalcFallPrice() {
//		return startingCalcFallPrice;
//	}
//
//	public void setStartingCalcFallPrice(double startingCalcFallPrice) {
//		this.startingCalcFallPrice = startingCalcFallPrice;
//	}
//
//	public double getEndingCalcFallPrice() {
//		return endingCalcFallPrice;
//	}
//
//	public void setEndingCalcFallPrice(double endingCalcFallPrice) {
//		this.endingCalcFallPrice = endingCalcFallPrice;
//	}
	
	

//	public double getFallPeriodLowRatio() {
//		return fallPeriodLowRatio;
//	}
//
//	public void setFallPeriodLowRatio(double fallPeriodLowRatio) {
//		this.fallPeriodLowRatio = fallPeriodLowRatio;
//	}
//
//	public double getFallPeriodHightRatio() {
//		return fallPeriodHightRatio;
//	}
//
//	public void setFallPeriodHightRatio(double fallPeriodHightRatio) {
//		this.fallPeriodHightRatio = fallPeriodHightRatio;
//	}

	public String toString() {
		return this.getYear()+ ":"+this.getIndexDatePeriod(); 				
		
	}
	
	public String toRaiseString() {
		return this.getYear()+ ":"+this.getIndexDatePeriod() 		
		+ ", Days: "+this.getNumOfTxDays()
		+ ", UP% : "+GeneralHelper.toPct(this.getStockPriceRaiseRatio())
		+ ", To-Period-Low % : "+GeneralHelper.toPct(this.getPeriodLowRatio())
		+ ", To-Peirod-Hight % : "+GeneralHelper.toPct(this.getPeriodHightRatio());
	}
	
//	public String toFallString() {
//		return this.getYear()+ ":"+this.getIndexDatePeriod() 		
//		+ ", Days: "+this.getNumOfTxDays()
//		+ ", DOWN% : "+GeneralHelper.to100Percentage(this.getStockPriceFallRatio())
//		+ ", To-Period-Low % : "+GeneralHelper.to100Percentage(this.getFallPeriodLowRatio())
//		+ ", To-Peirod-Hight % : "+GeneralHelper.to100Percentage(this.getFallPeriodHightRatio());
//	}

}