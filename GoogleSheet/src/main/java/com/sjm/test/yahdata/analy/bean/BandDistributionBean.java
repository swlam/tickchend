package com.sjm.test.yahdata.analy.bean;

import java.util.ArrayList;
import java.util.List;

import com.maas.util.GeneralHelper;
import com.sjm.test.yahdata.analy.bean.raw.StockBean;

public class BandDistributionBean {

	String code;
	String periodUnit; // MONTHLTY, WEEKLY
	String startDate;
	String endDate;
	List<StockBean> upperBandStockList;
	List<StockBean> lowerBandStockList;
	
	double upperBandAvg;
	double upperBandSD;
	double lowerBandAvg;
	double lowerBandSD;
	
	public BandDistributionBean(String code, String periodUnit) {
		this.code = code;
		this.periodUnit = periodUnit;
		this.upperBandStockList = new ArrayList<StockBean>(10);
		this.lowerBandStockList = new ArrayList<StockBean>(10);
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getPeriodUnit() {
		return periodUnit;
	}

	public void setPeriodUnit(String periodUnit) {
		this.periodUnit = periodUnit;
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

	public List<StockBean> getUpperBandStockList() {
		return upperBandStockList;
	}

	public void setUpperBandStockList(List<StockBean> upperBandStockList) {
		this.upperBandStockList = upperBandStockList;
	}

	public List<StockBean> getLowerBandStockList() {
		return lowerBandStockList;
	}

	public void setLowerBandStockList(List<StockBean> lowerBandStockList) {
		this.lowerBandStockList = lowerBandStockList;
	}

	public double getUpperBandAvg() {
		return upperBandAvg;
	}

	public void setUpperBandAvg(double upperBandAvg) {
		this.upperBandAvg = upperBandAvg;
	}

	public double getUpperBandSD() {
		return upperBandSD;
	}

	public void setUpperBandSD(double upperBandSD) {
		this.upperBandSD = upperBandSD;
	}

	public double getLowerBandAvg() {
		return lowerBandAvg;
	}

	public void setLowerBandAvg(double lowerBandAvg) {
		this.lowerBandAvg = lowerBandAvg;
	}

	public double getLowerBandSD() {
		return lowerBandSD;
	}

	public void setLowerBandSD(double lowerBandSD) {
		this.lowerBandSD = lowerBandSD;
	}
	
	
	public String toUpperBandData() {
		return this.getUpperBandAvg() +"\t"+ this.getUpperBandSD() +"\t"+ GeneralHelper.toPct(this.getUpperBandSD() / this.getUpperBandAvg());
	}
	
	public String toLowerBandData() {
		return this.getLowerBandAvg() +"\t"+ this.getLowerBandSD() +"\t"+ GeneralHelper.toPct(this.getLowerBandSD() / this.getLowerBandAvg());
	}
	
	public String toString() {
		return "\t# UpperBand: AVG = "+this.getUpperBandAvg() +", SD = "+ this.getUpperBandSD() +", SD/AVG = "+ GeneralHelper.toPct(this.getUpperBandSD() / this.getUpperBandAvg());
	}
	
	

}
