package com.sjm.test.yahdata.analy.bean;

import com.sjm.test.yahdata.analy.bean.raw.StockBean;

import lombok.Data;
@Data
public class PvrStockBean  {

	private StockBean maxVolStockBean;
	private StockBean currentStockBean;
	private StockBean minVolStockBean;
	private StockBean maxBodyChgPctStock;
	
	private String maxVolDesc = "";
	private boolean isMonoPeakVolStockBean = false;
	private double volumeHighRatio;
	private double volumeLowRatio;

	private String maxVolStockInRange = "";// 處於區間的哪個位置
//	private String volumeExtreameShrankDate;
	
	private String doubleVolumeDateMsg = "";
	private String numOfDoubleVolumeDate = "";
	
	private String shrinkageVolumeDateMsg = "";
	private String numOfShrinkageVolumeDate = "";
	
	private String crossedMaxVolHighLow = "";
	public PvrStockBean() {		
	}
	
	public PvrStockBean( StockBean currentStockBean, StockBean maxVolStockBean, String maxVolDesc, StockBean minVolStockBean, 
			StockBean maxBodyChgPctStock, double volumeHighRatio, double volumeLowRatio, boolean isMonoPeakVolStockBean) 
	{
		this.maxVolStockBean = maxVolStockBean;
		this.currentStockBean = currentStockBean;
		this.minVolStockBean = minVolStockBean;
		this.maxVolDesc = maxVolDesc;
		this.maxBodyChgPctStock = maxBodyChgPctStock;
		this.volumeHighRatio = volumeHighRatio;
		this.volumeLowRatio = volumeLowRatio;
		this.isMonoPeakVolStockBean = isMonoPeakVolStockBean;
	}

	public boolean isCurrentAboveMinVolStockHigh() {
		if( this.getMinVolStockBean()==null)
			return false;
		return this.getMinVolStockBean().getH() < this.getCurrentStockBean().getC();
	}
	public boolean isCurrentBelowMinVolStockLow() {
		if( this.getMinVolStockBean()==null)
			return false;
		return this.getMinVolStockBean().getL() > this.getCurrentStockBean().getC();
	}

	public boolean isCurrentAboveMaxVolStockHigh() {
		if( this.getMaxVolStockBean()==null)
			return false;
		return this.getMaxVolStockBean().getH() < this.getCurrentStockBean().getC();
	}
	public boolean isCurrentAboveMaxVolStockBodyTop() {
		if( this.getMaxVolStockBean()==null)
			return false;
		return this.getMaxVolStockBean().getBodyTop() < this.getCurrentStockBean().getC();
	}

	public boolean isCurrentBelowMaxVolStockLow() {
		if( this.getMaxVolStockBean()==null)
			return false;
		return this.getMaxVolStockBean().getL() > this.getCurrentStockBean().getC();
	}
	
	public boolean isCurrentBelowMaxVolStockBodyBottom() {
		if( this.getMaxVolStockBean()==null)
			return false;
		return this.getMaxVolStockBean().getBodyBottom() > this.getCurrentStockBean().getC();
	}
	

	
//	public String toString() {
//		return "Current:"+this.getCurrentStockBean().getTxnDate() + ", MaxVol:"+this.getMaxVolStockBean().getTxnDate()+ ", MinVol:"+this.getMinVolStockBean().getTxnDate();
//	}

}
