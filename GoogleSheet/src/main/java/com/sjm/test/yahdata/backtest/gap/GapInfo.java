package com.sjm.test.yahdata.backtest.gap;

import lombok.Data;

@Data
public class GapInfo {
	private String gapDate;
	private String fillStartDate;
	private String completelyFilledDate;
	private int noOfTxnDaysToStartFill;
	private int noOfTxnDaysToCompletelyFilled;
	private boolean isUP;
	private boolean isCompletelyFilled;
	private double dayChgPct;
	
	public GapInfo() {
		// TODO Auto-generated constructor stub
	}
	public GapInfo(String date) {
		gapDate = date;
	}
	public String toString() {
		return "gapDate:" + gapDate +", fillStartDate:"+ fillStartDate +" , completelyFilledDate:"+ completelyFilledDate +", Days to Fill: "+ noOfTxnDaysToStartFill +", Days to Fill complete: "+ noOfTxnDaysToCompletelyFilled;
	}

}
