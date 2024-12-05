package com.sjm.test.yahdata.analy.model;

import com.sjm.test.yahdata.analy.bean.raw.StockBean;

import lombok.Data;

@Data
public class DownBreakAndStandUpBean {

	StockBean leftLowStockBean;
	StockBean rightLowStockBean;
	StockBean lowestStockBean;
	boolean isQualified ;
	public DownBreakAndStandUpBean(StockBean lowestStockBean, StockBean leftLowStockBean, StockBean rightLowStockBean, boolean isQualified) {
		this.leftLowStockBean = leftLowStockBean;
		this.rightLowStockBean = rightLowStockBean;
		this.lowestStockBean = lowestStockBean;
		this.isQualified = isQualified;
	}
	
	public String toString() {
		return lowestStockBean.getStockCode() + ":" + isQualified + ", Low:" + lowestStockBean.getTxnDate()+ ", LF:" + leftLowStockBean.getTxnDate() +", RH:"+ rightLowStockBean.getTxnDate();
	}

}
