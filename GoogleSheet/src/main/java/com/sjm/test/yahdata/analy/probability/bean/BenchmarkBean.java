package com.sjm.test.yahdata.analy.probability.bean;

import com.maas.util.GeneralHelper;
import com.sjm.test.yahdata.analy.bean.raw.StockBean;

public class BenchmarkBean extends AbstractBenchmarkBean{

	private String startMMdd;
	private String endMMdd; 
	private String mm;
	private String startTxnDate;
	private String endTxnDate;
	private String periodLDate;
	private String periodHDate;
	
//	private StockBean minLStock;
//	private StockBean maxHStock;
	
	public BenchmarkBean(String stockCode) {
		this.setStockCode(stockCode);
	}

	public BenchmarkBean(String stockCode, String yyyy) {
		this.setStockCode(stockCode);
		this.setYyyy(yyyy);
	}
	
	

	public String getStartMMdd() {
		return startMMdd;
	}

	public void setStartMMdd(String startMMdd) {
		this.startMMdd = startMMdd;
	}

	public String getEndMMdd() {
		return endMMdd;
	}

	public void setEndMMdd(String endMMdd) {
		this.endMMdd = endMMdd;
	}
	
	public String getMm() {
		return mm;
	}




	public String getStartTxnDate() {
		return startTxnDate;
	}




	public String getPeriodLDate() {
		return periodLDate;
	}

	public void setPeriodLDate(String periodLDate) {
		this.periodLDate = periodLDate;
	}

	public String getPeriodHDate() {
		return periodHDate;
	}

	public void setPeriodHDate(String periodHDate) {
		this.periodHDate = periodHDate;
	}

	public void setStartTxnDate(String startTxDate) {
		this.startTxnDate = startTxDate;
	}




	public String getEndTxnDate() {
		return endTxnDate;
	}




	public void setEndTxnDate(String endTxDate) {
		this.endTxnDate = endTxDate;
	}




	public void setMm(String mm) {
		this.mm = mm;
	}

	public String toString() {
		return print();
	}
	
	

	public String print() {
		return this.getYyyy()+" "+this.getStockCode()+":"
				+ ((this.getStartMMdd()==null)?"": this.getStartMMdd() +"-"+this.getEndMMdd())
				+"\tc2c:"+ GeneralHelper.toPct(this.getPctC2C()) +", c2h:"+GeneralHelper.toPct(this.getPctC2Highest())
				+", c2l:"+GeneralHelper.toPct(this.getPctC2Lowest())
				;
	}
	
}
