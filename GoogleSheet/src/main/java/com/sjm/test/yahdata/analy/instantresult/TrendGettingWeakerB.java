package com.sjm.test.yahdata.analy.instantresult;

import com.sjm.test.yahdata.analy.model.InstantPerformanceResult;
import com.sjm.test.yahdata.analy.ta.PatternTrendHelper;

public class TrendGettingWeakerB extends TrendGettingWeakerA{


	@Override
	public boolean isCategoryHit(InstantPerformanceResult x) {
		
		if( super.isCategoryHit(x)
			&& 
			!this.isPrevDayStrong(x) && !this.isPrev2DayStrong(x)
			&& x.getAbv20D()<0 && x.getAbv50D()<0   							
			&& (x.getMa50200DStatus().contains("<") || x.getMa50250DStatus().contains("<"))			 
			&& x.getCurrentStockBean().getC() <	x.getCurrentStockBean().getPriceSma().getMa50()
			&& this.checkRSI(x)
		) {
			return true;
		}
			return false;
	}

	
	private boolean checkRSI(InstantPerformanceResult x) {
		boolean b1 = (x.getCurrentStockBean().getRsi9() <= x.getPrevStockBean().getRsi9() && x.getCurrentStockBean().getRsi9() <= x.getPrev2StockBean().getRsi9() );
		boolean b2 = (x.getCurrentStockBean().getRsi14() <= x.getPrevStockBean().getRsi14() && x.getCurrentStockBean().getRsi14() <= x.getPrev2StockBean().getRsi14() );

		return b1 || b2;
	}
	@Override
	public String toDescription() {
		return StockPickTagger.SHORT_2;
		
	}
}
