package com.sjm.test.yahdata.analy.recenthigh;

import com.maas.util.GeneralHelper;
import com.sjm.test.yahdata.analy.helper.CFGHelper;


public class BaseRecentHighBean {
	String stockCode;
	String stockName;
	String sector;
	Integer years;
	String currentDate;
	Double currentPrice;
	Double estimateAmount;
	Double dailyChangePct;
	Double dailyChangeHighestPct;
	Double dailyChangeLowestPct;
	Double threeDaysChangePct;
	Double mtdChangePct;
	String dailyCandleDescription;
	String dailyVolDescription;
	
	public BaseRecentHighBean() {
		// TODO Auto-generated constructor stub
	}

	public String getStockCode() {
		return stockCode;
	}

	public void setStockCode(String stockCode) {
		this.stockCode = stockCode;
	}

	public String getStockName() {
		return stockName;
	}

	public void setStockName(String stockName) {
		this.stockName = stockName;
	}

	public String getSector() {
		return sector;
	}

	public void setSector(String sector) {
		this.sector = sector;
	}

	public Integer getYears() {
		return years;
	}

	public void setYears(Integer years) {
		this.years = years;
	}

	public String getCurrentDate() {
		return currentDate;
	}

	public void setCurrentDate(String currentDate) {
		this.currentDate = currentDate;
	}

	public Double getCurrentPrice() {
		return currentPrice;
	}

	public void setCurrentPrice(Double currentPrice) {
		this.currentPrice = currentPrice;
	}

	public Double getEstimateAmount() {
		return estimateAmount;
	}

	public void setEstimateAmount(Double estimateAmount) {
		this.estimateAmount = estimateAmount;
	}

	public Double getDailyChangePct() {
		return dailyChangePct;
	}

	public void setDailyChangePct(Double dailyChangePct) {
		this.dailyChangePct = dailyChangePct;
	}

	public Double getThreeDaysChangePct() {
		return threeDaysChangePct;
	}

	public void setThreeDaysChangePct(Double threeDaysChangePct) {
		this.threeDaysChangePct = threeDaysChangePct;
	}

	public Double getMtdChangePct() {
		return mtdChangePct;
	}

	public void setMtdChangePct(Double mtdChangePct) {
		this.mtdChangePct = mtdChangePct;
	}

	public String getDailyCandleDescription() {
		return dailyCandleDescription;
	}

	public void setDailyCandleDescription(String dailyCandleDescription) {
		this.dailyCandleDescription = dailyCandleDescription;
	}

	public String getDailyVolDescription() {
		return dailyVolDescription;
	}

	public void setDailyVolDescription(String dailyVolDescription) {
		this.dailyVolDescription = dailyVolDescription;
	}

	public Double getDailyChangeHighestPct() {
		return dailyChangeHighestPct;
	}

	public void setDailyChangeHighestPct(Double dailyChangeHighestPct) {
		this.dailyChangeHighestPct = dailyChangeHighestPct;
	}

	public Double getDailyChangeLowestPct() {
		return dailyChangeLowestPct;
	}

	public void setDailyChangeLowestPct(Double dailyChangeLowestPct) {
		this.dailyChangeLowestPct = dailyChangeLowestPct;
	}
	
	
}
