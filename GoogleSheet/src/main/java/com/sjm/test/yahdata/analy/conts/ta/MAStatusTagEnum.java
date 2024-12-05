package com.sjm.test.yahdata.analy.conts.ta;

public enum MAStatusTagEnum {
	MA10_ABOVE_MA50 ("MA10高於MA50",	1),	
	BELOW_MA10 ("低於MA10收",	0),
	BELOW_MA50 ("低於MA50收",	0),	
	ABOVE_MA20 ("高於MA20收",	0),
	ABOVE_MA50 ("高於MA50收",	0),
	ABOVE_MA250 ("高於MA250收",	0)
	;
	
	
	private String name;
	private int strength;// weak -5 To 5 strong

	private MAStatusTagEnum(String name, int strength) {
		this.name = name;
		this.strength = strength;

	}

	// 普通方法
//	public static String getName(int index) {
//		for (CandleTagEnum c : CandleTagEnum.values()) {
//			if (c.getStrength() == index) {
//				return c.name;
//			}
//		}
//		return null;
//	}

	// get set 方法
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getStrength() {
		return strength;
	}

	public void setStrength(int strength) {
		this.strength = strength;
	}
	
	public String getDescription() {
		return this.getName() +": "+ this.getStrength();
	}
}