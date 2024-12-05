package com.sjm.test.yahdata.analy.instantresult;

import com.sjm.test.yahdata.analy.model.InstantPerformanceResult;
/*



	
	
 */
public class TrendGettingStrongerD extends BaseTrendGettingDirection{

	public TrendGettingStrongerD() {
	}
	
	@Override
	public String goAnalyze(InstantPerformanceResult x) {
		boolean isHit = this.isCategoryHit(x);
		
		if(isHit)
			return StockPickTagger.LONG_4;
		
		return null;
	}

	@Override
	public boolean isCategoryHit(InstantPerformanceResult x) {
		boolean isMeetTradingVol = this.isMeetTradingVolume(x);
		
		if(isMeetTradingVol ==false || x.getPriceVolStockBean() == null || x.getPriceVolStockBean().getNumOfDoubleVolumeDate().trim().isEmpty())
			return false;
		
		int numOfDoubleVol = Integer.valueOf(x.getPriceVolStockBean().getNumOfDoubleVolumeDate().trim());
		
		if(	numOfDoubleVol > 2 ) {
			return true;
		}
			
			
		return false;
		
		
	}
	
	public String toDescription() {
		return StockPickTagger.LONG_4;
	}
}
