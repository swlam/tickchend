package com.maas.ccass.ats.bean;

import com.maas.util.GeneralHelper;
import com.maas.util.NumberUtils;

import lombok.Data;
@Data
public class ActivelyTradedStockSummary {

	String stockCode;
//	String stockName;
	int numOfDays;
	long netAmount;
	int numOfBoughtDays;
	int numOfSoldDays;
	String everyDaySummary;
	String gradeSummary;
	
	public ActivelyTradedStockSummary() {
		// TODO Auto-generated constructor stub
	}
	public ActivelyTradedStockSummary(String stockCode, long netAmount, int numOfDays) {
		this.stockCode = stockCode;
		this.netAmount = netAmount;
		this.numOfDays = numOfDays;
	}

	
	
	public String toString() {
		return this.stockCode  + ": "+GeneralHelper.formatToCurrencyText(this.getNetAmount());
	}
	public String toStringPrint() {
		return this.stockCode + ": "+(double)this.getNetAmount() /1000000.0;//NumberUtils.amountConversion(this.getNetAmount());
	}
	
	public void printBoughtSold() {
		String a =this.stockCode 
				+ "\t"+((double)this.getNetAmount()/1000000.0 )
				+ "\t"+NumberUtils.amountConversion(this.getNetAmount());
		String b = "\t"+this.getNumOfBoughtDays();
		String s = "\t"+this.getNumOfSoldDays();
		String sumy = "\t"+this.getEveryDaySummary();
		String gsumy = "\t"+this.getGradeSummary();
		System.out.println(a + b + s + sumy +gsumy);
	}
}
