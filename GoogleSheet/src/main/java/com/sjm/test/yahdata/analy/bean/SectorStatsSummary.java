package com.sjm.test.yahdata.analy.bean;

import com.maas.util.GeneralHelper;

import lombok.Data;
@Data
public class SectorStatsSummary {

	private String sector;
	private Long stockCnt;
	private Double avg;
	private Double upDownRatio;
	private String remark;
	private String maxStockCode;
	private Double maxStockPencentage;
	
	private String minStockCode;
	private Double minStockPencentage;
	
	public SectorStatsSummary() {
		// TODO Auto-generated constructor stub
	}


	public String toString() {
		return this.getSector() + "\t" + this.getStockCnt() + "\t " + GeneralHelper.toPct(this.getUpDownRatio())+ "\t"+ GeneralHelper.toPct(this.getAvg())+ "\t"+ this.getMaxStockCode() + "\t"+ this.getMaxStockPencentage()+ "\t"+ this.getMinStockCode()+ "\t"+ this.getMinStockPencentage();
	}
	public static String getHeader() {
		return "板塊\t數目\t升跌比例\t平均升幅\tLeader Stock\tLeader Stock升幅\tInverse LeaderStock\tInverse LeaderStock升幅";
	}
	
	

}
