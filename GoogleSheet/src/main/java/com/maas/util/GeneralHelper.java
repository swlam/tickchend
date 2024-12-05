package com.maas.util;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import com.maas.ccass.ats.bean.ActivelyTradedStock;
import com.maas.ccass.model.SouthboundShareholding;
import com.sjm.test.yahdata.analy.cfg.GlobalConfig;
import com.sjm.test.yahdata.analy.conts.Const;


public class GeneralHelper {
	public static final DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
	public static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
	public static final DateTimeFormatter ymdFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
	
	
	public static final SimpleDateFormat yyyyMMdddateFormat = new SimpleDateFormat("yyyy-MM-dd");
	
    
	public static final DecimalFormat df =new DecimalFormat("#,###.#");
	
	public static final DecimalFormat dfPercent =new DecimalFormat("#%");
	
	public static long parseCurrencyText(String text) throws Exception{		
			return df.parse(text).longValue();		
	}
	public static String formatToCurrencyText(int number) throws Exception{		
		return df.format(number);	
	}
	
	public static String formatToCurrencyText(double number) {		
		return df.format(number);	
	}
	
	
	public static int getYearValue(String yyyyMMdd) {
		LocalDate initDate = LocalDate.parse(yyyyMMdd);
		return initDate.getYear();		
	}
	
	
	public static List<String> extractUniqueStockCode(List<SouthboundShareholding> list){
		List<String> stockCodeList = list.stream().filter(  distinctByKey(p -> p.getStockCode()) ).distinct().map(SouthboundShareholding::getStockCode)		
				.collect(Collectors.toList());
		return stockCodeList;
	}
	
	public static List<String> extractUniqueTxDate(List<ActivelyTradedStock> list){
		List<String> stockCodeList = list.stream().filter(  distinctByKey(p -> p.getTxnDate()) ).distinct().map(ActivelyTradedStock::getTxnDate)		
				.collect(Collectors.toList());
		return stockCodeList;
	}
	
	
	public static <T> Predicate<T> distinctByKey(Function<? super T, Object> keyExtractor) 
    {
        Map<Object, Boolean> map = new ConcurrentHashMap<>();
        return t -> map.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }
	
	public static<T> List<T> reverseList(List<T> list)
    {
        return IntStream.range(0, list.size())
                        .map(i -> (list.size() - 1 - i))    // IntStream
                        .mapToObj(list::get)                // Stream<T>
                        .collect(Collectors.toCollection(ArrayList::new));
    }
	
	private static DecimalFormat decimalFormat = new DecimalFormat("0.00");

	public static String  to2DecimalPlaces(double num) {
		return String.format("%.2f", num);
	}
	public static String  toDecimalPlaces(double num, int digits) {
		if(digits <1)
			digits = 1;
		String formatText = "%."+digits+"f";
		return String.format(formatText, num);
	}
	public static String toPct(Double org) {
		if(org == null || Double.isNaN(org))
			return Const.NA;
		if(GlobalConfig.TO_PERCENTAGE_ENABLE)
			return String.valueOf(org);
		 return decimalFormat.format(org *100)+"%";
	}
	
	public static String to100(Double org) {
		if(org == null || Double.isNaN(org))
			return Const.NA;
		
		 return decimalFormat.format(org *100);
	}
	
	public static String to2DigitsWithZeroPadded(int number) {
		String paddedNumber = String.format("%02d", number);
		return paddedNumber;
	}
	public static String format(double org) {
		return String.format("%.2f", org);
	}
	
	public static int txDateToIntNumber(String txDate) {
		if(txDate==null || txDate.isEmpty())
			return 0;
		
		String dTxt = txDate.replaceAll("-", "");
		return Integer.parseInt(dTxt);
	}
	
	public static String formatCalendar(Calendar date) {
//		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String dateStr = yyyyMMdddateFormat.format(date.getTime());
        return dateStr;
	}
	
	
    public static String truncateOrPadString(String input, int length, String padChar) {
        if (input.length() >= length) {
            return input.substring(0, length);
        } else {
            int numPads = length - input.length();
            StringBuilder sb = new StringBuilder(input);
            for (int i = 0; i < numPads; i++) {
                sb.append(padChar);
            }
            return sb.toString();
        }
    }
    
  //e.g. 深證成份指數 (399001.SZ) ==> 深證成份指數
  	public static String extractStockName(String input) {
          String[] parts = input.split("\\s\\(");
          if (parts.length > 0) {
              return parts[0];
          }
          return "";
      }
	
	public static void main(String a[]) {
		String input1 = "ABCDEFGHIJKLMN";
        String input2 = "ABC";
        String input3 = "AB";
        
        String output1 = truncateOrPadString(input1, 8, "#");
        String output2 = truncateOrPadString(input2, 5, "#");
        String output3 = truncateOrPadString(input3, 5, "#");
        
        System.out.println(output1); // Output: ABCDE
        System.out.println(output2); // Output: ABC##
        System.out.println(output3); // Output: AB###
	}
	
	
}
