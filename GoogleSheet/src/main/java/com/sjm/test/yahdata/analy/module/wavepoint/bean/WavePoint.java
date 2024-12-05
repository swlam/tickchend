package com.sjm.test.yahdata.analy.module.wavepoint.bean;

import com.sjm.test.yahdata.analy.bean.raw.StockBean;
import com.sjm.test.yahdata.analy.conts.type.IndicatorType;
import com.sjm.test.yahdata.analy.conts.type.WaveType;

import lombok.Data;

@Data
public class WavePoint {

	private IndicatorType indicatorType;
	private WaveType type; 
	private StockBean stockBean;
	private String date;
	private Integer dateInt;

	private double h;
	private double l;
	private double rsi9;
	
	public WavePoint(IndicatorType indicatorType, WaveType waveType, StockBean stockBean) {
		this(waveType, stockBean);
		this.indicatorType = indicatorType;
		
	}
	
	public WavePoint(WaveType waveType, StockBean stockBean) {
		this.indicatorType = IndicatorType.STOCK_PRICE;
		this.type = waveType;

		this.stockBean = stockBean;
		this.setH(stockBean.getH());
		this.setL(stockBean.getL());
		this.setRsi9(stockBean.getRsi9());
		if(stockBean!=null) {
			date = this.stockBean.getTxnDate();
			dateInt = this.stockBean.getTxnDateInt();
		}
	}
	
	
	public String toString() {
		return String.format("%s, %s, %f, %f",type, this.date, this.h, this.l);
	}



}