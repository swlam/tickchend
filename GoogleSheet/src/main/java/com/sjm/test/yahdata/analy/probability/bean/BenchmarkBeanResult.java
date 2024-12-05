package com.sjm.test.yahdata.analy.probability.bean;

import java.time.LocalDate;

import com.maas.util.DateHelper;
import com.maas.util.MarketValueDifferenceCalculator;
import com.sjm.test.yahdata.analy.conts.Const;
import com.sjm.test.yahdata.analy.module.lowhighdist.bean.LowHighDateSimplifyResult;

import lombok.Data;
@Data
public class BenchmarkBeanResult extends BaseBenchmarkResultBean{
	
	private String yearHeading;		
	private BenchmarkBean recentBenchmarkBean;	
	private BenchmarkBean currentBenchmarkBean;
	
	private LowHighDateSimplifyResult lowHighDateSimplifyResult; 
	private String startMMdd;
	private String endMMdd; 
	
	private int periodNoOfDays;
	private double marketCapByBillian = 0.0;
	
	
	//MarketValueDifferenceCalculator.convertToBillian(skProf.getMarketCap())));

	public BenchmarkBeanResult(String stockCode) {
		this.setStockCode(stockCode);
	}

//	public double c2cProfitAndDaysRatio() {
//		return getC2cStat().getMedian() / (double)getPeriodNoOfDays();
//	}
//	
//	public double o2cProfitAndDaysRatio() {
//		return getO2cStat().getMedian() / (double)getPeriodNoOfDays();
//	}
	
	public LocalDate getFakeRangeStartDate() {		
		return DateHelper.convert("2020-"+startMMdd);
	}
	
	public LocalDate getFakeRangeEndDate() {		
		return DateHelper.convert("2020-"+endMMdd);
	}
	
	
	public double getO2phRatingOfEffectiveness() {
		return Math.abs(getO2phStat().getMedian() *100 / (double) this.periodNoOfDays);
	}
	
	public double getC2phRatingOfEffectiveness() {
		return Math.abs(getC2phStat().getMedian() *100 / (double) this.periodNoOfDays);
	}
	
	public double getO2plRatingOfEffectiveness() {
		return Math.abs(getO2plStat().getMedian() *100 / (double) this.periodNoOfDays);
	}
	
	public double getC2plRatingOfEffectiveness() {
		return Math.abs(getC2plStat().getMedian() *100 / (double) this.periodNoOfDays);
	}
	
	public String toString() {
		return this.getStockCode() +":"+this.getStartMMdd() +"-"+ this.getEndMMdd();
	}
}
