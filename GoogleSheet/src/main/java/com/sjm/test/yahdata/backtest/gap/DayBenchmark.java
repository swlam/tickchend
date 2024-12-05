package com.sjm.test.yahdata.backtest.gap;

import lombok.Data;

@Data
public class DayBenchmark {
	private int dayNumber;
	private double upRatio;
	private double avgUpPct;
	private double avgDownPct;
	
	public DayBenchmark() {
	}

	public DayBenchmark(int dayNumber, double upRatio, double avgUpPct, double avgDownPct) {
		this.dayNumber = dayNumber;
		this.upRatio = upRatio;
		this.avgUpPct = avgUpPct;
		this.avgDownPct = avgDownPct;
	}
	
	
}
