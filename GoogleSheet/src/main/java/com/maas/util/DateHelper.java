package com.maas.util;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalAdjusters;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DateHelper {

	final static List<String> OFFMARKET_DATE_ARY 
	= Arrays.asList("2021/02/09","2021/02/10","2021/02/11","2021/02/12","2021/02/13","2021/02/14"
			,"2021/02/15","2021/02/16","2021/02/17"); //format is good?
	
	
	public final static String YYYYMMDD = "yyyyMMdd";
	public final static String YYYY_MM_DD = "yyyy-MM-dd";
	public final static String YYYY_MM_DD_HH_mm_ss = "yyyy-MM-dd HH:mm:ss";
	
	public final static DateTimeFormatter formatteryyyyMMdd = DateTimeFormatter.ofPattern(YYYYMMDD);
	public final static DateTimeFormatter formatteryyyy_MM_dd = DateTimeFormatter.ofPattern(YYYY_MM_DD);
	
	
	
	public static LocalDate add(LocalDate date, int workdays) {
	    if (workdays < 1) {
	        return date;
	    }

	    LocalDate result = date;
	    int addedDays = 0;
	    while (addedDays < workdays) {
	        result = result.plusDays(1);
	        if (!
	        	  (result.getDayOfWeek() == DayOfWeek.SATURDAY ||
	              result.getDayOfWeek() == DayOfWeek.SUNDAY  ||
	              isSkipDate(GeneralHelper.dateTimeFormatter.format(result))
	              )
	        	) {
	            ++addedDays;
	        }
	    }

	    return result;
	}
	
	public static String plusDays(String yyyyMMdd, int daysToAdd ) {		
		LocalDate ld = LocalDate.parse(yyyyMMdd, formatteryyyy_MM_dd);
		LocalDate yesterday = ld.plusDays(daysToAdd);
		String formattedDate = yesterday.format(DateTimeFormatter.ofPattern(YYYY_MM_DD));
		return formattedDate;
	}
	
	
	public static String plusDays(String yyyyMMdd, int daysToAdd, String datePattern) {		
		LocalDate ld = LocalDate.parse(yyyyMMdd, DateTimeFormatter.ofPattern(datePattern));
		LocalDate yesterday = ld.plusDays(daysToAdd);
		String formattedDate = yesterday.format(DateTimeFormatter.ofPattern(datePattern));
		return formattedDate;
	}
	
//	public static String plusMonths(String yyyyMMdd, int monthsToAdd) {		
//		LocalDate ld = LocalDate.parse(yyyyMMdd, formatteryyyyMMdd);
//		LocalDate previousMonth = ld.plusMonths(monthsToAdd);
//		String formattedDate = previousMonth.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
//		return formattedDate;
//	}
	
	
	public static String plusMonths(String yyyyMMdd, int monthsToAdd, String datePattern) {		
		LocalDate ld = LocalDate.parse(yyyyMMdd, DateTimeFormatter.ofPattern(datePattern));
		LocalDate previousMonth = ld.plusMonths(monthsToAdd);
		String formattedDate = previousMonth.format(DateTimeFormatter.ofPattern(datePattern));
		return formattedDate;
	}
	
	
	
	public static boolean before(String yyyyMMdd1, String yyyyMMdd2) {
		LocalDate d1 = LocalDate.parse(yyyyMMdd1,formatteryyyy_MM_dd);
		LocalDate d2 = LocalDate.parse(yyyyMMdd2, formatteryyyy_MM_dd);
		return d1.isBefore(d2);
	}
	
    

	
	public static int dayBetween(String dateBefore, String dateAfter) {
//		if("03-12".equalsIgnoreCase(dateBefore) || "03-12".equalsIgnoreCase(dateAfter))
//			System.out.println("PAUSE");
		
		if(dateBefore.length() ==5 && dateBefore.length()==5 ) {
			return calculateDaysBetween(dateBefore, dateAfter);
		}
		return dayBetween(LocalDate.parse(dateBefore, formatteryyyy_MM_dd), LocalDate.parse(dateAfter, formatteryyyy_MM_dd) );
		
	}
	public static int dayBetween(LocalDate dateBefore, LocalDate dateAfter) {
//		LocalDate dateBefore;
//		LocalDate dateAfter;
		
		Long daysBetween = ChronoUnit.DAYS.between(dateBefore, dateAfter);
		return daysBetween.intValue();
	}
	
	public static int countBusinessDaysBetween(String dateBefore, String dateAfter) {
		return countBusinessDaysBetween(LocalDate.parse(dateBefore,formatteryyyy_MM_dd), LocalDate.parse(dateAfter, formatteryyyy_MM_dd) );
	}
	
	
	public static int calculateDaysBetween(String date1, String date2) {
		
		int currentYear = Year.now().getValue();
		String fullDate1 = currentYear + "-" + date1;
        String fullDate2 = currentYear + "-" + date2;
        
        LocalDate localDate1 = LocalDate.parse(fullDate1, DateTimeFormatter.ofPattern(com.maas.util.DateHelper.YYYY_MM_DD));
        LocalDate localDate2 = LocalDate.parse(fullDate2, DateTimeFormatter.ofPattern(com.maas.util.DateHelper.YYYY_MM_DD));
     // 计算两个日期之间的天数差异
        int daysBetween = (int)ChronoUnit.DAYS.between(localDate1, localDate2) +1;
        
        
        // 返回天数差异
        return Math.abs(daysBetween);
	}
	
//	public static void main(String[] args) {
//        String date1 = "08-20";
//        String date2 = "08-23";
//
//        long daysBetween = calculateDaysBetween(date1, date2);
//        System.out.println("Days between " + date1 + " and " + date2 + ": " + daysBetween);
//    }
	
	public static int countBusinessDaysBetween( LocalDate startDate,
	         LocalDate endDate )
	{
	    // Validate method arguments
	    if (startDate == null || endDate == null) {
	        throw new IllegalArgumentException("Invalid method argument(s) to  countBusinessDaysBetween (" + startDate
	            + "," + endDate + ")");
	    }

//	    // Predicate 1: Is a given date is a holiday
//	    Predicate<LocalDate> isHoliday = date -> holidays.isPresent() 
//	            && holidays.get().contains(date);

	    // Predicate 2: Is a given date is a weekday
	    Predicate<LocalDate> isWeekend = date -> date.getDayOfWeek() == DayOfWeek.SATURDAY
	            || date.getDayOfWeek() == DayOfWeek.SUNDAY;

	    // Get all days between two dates
	    long daysBetween = ChronoUnit.DAYS.between(startDate, endDate);

	    // Iterate over stream of all dates and check each day against any weekday or
	    // holiday
	    List<LocalDate> resultList = Stream.iterate(startDate, date -> date.plusDays(1))
	            .limit(daysBetween)
	            .filter(isWeekend.negate())
	            .collect(Collectors.toList());
	    
	    return resultList.size();
	}
	
	
	
	
	public static int yearBetween(LocalDate dateBefore, LocalDate dateAfter) {
		Long yearBetween = ChronoUnit.YEARS.between(dateBefore, dateAfter);
		return yearBetween.intValue();
	}
	
	public static String getNextDate(String initTxnDate) {
		LocalDate localDate = LocalDate.parse(initTxnDate, formatteryyyy_MM_dd);		
		localDate = localDate.plusDays(1);
		return localDate.toString();
	}
	
	
	
	public static boolean isBetween(String startDateStr, String endDateStr, String testDateStr) {
		LocalDate startDate = LocalDate.parse(startDateStr,formatteryyyy_MM_dd);
		LocalDate endDate = LocalDate.parse(endDateStr,formatteryyyy_MM_dd);
		
		LocalDate testDate = LocalDate.parse(testDateStr,formatteryyyy_MM_dd);
		
		boolean b1 =  testDate.isAfter(startDate) && testDate.isBefore(endDate);
		
		boolean b2 =  (testDate.isEqual(startDate) || testDate.equals(endDate));

		return b1 || b2;

	}
	
	public static boolean isBetweenEndDateExclusive(String startDateStr, String endDateStr, String testDateStr) {
		LocalDate startDate = LocalDate.parse(startDateStr,formatteryyyy_MM_dd);
		LocalDate endDate = LocalDate.parse(endDateStr,formatteryyyy_MM_dd);
		
		LocalDate testDate = LocalDate.parse(testDateStr,formatteryyyy_MM_dd);
		
		boolean b1 =  testDate.isAfter(startDate) && testDate.isBefore(endDate);		
		boolean b2 =  testDate.isEqual(startDate);

		return b1 || b2;

	}
	
	public static String getDayNameOfWeek(String yyyy_MM_dd) {
		return LocalDate.parse(yyyy_MM_dd, formatteryyyy_MM_dd).getDayOfWeek().name();
	}
	
	public static final String BEGIN_DATE = "2021/01/18";
	
	
	
	
	public static boolean isSkipDate(String dateString) {
		return OFFMARKET_DATE_ARY.contains(dateString);
		
	}
	
	public static boolean isThanksgivingDate(LocalDate localDate) {
		LocalDate thanksGivingdate = getThanksgivingDate (localDate.getYear() );
		return localDate.isEqual(thanksGivingdate);
	}
	
	public static boolean isThanksgivingDate(String yyyymmdd) {
		LocalDate localDate = LocalDate.parse(yyyymmdd,formatteryyyy_MM_dd);
		LocalDate thanksGivingdate = getThanksgivingDate (localDate.getYear() );
		return localDate.isEqual(thanksGivingdate);
	}
	
	  private static LocalDate getThanksgivingDate(int year) {
	  	  
		  LocalDate date = Year.of(year).atMonth(Month.NOVEMBER).atDay(1);
	        TemporalAdjuster adj = TemporalAdjusters.next(DayOfWeek.THURSDAY);
	        LocalDate nextThur = date.with(adj);
	        LocalDate secondThur = nextThur.with(adj);
	        LocalDate thirdThur = secondThur.with(adj);
	        LocalDate fourthThur = thirdThur.with(adj);
	        
	        if( isThursday(date)) {
	        	return thirdThur;
	        }
	        
	        return fourthThur;
	  }
	  
	  
	  private static boolean isThursday(final LocalDate ld)
	    {
	        DayOfWeek day = DayOfWeek.of(ld.get(ChronoField.DAY_OF_WEEK));
	        return day == DayOfWeek.THURSDAY ;
	    }
	  
	  public static LocalDate convert(String yyyyMM) {
//		  String dateString = "2020-06-03";

		  try {
		      LocalDate date = LocalDate.parse(yyyyMM);
//		      System.out.println(date);
		      return date;
		  } catch (DateTimeParseException ex) {
		      System.out.println("Invalid date format: " + ex.getMessage());
		      return null;
		  }
	  }
	  
	  
	  public static boolean isInSameWeek(LocalDate date1, LocalDate date2) {
	        DayOfWeek startOfWeek = DayOfWeek.SUNDAY;
	        DayOfWeek endOfWeek = DayOfWeek.SATURDAY;

	        // 获取两个日期所属的星期几
	        DayOfWeek dayOfWeek1 = date1.getDayOfWeek();
	        DayOfWeek dayOfWeek2 = date2.getDayOfWeek();

	        // 获取两个日期在一周中的偏移量（以星期日为起始）
	        int offset1 = dayOfWeek1.getValue() - startOfWeek.getValue();
	        int offset2 = dayOfWeek2.getValue() - startOfWeek.getValue();

	        // 获取两个日期所属的周开始日期（星期日）和周结束日期（星期六）
	        LocalDate weekStart1 = date1.minusDays(offset1);
	        LocalDate weekEnd1 = weekStart1.plusDays(endOfWeek.getValue() - startOfWeek.getValue());

	        LocalDate weekStart2 = date2.minusDays(offset2);
	        LocalDate weekEnd2 = weekStart2.plusDays(endOfWeek.getValue() - startOfWeek.getValue());

	        // 判断两个日期是否在同一周内
	        return weekStart1.equals(weekStart2) && weekEnd1.equals(weekEnd2);
	    }

	    public static boolean isInSameMonth(LocalDate date1, LocalDate date2) {
	        int month1 = date1.getMonthValue();
	        int month2 = date2.getMonthValue();
	        int year1 = date1.getYear();
	        int year2 = date2.getYear();

	        return year1 == year2 && month1 == month2;
	    }

	    
	    
	    
	    public static void main(String[] args) {
	        LocalDate date1 = LocalDate.of(2023, 8, 21);
	        LocalDate date2 = LocalDate.of(2023, 8, 14);

	        boolean isInSameWeek = isInSameWeek(date1, date2);
	        boolean isInSameMonth = isInSameMonth(date1, date2);
	        System.out.println(date1);
	        System.out.println(date2);
	        System.out.println("Are the dates in the same week? " + isInSameWeek);
	        System.out.println("Are the dates in the same month? " + isInSameMonth);
	    }
}
