package com.maas.util;

import java.math.BigDecimal;

public class MarketValueDifferenceCalculator {

	
	  public static Double convertToBillian(String value) {
		  Double result;
		  try {
	        if (value.endsWith("T") || value.endsWith("t")) {
	            String numberPart = value.substring(0, value.length() - 1);
	            result = Double.parseDouble(numberPart) * 1000;//new BigDecimal(numberPart).multiply(new BigDecimal("1000000000000"));
	        } else if (value.endsWith("B") || value.endsWith("b")) {
	            String numberPart = value.substring(0, value.length() - 1);
//	            result = new BigDecimal(numberPart).multiply(new BigDecimal("1000000000"));
	            result = Double.parseDouble(numberPart) ;
	        } else {
	            result = Double.parseDouble(value);
	        }
		  }catch(Exception e) {
			  result = 0.0;
		  }
	        return result;
	    }
	  
	  public static void main(String arg[]) {
		  Double a = convertToBillian("10B");
		  Double b = convertToBillian("2.4T");
		  System.out.println(a);
		  System.out.println(b);
	  }
}
