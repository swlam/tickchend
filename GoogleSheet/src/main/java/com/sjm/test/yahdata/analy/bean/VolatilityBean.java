package com.sjm.test.yahdata.analy.bean;

public class VolatilityBean {

	String date;
	Double vLow2Hight;
	Double vHight2Low;
	Double actualHLDifference;
	
	public VolatilityBean() {
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}
	

	public Double getActualHLDifference() {
		return actualHLDifference;
	}

	public void setActualHLDifference(Double highest) {
		this.actualHLDifference = highest;
	}

	

	public String toString() {
		return date + ", Volatility(L->H: "+this.getvLow2Hight()+", H->L:" +this.getvHight2Low()+")";
	}

	public Double getvLow2Hight() {
		return vLow2Hight;
	}

	public void setvLow2Hight(Double vLow2Hight) {
		this.vLow2Hight = vLow2Hight;
	}

	public Double getvHight2Low() {
		return vHight2Low;
	}

	public void setvHight2Low(Double vHight2Low) {
		this.vHight2Low = vHight2Low;
	}


	
}
