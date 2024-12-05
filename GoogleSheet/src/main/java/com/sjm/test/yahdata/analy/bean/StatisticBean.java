package com.sjm.test.yahdata.analy.bean;

import java.util.DoubleSummaryStatistics;
public class StatisticBean {

	private String inMonthPeriod;
	private DoubleSummaryStatistics upSideSummaryStatistics;
	private DoubleSummaryStatistics downSideSummaryStatistics;
	
	private DoubleSummaryStatistics upSidePeriod2LSummaryStatistics;
	private DoubleSummaryStatistics upSidePeriod2HSummaryStatistics;
	
	private DoubleSummaryStatistics downSidePeriod2LSummaryStatistics;
	private DoubleSummaryStatistics downSidePeriod2HSummaryStatistics;
	
	private double upCountRatio;
	private double downCountRatio;
	
	private long upCount;
	private long downCount;
	private long numOfYears;
	
	
	private double upCountRatioInFullHistData;
	
	public StatisticBean() {
	}

	public String getInMonthPeriod() {
		return inMonthPeriod;
	}

	public void setInMonthPeriod(String inMonthPeriod) {
		this.inMonthPeriod = inMonthPeriod;
	}

	public DoubleSummaryStatistics getUpSideSummaryStatistics() {
		return upSideSummaryStatistics;
	}

	public void setUpSideSummaryStatistics(DoubleSummaryStatistics positiveSummaryStatistics) {
		this.upSideSummaryStatistics = positiveSummaryStatistics;
	}

	public DoubleSummaryStatistics getDownSideSummaryStatistics() {
		return downSideSummaryStatistics;
	}

	public void setDownSideSummaryStatistics(DoubleSummaryStatistics negativeSummaryStatistics) {
		this.downSideSummaryStatistics = negativeSummaryStatistics;
	}


	public DoubleSummaryStatistics getUpSidePeriod2LSummaryStatistics() {
		return upSidePeriod2LSummaryStatistics;
	}

	public void setUpSidePeriod2LSummaryStatistics(DoubleSummaryStatistics upSidePeriod2LSummaryStatistics) {
		this.upSidePeriod2LSummaryStatistics = upSidePeriod2LSummaryStatistics;
	}

	public DoubleSummaryStatistics getUpSidePeriod2HSummaryStatistics() {
		return upSidePeriod2HSummaryStatistics;
	}

	public void setUpSidePeriod2HSummaryStatistics(DoubleSummaryStatistics upSidePeriod2HSummaryStatistics) {
		this.upSidePeriod2HSummaryStatistics = upSidePeriod2HSummaryStatistics;
	}

	public DoubleSummaryStatistics getDownSidePeriod2LSummaryStatistics() {
		return downSidePeriod2LSummaryStatistics;
	}

	public void setDownSidePeriod2LSummaryStatistics(DoubleSummaryStatistics downSidePeriod2LSummaryStatistics) {
		this.downSidePeriod2LSummaryStatistics = downSidePeriod2LSummaryStatistics;
	}

	public DoubleSummaryStatistics getDownSidePeriod2HSummaryStatistics() {
		return downSidePeriod2HSummaryStatistics;
	}

	public void setDownSidePeriod2HSummaryStatistics(DoubleSummaryStatistics downSidePeriod2HSummaryStatistics) {
		this.downSidePeriod2HSummaryStatistics = downSidePeriod2HSummaryStatistics;
	}

	public double getUpCountRatio() {
		return upCountRatio;
	}

	public void setUpCountRatio(double upCountRatio) {
		this.upCountRatio = upCountRatio;
	}

	public double getDownCountRatio() {
		return downCountRatio;
	}

	public void setDownCountRatio(double downCountRatio) {
		this.downCountRatio = downCountRatio;
	}

	public long getNumOfYears() {
		return numOfYears;
	}

	public void setNumOfYears(long numOfYears) {
		this.numOfYears = numOfYears;
	}

	public long getUpCount() {
		return upCount;
	}

	public void setUpCount(long upCount) {
		this.upCount = upCount;
	}

	public long getDownCount() {
		return downCount;
	}

	public void setDownCount(long downCount) {
		this.downCount = downCount;
	}

	public double getUpCountRatioInFullHistData() {
		return upCountRatioInFullHistData;
	}

	public void setUpCountRatioInFullHistData(double upCountRatioInFullHistData) {
		this.upCountRatioInFullHistData = upCountRatioInFullHistData;
	}

	
	
//	public String toString() {
//		return  this.inMonthPeriod+ "\t UpCount%: "+ GeneralHelper.to100Percentage(this.getUpCountRatio())
//				+ "\t DownCount%: "+ GeneralHelper.to100Percentage(this.getDownCountRatio())
//				+"\t Years: "+ this.getNumOfYears();
//	}

	
}
