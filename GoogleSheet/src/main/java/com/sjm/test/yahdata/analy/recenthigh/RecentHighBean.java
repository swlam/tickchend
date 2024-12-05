package com.sjm.test.yahdata.analy.recenthigh;

import java.util.List;

import com.sjm.test.yahdata.analy.bean.Island;
import com.sjm.test.yahdata.analy.bean.StrongWeakTypeBean;
import com.sjm.test.yahdata.analy.bean.raw.StockBean;
import com.sjm.test.yahdata.analy.bean.PvrStockBean;
import com.sjm.test.yahdata.analy.bean.VolumePriceBean;
import com.sjm.test.yahdata.analy.ta.rule.ma.situation.bean.PriceMASituationSummaryBean;

import lombok.Data;

@Data
public class RecentHighBean extends BaseRecentHighBean{

	
	Double upSideRatioToHistoricalHigh;
	Double upSideRatioToYearHigh;
	StockBean currentStockBean;
	
	StockBean historicalHighStockBean;
	StockBean yearHighStockBean;
	
	StockBean subsequentPeriodLowestStockBean;
	private int numOfdaysFromH2Current;
	private int numOfDaysFromH2L;
	
	private double adjustmentRatioH2L;
	private double currentAdjustmentRatio;
	private double currentRecoverRatio;
	private double maxRecoverRatio;
	
	Double lowestPrice2CurrentPriceRatio;
	
	Double ytd;
	Double q1;
	Double q2;
	Double q3;
	Double q4;
	
	PriceMASituationSummaryBean movingAvgSummary;
	String maBullishPattern;
	String weeklyMonthlySummary;
	String weeklyVolSummary;
	PvrStockBean priceVolRelationship;
	
	String vpStatus;
	String vpSummary;
	
	
	String chartPatternWeekly;
	
	List<VolumePriceBean> maCrossList;
	
	double volatility20D;
	double volatility5D;
	StrongWeakTypeBean strongWeakType;
	String gapType;
	String maRecentType;
	private Island island;
	String highVolumeDaySituation;
	String recentDaysVolumeStatus;//avg(5) / avg(20)
	String gapDownStandUpSummary;
	String priceBreakUpVolPushUpSummary;
	
	double rsi14;
	String rsi14TrendIn3Days;
	
