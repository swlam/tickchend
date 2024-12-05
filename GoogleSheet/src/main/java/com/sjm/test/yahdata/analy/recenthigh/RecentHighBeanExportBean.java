//package com.sjm.test.yahdata.analy.recenthigh;
//
//import java.util.List;
//
//import com.sjm.test.yahdata.analy.bean.StockBean;
//import com.sjm.test.yahdata.analy.pv.PvrStockBean;
//import com.sjm.test.yahdata.analy.ta.VolumePriceBean;
//import com.sjm.test.yahdata.analy.ta.rule.ma.situation.MASituationSummaryBean;
//
//public class RecentHighBeanExportBean {
//
//	String stockCode;
//	String stockName;
//	String sector;
//	String years;
//	String currentDate;
//	String currentPrice;
//	String estimateAmount;
//	String dailyChangePct;
//	String threeDaysChangePct;
//	String mtdChangePct;
//
//	String upSideRatioToHistoricalHigh;
//	String upSideRatioToYearHigh;
//	StockBean currentStockBean;
//	
//	StockBean historicalHighStockBean;
//	StockBean yearHighStockBean;
//	
////	private String subsequentPeriodLDate;
//	StockBean subsequentPeriodLowestStockBean;
//	private int numOfdaysFromH2Current;
//	private int numOfDaysFromH2L;
//	
//	private String adjustmentRatioH2L;
//	private String currentAdjustmentRatio;
//	private String currentRecoverRatio;
//	private String maxRecoverRatio;
//	
//	String lowestPrice2CurrentPriceRatio;
//	
//
//	String ytd;
//	String q1;
//	String q2;
//	String q3;
//	String q4;
//	
//	MASituationSummaryBean movingAvgSummary;
//	String maBullishPattern;
////	String vpSituation;
//	PvrStockBean priceVolRelationship;
//	
//	String vpStatus;
//	String vpSummary;
//	
////	String weekRhythmSummary;
//	
//	String chartPatternWeekly;
//	
//	String maCrossList;
//	
//	boolean isAboveTwoDaysHigh;
//	boolean isBelowLowestPricePreviousThreeDays;
//	String gapType;
//	boolean isIncreaseVolume;
//	String gapDownStandUpSummary;
//	String priceBreakUpVolPushUpSummary;
//	
//	public RecentHighBeanExportBean() {
//		// TODO Auto-generated constructor stub
//	}
//
////	public String getCode() {
////		return code;
////	}
////
////	public void setCode(String code) {
////		this.code = code;
////	}
//
//
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
//	public String getUpSideRatioToHistoricalHigh() {
//		return upSideRatioToHistoricalHigh;
//	}
//
//	public void setUpSideRatioToHistoricalHigh(String upSideRatioToHistoricalHigh) {
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
//	public String getUpSideRatioToYearHigh() {
//		return upSideRatioToYearHigh;
//	}
//
//	public void setUpSideRatioToYearHigh(String upSideRatioToYearHigh) {
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
//	public String getYtd() {
//		return ytd;
//	}
//
//	public void setYtd(String ytd) {
//		this.ytd = ytd;
//	}
//
//	public String getQ1() {
//		return q1;
//	}
//
//	public void setQ1(String q1) {
//		this.q1 = q1;
//	}
//
//	public String getQ2() {
//		return q2;
//	}
//
//	public void setQ2(String q2) {
//		this.q2 = q2;
//	}
//
//	public String getQ3() {
//		return q3;
//	}
//
//	public void setQ3(String q3) {
//		this.q3 = q3;
//	}
//
//	public String getQ4() {
//		return q4;
//	}
//
//	public void setQ4(String q4) {
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
//	public String getAdjustmentRatioH2L() {
//		return adjustmentRatioH2L;
//	}
//
//	public void setAdjustmentRatioH2L(String adjustmentRatioH2L) {
//		this.adjustmentRatioH2L = adjustmentRatioH2L;
//	}
//
//	public String getCurrentAdjustmentRatio() {
//		return currentAdjustmentRatio;
//	}
//
//	public void setCurrentAdjustmentRatio(String currentAdjustmentRatio) {
//		this.currentAdjustmentRatio = currentAdjustmentRatio;
//	}
//
//	public String getCurrentRecoverRatio() {
//		return currentRecoverRatio;
//	}
//
//	public void setCurrentRecoverRatio(String currentRecoverRatio) {
//		this.currentRecoverRatio = currentRecoverRatio;
//	}
//
//	public String getMaxRecoverRatio() {
//		return maxRecoverRatio;
//	}
//
//	public void setMaxRecoverRatio(String maxRecoverRatio) {
//		this.maxRecoverRatio = maxRecoverRatio;
//	}
//
//	public String getVpSummary() {
//		return vpSummary;
//	}
//
//	public void setVpSummary(String vpSummary) {
//		this.vpSummary = vpSummary;
//	}
//
//	
////	public String getVpStatus() {
////		return vpStatus;
////	}
//
////	public void setVpStatus(String vpStatus) {
////		this.vpStatus = vpStatus;
////	}
//
//	public String getSector() {
//		return sector;
//	}
//
//	public void setSector(String sector) {
//		this.sector = sector;
//	}
//
////	public String getWeekRhythmSummary() {
////		return weekRhythmSummary;
////	}
////
////	public void setWeekRhythmSummary(String weekRhythmSummary) {
////		this.weekRhythmSummary = weekRhythmSummary;
////	}
//
////	public String getVpSituation() {
////		return vpSituation;
////	}
////
////	public void setVpSituation(String vpSituation) {
////		this.vpSituation = vpSituation;
////	}
//
//	public String getChartPatternWeekly() {
//		return chartPatternWeekly;
//	}
//
//	public void setChartPatternWeekly(String charPatternWeekly) {
//		this.chartPatternWeekly = charPatternWeekly;
//	}
//
//	public String getDailyChangePct() {
//		return dailyChangePct;
//	}
//
//	public void setDailyChangePct(String dailyChangePct) {
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
//	public String getMaCrossList() {
//		return maCrossList;
//	}
//
//	public void setMaCrossList(String maCrossList) {
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
//	public boolean isAboveTwoDaysHigh() {
//		return isAboveTwoDaysHigh;
//	}
//
//	public void setAboveTwoDaysHigh(boolean isAboveThreeDaysHigh) {
//		this.isAboveTwoDaysHigh = isAboveThreeDaysHigh;
//	}
//
//	public boolean isBelowLowestPricePreviousThreeDays() {
//		return isBelowLowestPricePreviousThreeDays;
//	}
//
//	public void setBelowLowestPricePreviousThreeDays(boolean isBelowThreeDaysLow) {
//		this.isBelowLowestPricePreviousThreeDays = isBelowThreeDaysLow;
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
//	public String getLowestPrice2CurrentPriceRatio() {
//		return lowestPrice2CurrentPriceRatio;
//	}
//
//	public void setLowestPrice2CurrentPriceRatio(String lowestPrice2CurrentPriceRatio) {
//		this.lowestPrice2CurrentPriceRatio = lowestPrice2CurrentPriceRatio;
//	}
//
////	public String getMtdPct() {
////		return mtdPct;
////	}
////
////	public void setMtdPct(String mtdPct) {
////		this.mtdPct = mtdPct;
////	}
////
////	public String getThreeDaysPct() {
////		return threeDaysPct;
////	}
////
////	public void setThreeDaysPct(String threeDaysPct) {
////		this.threeDaysPct = threeDaysPct;
////	}
//
//	public boolean isIncreaseVolume() {
//		return isIncreaseVolume;
//	}
//
//	public void setIncreaseVolume(boolean isIncreaseVolume) {
//		this.isIncreaseVolume = isIncreaseVolume;
//	}
//
//	public String getGapDownStandUpSummary() {
//		return gapDownStandUpSummary;
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
//		
//
//	
//}
