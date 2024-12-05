package com.sjm.test.yahdata.analy.probability.bean;

public class MonthlyVolatilityProbabilityBean {

	private String yyyymm; // 01/02/03/04
	private String direction ; //up/down/steady
	private double percentage;
	
	public MonthlyVolatilityProbabilityBean() {
	}

	public String getYYYYMM() {
		return yyyymm;
	}

	public void setYYYYMM(String yyyymm) {
		this.yyyymm = yyyymm;
	}

	public String getDirection() {
		return direction;
	}

	public void setDirection(String direction) {
		this.direction = direction;
	}

	public double getPercentage() {
		return percentage;
	}

	public void setPercentage(double percentage) {
		this.percentage = percentage;
	}
	
	

}
