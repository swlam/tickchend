package com.sjm.test.yahdata.benchmarks;

import com.sjm.test.yahdata.analy.bean.GapBean;
import com.sjm.test.yahdata.analy.bean.raw.StockBean;

import lombok.Data;

@Data
public class BenchmarkSummary {

	private Integer pastNumOfDays;
	private String fromDate;
	private String toDate;
	private String stockCode;
	private String sector;
	private Double dailyChangePct;
	private Double dailyEstTradeAmount;
	private Double percentageC2PeriodH;
	private Double percentageC2PeriodL;
	private Double percentageL2PeriodH;
	private Double percentageE2EC2C;
	private Double percentageE2EL2C;
	private Double percentageE2EL2H;
	private Double percentageE2EH2L;
	private Double percentageE2EH2C;
	
	public BenchmarkSummary() {
	}

//	public Integer getPastNumOfDays() {
//		return pastNumOfDays;
//	}
//
//	public void setPastNumOfDays(Integer pastNumOfDays) {
//		this.pastNumOfDays = pastNumOfDays;
//	}
//
//	public String getFromDate() {
//		return fromDate;
//	}
//
//	public void setFromDate(String fromDate) {
//		this.fromDate = fromDate;
//	}
//
//	public String getToDate() {
//		return toDate;
//	}
//
//	public void setToDate(String toDate) {
//		this.toDate = toDate;
//	}
//
//	public String getStockCode() {
//		return stockCode;
//	}
//
//	public void setStockCode(String stockCode) {
//		this.stockCode = stockCode;
//	}
//
//	public Double getPercentageE2EC2C() {
//		return percentageE2EC2C;
//	}
//
//	public void setPercentageE2EC2C(Double percentage) {
//		this.percentageE2EC2C = percentage;
//	}
//	
//
//	public Double getPercentageC2PeriodH() {
//		return percentageC2PeriodH;
//	}
//
//	public void setPercentageC2PeriodH(Double percentageC2PeriodH) {
//		this.percentageC2PeriodH = percentageC2PeriodH;
//	}
//
//	public Double getPercentageC2PeriodL() {
//		return percentageC2PeriodL;
//	}
//
//	public void setPercentageC2PeriodL(Double percentageC2PeriodL) {
//		this.percentageC2PeriodL = percentageC2PeriodL;
//	}
//	
////	public Double getDailyChangePct() {
////		return dailyChangePct;
////	}
////
////	public void setDailyChangePct(Double dailyChangePct) {
////		this.dailyChangePct = dailyChangePct;
////	}
//	
//
//	public Double getPercentageL2PeriodH() {
//		return percentageL2PeriodH;
//	}
//
//	public void setPercentageL2PeriodH(Double percentageL2PeriodH) {
//		this.percentageL2PeriodH = percentageL2PeriodH;
//	}
//
//	public Double getPercentageE2EL2H() {
//		return percentageE2EL2H;
//	}
//
//	public void setPercentageE2EL2H(Double percentageE2EL2H) {
//		this.percentageE2EL2H = percentageE2EL2H;
//	}
//
//	public String getSector() {
//		return sector;
//	}
//
//	public void setSector(String sector) {
//		this.sector = sector;
//	}
//
//	
//	public Double getPercentageE2EL2C() {
//		return percentageE2EL2C;
//	}
//
//	public void setPercentageE2EL2C(Double percentageE2EL2C) {
//		this.percentageE2EL2C = percentageE2EL2C;
//	}
//	
//
//	public Double getPercentageE2EH2L() {
//		return percentageE2EH2L;
//	}
//
//	public void setPercentageE2EH2L(Double percentageE2EH2L) {
//		this.percentageE2EH2L = percentageE2EH2L;
//	}
//
//	
//	
//	public Double getPercentageE2EH2C() {
//		return percentageE2EH2C;
//	}
//
//	public void setPercentageE2EH2C(Double percentageE2EH2C) {
//		this.percentageE2EH2C = percentageE2EH2C;
//	}

	public String toString() {
		return this.getStockCode() +" " + this.getSector() +" " + this.getPercentageE2EC2C()+" [" +this.getFromDate() +" to" +this.getToDate()+"]";
	}
	
}
