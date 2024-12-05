package com.sjm.test.yahdata.analy.module;

import java.util.List;

import com.sjm.test.yahdata.analy.bean.raw.StockBean;
import com.sjm.test.yahdata.analy.ta.rule.ma.situation.bean.PriceMASituationSummaryBean;
import com.sjm.test.yahdata.analy.ta.rule.ma.situation.engine.PriceMASituationEngine;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MovingAvgReport {
	private static PriceMASituationEngine engine = new PriceMASituationEngine();
	
	public MovingAvgReport() {
	}

	public static String exportMovingAvgStatus(List<StockBean> productStockList ) {
		String msg = "";
		PriceMASituationSummaryBean maSituationSummary = engine.detect(productStockList);
		if(maSituationSummary==null) {
			log.warn(productStockList.get(0).getStockCode() + " MA situation Summary is NULL" );
			msg = productStockList.get(0).getStockCode() + "\tN/A";			
		}else {
			msg = productStockList.get(0).getStockCode() + "\t" +maSituationSummary.getMovingAvgStatusSet().toString();
		}
		return msg;
	}

}
