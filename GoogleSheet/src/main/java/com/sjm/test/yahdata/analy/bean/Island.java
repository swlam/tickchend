package com.sjm.test.yahdata.analy.bean;

import lombok.Data;

@Data
public class Island {

	private String islandType;
	private String islandDate;
	private int numOfTxnDates;
	
	public Island() {}
	
//	public Island(String islandType, String islandDate) {
//		this.islandType = islandType;
//		this.islandDate = islandDate;
//	}

	public Island(String islandType, String islandDate, int numOfTxnDates) {
		this.islandType = islandType;
		this.islandDate = islandDate;
		this.numOfTxnDates = numOfTxnDates;
	}
}
