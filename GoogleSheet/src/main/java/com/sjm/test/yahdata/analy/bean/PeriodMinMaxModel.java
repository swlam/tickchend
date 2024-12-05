package com.sjm.test.yahdata.analy.bean;

public class PeriodMinMaxModel {

	private double lowest;
	private double highest;
	private String lowestDate;
	private String highestValueDate;
	
	public PeriodMinMaxModel() {
	}

	public double getLowest() {
		return lowest;
	}

	public void setLowest(double lowest) {
		this.lowest = lowest;
	}

	public double getHighest() {
		return highest;
	}

	public void setHighest(double highest) {
		this.highest = highest;
	}

	public String getLowestDate() {
		return lowestDate;
	}

	public void setLowestDate(String lowestDate) {
		this.lowestDate = lowestDate;
	}

	public String getHighestValueDate() {
		return highestValueDate;
	}

	public void setHighestValueDate(String highestValueDate) {
		this.highestValueDate = highestValueDate;
	}
	
	

}
