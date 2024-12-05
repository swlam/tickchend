package com.sjm.test.yahdata.analy.module.lowhighdist.bean;

import com.maas.util.DateHelper;
import com.maas.util.GeneralHelper;
import com.sjm.test.yahdata.analy.bean.PeriodStatisBean;
import com.sjm.test.yahdata.analy.helper.NumberOptHelper;

public class LowHighDateRangeBean {

	
	String dateOfMinStartingMonth;
	String dateOfMaxStartingMonth;
	String customStartDate;
	String periodHighestPriceDate;
	String periodLowestPriceDate;
	
	double customStartDatePrice;
	double periodHighestPrice;
	double periodLowestPrice;
	
//	double percentage;
	double percentageCustomDateToHDate;
	double percentagePeriodLToH;	
	double percentageCustomDateToLDate;
	
	private PeriodStatisBean periodStatisBean;
	
	public LowHighDateRangeBean() {
	}

	public String getDateOfMinStartingMonth() {
		return dateOfMinStartingMonth;
	}

	public void setDateOfMinStartingMonth(String startDate) {
		this.dateOfMinStartingMonth = startDate;
	}

	public String getPeriodHighestPriceDate() {
		return periodHighestPriceDate;
	}

	public void setPeriodHighestPriceDate(String periodHighestPriceDate) {
		this.periodHighestPriceDate = periodHighestPriceDate;
	}

//	public double getPercentage() {
//		return percentage;
//	}
//
//	public void setPercentage(double percentage) {
//		this.percentage = percentage;
//	}
	
	
	public String getDateOfMaxStartingMonth() {
		return dateOfMaxStartingMonth;
	}

	public void setDateOfMaxStartingMonth(String dateOfMaxStartingMonth) {
		this.dateOfMaxStartingMonth = dateOfMaxStartingMonth;
	}

	
//	public double getDateOfMinStartingMonthPrice() {
//		return dateOfMinStartingMonthPrice;
//	}
//
//	public void setDateOfMinStartingMonthPrice(double dateOfMinStartingMonthPrice) {
//		this.dateOfMinStartingMonthPrice = dateOfMinStartingMonthPrice;
//	}
//
//	public double getDateOfMaxStartingMonthPrice() {
//		return dateOfMaxStartingMonthPrice;
//	}
//
//	public void setDateOfMaxStartingMonthPrice(double dateOfMaxStartingMonthPrice) {
//		this.dateOfMaxStartingMonthPrice = dateOfMaxStartingMonthPrice;
//	}

	public double getPeriodHighestPrice() {
		return periodHighestPrice;
	}

	public void setPeriodHighestPrice(double endDatePrice) {
		this.periodHighestPrice = endDatePrice;
	}
	
	public double getCustomStartDatePrice() {
		return customStartDatePrice;
	}

	public void setCustomStartDatePrice(double customStartDatePrice) {
		this.customStartDatePrice = customStartDatePrice;
	}

	public double getPercentageCustomDateToHDate() {
		return percentageCustomDateToHDate;
	}

	public void setPercentageCustomDateToHDate(double percentageCustom) {
		this.percentageCustomDateToHDate = percentageCustom;
	}

	public String getCustomStartDate() {
		return customStartDate;
	}

	public void setCustomStartDate(String customStartDate) {
		this.customStartDate = customStartDate;
	}

	public String getPeriodLowestPriceDate() {
		return periodLowestPriceDate;
	}

	public void setPeriodLowestPriceDate(String periodLowDate) {
		this.periodLowestPriceDate = periodLowDate;
	}

	public double getPeriodLowestPrice() {
		return periodLowestPrice;
	}

	public void setPeriodLowestPrice(double periodLowPrice) {
		this.periodLowestPrice = periodLowPrice;
	}

	public double getPercentagePeriodLToH() {
		return percentagePeriodLToH;
	}

