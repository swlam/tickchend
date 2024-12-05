package com.sjm.test.yahdata.analy.bean;

import com.sjm.test.yahdata.analy.probability.bean.BenchmarkBeanResult;

import lombok.Data;

@Data
public class MonthlyBenchmarkSummaryBean extends BenchmarkBeanResult{

	private String month;
	
	public MonthlyBenchmarkSummaryBean(String month, String stockCode) {		
		super(stockCode);
		this.setMonth(month);
	}
	
	public String getKey() {
		return this.getMonth() +"_"+ this.getStockCode();
	}
}
