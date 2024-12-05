package com.sjm.test.yahdata.analy.module.lowhighdist.bean;

import java.util.List;

import com.maas.util.GeneralHelper;
import com.sjm.test.yahdata.analy.bean.VolumePriceBean;
import com.sjm.test.yahdata.analy.conts.Const;
import com.sjm.test.yahdata.analy.helper.CFGHelper;
import com.sjm.test.yahdata.analy.ta.rule.ma.situation.bean.PriceMASituationSummaryBean;

public class LowHighDateDistrDetailResultSummary extends BaseLowHighDateDistrBean{

	
	private PriceMASituationSummaryBean currentMASituation;
	private List<VolumePriceBean> recentCandleSituationList;
	
	private double periodC2CAvg;
	private double periodC2LAvg;
	private double periodC2HAvg;
	private double periodL2HAvg;
	
	public LowHighDateDistrDetailResultSummary() {
	}
	
	public PriceMASituationSummaryBean getCurrentMASituation() {
		return currentMASituation;
	}

	public void setCurrentMASituation(PriceMASituationSummaryBean currentMASituation) {
		this.currentMASituation = currentMASituation;
	}
	

	public double getPeriodC2CAvg() {
		return periodC2CAvg;
	}

	public void setPeriodC2CAvg(double periodC2CAvg) {
		this.periodC2CAvg = periodC2CAvg;
	}

	public double getPeriodC2LAvg() {
		return periodC2LAvg;
	}

	public void setPeriodC2LAvg(double periodC2LAvg) {
		this.periodC2LAvg = periodC2LAvg;
	}

	public double getPeriodC2HAvg() {
		return periodC2HAvg;
	}

	public void setPeriodC2HAvg(double periodC2HAvg) {
		this.periodC2HAvg = periodC2HAvg;
	}
	

	public double getPeriodL2HAvg() {
		return periodL2HAvg;
	}

	public void setPeriodL2HAvg(double periodL2HAvg) {
		this.periodL2HAvg = periodL2HAvg;
	}

	
	
	public List<VolumePriceBean> getRecentCandleSituationList() {
		return recentCandleSituationList;
	}

	public void setRecentCandleSituationList(List<VolumePriceBean> recentCandleSituationList) {
		this.recentCandleSituationList = recentCandleSituationList;
	}

	public String toPrintDetailString() {
		String maMessage = "";
		String maValuesMsg = "";
		
		if(this.getCurrentMASituation()!=null) {
			maMessage = ( this.getCurrentMASituation().getMovingAvgStatusSet().toString());
			maValuesMsg = this.getCurrentMASituation().toString();
		}
		
		StringBuilder vpStr = new StringBuilder();
		if(this.getRecentCandleSituationList()!=null) {
			for (VolumePriceBean bean : recentCandleSituationList) {
				vpStr.append(bean.toString());
				vpStr.append(", ");
			}
			vpStr.deleteCharAt(vpStr.length()-1);
			vpStr.deleteCharAt(vpStr.length()-1);
			
		}
		
		
		return   this.getStockCode().replace(".HK", "")
				+"\t" + (CFGHelper.getStockProfileMap().get(this.getStockCode())==null?Const.NA:CFGHelper.getStockProfileMap().get(this.getStockCode()).getName())
				+"\t" + (CFGHelper.getStockProfileMap().get(this.getStockCode())==null?Const.NA:CFGHelper.getStockProfileMap().get(this.getStockCode()).getSector())
				+ "\t" + this.getBelongETF()
				+ "\t" + this.getYears()
//				+ "\t" + maMessage				
//				+ "\t" + GeneralHelper.to100Percentage(this.getPeriodC2CAvg())
//				+ "\t" + GeneralHelper.to100Percentage(this.getPeriodC2LAvg())
//				+ "\t" + GeneralHelper.to100Percentage(this.getPeriodC2HAvg())
//				+ "\t" + GeneralHelper.to100Percentage(this.getPeriodL2HAvg())
				+ "\t" + GeneralHelper.toPct(this.getOccursTimeSlot1())
				+ "\t" + GeneralHelper.toPct(this.getOccursTimeSlot2()) 
				+ "\t" + GeneralHelper.toPct(this.getOccursTimeSlot3()) 
				+ "\t" + GeneralHelper.toPct(this.getOccursTimeSlot4()) 
				+ "\t"+this.getTimeSlot1() + "\t " + this.getTimeSlotDateList1()
				+ "\t"+this.getTimeSlot2() + "\t " + this.getTimeSlotDateList2()
				+ "\t"+this.getTimeSlot3() + "\t " + this.getTimeSlotDateList3()
				+ "\t"+this.getTimeSlot4() + "\t " + this.getTimeSlotDateList4()
//				+ "\t" +  maValuesMsg
//				+ "\t" + vpStr
				;
	}
	
	public String toString() {
		return "\tT-1\t"+GeneralHelper.toPct(this.getOccursTimeSlot1()) 
		+ "\tT-2\t"+GeneralHelper.toPct(this.getOccursTimeSlot2()) 
		+ "\tT-3\t"+GeneralHelper.toPct(this.getOccursTimeSlot3())
		+ "\tT-4\t"+GeneralHelper.toPct(this.getOccursTimeSlot4());
	}

}