	public void setPercentagePeriodLToH(double percentagePeriodLToH) {
		this.percentagePeriodLToH = percentagePeriodLToH;
	}

	
	
//	public String toString() {
//		return dateOfMinStartingMonth +"("+ dateOfMinStartingMonthPrice+ ") / " + dateOfMaxStartingMonth + "("+dateOfMaxStartingMonthPrice + ") \t- "+ periodHighestPriceDate+"("+periodHighestPrice + ") "+percentage*100+"%";
//	}
	
	
//	public String print() {
//		int minStartingMonthInt = GeneralHelper.txDateToIntNumber(dateOfMinStartingMonth);
//		int maxStartingMonthInt = GeneralHelper.txDateToIntNumber(dateOfMaxStartingMonth);
//		String symbol = "<";
//		if(minStartingMonthInt> maxStartingMonthInt)
//			symbol = ">";
//		return dateOfMinStartingMonth +"\t("+ dateOfMinStartingMonthPrice+ ")  \t"+symbol + "\t"+ dateOfMaxStartingMonth + "\t("+dateOfMaxStartingMonthPrice + ")\tto\t"+ periodHighestPriceDate+"\t("+periodHighestPrice + ") \t"+percentage*100+"%";
//	}
	
	
	public double getPercentageCustomDateToLDate() {
		return percentageCustomDateToLDate;
	}

	public void setPercentageCustomDateToLDate(double percentagePeriodCustToH) {
		this.percentageCustomDateToLDate = percentagePeriodCustToH;
	}

	public String printCustomDate2MaxH() {
		
		boolean isPeriodLowDateIsBefore = DateHelper.before(this.getPeriodLowestPriceDate(), this.getPeriodHighestPriceDate());
		
		String pattern = ""+ (isPeriodLowDateIsBefore?"L->H":"H->L");
				
		if(isPeriodLowDateIsBefore) {
			if(this.getCustomStartDatePrice()<this.getPeriodLowestPrice()) {
				pattern = "(L)->"+pattern;
			}else {
				pattern = "(H)->"+pattern;
			}
		}else {
			if(this.getCustomStartDatePrice()<this.getPeriodHighestPrice()) {
				pattern = "(L)->"+pattern;
			}else {
				pattern = "(H)->"+pattern;
			}	
		}
		
		
		return this.getCustomStartDate() //+ "\t"+dayBetween(customStartDate, endDate)				
				+ "\t"+ this.getPeriodLowestPriceDate() //+ "\t"+this.getPeriodLowPrice()				
				+ "\t"+ this.getPeriodHighestPriceDate() //+ "\t"+endDatePrice				
				+ "\t"+ GeneralHelper.toPct(this.getPercentageCustomDateToHDate())				
				+ "\t"+ GeneralHelper.toPct(this.getPercentagePeriodLToH())
				+ "\t"+ GeneralHelper.toPct(this.getPercentageCustomDateToLDate()) // need calc
				+ "\t"+ pattern
				;
	}
	
	
//	public String printMaxL2MaxH() {
//		return dateOfMaxStartingMonth + "\t"+dateOfMaxStartingMonthPrice + "\t"+ periodHighestPriceDate+"\t"+periodHighestPrice
//				+ "\t"+DateHelper.dayBetween(dateOfMaxStartingMonth, periodHighestPriceDate)
//				+ "\t"+GeneralHelper.to100Percentage(percentage);
//	}
	
	
	public PeriodStatisBean getPeriodStatisBean() {
		return periodStatisBean;
	}

	public void setPeriodStatisBean(PeriodStatisBean periodStatisBean) {
		this.periodStatisBean = periodStatisBean;
	}
	
	
	
	public static void main(String arg[]) {
		
//				double a1 = (85.71875 - 91.3125);
//		        BigDecimal a = new BigDecimal(a1);
//		        BigDecimal b = new BigDecimal(91.3125);
//		        BigDecimal c = a.divide(b, 5, RoundingMode.HALF_UP);
//		        System.out.println(a + "/" + b + " = " + c);
		        
		        
		        double a = 85.71875;
		        double b = 91.3125;
		        
		        double dif = a-b;
		        
		        double result = dif / b;
		        double result2 = NumberOptHelper.divide(dif, b);
		        
		        
		        System.out.println(a + "/" + b + " = " + result);
		        
		        System.out.println(a + "/" + b + " = " + result2);
		   
		
	}

	

}
