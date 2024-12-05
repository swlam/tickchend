package com.sjm.test.yahdata.analy.ta.rule.ma.situation.bean;

import lombok.Data;

@Data
public class MovingAvgCrossResultBean {

	String crossDate = null;
	String crossDirection = null; // UP / DOWN
	double longMaValue = 0.0;
	double shortMaValue= 0.0;
	int longLength = 0;
	int shortLength = 0;
	
	public MovingAvgCrossResultBean() {
		crossDate = "";
		crossDirection = ""; // UP / DOWN
		longMaValue = 0.0;
		shortMaValue= 0.0;
		longLength = 0;
		shortLength = 0;
	}
	
	public MovingAvgCrossResultBean(String crossDate, String crossDirection, double shortMaValue, double longMaValue, int shortLength, int longLength) {
		this.crossDate = crossDate;
		this.crossDirection = crossDirection; // UP / DOWN
		this.longMaValue = longMaValue;
		this.shortMaValue = shortMaValue;
		this.longLength = longLength;
		this.shortLength = shortLength;
	}

	public String toCurrentAbvMaCross(double closeValue) {
//		String rtn = "";
		if(this.shortMaValue!=0.0 && closeValue>this.shortMaValue)
			return ">";
		else if(this.shortMaValue!=0.0 && closeValue < this.shortMaValue)
			return "<";
		return "";
		
		
	}
}
