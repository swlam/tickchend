package com.sjm.test.yahdata.analy.module.lowhighdist.bean;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

import com.maas.util.GeneralHelper;

import lombok.Data;
@Data
public class BaseLowHighDateDistrBean {

	private String stockCode;
	private String belongETF;
	private int years;
	private String timeSlot1;
	private String timeSlot2;
	private String timeSlot3;
	private String timeSlot4;
	
	private double occursTimeSlot1;
	private double occursTimeSlot2;
	private double occursTimeSlot3;
	private double occursTimeSlot4;
	
	private List<String> timeSlotDateList1;
	private List<String> timeSlotDateList2;
	private List<String> timeSlotDateList3;
	private List<String> timeSlotDateList4;
	

	
//	public BaseLowHighDateDistrBean() {
//		// TODO Auto-generated constructor stub
//	}
//	
//	public String getStockCode() {
//		return stockCode;
//	}
//
//	public void setStockCode(String stockCode) {
//		this.stockCode = stockCode;
//	}
//
//	public int getYears() {
//		return years;
//	}
//
//	public void setYears(int years) {
//		this.years = years;
//	}
//
//	public double getOccursTimeSlot1() {
//		return occursTimeSlot1;
//	}
//
//	public void setOccursTimeSlot1(double occursTimeFrame1) {
//		this.occursTimeSlot1 = occursTimeFrame1;
//	}
//
//	public double getOccursTimeSlot2() {
//		return occursTimeSlot2;
//	}
//
//	public void setOccursTimeSlot2(double occursTimeFrame2) {
//		this.occursTimeSlot2 = occursTimeFrame2;
//	}
//
//	public double getOccursTimeSlot3() {
//		return occursTimeSlot3;
//	}
//
//	public void setOccursTimeSlot3(double occursTimeFrame3) {
//		this.occursTimeSlot3 = occursTimeFrame3;
//	}
//
//	public double getOccursTimeSlot4() {
//		return occursTimeSlot4;
//	}
//
//	public void setOccursTimeSlot4(double occursTimeFrame4) {
//		this.occursTimeSlot4 = occursTimeFrame4;
//	}
//	
//	
//
//	public List<String> getTimeSlotDateList1() {
//		return timeSlotDateList1;
//	}
//
//	public void setTimeSlotDateList1(List<String> timeFrameDateList1) {
//		this.timeSlotDateList1 = timeFrameDateList1;
//	}
//
//	public List<String> getTimeSlotDateList2() {
//		return timeSlotDateList2;
//	}
//
//	public void setTimeSlotDateList2(List<String> timeFrameDateList2) {
//		this.timeSlotDateList2 = timeFrameDateList2;
//	}
//
//	public List<String> getTimeSlotDateList3() {
//		return timeSlotDateList3;
//	}
//
//	public void setTimeSlotDateList3(List<String> timeFrameDateList3) {
//		this.timeSlotDateList3 = timeFrameDateList3;
//	}
//
//	public List<String> getTimeSlotDateList4() {
//		return timeSlotDateList4;
//	}
//
//	public void setTimeSlotDateList4(List<String> timeFrameDateList4) {
//		this.timeSlotDateList4 = timeFrameDateList4;
//	}
//
//	
//	public String getTimeSlot1() {
//		return timeSlot1;
//	}
//
//	public void setTimeSlot1(String timeFrame1) {
//		this.timeSlot1 = timeFrame1;
//	}
//
//	public String getTimeSlot2() {
//		return timeSlot2;
//	}
//
//	public void setTimeSlot2(String timeFrame2) {
//		this.timeSlot2 = timeFrame2;
//	}
//
//	public String getTimeSlot3() {
//		return timeSlot3;
//	}
//
//	public void setTimeSlot3(String timeFrame3) {
//		this.timeSlot3 = timeFrame3;
//	}
//
//	public String getTimeSlot4() {
//		return timeSlot4;
//	}
//
//	public void setTimeSlot4(String timeFrame4) {
//		this.timeSlot4 = timeFrame4;
//	}
	

	public String toPrintDetailString() {		
		return   stockCode.replace(".HK", "")
				+ "\t" + this.getYears()
				+ "\t" + GeneralHelper.toPct(occursTimeSlot1)
				+ "\t" + GeneralHelper.toPct(occursTimeSlot2) 
				+ "\t" + GeneralHelper.toPct(occursTimeSlot3) 
				+ "\t" + GeneralHelper.toPct(occursTimeSlot4) 
				+ "\t"+this.getTimeSlot1() + "\t " + this.getTimeSlotDateList1()
				+ "\t"+this.getTimeSlot2() + "\t " + this.getTimeSlotDateList2()
				+ "\t"+this.getTimeSlot3() + "\t " + this.getTimeSlotDateList3()
				+ "\t"+this.getTimeSlot4() + "\t " + this.getTimeSlotDateList4()				
				;
	}
	
	
	public String toPrintSummary(){
		List<Double> occursList = Arrays.asList(occursTimeSlot1, occursTimeSlot2, occursTimeSlot3, occursTimeSlot4);
		Double max = occursList.stream().mapToDouble(x->x).max().orElseThrow(NoSuchElementException::new);
		String maxRange = "";
		
		
		String actualSlot = "";
		if(occursTimeSlot1==max) { 
			
			actualSlot = ": "+this.getTimeSlotDateList1().get(0) +"-"+ this.getTimeSlotDateList1().get(this.getTimeSlotDateList1().size()-1);
			maxRange = "["+timeSlot1 + actualSlot+ "]: "+GeneralHelper.toPct(occursTimeSlot1);		
		}
		
		if(occursTimeSlot2==max) {
			actualSlot = ": "+this.getTimeSlotDateList2().get(0) +"-"+ this.getTimeSlotDateList2().get(this.getTimeSlotDateList2().size()-1);
			maxRange = "["+timeSlot2 + actualSlot + "]: "+GeneralHelper.toPct(occursTimeSlot2); 
			}
		if(occursTimeSlot3==max) {
			actualSlot = ": "+this.getTimeSlotDateList3().get(0) +"-"+ this.getTimeSlotDateList3().get(this.getTimeSlotDateList3().size()-1);
			maxRange = "["+timeSlot3 + actualSlot + "]: "+GeneralHelper.toPct(occursTimeSlot3); 
		}
		if(occursTimeSlot4==max) { 
			actualSlot = ": "+this.getTimeSlotDateList4().get(0) +"-"+ this.getTimeSlotDateList4().get(this.getTimeSlotDateList4().size()-1);
			maxRange = "["+timeSlot4 + actualSlot + "]: "+GeneralHelper.toPct(occursTimeSlot4); 
		}
		
		return stockCode.replace(".HK", "") + "\tHigh-Proba.:\t"+maxRange;//+", Low-Probab.:\t"+minRange;
	}
	
	public String toString() {
		return "\tT-1\t"+GeneralHelper.toPct(occursTimeSlot1) 
		+ "\tT-2\t"+GeneralHelper.toPct(occursTimeSlot2) 
		+ "\tT-3\t"+GeneralHelper.toPct(occursTimeSlot3)
		+ "\tT-4\t"+GeneralHelper.toPct(occursTimeSlot4);
	}

}
