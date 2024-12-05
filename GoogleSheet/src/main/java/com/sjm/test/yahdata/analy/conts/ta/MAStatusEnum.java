package com.sjm.test.yahdata.analy.conts.ta;

public enum MAStatusEnum {
	
	LONG_ARRANGE ("多頭排列"), //default name 
	SHORT_ARRANGE ("空頭排列"), //default name
	
	FULL_LONG_ARRANGE ("全多頭排列(5>10>20>50>200)"	), //5>10>20>50>200	
	SMALL_LONG_ARRANGE ("小多頭排列(5>10>20)"), //5>10>20
	LONG_READY_ARRANGE ("準多頭排列"), 
	MID_TERM_LONG_ARRANGE ("中多頭排列(10>20>50)"), //10>20>50
		
	FULL_SHORT_ARRANGE ("全空頭排列(5<10<20<50<200)"),	//5<10<20<50<200
	SMALL_SHORT_ARRANGE ("小空頭排列(5<10<20)"),	//5<10<20
	MID_TERM_SHORT_ARRANGE ("半空頭排列(10<20<50)"),//10<20<50
	SHORT_READY_ARRANGE ("準空頭排列"), 
	
	ABV_5D ("C>5D"),
	ABV_10D ("C>10D"),
	ABV_20D ("C>20D"),
	ABV_50D ("C>50D"),
	ABV_100D (">100D"),
	ABV_200D ("C>200D"),
	BLW_5D ("C<5D"),
	BLW_10D ("C<10D"),
	BLW_20D ("C<20D"),
	BLW_50D ("C<50D"),
	BLW_100D ("<100D"),
	BLW_200D ("C<200D"),
	
	NA ("N/A");

	private String name;

	private MAStatusEnum(String name) {
		this.name = name;
	}


	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String toString() {
		return this.getName();
	}
}