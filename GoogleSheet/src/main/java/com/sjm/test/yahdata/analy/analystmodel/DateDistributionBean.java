package com.sjm.test.yahdata.analy.analystmodel;

import java.util.ArrayList;
import java.util.List;

public class DateDistributionBean {

	private List<String> dateList;
	private String dateRangeStart;
	private String dateRangeEnd;
	
	boolean isLastDateRange;
	
	public DateDistributionBean() {
		dateList = new ArrayList<String>();
		isLastDateRange = false;
	}

	public List<String> getDateList() {
		return dateList;
	}

	public void setDateList(List<String> dateList) {
		this.dateList = dateList;
	}

	public String getDateRangeStart() {
		return dateRangeStart;
	}

	public void setDateRangeStart(String dateRangeStart) {
		this.dateRangeStart = dateRangeStart;
	}

	public String getDateRangeEnd() {
		return dateRangeEnd;
	}

	public void setDateRangeEnd(String dateRangeEnd) {
		this.dateRangeEnd = dateRangeEnd;
	}

	public boolean isLastDateRange() {
		return isLastDateRange;
	}

	public void setLastDateRange(boolean isLastDateRange) {
		this.isLastDateRange = isLastDateRange;
	}

		

}
