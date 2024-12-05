package com.sjm.test.yahdata.analy.model;

import lombok.Data;

@Data
public class StatisticsResult {

	private String txnDate;
	private Integer strongCnt;
	private Integer weakCnt;
	private Integer numOfStock;
	private String leadingStock;
	private String tailStock;
	
	private Double ratioDailyPositiveReturn;
	private Double ratioDailyNegativeReturn;
	
	private Long rsi9Abv50Cnt;
	private Long abv20DCnt;
	private Long abv50DCnt;
	private Long abv100DCnt;
	private Long abv200DCnt;
	private Long ma50AbvMA200Cnt;
	private Long ma2AbvMA19Cnt;
	private Double iconicAClose;
	private Double iconicAPct;
	private Double iconicARsi9;
	private Double iconicARsi14;
	
	private Double iconicBClose;
	private Double iconicBPct;
	private Double iconicBRsi9;
	private Double iconicBRsi14;
	
	private Double iconicCClose;
	private Double iconicCPct;
	private Double iconicCRsi9;
	private Double iconicCRsi14;
	
	private Double iconicDClose;
	private Double iconicDPct;
	private Double iconicDRsi9;
	private Double iconicDRsi14;
	
	
	private Double ratioUpHSI;
	private Double ratioUpHSTech;
	private Double ratioUpRedChip;
	
	private Double ratioUpSPY;
	private Double ratioUpQQQ;
	private Double ratioUpDIA;
	private Double ratioUpChinaConcept;
	private Double ratioUpDualCounter;
	public StatisticsResult() {
		// TODO Auto-generated constructor stub
	}

}
