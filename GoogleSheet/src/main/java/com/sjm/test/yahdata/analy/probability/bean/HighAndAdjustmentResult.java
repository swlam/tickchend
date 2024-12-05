package com.sjm.test.yahdata.analy.probability.bean;

import com.maas.util.GeneralHelper;
@Deprecated
public class HighAndAdjustmentResult {

	private String stockCode;
	private String hDate;
	private String subsequentPeriodLDate;
	private String currentDate;
	
	private int numOfdaysFromH2Current;
	private int numOfDaysFromH2L;
	
	private double adjustmentRatioH2L;
	private double currentAdjustmentRatio;
	private double currentRecoverRatio;
	private double maxRecoverRatio;
	
	public HighAndAdjustmentResult() {
	}
	public HighAndAdjustmentResult(String stockCode) {
		this.stockCode = stockCode;
	}
	public String getStockCode() {
		return stockCode;
	}
	public void setStockCode(String stockCode) {
		this.stockCode = stockCode;
	}
	public String gethDate() {
		return hDate;
	}
	public void sethDate(String hDate) {
		this.hDate = hDate;
	}
	
public String getSubsequentPeriodLDate() {
		return subsequentPeriodLDate;
	}
	public void setSubsequentPeriodLDate(String subsequentPeriodLDate) {
		this.subsequentPeriodLDate = subsequentPeriodLDate;
	}
	//	public String getlDate() {
//		return subsequentPeriodLDate;
//	}
//	public void setlDate(String lDate) {
//		this.subsequentPeriodLDate = lDate;
//	}
	public String getCurrentDate() {
		return currentDate;
	}
	public void setCurrentDate(String currentDate) {
		this.currentDate = currentDate;
	}
	public int getNumOfdaysFromH2Current() {
		return numOfdaysFromH2Current;
	}
	public void setDaysFromH2Current(int daysFromH2Current) {
		this.numOfdaysFromH2Current = daysFromH2Current;
	}
	public int getNumOfDaysFromH2L() {
		return numOfDaysFromH2L;
	}
	public void setNumOfDaysFromH2L(int daysFromH2L) {
		this.numOfDaysFromH2L = daysFromH2L;
	}
	public double getCurrentAdjustmentRatio() {
		return currentAdjustmentRatio;
	}
	public void setCurrentAdjustmentRatio(double currentAdjustmentRatio) {
		this.currentAdjustmentRatio = currentAdjustmentRatio;
	}
	public double getCurrentRecoverRatio() {
		return currentRecoverRatio;
	}
	public void setCurrentRecoverRatio(double currentRecoverRatio) {
		this.currentRecoverRatio = currentRecoverRatio;
	}
	public double getMaxRecoverRatio() {
		return maxRecoverRatio;
	}
	public void setMaxRecoverRatio(double maxRecoverRatio) {
		this.maxRecoverRatio = maxRecoverRatio;
	}
	
	
	public double getAdjustmentRatioH2L() {
		return adjustmentRatioH2L;
	}
	public void setAdjustmentRatioH2L(double adjustmentRatioH2L) {
		this.adjustmentRatioH2L = adjustmentRatioH2L;
	}
	
	public String toString() {
		
		return this.getStockCode()
				+ "\tCur:" + this.getCurrentDate()
				+ "\tH-Date: " + this.gethDate()
				+ "\tPeriodL-Date: " + this.getSubsequentPeriodLDate()
				+ "\tDays(H2Cur): " + this.getNumOfdaysFromH2Current()//daysFromH2Current
				+ "\tDays(H2L): " + this.getNumOfDaysFromH2L() 
				
				+ "\tAdj(H2L):" + GeneralHelper.toPct(this.getAdjustmentRatioH2L())
				+ "\tAdj(H2Cur):" + GeneralHelper.toPct(this.getCurrentAdjustmentRatio())
				+ "\tMaxRecover: "+ GeneralHelper.toPct(this.getMaxRecoverRatio())
				+ "\tCurRecover: "+ GeneralHelper.toPct(this.getCurrentRecoverRatio());
	}
	
	
	public String toCSVLine() {
		return this.getStockCode()
				+ "\t" + this.getCurrentDate()
				+ "\t" + this.gethDate()
				+ "\t" + this.getSubsequentPeriodLDate()
				+ "\t" + this.getNumOfdaysFromH2Current()//daysFromH2Current
				+ "\t" + this.getNumOfDaysFromH2L() 
				+ "\t" + this.getAdjustmentRatioH2L()
				+ "\t" + this.getCurrentAdjustmentRatio()
				+ "\t "+ this.getMaxRecoverRatio()
				+ "\t "+ this.getCurrentRecoverRatio();
		
//				+ "\t" + GeneralHelper.to100Percentage(this.getAdjustmentRatioH2L())
//				+ "\t" + GeneralHelper.to100Percentage(this.getCurrentAdjustmentRatio())
//				+ "\t "+ GeneralHelper.to100Percentage(this.getMaxRecoverRatio())
//				+ "\t "+ GeneralHelper.to100Percentage(this.getCurrentRecoverRatio());
				
	}
}
