package com.sjm.test.yahdata.analy.ta.pattern;

import com.maas.util.GeneralHelper;
import com.sjm.test.yahdata.analy.bean.PvrStockBean;
import com.sjm.test.yahdata.analy.bean.raw.StockBean;
import com.sjm.test.yahdata.analy.conts.Const;
import com.sjm.test.yahdata.analy.helper.CFGHelper;
import com.sjm.test.yahdata.analy.recenthigh.BaseRecentHighBean;

import lombok.Data;
@Data
public class WPatternInfo extends BaseMWShapeInfo{
	StockBean leftLStockBean;
	StockBean rightLStockBean;
//	StockBean criticalPointBtwn; // highest point
	double downTopDistance;
	double downTopDistanceRatio;
	double maxUpBreakRatio;
	double currPriceToHighPointBtwnRatio;
	
	public WPatternInfo(StockBean currentStockBean, String type) {
		this.currentStockBean = currentStockBean ; 
		this.type = type;
		this.setStockCode(currentStockBean.getStockCode());
	}

	public WPatternInfo(String type) {
		this.type = type;
	}

//	public static String toHeader() {
//		return "CODE\tName\tSector\tTYPE\t現在日期\t現價"
//				+ "\tEst.成交額(M)\tDaily Change %\t3Days(O2C) %\tMTD(O2C) %"
//				+ "\tLeft-L-Date\tLow($)\tRight-L-Date\tHigh($)\t相距日子(LtoR)\t頸線日期\t頸線High($))"
//				+ "\t高量日\t高量日-H\t高量日-L\t現價>高量日-H\t現價<高量日-L"
//				+ "\tDownTopDistance\tDownTopRatio\t近期高位離頸線的距離\t現價離頸線的距離";
//		
//	}
//	
//	public String toMessage() {
//		double estPrice = this.getCurrentStockBean().getL() + (this.getCurrentStockBean().getH()-this.getCurrentStockBean().getL());
//		String txAmt = GeneralHelper.to2DecimalPlaces((estPrice*this.getCurrentStockBean().getVolume())/1000000);
//		
//		
//		return this.getCurrentStockBean().getStockCode() + "\t"
//				+ (CFGHelper.getStockProfileMap().get(this.getCurrentStockBean().getStockCode())==null?"NA":CFGHelper.getStockProfileMap().get(this.getCurrentStockBean().getStockCode()).getName())+ "\t"
//				+ (CFGHelper.getStockProfileMap().get(this.getCurrentStockBean().getStockCode())==null?"NA":CFGHelper.getStockProfileMap().get(this.getCurrentStockBean().getStockCode()).getSector())+ "\t"
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
//				+ this.getLeftLStockBean().getTxnDate() + "\t" + GeneralHelper.format(this.getLeftLStockBean().getL()) + "\t"
//				+ this.getRightLStockBean().getTxnDate() + "\t" + GeneralHelper.format(this.getRightLStockBean().getL()) + "\t"
//				+ this.getCalendarDayBetweenLowPoint()+ "\t"
//				+ this.getHighPointBtwn().getTxnDate() + "\t" + GeneralHelper.format(this.getHighPointBtwn().getH()) + "\t" //頸線 Neckline
//				
//				+ (this.getPriceVolRelationship()==null?Const.NA:this.getPriceVolRelationship().getMaxVolStockBean().getTxnDate()) 
//				+ "\t"
//				+ (this.getPriceVolRelationship()==null?Const.NA:this.getPriceVolRelationship().getMaxVolStockBean().getH()) 
//				+ "\t"
//				+ (this.getPriceVolRelationship()==null?Const.NA:this.getPriceVolRelationship().getMaxVolStockBean().getL()) 
//				+ "\t"
//				+ (this.getPriceVolRelationship()==null?"NA":this.getPriceVolRelationship().isCurrentAboveMaxVolStockHigh()) + "\t"
//				+ (this.getPriceVolRelationship()==null?"NA":this.getPriceVolRelationship().isCurrentBelowMaxVolStockLow()) + "\t"
//														
//				+ GeneralHelper.format(this.getDownTopDistance()) + "\t"
//				+ GeneralHelper.to100Pct(this.getDownTopDistanceRatio()) + "\t"
//				+ GeneralHelper.to100Pct(this.getMaxUpBreakRatio()) +"\t"
//				+ GeneralHelper.to100Pct(this.getCurrPriceToHighPointBtwnRatio());
//	}
	
	public String toString() {
		return this.getCurrentStockBean().toString();
	}
	
}
