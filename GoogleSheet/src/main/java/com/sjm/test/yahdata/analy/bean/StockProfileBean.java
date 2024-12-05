package com.sjm.test.yahdata.analy.bean;

import lombok.Data;

@Data
public class StockProfileBean {

	private String code= "";
	private String name= "";
	private String marketCap= "";
	private String peRatio= "";
	
	private String earningsDate= "";
	private String exDivDate= "";
	
	private String sector = "";
	private String industry = "";
	
}
