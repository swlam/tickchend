package com.sjm.test.yahdata.analy.ta.rule.ma.situation.bean;

import java.util.Set;

import com.maas.util.GeneralHelper;
import com.sjm.test.yahdata.analy.bean.raw.PriceSMABean;
import com.sjm.test.yahdata.analy.conts.ta.MAStatusEnum;

import lombok.Data;
@Data
public class PriceMASituationSummaryBean {

	String stockCode;
	String txnDate;
	Double price;
	Double percentageCurrent2MA5;
	Double percentageCurrent2MA10;
	Double percentageCurrent2MA20;
	Double percentageCurrent2MA50;
	Double percentageCurrent2MA200;
	Double percentageCurrent2MA250;
	
//	Double ma2;
//	Double ma5;
//	Double ma10;
//	Double ma19;
//	Double ma20;
//	Double ma50;
//	Double ma100;
//	Double ma200;
//	Double ma250;
	private PriceSMABean priceSma;
	
	Double ma2Prev1;
	Double ma19Prev1;
	Double ma50Prev1;
	Double ma200Prev1;
	Double ma250Prev1;
	
	Double ma2Prev2;
	Double ma19Prev2;
	Double ma50Prev2;
	Double ma200Prev2;
	Double ma250Prev2;
	
	Double ma2Prev3;
	Double ma19Prev3;
	Double ma50Prev3;
	Double ma200Prev3;
	Double ma250Prev3;
	
	boolean isAbv2D;
	boolean isAbv19D;
	
	double abv5D;
	double abv10D;
	double abv20D;
	double abv50D;
	double abv100D;
	double abv200D;
	double abv250D;
	
	boolean consecutive5daysAbvCurrent50D = false;
	boolean consecutive5daysAbvCurrent200D = false;
	boolean consecutive5daysBlwCurrent50D = false;
	boolean consecutive5daysBlwCurrent200D = false;
	
	MovingAvgCrossResultBean ma50200CrossUpDate;
	MovingAvgCrossResultBean ma50250CrossUpDate;
	MovingAvgCrossResultBean ma50200CrossDownDate;
	
	Double stagnantMARatio;
	Double stagnantPrev1MARatio;
	Double stagnantPrev2MARatio;
	Double stagnantPrev3MARatio;
	Double stagnantShortTermMARatio;
	Double stagnantMidTermMARatio;
	
//	MAStatusEnum maStatus;
	Set<MAStatusEnum> movingAvgStatusSet;
	
	//Long to arrange 多頭排列
//	boolean isLongToArrange;
	
	public String toString() {
		return txnDate
				+ ", Price:"+GeneralHelper.format(price)
				+ ", 5D("+GeneralHelper.format(priceSma.getMa5())+")"+ ", 10D("+GeneralHelper.format(priceSma.getMa10())+")"+ ", 20D("+GeneralHelper.format(priceSma.getMa20())+")"+ ", 50D("+GeneralHelper.format(priceSma.getMa50())+")"+ ", 200D("+GeneralHelper.format(priceSma.getMa200())+")"+ ", 250D("+GeneralHelper.format(priceSma.getMa250())+")";
	}
}
