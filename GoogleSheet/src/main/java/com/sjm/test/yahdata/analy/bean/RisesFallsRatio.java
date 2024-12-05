package com.sjm.test.yahdata.analy.bean;

import lombok.Data;

@Data
public class RisesFallsRatio {

	private Double risesRatio;
	private Double fallsRatio;
	private String end2EndPattern;
	
	public RisesFallsRatio(String end2EndPattern) {
		risesRatio=0.0;
		fallsRatio = 0.0;
		this.end2EndPattern = end2EndPattern;
	}
	
}
