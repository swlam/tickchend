package com.sjm.test.yahdata.analy.bean;

public class PeriodStatisBean {

	String stockCode;
	String periodStart;
	String periodEnd;
	double periodC2CRatio;
	double periodC2LRatio;
	double periodC2HRatio;
	
	double periodL2HRatio;
	
	public PeriodStatisBean() {
		// TODO Auto-generated constructor stub
	}

	public String getStockCode() {
		return stockCode;
	}

	public void setStockCode(String stockCode) {
		this.stockCode = stockCode;
	}

	public String getPeriodStart() {
		return periodStart;
	}

	public void setPeriodStart(String periodStart) {
		this.periodStart = periodStart;
	}

	public String getPeriodEnd() {
		return periodEnd;
	}

	public void setPeriodEnd(String periodEnd) {
		this.periodEnd = periodEnd;
	}

	public double getPeriodC2CRatio() {
		return periodC2CRatio;
	}

	public void setPeriodC2CRatio(double periodC2CRatio) {
		this.periodC2CRatio = periodC2CRatio;
	}

	public double getPeriodC2LRatio() {
		return periodC2LRatio;
	}

	public void setPeriodC2LRatio(double periodC2LRatio) {
		this.periodC2LRatio = periodC2LRatio;
	}

	public double getPeriodC2HRatio() {
		return periodC2HRatio;
	}

	public void setPeriodC2HRatio(double periodC2HRatio) {
		this.periodC2HRatio = periodC2HRatio;
	}
	
	
	public double getPeriodL2HRatio() {
		return periodL2HRatio;
	}

	public void setPeriodL2HRatio(double periodL2HRatio) {
		this.periodL2HRatio = periodL2HRatio;
	}

	public String toString() {
		return "["+stockCode +":"+periodStart +"-"+ periodEnd+"]";
	}
}

