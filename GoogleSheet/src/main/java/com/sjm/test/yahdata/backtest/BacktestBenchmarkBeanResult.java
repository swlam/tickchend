package com.sjm.test.yahdata.backtest;

import com.sjm.test.yahdata.analy.bean.BaseSummaryStatistics;

import lombok.Data;
@Data
public class BacktestBenchmarkBeanResult {

	String code;
	private int numOfYears;
	private int cntUp;
	private int cntDown;
	private int cntAll;
	private int noOfTxDays;
	private double upRatio;
	private String strategyPattern;
	private int days;
	
//	private BaseSummaryStatistics end2EndStat;
	private BaseSummaryStatistics c2cStat;
	private BaseSummaryStatistics c2plStat;
	private BaseSummaryStatistics c2phStat;
	private BaseSummaryStatistics percentageRangeStat;
	
	private String lastTxnDate;
	
	public BacktestBenchmarkBeanResult(String stockCode) {
		this.setCode(stockCode);
	}
	



	public String toDescription() {
		return strategyPattern+", Sample: " + cntUp + " of" +cntAll + ", Days:"+days;
	}

}
