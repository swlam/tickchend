package com.sjm.test.yahdata.analy.instantresult;

import com.sjm.test.yahdata.analy.model.InstantPerformanceResult;
import com.sjm.test.yahdata.analy.ta.KHelper;
import com.sjm.test.yahdata.analy.ta.pattern.BollingerBandApi;

public class TrendGettingStrongerE extends BaseTrendGettingDirection{

	private BollingerBandApi bollingerBandApi;
	
	public TrendGettingStrongerE() {
		bollingerBandApi = new BollingerBandApi();
		
	}
	
	@Override
	public String goAnalyze(InstantPerformanceResult x) {
		boolean isHit = this.isCategoryHit(x);
		
		if(isHit)
			return StockPickTagger.LONG_5;
		
		return null;
	}

	@Override
	public boolean isCategoryHit(InstantPerformanceResult x) {
		boolean isMeetTradingVol = this.isMeetTradingVolume(x);
		int numOfDoubleVol = 0;
		
		if(x.getPriceVolStockBean()!=null && x.getPriceVolStockBean().getNumOfDoubleVolumeDate().trim().isEmpty()==false)
			numOfDoubleVol = Integer.valueOf(x.getPriceVolStockBean().getNumOfDoubleVolumeDate().trim());

		if(x.getCurrentStockBean().getBollingerBand()==null)
			return false;
		
		
		double bbUpper = x.getCurrentStockBean().getBollingerBand().getUpper();
		double bbMiddle = x.getCurrentStockBean().getBollingerBand().getMiddle();
		double prevBbUpper = x.getPrevStockBean().getBollingerBand().getUpper();
		
		boolean condition1 = false;
		boolean condition2 = false;
		boolean condition3 = false;
		if( x.getCurrentStockBean().getC() >= bbUpper * 0.99 && KHelper.isBullishCandle(x.getCurrentStockBean())
			&& prevBbUpper>x.getPrevStockBean().getH()
		) {
			condition1 = true;
		}
		
		//up break
		double upBreakPct = (x.getCurrentStockBean().getBodyTop() - bbUpper) / bbUpper;
		if( upBreakPct > 0.001 && upBreakPct < 0.02 && KHelper.isBearishCandle(x.getPrevStockBean())) 
		{
			condition2 = true;
		}
		
		double hUpBreakPct = (x.getCurrentStockBean().getH() - bbUpper) / bbUpper;
		if( hUpBreakPct > 0.003 
			&& bbUpper >= x.getCurrentStockBean().getBodyTop() && bbMiddle < x.getCurrentStockBean().getBodyBottom()
			&& KHelper.isBearishCandle(x.getPrevStockBean())) 
		{
			condition3 = true;
		}
		
		if(isMeetTradingVol && (condition1 || condition2 || condition3)
				&& (x.getMa50200DStatus().contains(">") || x.getMa50250DStatus().contains(">"))
				&& (x.getRsi9()>=0.5 && x.getRsi14()>=0.5) 		
				&& numOfDoubleVol >= 1
		)
			return true;
			
			
		return false;
		
		
	}
	
	
	
	
	
	
	
	public String toDescription() {
		return "剛突破Bollinger Band (Upper)";
	}
}
