package com.sjm.test.yahdata.analy.module.lowhighdist.bean;

//import org.apache.http.Consts;

import com.maas.util.GeneralHelper;
import com.sjm.test.yahdata.analy.conts.Const;

import lombok.Data;
@Data
public class LowHighDateSimplifyResult {

	String stockCode;
	String highProbabilityLDates;//低位日子 
	double probabilityLDate; //低位日子Prob.
	int dayBetweenLowest;
	
	String highProbabilityHDates;//高位日子
	double probabilityHDate; //高位日子Prob.
	int dayBetweenHighest;
	
	public static final String getTitle() {
		return "低位日期\t低位日子數\t低位日子Prob.\t高位日期\t高位日子數\t高位日子Prob.";
	}
	public static final String getEmptyData() {		
		return " \t \t \t \t \t ";
	}
	
	
	public String toPrintResult() {

		String m = 
		this.getHighProbabilityLDates() + "\t" + 
		this.getDayBetweenLowest() + "\t" + 
				GeneralHelper.toPct(this.getProbabilityLDate());
		
		m += "\t" + this.getHighProbabilityHDates() +"\t"+ this.getDayBetweenHighest() + "\t" + GeneralHelper.toPct(this.getProbabilityHDate());
		
		return m;
		
	}
	
	
}
