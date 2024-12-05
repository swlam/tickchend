package com.sjm.test.yahdata.analy.ta.indicator.divergence;

import java.util.List;

import com.sjm.test.yahdata.analy.bean.raw.StockBean;
import com.sjm.test.yahdata.analy.module.wavepoint.bean.WavePoint;
import com.sjm.test.yahdata.analy.ta.indicator.rsi.DivergencePriceVol;
import com.sjm.test.yahdata.analy.ta.indicator.rsi.DivergenceResult;
import com.sjm.test.yahdata.analy.ta.indicator.rsi.RSICalculator;

public class DivergenceFinder {

	public static DivergenceFinder instance = null;
	

	public static DivergenceFinder getInstance() {
		if(instance == null)
			instance = new DivergenceFinder();
		return instance;
	}
	

	
	public DivergenceResult findRsiDiverence(List<StockBean> stockList, List<WavePoint> topList, List<WavePoint> botList) {
		DivergenceResult rsiDivr = RSICalculator.getInstance().calcRsiDiverence(stockList, topList, botList);
		return rsiDivr;
	}
	
	
//	public DivergenceResult findPriceVolDiverence(List<StockBean> stockList) {
//		return DivergencePriceVol.getInstance().calcPriceAndVolume(stockList);
//	}

}
