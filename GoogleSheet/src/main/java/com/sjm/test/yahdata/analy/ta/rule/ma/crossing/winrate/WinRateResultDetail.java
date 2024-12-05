package com.sjm.test.yahdata.analy.ta.rule.ma.crossing.winrate;

import com.maas.util.GeneralHelper;

import lombok.Data;
@Data
public class WinRateResultDetail {
	String stockCode;
	String tag;
	double avgPercentageC2H;
	double avgDaysC2H;
	double avgPercentageC2HC;
	double avgDaysC2HC;
	double avgPercentageC2L;
	double avgDaysC2L;
	double avgPercentageC2LC;
	double avgDaysC2LC;
	
	double avgPercentagePeriodEndC2C;
	
	double medianC2Highest ;
	double medianC2Lowest;
	double medianC2C;
	
	public String getMessage() {
		
		StringBuilder rtn = new StringBuilder();
		rtn.append( 
//		"\t "+tag+"\t" + GeneralHelper.to100Percentage(hitRatio)								
//		+"\t"+upCount + "\t"+total				
		 GeneralHelper.toPct(medianC2C) + "\t"+ GeneralHelper.toPct(medianC2Highest) + "\t"+ GeneralHelper.toPct(medianC2Lowest) 
		+ "\t"+ GeneralHelper.toPct(avgPercentageC2H) + "\t" + GeneralHelper.format( avgDaysC2H) 
		+ "\t"+ GeneralHelper.toPct(avgPercentageC2HC) + "\t" + GeneralHelper.format( avgDaysC2HC)
		+ "\t"+ GeneralHelper.toPct(avgPercentageC2L) + "\t" + GeneralHelper.format( avgDaysC2L) 
		+ "\t"+ GeneralHelper.toPct(avgPercentageC2LC) + "\t" + GeneralHelper.format( avgDaysC2LC)
		+ "\t"+ GeneralHelper.toPct(avgPercentagePeriodEndC2C) 
		);
		
		return rtn.toString();
	}


//	public double getAvgPercentageC2H() {
//		return avgPercentageC2H;
//	}
//
//
//	public void setAvgPercentageC2H(double avgPercentageC2H) {
//		this.avgPercentageC2H = avgPercentageC2H;
//	}
//
//
//	public double getAvgDaysC2H() {
//		return avgDaysC2H;
//	}
//
//	public void setAvgDaysC2H(double avgDaysC2H) {
//		this.avgDaysC2H = avgDaysC2H;
//	}
//
//	public double getAvgPercentageC2HC() {
//		return avgPercentageC2HC;
//	}
//
//
//
//	public void setAvgPercentageC2HC(double avgPercentageC2HC) {
//		this.avgPercentageC2HC = avgPercentageC2HC;
//	}
//
//
//
//	public double getAvgDaysC2HC() {
//		return avgDaysC2HC;
//	}
//
//
//
//	public void setAvgDaysC2HC(double avgDaysC2HC) {
//		this.avgDaysC2HC = avgDaysC2HC;
//	}
//
//
//
//	public double getAvgPercentageC2L() {
//		return avgPercentageC2L;
//	}
//
//
//
//	public void setAvgPercentageC2L(double avgPercentageC2L) {
//		this.avgPercentageC2L = avgPercentageC2L;
//	}
//
//
//
//	public double getAvgPercentageC2LC() {
//		return avgPercentageC2LC;
//	}
//
//
//
//	public void setAvgPercentageC2LC(double avgPercentageC2LC) {
//		this.avgPercentageC2LC = avgPercentageC2LC;
//	}
//
//
//	public double getAvgDaysC2L() {
//		return avgDaysC2L;
//	}
//
//
//	public void setAvgDaysC2L(double avgDaysC2L) {
//		this.avgDaysC2L = avgDaysC2L;
//	}
//
//
//	public double getAvgDaysC2LC() {
//		return avgDaysC2LC;
//	}
//
//
//	public void setAvgDaysC2LC(double avgDaysC2LC) {
//		this.avgDaysC2LC = avgDaysC2LC;
//	}
//
//
//	public double getAvgPercentagePeriodEndC2C() {
//		return avgPercentagePeriodEndC2C;
//	}
//
//
//	public void setAvgPercentagePeriodEndC2C(double avgPercentagePeriodEndC2C) {
//		this.avgPercentagePeriodEndC2C = avgPercentagePeriodEndC2C;
//	}
//
//
//	public String getStockCode() {
//		return stockCode;
//	}
//
//
//	public void setStockCode(String stockCode) {
//		this.stockCode = stockCode;
//	}
//
//
//	public String getTag() {
//		return tag;
//	}
//
//
//	public void setTag(String tag) {
//		this.tag = tag;
//	}
//
//
//	public double getMedianC2Highest() {
//		return medianC2Highest;
//	}
//
//
//	public void setMedianC2Highest(double mediaC2Highest) {
//		this.medianC2Highest = mediaC2Highest;
//	}
//
//
//	public double getMedianC2Lowest() {
//		return medianC2Lowest;
//	}
//
//
//	public void setMedianC2Lowest(double mediaC2Lowest) {
//		this.medianC2Lowest = mediaC2Lowest;
//	}
//
//
//	public double getMedianC2C() {
//		return medianC2C;
//	}
//
//
//	public void setMedianC2C(double medianC2C) {
//		this.medianC2C = medianC2C;
//	}


}
