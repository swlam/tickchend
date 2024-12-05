package com.sjm.test.yahdata.analy.ta.pattern;

import com.sjm.test.yahdata.analy.bean.raw.StockBean;
import com.sjm.test.yahdata.analy.recenthigh.BaseRecentHighBean;

import lombok.Data;
@Data
public class BaseMWShapeInfo extends BaseRecentHighBean{
	String type;
	StockBean currentStockBean;
	StockBean criticalPointBtwn; // lowPointBtwn point OR highest point
	String supportMessage;
	int calendarDayBetweenCriticalPoint;
	String breakingDate;
}
