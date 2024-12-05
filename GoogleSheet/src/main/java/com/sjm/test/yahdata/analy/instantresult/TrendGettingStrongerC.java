package com.sjm.test.yahdata.analy.instantresult;

import com.sjm.test.yahdata.analy.conts.KPatternConst;
import com.sjm.test.yahdata.analy.conts.type.RsiType;
import com.sjm.test.yahdata.analy.model.InstantPerformanceResult;
import com.sjm.test.yahdata.analy.ta.helper.VolumePriceStructureHelper;

public class TrendGettingStrongerC extends TrendGettingStrongerA{


	@Override
	public boolean isCategoryHit(InstantPerformanceResult x) {
		boolean isMeetTradingVol = isMeetTradingVolume(x);
		if(isMeetTradingVol ==false || x.getPriceVolStockBean() == null || x.getPriceVolStockBean().getNumOfDoubleVolumeDate().trim().isEmpty())
			return false;
		
		int numOfDoubleVol = Integer.valueOf(x.getPriceVolStockBean().getNumOfDoubleVolumeDate().trim());
		
		if( isMeetTradingVol == true
//				&& (this.isValidDailyVolume(x.getDailyVolDescription()) ||  this.isValidDailyVolume(x.getRecentDaysVolumeStatus()) )
			&& x.getAbv20D()>0 && x.getAbv50D()>0  &&  x.getAbv200D()>0			
			&& (x.getMa50200DStatus().contains(">") || x.getMa50250DStatus().contains(">"))
			&& (x.getPrevStockBean().getRsi9() < x.getRsi9() && (x.getRsi9()> 0.5 && x.getRsi14()> 0.5))
			&& 
			(numOfDoubleVol > 0 || RsiType.BOTTOM_DIVERGENCE == x.getRsiDiverence().getDivergenceType() )
			
//			&& x.getRsiDiverence().getDivergenceType().contains("底背弛")
			
		) {
			return true;
		}
			
		return false;
	}
	
	
	
	public String toDescription() {
		return StockPickTagger.LONG_3 ;
	}
}
