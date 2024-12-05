package com.sjm.test.yahdata.analy.ta;

import com.sjm.test.yahdata.analy.bean.raw.StockBean;

public class ValidateHelper {

	public ValidateHelper() {
		// TODO Auto-generated constructor stub
	}

	public static boolean isVolumeData(StockBean b) {
		if( b.getVolume() <=0)
			return false;
		return true;
	}
	
	public static boolean isPriceData(StockBean b) {
		if( b.getC()<=0 || b.getL()<=0 || b.getH()<=0 || b.getL()<=0)
			return false;
		return true;
	}
}
