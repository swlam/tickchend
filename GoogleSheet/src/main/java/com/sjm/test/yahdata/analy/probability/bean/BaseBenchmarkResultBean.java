package com.sjm.test.yahdata.analy.probability.bean;

import java.util.List;

import com.sjm.test.yahdata.analy.bean.BaseSummaryStatistics;

import lombok.Data;
@Data
public class BaseBenchmarkResultBean {

	private String stockCode;
	private String stockName;
	private String belongETF;
	private String sector;
	private String initDate;
	
	private BaseSummaryStatistics end2EndHLStat;
	private BaseSummaryStatistics end2EndLHStat;
	private BaseSummaryStatistics c2cStat;
	private BaseSummaryStatistics c2plStat;
	private BaseSummaryStatistics c2phStat;
	
	private BaseSummaryStatistics o2cStat;
	private BaseSummaryStatistics o2plStat;
	private BaseSummaryStatistics o2phStat;
	
	private BaseSummaryStatistics percentageRangeStat;
	
	private int numOfYears;
	
	
	//probability
	private double risesRatioC2C; //probability 
	private double fallsRatioC2C; //probability 
	
	private double risesRatioO2C; //probability 
	private double fallsRatioO2C; 
	
	
	private double risesRatioEnd2EndH2L;
	private double fallsRatioEnd2EndH2L;
	
	private double risesRatioEnd2EndL2H;
	private double fallsRatioEnd2EndL2H;
	
	private double risesRatioC2ph;
	private double fallsRatioC2pl;
	
	private double risesRatioO2ph;
	private double fallsRatioO2pl; //probability
	
	
	private double fallBackToNegativeReturnRatio;
	private List<String> nonNegativeRatioDates;		
	
	private List<String> o2cDownYearList;
	
	public BaseBenchmarkResultBean() {
	}

	

	
}
