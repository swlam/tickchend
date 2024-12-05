package com.sjm.test.yahdata.analy.bollinger;

import lombok.Data;

@Data
public class BollingerBand {

	private String txnDate;
	private double upper;
	private double middle;
	private double lower;
	
	public BollingerBand() {}
	
	public BollingerBand(String txnDate, double upper, double middle, double lower) {
		this.txnDate = txnDate;
		this.upper = upper;
		this.middle = middle;
		this.lower = lower;
	}
	
	public String toString() {
		return this.txnDate +", U:"+this.upper +", M:"+ this.middle +",L:"+ this.lower;
	}

}
