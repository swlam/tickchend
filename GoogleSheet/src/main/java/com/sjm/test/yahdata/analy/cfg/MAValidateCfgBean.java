package com.sjm.test.yahdata.analy.cfg;

import lombok.Data;

@Data
public class MAValidateCfgBean {

	private int maShort;
	private int maLong;
	private boolean isAboveMA;
	private boolean isBelowMA;
	public MAValidateCfgBean(int maShort, int maLong, boolean isAboveMA, boolean isBelowMA) {
		this.maShort = maShort;
		this.maLong = maLong;
		this.isAboveMA = isAboveMA;
		this.isBelowMA = isBelowMA;
	}
	
	public String toString() {
		return "MA("+maShort +", "+maLong+ "), IS_ABV:"+isAboveMA+ ", IS_BLW:"+isBelowMA;
	}
	
}
