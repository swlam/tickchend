package com.sjm.test.yahdata.analy.ta.pattern;

import com.sjm.test.yahdata.analy.bean.raw.StockBean;

import lombok.Data;
@Data
public class MPatternInfo extends BaseMWShapeInfo{
	StockBean leftHStockBean;
	StockBean rightHStockBean;
//	StockBean criticalPointBtwn; // lowPointBtwn point
	double topDownDistance;
	double topDownDistanceRatio;
	double maxDrawdownRatio;
	double currPriceToHighPointBtwnRatio;
	
	public MPatternInfo(StockBean currentStockBean, String type) {
		this.currentStockBean = currentStockBean ; 
		this.type = type;
		this.setStockCode(currentStockBean.getStockCode());
	}
	
	public MPatternInfo(String type) {
		this.type = type;
	}
	

//	public static String toHeader() {
//		return "CODE\tName\tSector\tTYPE\t現在日期\t現價"
//				+ "\tEst.成交額(M)\tDaily Change %\t3Days(O2C) %\tMTD(O2C) %"
//				+ "\tLeft-H-Date\tHigh($)\tRight-H-Date\tHigh($)\t相距日子(LtoR)\t頸線支點日期\t頸線Low($)"
//				+ "\t高量日\t高量日-H\t高量日-L\t現價>高量日-H\t現價<高量日-L"
//				+ "\tTopDownDistance\tTopDownRatio\tMaxDrawdownRatio From 頸線\t現價離頸線的距離";
//	}
//	public String toMessage() {
//		double estPrice = this.getCurrentStockBean().getL() + (this.getCurrentStockBean().getH()-this.getCurrentStockBean().getL());
//		String txAmt = GeneralHelper.to2DecimalPlaces((estPrice*this.getCurrentStockBean().getVolume())/1000000);
//		
//		return this.getCurrentStockBean().getStockCode() + "\t"
//				+ (CFGHelper.getStockProfileMap().get(this.getCurrentStockBean().getStockCode())==null?Const.NA:CFGHelper.getStockProfileMap().get(this.getCurrentStockBean().getStockCode()).getName())+ "\t"
//				+ (CFGHelper.getStockProfileMap().get(this.getCurrentStockBean().getStockCode())==null?Const.NA:CFGHelper.getStockProfileMap().get(this.getCurrentStockBean().getStockCode()).getSector())+ "\t"
//				+ this.getType() + "\t"
//				+ this.getCurrentStockBean().getTxnDate() + "\t" + GeneralHelper.format(this.getCurrentStockBean().getC()) + "\t"
//				
//				
//				+ txAmt+ "\t"
//				+ (this.getDailyChangePct()==null?Const.NA:GeneralHelper.to100Pct(this.getDailyChangePct()))+ "\t"
//				+ (this.getThreeDaysChangePct()==null?Const.NA:GeneralHelper.to100Pct(this.getThreeDaysChangePct()))+ "\t"
//				+ (this.getMtdChangePct()==null?Const.NA:GeneralHelper.to100Pct(this.getMtdChangePct()))+ "\t"
//				 
//				
//				+ this.getLeftHStockBean().getTxnDate() + "\t" + GeneralHelper.format(this.getLeftHStockBean().getH()) + "\t"
//				+ this.getRightHStockBean().getTxnDate() + "\t" + GeneralHelper.format(this.getRightHStockBean().getH()) + "\t"
//				+ this.getCalendarDayBetweenHighPoint()+ "\t"
//				+ this.getLowPointBtwn().getTxnDate() + "\t" + GeneralHelper.format(this.getLowPointBtwn().getL()) + "\t" //頸線 Neckline
//				
////				
////				+ (this.getPriceVolRelationship()==null?"NA":this.getPriceVolRelationship().getMaxVolStockBean().getTxnDate())
////				+ "\t"
////				+ (this.getPriceVolRelationship()==null?"NA":this.getPriceVolRelationship().getMaxVolStockBean().getH())
////				+ "\t"
////				+ (this.getPriceVolRelationship()==null?"NA":this.getPriceVolRelationship().getMaxVolStockBean().getL())
////				+ "\t"
////				+ (this.getPriceVolRelationship()==null?"NA":this.getPriceVolRelationship().isCurrentAboveMaxVolStockHigh())+ "\t"
////				+ (this.getPriceVolRelationship()==null?"NA":this.getPriceVolRelationship().isCurrentBelowMaxVolStockLow())+ "\t"
//				
//				
//				
//				+ GeneralHelper.format(this.getTopDownDistance()) + "\t"
//				+ GeneralHelper.to100Pct(this.getTopDownDistanceRatio()) + "\t"
//				+ GeneralHelper.to100Pct(this.getMaxDrawdownRatio()) +"\t"
//				+ GeneralHelper.to100Pct(this.getCurrPriceToHighPointBtwnRatio());
//	}
	
	public String toString() {
		return this.getCurrentStockBean().toString();
	}
	
}
