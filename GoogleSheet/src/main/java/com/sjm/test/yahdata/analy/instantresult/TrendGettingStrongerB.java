package com.sjm.test.yahdata.analy.instantresult;

import com.sjm.test.yahdata.analy.model.InstantPerformanceResult;

public class TrendGettingStrongerB extends TrendGettingStrongerA{

	@Override
	public boolean isCategoryHit(InstantPerformanceResult x) {
		
		if(
			super.isCategoryHit(x)
			&&
			!this.isPrevDayWeak(x) && !this.isPrev2DayWeak(x)
			&& (x.getAbv200D()>=0 || x.getAbv50D()>=0)
			&& (x.getMa50200DStatus().contains(">") || x.getMa50250DStatus().contains(">"))
			&& x.getCurrentStockBean().getC() >	x.getCurrentStockBean().getPriceSma().getMa50()
			&& this.checkRSI(x)
			
		) {
			return true;
		}					
			return false;				
	}
	
	private boolean checkRSI(InstantPerformanceResult x) {
		boolean b1 = (x.getCurrentStockBean().getRsi9() >= x.getPrevStockBean().getRsi9() && x.getCurrentStockBean().getRsi9() >= x.getPrev2StockBean().getRsi9() );
		boolean b2 = (x.getCurrentStockBean().getRsi14() >= x.getPrevStockBean().getRsi14() && x.getCurrentStockBean().getRsi14() >= x.getPrev2StockBean().getRsi14() );

		return b1 || b2;
	}
	
//	private boolean checkMACD(InstantPerformanceResult x) {
//		boolean b1 = (x.getCurrentStockBean().getMacdData().getMacd() > 0 && x.getPrevStockBean().getMacdData().getMacd() >= 0 && x.getPrev2StockBean().getMacdData().getMacd() >= 0);
//		boolean b2 = (x.getCurrentStockBean().getMacdData().getMacd() >= x.getPrevStockBean().getMacdData().getMacd() && x.getPrevStockBean().getMacdData().getMacd() >= x.getPrev2StockBean().getMacdData().getMacd() );
//		return b1 && b2;
//	}
	
	public String toDescription() {
		return StockPickTagger.LONG_2;
	}
}
