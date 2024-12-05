package com.sjm.test.yahdata.backtest;

import com.maas.util.GeneralHelper;
import com.sjm.test.yahdata.analy.bean.VolumePriceBean;
import com.sjm.test.yahdata.analy.bean.raw.StockBean;

public class PeriodEndResultBean {

	private boolean isMatch = false;
//	private VolumePriceBean currentStockBean;
	private StockBean currentStockBean;
	private double percentageC2C;
	private double percentageC2H;
	private double percentageC2L;
	
	private StockBean periodEndStockBean;
	private StockBean periodHStockBean;
	private StockBean periodLStockBean;
	
	
	public PeriodEndResultBean() {
	}
	
	public String toString() {
		return "Cur: "+currentStockBean.getTxnDate() +" "+ (this.isMatch?"Y":"N") 
				+ ", $"+currentStockBean.getC()
				+ ", Next: " + periodEndStockBean.getTxnDate()
				+ ", $:" + periodEndStockBean.getC()
				+ ", C2C: "+ GeneralHelper.toPct(this.getPercentageC2C())				
			;
	}
	
	public void printInfo() {
		System.out.println(currentStockBean.toString());
	}
	

	public boolean isMatch() {
		return isMatch;
	}

	public void setMatch(boolean isMatch) {
		this.isMatch = isMatch;
	}

	public StockBean getCurrentStockBean() {
		return currentStockBean;
	}

	public void setCurrentStockBean(StockBean currentStockBean) {
		this.currentStockBean = currentStockBean;
	}


	public double getPercentageC2C() {
		return percentageC2C;
	}


	public void setPercentageC2C(double percentageC2C) {
		this.percentageC2C = percentageC2C;
	}


	public StockBean getPeriodEndStockBean() {
		return periodEndStockBean;
	}


	public void setPeriodEndStockBean(StockBean periodEndStockBean) {
		this.periodEndStockBean = periodEndStockBean;
	}

	public double getPercentageC2H() {
		return percentageC2H;
	}

	public void setPercentageC2H(double percentageC2H) {
		this.percentageC2H = percentageC2H;
	}

	public double getPercentageC2L() {
		return percentageC2L;
	}

	public void setPercentageC2L(double percentageC2L) {
		this.percentageC2L = percentageC2L;
	}

	public StockBean getPeriodHStockBean() {
		return periodHStockBean;
	}

	public void setPeriodHStockBean(StockBean periodHStockBean) {
		this.periodHStockBean = periodHStockBean;
	}

	public StockBean getPeriodLStockBean() {
		return periodLStockBean;
	}

	public void setPeriodLStockBean(StockBean periodLStockBean) {
		this.periodLStockBean = periodLStockBean;
	}


	

}
