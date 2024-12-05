package com.sjm.test.yahdata.analy.model;

import com.sjm.test.yahdata.analy.bean.raw.StockBean;

import lombok.Data;

/*
 * inherrit to PriceVolumeRelation (pvr)
 * 縮量summary
 * 最縮量的日期(at least 2 dates)
 * 最縮量的日期，最縮量高低價
 * 列出隔日有倍量的極縮量日()
 * 
 * 
 * 高量summary
 * 高量日	現價>高量日燭身頂	現價<高量日燭身底
 */
@Data
public class PriceVolumeAnalysisReportBean {
	private String volumeExtreameShrankDate;
	private String extremelyLowVolumeDate;
	private String extremelyLowVolumeLowAndHigh;
	
	private StockBean maxVolStockBean;
	private StockBean currentStockBean;
	private StockBean minVolStockBean;
	
	public PriceVolumeAnalysisReportBean() {
		// TODO Auto-generated constructor stub
	}

	public boolean isCurrentAboveMinVolStockHigh() {
		return this.getMinVolStockBean().getH() < this.getCurrentStockBean().getC();
	}
	public boolean isCurrentBelowMinVolStockLow() {
		return this.getMinVolStockBean().getL() > this.getCurrentStockBean().getC();
	}
	

	public boolean isCurrentAboveMaxVolStockHigh() {
		return this.getMaxVolStockBean().getH() < this.getCurrentStockBean().getC();
	}
	public boolean isCurrentAboveMaxVolStockBodyTop() {
		return this.getMaxVolStockBean().getBodyTop() < this.getCurrentStockBean().getC();
	}

	public boolean isCurrentBelowMaxVolStockLow() {
		return this.getMaxVolStockBean().getL() > this.getCurrentStockBean().getC();
	}
	
	public boolean isCurrentBelowMaxVolStockBodyBottom() {
		return this.getMaxVolStockBean().getBodyBottom() > this.getCurrentStockBean().getC();
	}
}
