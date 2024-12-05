package com.sjm.test.yahdata.analy.instantresult;

import com.sjm.test.yahdata.analy.conts.Const;
import com.sjm.test.yahdata.analy.conts.type.RsiType;
import com.sjm.test.yahdata.analy.model.InstantPerformanceResult;
import com.sjm.test.yahdata.analy.ta.helper.VolumePriceStructureHelper;

public class CustomBuySignalA extends BaseTrendGettingDirection{

	public CustomBuySignalA() {
	}

	@Override
	public String goAnalyze(InstantPerformanceResult x) {
		boolean isHit = this.isCategoryHit(x);
		
		if(isHit)
			return StockPickTagger.B1;
		
		return null;
	}

	
	@Override
	public boolean isCategoryHit(InstantPerformanceResult x) {
		if( RsiType.TOP_DIVERGENCE_UP_BREAK == x.getRsiDiverence().getDivergenceType()) {
			return true;
		}
				
		boolean bBase =  //( x.getWaveShape() !=null
			 (x.getRsi9()>=0.55 || x.getRsi14()>=0.55)
			&& (x.getRsi9()<=0.80 || x.getRsi14()<0.80)
			&& x.getPrevStockBean().getC() > x.getPrevStockBean().getPriceSma().getMa50()
			&& x.getCurrentStockBean().getC() > x.getCurrentStockBean().getPriceSma().getMa50();
			//&& x.getWaveShape().contains(Const.UP)
			//);

			boolean bWaveShape = x.getWaveShape() !=null && x.getWaveShape().getShapeResult().contains(Const.UP);
//			boolean b1 = x.getWaveTopBottomStatus().getStockTrendRemark().contains("升破前頂-D");
//			boolean b2 = x.getWaveTopBottomStatus().getShape().contains(Const.UP) && x.getWaveTopBottomStatus().getShape().contains("D");

			boolean bTurnStrong = !this.isPrevDayStrong(x) && this.isTodayStrong(x);
			boolean isEnoughVol = Const.IS_INTRADAY ?(x.getCurrentStockBean().getDayVolumeChgPct() > 0.5):(x.getCurrentStockBean().getDayVolumeChgPct() >= 1);

			boolean isHighCloseQualified = VolumePriceStructureHelper.WEEKLY_H_CLOSE.equalsIgnoreCase(x.getPriceStatus())
					|| VolumePriceStructureHelper.WEEKLY_BODY_TOP_H.equalsIgnoreCase(x.getPriceStatus())
					|| VolumePriceStructureHelper.MONTHLY_H_CLOSE.equalsIgnoreCase(x.getPriceStatus())
					|| VolumePriceStructureHelper.MONTHLY_BODY_TOP_H.equalsIgnoreCase(x.getPriceStatus())
					|| VolumePriceStructureHelper.DAY50_H_CLOSE.equalsIgnoreCase(x.getPriceStatus())
					|| VolumePriceStructureHelper.DAY50_BODY_TOP_H.equalsIgnoreCase(x.getPriceStatus())
					|| VolumePriceStructureHelper.DAY250_H_CLOSE.equalsIgnoreCase(x.getPriceStatus())
					|| VolumePriceStructureHelper.DAY250_BODY_TOP_H.equalsIgnoreCase(x.getPriceStatus());


			return (isEnoughVol && bBase) || bTurnStrong || isHighCloseQualified || bWaveShape;

			
//			//小浪方向: e.g. UP破小W, HL
//			boolean b1 = x.getWaveTopBottomStatus().getTrendDescription() .contains("回調-似UPBreak");
//			boolean b1 = x.getWaveTopBottomStatus().getTrendDescription() .contains("破底翻");
//			boolean b2 = x.getWaveTopBottomStatus().getStockTrendRemark().contains("0.764上");
//			
//			
//			boolean b3 = x.getWaveTopBottomStatus().getStockTrendRemark().contains("升破前頂-Day(1)");
//			
//			//小浪型狀: e.g. UP破小W(D0), HL / [3平頂待UP] / 待UP破小W, LH / UP破小1頂(D0)
//			boolean b4 = x.getWaveTopBottomStatus().getShape() .contains(Const.UP);
//			
//			//月內近日平均線有支持/阻力: e.g. [10D出支持, 20D出阻力]
//			boolean b5 = x.getMovingAverageLongSideSupport().contains("支持");
//			
//			boolean otherB = x.getCurrentStockBean().getDayVolumeChgPct() < 3.0; //
//			boolean b5 = x.getMovingAverageLongSideSupport().contains("阻力");
	
	}
	
	
	
	public String toDescription() {
		return StockPickTagger.B1;
	}
}
