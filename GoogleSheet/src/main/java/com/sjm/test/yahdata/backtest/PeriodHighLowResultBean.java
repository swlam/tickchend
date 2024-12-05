package com.sjm.test.yahdata.backtest;

import com.maas.util.GeneralHelper;
import com.sjm.test.yahdata.analy.bean.raw.StockBean;
import com.sjm.test.yahdata.analy.probability.bean.AbstractBenchmarkBean;

public class PeriodHighLowResultBean  extends AbstractBenchmarkBean {


	private boolean isMatch = false;

	private double days;
	private StockBean currentStockBean;
	
	private double percentageC2HC;	
	private double percentageC2LC;
	
	private StockBean nextHStockBean;
	private StockBean nextLStockBean;
	private StockBean nextHighCloseStockBean;
	private StockBean nextLowCloseStockBean;
	private StockBean periodEndStockBean;

	
	private int numOfDaysUntilNextH;
	private int numOfDaysUntilNextHClose;
	private int numOfDaysUntilNextL;
	private int numOfDaysUntilNextLClose;
	
	
	
	public PeriodHighLowResultBean() {
	}

	
//	public String toString() {
//		return "Cur:"+currentStockBean.getTxnDate() +" "+ (this.isMatch?"Y":"N") 
//				+ " $"+currentStockBean.getC()
//				+ " Next H Date:" + nextHStockBean.getTxnDate()
//				+ " Next H Close Date:" + nextHighCloseStockBean.getTxnDate()
//				+ " Next L Date:" + nextLStockBean.getTxnDate()
//				+ " Next L Close Date:" + nextLowCloseStockBean.getTxnDate()				
//				
//				+", Until Next H $"+this.getNumOfDaysUntilNextH() +" " + GeneralHelper.to100Percentage(this.getPercentageC2Highest())
//				+", Until Next H Close $"+this.getNumOfDaysUntilNextHClose() +" " + GeneralHelper.to100Percentage(this.getPercentageC2HC())
//				+", Until Next L $"+this.getNumOfDaysUntilNextL() +" "+ GeneralHelper.to100Percentage(this.getPercentageC2Lowest())
//				+", Until Next L Close $"+this.getNumOfDaysUntilNextLClose() +" "+ GeneralHelper.to100Percentage(this.getPercentageC2LC())
//				
//			;
//	}
	
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


	public StockBean getNextHStockBean() {
		return nextHStockBean;
	}


//	public double getPercentageC2H() {
//		return percentageC2H;
//	}
//
//
//	public void setPercentageC2H(double percentageC2H) {
//		this.percentageC2H = percentageC2H;
//	}


	public double getPercentageC2HC() {
		return percentageC2HC;
	}


	public void setPercentageC2HC(double percentageC2HC) {
		this.percentageC2HC = percentageC2HC;
	}


//	public double getPercentageC2L() {
//		return percentageC2L;
//	}
//
//
//	public void setPercentageC2L(double percentageC2L) {
//		this.percentageC2L = percentageC2L;
//	}


	public double getPercentageC2LC() {
		return percentageC2LC;
	}


	public void setPercentageC2LC(double percentageC2LC) {
		this.percentageC2LC = percentageC2LC;
	}


	public void setNextHStockBean(StockBean nextHStockBean) {
		this.nextHStockBean = nextHStockBean;
	}


	public StockBean getNextLStockBean() {
		return nextLStockBean;
	}


	public void setNextLStockBean(StockBean nextLStockBean) {
		this.nextLStockBean = nextLStockBean;
	}


	public StockBean getNextHighCloseStockBean() {
		return nextHighCloseStockBean;
	}


	public void setNextHighCloseStockBean(StockBean nextHighCloseStockBean) {
		this.nextHighCloseStockBean = nextHighCloseStockBean;
	}


	public StockBean getNextLowCloseStockBean() {
		return nextLowCloseStockBean;
	}


	public void setNextLowCloseStockBean(StockBean nextLowCloseStockBean) {
		this.nextLowCloseStockBean = nextLowCloseStockBean;
	}


	public int getNumOfDaysUntilNextH() {
		return numOfDaysUntilNextH;
	}


	public void setNumOfDaysUntilNextH(int numOfDaysUntilNextH) {
		this.numOfDaysUntilNextH = numOfDaysUntilNextH;
	}


	public int getNumOfDaysUntilNextHClose() {
		return numOfDaysUntilNextHClose;
	}


	public void setNumOfDaysUntilNextHClose(int numOfDaysUntilNextHClose) {
		this.numOfDaysUntilNextHClose = numOfDaysUntilNextHClose;
	}


	public int getNumOfDaysUntilNextL() {
		return numOfDaysUntilNextL;
	}


	public void setNumOfDaysUntilNextL(int numOfDaysUntilNextL) {
		this.numOfDaysUntilNextL = numOfDaysUntilNextL;
	}


	public int getNumOfDaysUntilNextLClose() {
		return numOfDaysUntilNextLClose;
	}


	public void setNumOfDaysUntilNextLClose(int numOfDaysUntilNextLClose) {
		this.numOfDaysUntilNextLClose = numOfDaysUntilNextLClose;
	}



	public StockBean getPeriodEndStockBean() {
		return periodEndStockBean;
	}


	public void setPeriodEndStockBean(StockBean periodEndStockBean) {
		this.periodEndStockBean = periodEndStockBean;
	}


	public String print() {
		return this.getYyyy()+":"+this.getCurrentStockBean().getTxnDate() +"-"+this.getPeriodEndStockBean().getTxnDate()
		+"\tc2c:"+ GeneralHelper.toPct(this.getPctC2C()) 
		+" c2h:"+GeneralHelper.toPct(this.getPctC2Highest());
	}


	public double getDays() {
		return days;
	}


	public void setDays(double days) {
		this.days = days;
	}
	
	

//	public int getNumOfDays() {
//		return numOfDays;
//	}
//
//
//	public void setNumOfDays(int numOfDays) {
//		this.numOfDays = numOfDays;
//	}
//	
	
	

}