	public RecentHighBean() {
		// TODO Auto-generated constructor stub
	}



//	public StockBean getCurrentStockBean() {
//		return currentStockBean;
//	}
//
//	public void setCurrentStockBean(StockBean currentStockBean) {
//		this.currentStockBean = currentStockBean;
//	}
//
//	public StockBean getHistoricalHighStockBean() {
//		return historicalHighStockBean;
//	}
//
//	public void setHistoricalHighStockBean(StockBean historicalHighStockBean) {
//		this.historicalHighStockBean = historicalHighStockBean;
//	}
//
//	public Double getUpSideRatioToHistoricalHigh() {
//		return upSideRatioToHistoricalHigh;
//	}
//
//	public void setUpSideRatioToHistoricalHigh(Double upSideRatioToHistoricalHigh) {
//		this.upSideRatioToHistoricalHigh = upSideRatioToHistoricalHigh;
//	}
//
//	public MASituationSummaryBean getMovingAvgSummary() {
//		return movingAvgSummary;
//	}
//
//	public void setMovingAvgSummary(MASituationSummaryBean movingAvgSummary) {
//		this.movingAvgSummary = movingAvgSummary;
//	}
//
//	public StockBean getYearHighStockBean() {
//		return yearHighStockBean;
//	}
//
//	public void setYearHighStockBean(StockBean yearHighStockBean) {
//		this.yearHighStockBean = yearHighStockBean;
//	}
//
//	public Double getUpSideRatioToYearHigh() {
//		return upSideRatioToYearHigh;
//	}
//
//	public void setUpSideRatioToYearHigh(Double upSideRatioToYearHigh) {
//		this.upSideRatioToYearHigh = upSideRatioToYearHigh;
//	}
//
////	public int getYears() {
////		return years;
////	}
////
////	public void setYears(int years) {
////		this.years = years;
////	}
//
//	public Double getYtd() {
//		return ytd;
//	}
//
//	public void setYtd(Double ytd) {
//		this.ytd = ytd;
//	}
//
//	public Double getQ1() {
//		return q1;
//	}
//
//	public void setQ1(Double q1) {
//		this.q1 = q1;
//	}
//
//	public Double getQ2() {
//		return q2;
//	}
//
//	public void setQ2(Double q2) {
//		this.q2 = q2;
//	}
//
//	public Double getQ3() {
//		return q3;
//	}
//
//	public void setQ3(Double q3) {
//		this.q3 = q3;
//	}
//
//	public Double getQ4() {
//		return q4;
//	}
//
//	public void setQ4(Double q4) {
//		this.q4 = q4;
//	}
//
//
//	public StockBean getSubsequentPeriodLowestStockBean() {
//		return subsequentPeriodLowestStockBean;
//	}
//
//	public void setSubsequentPeriodLowestStockBean(StockBean subsequentPeriodLowestStockBean) {
//		this.subsequentPeriodLowestStockBean = subsequentPeriodLowestStockBean;
//	}
//
//	public int getNumOfdaysFromH2Current() {
//		return numOfdaysFromH2Current;
//	}
//
//	public void setNumOfdaysFromH2Current(int numOfdaysFromH2Current) {
//		this.numOfdaysFromH2Current = numOfdaysFromH2Current;
//	}
//
//	public int getNumOfDaysFromH2L() {
//		return numOfDaysFromH2L;
//	}
//
//	public void setNumOfDaysFromH2L(int numOfDaysFromH2L) {
//		this.numOfDaysFromH2L = numOfDaysFromH2L;
//	}
//
//	public double getAdjustmentRatioH2L() {
//		return adjustmentRatioH2L;
//	}
//
//	public void setAdjustmentRatioH2L(double adjustmentRatioH2L) {
//		this.adjustmentRatioH2L = adjustmentRatioH2L;
//	}
//
//	public double getCurrentAdjustmentRatio() {
//		return currentAdjustmentRatio;
//	}
//
//	public void setCurrentAdjustmentRatio(double currentAdjustmentRatio) {
//		this.currentAdjustmentRatio = currentAdjustmentRatio;
//	}
//
//	public double getCurrentRecoverRatio() {
//		return currentRecoverRatio;
//	}
//
//	public void setCurrentRecoverRatio(double currentRecoverRatio) {
//		this.currentRecoverRatio = currentRecoverRatio;
//	}
//
//	public double getMaxRecoverRatio() {
//		return maxRecoverRatio;
//	}
//
//	public void setMaxRecoverRatio(double maxRecoverRatio) {
//		this.maxRecoverRatio = maxRecoverRatio;
//	}
//
//	public String getVpSummary() {
//		return vpSummary;
//	}
//
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
//	public String getChartPatternWeekly() {
//		return chartPatternWeekly;
//	}
//
//	public void setChartPatternWeekly(String charPatternWeekly) {
//		this.chartPatternWeekly = charPatternWeekly;
//	}
//
//	public Double getDailyChangePct() {
//		return dailyChangePct;
//	}
//
//	public void setDailyChangePct(Double dailyChangePct) {
//		this.dailyChangePct = dailyChangePct;
//	}
//
//	public PvrStockBean getPriceVolRelationship() {
//		return priceVolRelationship;
//	}
//
//	public void setPriceVolRelationship(PvrStockBean priceVolRelationship) {
//		this.priceVolRelationship = priceVolRelationship;
//	}
//
//	public List<VolumePriceBean> getMaCrossList() {
//		return maCrossList;
//	}
//
//	public void setMaCrossList(List<VolumePriceBean> maCrossList) {
//		this.maCrossList = maCrossList;
//	}
//
//	public String getMaBullishPattern() {
//		return maBullishPattern;
//	}
//
//	public void setMaBullishPattern(String maBullishPattern) {
//		this.maBullishPattern = maBullishPattern;
//	}
//
//	
////	public boolean isGoStrength() {
////		return isGoStrength;
////	}
////
////	public void setGoStrength(boolean isAboveThreeDaysHigh) {
////		this.isGoStrength = isAboveThreeDaysHigh;
////	}
////
////	public boolean isGoWeek() {
////		return isGoWeek;
////	}
////
////	public void setGoWeek(boolean isBelowThreeDaysLow) {
////		this.isGoWeek = isBelowThreeDaysLow;
////	}
//
//	public StrongWeakTypeBean getStrongWeakType() {
//		return strongWeakType;
//	}
//
//	public void setStrongWeakType(StrongWeakTypeBean strongWeakType) {
//		this.strongWeakType = strongWeakType;
//	}
//
//	public String getGapType() {
//		return gapType;
//	}
//
//	public void setGapType(String gapType) {
//		this.gapType = gapType;
//	}
//
//	public Double getLowestPrice2CurrentPriceRatio() {
//		return lowestPrice2CurrentPriceRatio;
//	}
//
//	public void setLowestPrice2CurrentPriceRatio(Double lowestPrice2CurrentPriceRatio) {
//		this.lowestPrice2CurrentPriceRatio = lowestPrice2CurrentPriceRatio;
//	}
//
////	public Double getMtdPct() {
////		return mtdPct;
////	}
////
////	public void setMtdPct(Double mtdPct) {
////		this.mtdPct = mtdPct;
////	}
////
////	public Double getThreeDaysPct() {
////		return threeDaysPct;
////	}
////
////	public void setThreeDaysPct(Double threeDaysPct) {
////		this.threeDaysPct = threeDaysPct;
////	}
//
////	public String isIncreaseVolume() {
////		return recentDaysVolumeStatus;
////	}
//////recent daysVolume
////	public void setIncreaseVolume(String isIncreaseVolume) {
////		this.recentDaysVolumeStatus = isIncreaseVolume;
////	}
//
//	public String getGapDownStandUpSummary() {
//		return gapDownStandUpSummary;
//	}
//
//	public String getRecentDaysVolumeStatus() {
//		return recentDaysVolumeStatus;
//	}
//
//	public void setRecentDaysVolumeStatus(String recentDaysVolumeStatus) {
//		this.recentDaysVolumeStatus = recentDaysVolumeStatus;
//	}
//
//	public void setGapDownStandUpSummary(String gapDownStandUpSummary) {
//		this.gapDownStandUpSummary = gapDownStandUpSummary;
//	}
//
//	public String getPriceBreakUpVolPushUpSummary() {
//		return priceBreakUpVolPushUpSummary;
//	}
//
//	public void setPriceBreakUpVolPushUpSummary(String priceBreakUpVolPushUpSummary) {
//		this.priceBreakUpVolPushUpSummary = priceBreakUpVolPushUpSummary;
//	}
//
//	public String getWeeklyMonthlySummary() {
//		return weeklyMonthlySummary;
//	}
//
//	public void setWeeklyMonthlySummary(String weeklyMonthlySummary) {
//		this.weeklyMonthlySummary = weeklyMonthlySummary;
//	}
//
//	
//	
//	public String getWeeklyVolSummary() {
//		return weeklyVolSummary;
//	}
//
//
//
//	public void setWeeklyVolSummary(String weeklyVolSummary) {
//		this.weeklyVolSummary = weeklyVolSummary;
//	}
//
//
//
//	public String getIslandType() {
//		return islandType;
//	}
//
//	public void setIslandType(String islandType) {
//		this.islandType = islandType;
//	}
//
//	public String getMaRecentType() {
//		return maRecentType;
//	}
//
//	public void setMaRecentType(String maRecentType) {
//		this.maRecentType = maRecentType;
//	}
//
//	public double getVolatility20D() {
//		return volatility20D;
//	}
//
//	public void setVolatility20D(double volatility20d) {
//		volatility20D = volatility20d;
//	}
//
//	public double getVolatility5D() {
//		return volatility5D;
//	}
//
//	public void setVolatility5D(double volatility5d) {
//		volatility5D = volatility5d;
//	}
//
//	public double getRsi14() {
//		return rsi14;
//	}
//
//	public void setRsi14(double rsi14) {
//		this.rsi14 = rsi14;
//	}
//
//	public String getRsi14TrendIn3Days() {
//		return rsi14TrendIn3Days;
//	}
//
//	public void setRsi14TrendIn3Days(String rsi14TrendIn3Days) {
//		this.rsi14TrendIn3Days = rsi14TrendIn3Days;
//	}
//
//
//
//	public String getHighVolumeDaySituation() {
//		return highVolumeDaySituation;
//	}
//
//
//
//	public void setHighVolumeDaySituation(String highVolumeDaySituation) {
//		this.highVolumeDaySituation = highVolumeDaySituation;
//	}

	
		

	
}
