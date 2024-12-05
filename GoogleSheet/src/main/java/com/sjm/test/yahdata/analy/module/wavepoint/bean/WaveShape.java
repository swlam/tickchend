package com.sjm.test.yahdata.analy.module.wavepoint.bean;

import java.util.List;
import java.util.Set;

import com.sjm.test.yahdata.analy.pv.model.PriceVolResult;

import lombok.Data;

@Data
public class WaveShape {
	private List<WavePoint> sortedTopList;
	private List<WavePoint> sortedBotList;
	
//	private int periodDays = 0;

	private String shapeResult;

	private String waveSituation;
//	private PriceVolResult recentVolPriceDivergencyResult;
	public WaveShape() {
		// TODO Auto-generated constructor stub
	}
	
	public WaveShape(int periodDays) {
//		this.periodDays = periodDays;
	}

}
