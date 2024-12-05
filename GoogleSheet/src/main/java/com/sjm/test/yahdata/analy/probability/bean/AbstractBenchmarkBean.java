package com.sjm.test.yahdata.analy.probability.bean;

import lombok.Data;

@Data
public  class AbstractBenchmarkBean {

	private String stockCode;

	private double pctC2C;
	private double pctC2Lowest;
	private double pctC2Highest;
	
	private double pctO2C;
	private double pctO2Lowest;
	private double pctO2Highest;
	
	private double pctL2C;
	private double pctL2Lowest;
	private double pctL2Highest;
	
	private double pctH2C;
	private double pctH2Lowest;
	private double pctH2Highest;
	
	
	private double percentageRange;
	private String yyyy;
	
	private double pctEnd2EndLH;
	private double pctEnd2EndHL;
	
	public AbstractBenchmarkBean() {
	}
	
}
