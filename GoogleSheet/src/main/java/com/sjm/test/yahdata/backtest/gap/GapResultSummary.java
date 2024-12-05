package com.sjm.test.yahdata.backtest.gap;

import lombok.Data;

@Data
public class GapResultSummary {

	String periodDays;
	int noOfStartFilled;
	int noOfCompletelyFilled;
	
	public GapResultSummary() {
		// TODO Auto-generated constructor stub
	}

}
