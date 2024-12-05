package com.sjm.test.yahdata.analy.instantresult;

import com.sjm.test.yahdata.analy.conts.type.RsiType;
import com.sjm.test.yahdata.analy.model.InstantPerformanceResult;

public class TrendGettingWeakerC extends BaseTrendGettingWeakDirection{

	@Override
	public boolean isCategoryHit(InstantPerformanceResult x) {
		if(
			x.getAbv20D()<0 && x.getAbv50D()<0
//			&& (this.isValidDailyVolume(x.getDailyVolDescription()) ||  this.isValidDailyVolume(x.getRecentDaysVolumeStatus()) )
			&& x.getMa219DStatus().contains("<")			
			&& (x.getMa50200DStatus().contains("<") || x.getMa50250DStatus().contains("<"))
			&& (x.getPrevStockBean().getRsi9() > x.getRsi9() && (x.getRsi9() < 0.5 || x.getRsi14()< 0.5 )) 
			&& 
			RsiType.TOP_DIVERGENCE == x.getRsiDiverence().getDivergenceType()
//			x.getRsiDiverence().getDivergenceType().contains("頂背弛")
		) {
			return true;
		}
		return false;
	}

	@Override
	public String toDescription() {
		return StockPickTagger.SHORT_3;
		
	}
}
