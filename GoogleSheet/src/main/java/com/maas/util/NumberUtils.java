package com.maas.util;

public class NumberUtils {
	
//    private static final Double MILLION = 10000.0;
//    private static final Double MILLIONS = 1000000.0;
//    private static final Double HUNDRED_MILLION = 100000000.0;
//    private static final String MILLION_UNIT = "萬";
//    private static final String HUNDRED_MILLION_UNIT = "億";
    
    
    public static String amountConversion(long amount) {
        return amountConversion((double)amount);
    }
    
    public static String amountConversion(double amount) {
        String unit = "億";
        StringBuilder result = new StringBuilder();
        boolean isNegative = false;

        if (amount < 0) {
            isNegative = true;
            amount = -amount;
        }

        double amountInYi = (double) amount / 1_0000_0000;

        result.append(String.format("%.2f", amountInYi));
        result.append(unit);

        if (isNegative) {
            result.insert(0, "-");
        }

        return result.toString();
    }
    
//    public static String amountConversionMillion(long amount) {
//        
//        return amountConversionMillion((double)amount);
//        
//    }
    
//    public static String amountConversionMillion(double amount) {
//        String unit = "M";
//        StringBuilder result = new StringBuilder();
//        boolean isNegative = false;
//
//        if (amount < 0) {
//            isNegative = true;
//            amount = -amount;
//        }
//
//        double amountInYi = (double) amount / 100_0000;
//
//        result.append(String.format("%.2f", amountInYi));
//        result.append(unit);
//
//        if (isNegative) {
//            result.insert(0, "-");
//        }
//
//        return result.toString();
//    }
//    
//    public static double conversionTenThousand(long amount) {
//    	return conversionTenThousand((double)amount);
//    }
//    
//    public static double conversionTenThousand(double amount) {
//        String unit = "";
//        StringBuilder result = new StringBuilder();
//        boolean isNegative = false;
//
//        if (amount < 0) {
//            isNegative = true;
//            amount = -amount;
//        }
//
//        double amountInYi = (double) amount / 10000;
//        return amountInYi;
////        result.append(String.format("%.2f", amountInYi));
////        result.append(unit);
////
////        if (isNegative) {
////            result.insert(0, "-");
////        }
////
////        return result.toString();
//    }
}
