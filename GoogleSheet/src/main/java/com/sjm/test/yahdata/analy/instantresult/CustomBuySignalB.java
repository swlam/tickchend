package com.sjm.test.yahdata.analy.instantresult;

import com.sjm.test.yahdata.analy.conts.Const;
import com.sjm.test.yahdata.analy.model.InstantPerformanceResult;

public class CustomBuySignalB extends BaseTrendGettingDirection{

	public CustomBuySignalB() {
	}

	@Override
	public String goAnalyze(InstantPerformanceResult x) {
		boolean isHit = this.isCategoryHit(x);
		
		if(isHit)
			return StockPickTagger.B2;
		
		return null;
	}
	
	
	@Override
	public boolean isCategoryHit(InstantPerformanceResult x) {
				
		
		
		boolean bBase =  ( x.getWaveShape()!=null 
				&& (x.getRsi9()>=0.55 || x.getRsi14()>=0.55)
				&& (x.getRsi9()<=0.80 || x.getRsi14()<0.80)
				&& x.getPrevStockBean().getC() > x.getPrevStockBean().getPriceSma().getMa50()
				&& x.getCurrentStockBean().getC() > x.getCurrentStockBean().getPriceSma().getMa50()
				); 
				
			
				boolean b1 = x.getWaveShape() == null || x.getWaveShape().getShapeResult().contains(Const.UP);
//				boolean b2 = x.getWaveTopBottomStatus().getTrendDescription() .contains("破底翻");
//				boolean b3 = x.getWaveTopBottomStatus().getStockTrendRemark().contains("0.764上");
		
//				boolean b1 = x.getWaveTopBottomStatus().getStockTrendRemark().contains("升破前頂-D");
//				boolean b4 = x.getWaveTopBottomStatus().getShape().contains(Const.UP);
//				boolean b5 = x.getMovingAverageLongSideSupport().contains("支持");

				boolean isEnoughVol = Const.IS_INTRADAY ?(x.getCurrentStockBean().getDayVolumeChgPct() > 0.5):(x.getCurrentStockBean().getDayVolumeChgPct() > 1);

		return (isEnoughVol && bBase && b1 );

	}
	
	
	
	public String toDescription() {
		return StockPickTagger.B1;
	}
}
