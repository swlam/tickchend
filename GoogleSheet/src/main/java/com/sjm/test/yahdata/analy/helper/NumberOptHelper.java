package com.sjm.test.yahdata.analy.helper;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class NumberOptHelper {

	public NumberOptHelper() {
		// TODO Auto-generated constructor stub
	}
	
	
	public static double divide(double a, double b) {
		BigDecimal aBig = new BigDecimal(a);
        BigDecimal bBig = new BigDecimal(b);
        
        
        BigDecimal c = aBig.divide(bBig, 5, RoundingMode.HALF_UP);
        return c.doubleValue();
	}

}
