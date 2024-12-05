package com.sjm.test.yahdata.analy.instantresult;

import com.sjm.test.yahdata.analy.conts.Const;
import com.sjm.test.yahdata.analy.conts.KPatternConst;
import com.sjm.test.yahdata.analy.conts.type.CandlestickTradingPatternType;
import com.sjm.test.yahdata.analy.model.InstantPerformanceResult;
import com.sjm.test.yahdata.analy.ta.KHelper;
import com.sjm.test.yahdata.analy.ta.helper.VolumePriceStructureHelper;

public class TrendGettingWeakerA extends BaseTrendGettingWeakDirection{

	
	@Override
	public boolean isCategoryHit(InstantPerformanceResult x) {
		boolean isMeetTradingVol = isMeetTradingVolume(x);
		
		
		boolean isTodayWeek = KHelper.isBearishCandle(x.getCurrentStockBean())
		&& x.getPrevStockBean().getBodyBottom() > x.getCurrentStockBean().getBodyBottom()
		&& x.getPrevStockBean().getL() > x.getCurrentStockBean().getL()
		&& x.getPrevStockBean().getL() > x.getCurrentStockBean().getC();
		boolean bWaveShape = x.getWaveShape() !=null && x.getWaveShape().getShapeResult().contains(Const.DOWN);

		if(	isMeetTradingVol			
		&& 
		(
				this.isNotBottomReversal(x)
				&& 
			(
				this.isTodayWeak(x) || this.checkWeakIn2Days(x) || this.checkWeakIn3Days(x)
				|| x.getDailyImportantCandlestickTradingPattern().contains(CandlestickTradingPatternType.GAP_DOWN_BEARISH.toString())
				|| x.getDailyImportantCandlestickTradingPattern().contains(CandlestickTradingPatternType.GAP_DOWN_HAMMER.toString())
				|| x.getDailyImportantCandlestickTradingPattern().contains(CandlestickTradingPatternType.TWEEZER_TOP.toString())
				|| x.getDailyImportantCandlestickTradingPattern().contains(CandlestickTradingPatternType.FULL_BLACK.toString())
//				|| (x.getWaveTopBottomStatus().getReversalPattern()!=null 
//					&& x.getWaveTopBottomStatus().getReversalPattern().equalsIgnoreCase(KPatternConst.KP_TOP_REVERSAL)
//					)
				|| KPatternConst.ISLAND_TOP_REVERSAL.contains(x.getIsland().getIslandType())
				|| isTodayWeek
				|| (
					x.getDailyCandleStatus().contains(KPatternConst.L_DARK_K)
					|| x.getDailyCandleStatus().contains(KPatternConst.M_DARK_K)
					|| x.getDailyCandleStatus().contains(KPatternConst.G_DARK_K)					 
					)
				||
				(
				x.getCurrentStockBean().getC() < x.getPrevStockBean().getBodyBottom() && x.getCurrentStockBean().getC() < x.getCurrentStockBean().getO()
				&& x.getCurrentStockBean().getDayVolumeChgPct() > 1.10 
				)
				|| !this.isPrevDayWeak(x) && this.isTodayWeak(x)
				|| (
						VolumePriceStructureHelper.WEEKLY_L_CLOSE.equalsIgnoreCase(x.getPriceStatus())
						|| VolumePriceStructureHelper.WEEKLY_BODY_BOTTOM_L.equalsIgnoreCase(x.getPriceStatus())
						|| VolumePriceStructureHelper.MONTHLY_L_CLOSE.equalsIgnoreCase(x.getPriceStatus())
						|| VolumePriceStructureHelper.MONTHLY_BODY_BOTTOM_L.equalsIgnoreCase(x.getPriceStatus())
						|| VolumePriceStructureHelper.DAY50_L_CLOSE.equalsIgnoreCase(x.getPriceStatus())
						|| VolumePriceStructureHelper.DAY50_BODY_BOTTOM_L.equalsIgnoreCase(x.getPriceStatus())
						|| VolumePriceStructureHelper.DAY250_L_CLOSE.equalsIgnoreCase(x.getPriceStatus())
						|| VolumePriceStructureHelper.DAY250_BODY_BOTTOM_L.equalsIgnoreCase(x.getPriceStatus())
				)
				|| bWaveShape

			)
			
		)

		) {
			return true;
		}
		return false;
	}
	
	private boolean checkWeakIn2Days(InstantPerformanceResult x) {
		return this.isTodayStrong(x) == false && this.isPrevDayWeak(x);								
	}
	
	private boolean checkWeakIn3Days(InstantPerformanceResult x) {
		return this.isTodayStrong(x) == false && this.isPrevDayStrong(x)==false && this.isPrev2DayWeak(x);								
	}
	
	private boolean isNotBottomReversal(InstantPerformanceResult x) {
		boolean hasIslandType = x.getIsland().getIslandType().isEmpty()==false;
		boolean isReversal = KPatternConst.ISLAND_BOTTOM_REVERSAL.contains(x.getIsland().getIslandType());
		
		boolean isReversalResult = (hasIslandType ==false) && (isReversal==true);
		return (!isReversalResult); 
	}
	
	@Override
	public String toDescription() {
		return StockPickTagger.SHORT_1;
	}
}
