package com.sjm.test.yahdata.analy.main;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalAdjusters;

public class ThankgivingDays {

	public ThankgivingDays() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
		int year = 2010;
		
		for(int i=year; i<2023; i++) {
			System.out.println(i + " - " +getThanksgivingDate(i).toString());	
		}
		
	    
	  }

	public static boolean isThanksgivingDate(String yyyymmdd) {
		LocalDate localDate = LocalDate.parse(yyyymmdd);
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

}
