package com.sjm.test.yahdata.analy.bean;

import com.maas.util.GeneralHelper;

public class BaseSummaryStatistics {

	private String type;
	private double avg;
	private double median;
	private double max;
	private double min;
	private double min2nd;
	private double sd;
	private long count;
	
	public BaseSummaryStatistics(String type) {
		this.type = type;
	}

	public double getAvg() {
		return avg;
	}

	public void setAvg(double avg) {
		this.avg = avg;
	}

	public double getMedian() {
		return median;
	}

	public void setMedian(double median) {
		this.median = median;
	}

	public double getMax() {
		return max;
	}

	public void setMax(double max) {
		this.max = max;
	}

	public double getMin() {
		return min;
	}

	public void setMin(double min) {
		this.min = min;
	}

	public long getCount() {
		return count;
	}

	public void setCount(long count) {
		this.count = count;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	public double getMin2nd() {
		return min2nd;
	}

	public void setMin2nd(double min2nd) {
		this.min2nd = min2nd;
	}

	
	public double getSd() {
		return sd;
	}

	public void setSd(double sd) {
		this.sd = sd;
	}

	public String getTitle() {
		return "NO TITLE";
	}
	
	public String getDescription() {
		return GeneralHelper.toPct(this.getMedian())+"\t"
				+ GeneralHelper.toPct(this.getAvg())+"\t"
				+ GeneralHelper.toPct(this.getMax())+"\t"
				+ GeneralHelper.toPct(this.getMin())+"\t"
				+ GeneralHelper.toPct(this.getMin2nd())+"\t"
				+ GeneralHelper.format(this.getSd());
	}
	
	

}
