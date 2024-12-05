package com.sjm.test.yahdata.analy.module.wavepoint.bean;

import java.util.List;

import com.maas.util.GeneralHelper;
import com.sjm.test.yahdata.analy.conts.Const;
import com.sjm.test.yahdata.analy.conts.ta.StockTrendStatus;
import com.sjm.test.yahdata.analy.pv.model.PriceVolResult;

import lombok.Data;

@Data
public class WavePointAnalyticalResult {

	public static final String BREAK_DEGREE_FIRM = "堅";
	
	private String code = null;
	private int periodDays = 0;

	private String remark = null; // e.g. bot, top description
	private String reversalPattern = null; //反轉型態(小浪)\t反轉日期(小浪)
	private Double breakPct;
	private Double breakExtreamPct;
	private String breakDegree = Const.SPACE; //BREAK_DEGREE_FIRM, 堅
	private List<WavePoint> topList;
	private List<WavePoint> botList;
	private WavePoint prevTopAndBot;
	private String shape = Const.SPACE;
	
	private StockTrendStatus stockTrendStatus;
	private String stockTrendRemark="";

//	private PriceVolResult recentVolPriceDivergencyResult;
	
	
	public WavePointAnalyticalResult(int periodDays) {
		this.periodDays = periodDays;
	}
	
//	public WavePointAnalyticalResult(String code, String trendWay, String remark) {
//		this.code = code;
//		this.trendWay = trendWay;
//		this.remark = remark;
//		this.reversalPattern = "\t";
//	}
	public WavePointAnalyticalResult(String code, String remark) {
		this.code = code;
//		this.trendWay = trendWay;
		this.remark = remark;
		this.reversalPattern = "\t";
	}
	
	public String toString() {
		if(this.stockTrendStatus==null)
			return code+ " "+ remark;
		
		return code + "("+ this.periodDays+"), " + stockTrendStatus + ", "+ remark;
	}

	public String getTrendDescription() {
		return ((stockTrendStatus==null)?Const.NA:stockTrendStatus+"-"+this.getStockTrendRemark());
	}
	public static String getColumnHeader() {
		return "小浪方向\t突破Pct(小浪)\t小浪型狀\t上一個小浪頂底日";
	}
	public String getColumnData() {
		return  this.getTrendDescription()
				+ "\t" + ((breakPct==null)?Const.NA:GeneralHelper.toPct(breakPct))				
				+ "\t" + ((shape==null)?Const.NA:shape)
				+ "\t" + ((prevTopAndBot==null)?Const.NA:prevTopAndBot.getDate())								
				;
	}
	
	
//	public static String getColumnHeader() {
//		return "方向(小浪)\t突破Pct(小浪)\t突破Max/Min Pct(小浪)\t被突破的上一個頂底日期\t反轉型態(小浪)\t反轉日期(小浪)\tREMARK(小浪)\tShape";
//	}
//	public String getColumnData() {
//		return  ((stockTrendStatus==null)?Const.NA:stockTrendStatus+"-"+this.getStockTrendRemark()) //((trendWay==null)?Const.NA:trendWay)
//				+ "\t" + ((breakPct==null)?Const.NA:GeneralHelper.to100Pct(breakPct))
//				+ "\t" + ((breakExtreamPct==null)?Const.NA:GeneralHelper.to100Pct(breakExtreamPct))
//				+ "\t" + ((prevTopAndBot==null)?Const.NA:prevTopAndBot.getDate())
//				+ "\t" + ((reversalPattern==null)?Const.NA
//				+ "\t" + Const.NA:reversalPattern)
//				+ "\t" + ((remark==null)?Const.NA:remark)
//				+ "\t" + ((shape==null)?Const.NA:shape);
//	}
	
	
}
