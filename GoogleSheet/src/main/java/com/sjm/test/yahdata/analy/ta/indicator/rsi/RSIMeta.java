package com.sjm.test.yahdata.analy.ta.indicator.rsi;

import com.maas.util.GeneralHelper;

public class RSIMeta {

	
	private String stockCode;
	private String txnDate;
	private int preiodLength;
	
	private double gain = 0.0;
	private double change;
	private double loss = 0.0;
	private double avgGain; 	//UP
	private double avgLoss;		//DN
	private double rsValue;	//rs = UP / DN
	private double rsiValue; // rsiValue = 100 - (100/(1+RS)) == 100 * UP / (UP +DN)
	
	
	public RSIMeta(int length) {
		this.setPreiodLength(length);
	}


	public String getStockCode() {
		return stockCode;
	}


	public void setStockCode(String stockCode) {
		this.stockCode = stockCode;
	}


	public String getTxnDate() {
		return txnDate;
	}


	public void setTxnDate(String txnDate) {
		this.txnDate = txnDate;
	}


	public int getPreiodLength() {
		return preiodLength;
	}


	public void setPreiodLength(int preiodLength) {
		this.preiodLength = preiodLength;
	}


	public double getGain() {
		return gain;
	}


	public void setGain(double gain) {
		this.gain = gain;
	}


	public double getChange() {
		return change;
	}


	public void setChange(double change) {
		this.change = change;
	}


	public double getLoss() {
		return loss;
	}


	public void setLoss(double loss) {
		this.loss = loss;
	}


	public double getAvgGain() {
		return avgGain;
	}


	public void setAvgGain(double avgGain) {
		this.avgGain = avgGain;
	}


	public double getAvgLoss() {
		return avgLoss;
	}


	public void setAvgLoss(double avgLoss) {
		this.avgLoss = avgLoss;
	}


	public double getRsValue() {
		return rsValue;
	}


	public void setRsValue(double rsValue) {
		this.rsValue = rsValue;
	}


	public double getRsiValue() {
		return rsiValue;
	}


	public void setRsiValue(double rsiValue) {
		this.rsiValue = rsiValue;
	}
	
	
	public String toString() {
//		
		return this.getStockCode() +"," +this.getTxnDate() +", RSI("+this.getPreiodLength()+ ")=" + GeneralHelper.to2DecimalPlaces(this.getRsiValue());
	}
	
	
//	IF(I17=0,100,100−(100÷(1+J17)))

}
