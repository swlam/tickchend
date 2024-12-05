package com.sjm.test.yahdata.analy.instantresult;

import com.sjm.test.yahdata.analy.conts.KPatternConst;
import com.sjm.test.yahdata.analy.conts.ta.CandleEventTagEnum;
import com.sjm.test.yahdata.analy.conts.type.CandlestickTradingPatternType;
import com.sjm.test.yahdata.analy.model.InstantPerformanceResult;
import com.sjm.test.yahdata.analy.ta.KHelper;

public class TrendGettingStrongerA extends BaseTrendGettingStrongDirection{

	@Override
	public boolean isCategoryHit(InstantPerformanceResult x) {
		boolean isMeetTradingVol = isMeetTradingVolume(x);
		
		boolean isTodayCandleStrong = KHelper.isBullishCandle(x.getCurrentStockBean())  
				&& x.getCurrentStockBean().getC() >= x.getPrevStockBean().getBodyTop() 
				&& x.getCurrentStockBean().getH() >= x.getPrevStockBean().getH();
		
		if(	isMeetTradingVol
			&& 
			(
					this.isNotTopReversal(x)
				&& (
					this.isTodayStrong(x) || this.checkStrongIn2Days(x) || checkStrongIn3Days(x)
					|| this.isTodayBullishPattern(x)
					|| isTodayCandleStrong
					|| x.getDailyImportantCandlestickTradingPattern().contains(CandlestickTradingPatternType.GAP_UP_BULLISH.toString())
	//				|| x.getDailyCandleDescription().contains(CandleStickHelper.HAMMER)  
					|| x.getDailyImportantCandlestickTradingPattern().contains(CandlestickTradingPatternType.GAP_UP_BEARISH_SHOOTING.toString())
					|| x.getDailyImportantCandlestickTradingPattern().contains(CandlestickTradingPatternType.TWEEZER_BOTTOM.toString())
					|| x.getDailyImportantCandlestickTradingPattern().contains(CandlestickTradingPatternType.FULL_WHITE.toString())
//					|| ( x.getWaveTopBottomStatus().getReversalPattern()!=null && x.getWaveTopBottomStatus().getReversalPattern().contains(KPatternConst.KP_BOTTOM_REVERSAL) )
					|| KPatternConst.ISLAND_BOTTOM_REVERSAL.contains(x.getIsland().getIslandType())
					
//					|| StockTrendStatus.BREAKOUT.equals(x.getWaveTopBottomStatus().getStockTrendStatus())
//					|| StockTrendStatus.BREAKOUT_DIRECT.equals(x.getWaveTopBottomStatus().getStockTrendStatus())
					
					|| (
						x.getCurrentStockBean().getC() > x.getPrevStockBean().getBodyTop() && x.getCurrentStockBean().getC() > x.getCurrentStockBean().getO()
						&& x.getCurrentStockBean().getDayVolumeChgPct() > 1.10 
						)
				)				
				&&	
				(
					!( x.getDailyCandleStatus().contains(KPatternConst.L_DARK_K) || x.getDailyCandleStatus().contains(KPatternConst.M_DARK_K) || x.getDailyCandleStatus().contains(KPatternConst.G_DARK_K) )				
				)
				
//				&& (x.getAbv5D()> -0.02)

//				&& this.isMeetStrongSideRSI(x)

			)
			
		)//if
		{
			return true;
		}
		return false;
	}
	
	
	private boolean isNotTopReversal(InstantPerformanceResult x) {
		boolean hasIslandType = x.getIsland().getIslandType().isEmpty()==false;
		boolean isReversal = KPatternConst.ISLAND_TOP_REVERSAL.contains(x.getIsland().getIslandType());
		
		boolean isReversalResult = (hasIslandType ==false) && (isReversal==true);
		return (!isReversalResult); 
	}
	
	
//	private boolean checkGapDownStandUp(InstantPerformanceResult x) {
//		boolean b = x.getBullishPatternDesc().contains(CandleTagEnum.EVNT_GAPUP_AND_GO.getName())
//		|| x.getBullishPatternDesc().contains(CandleTagEnum.EVNT_GAPUP_AND_GO.getName())
//		|| x.getBullishPatternDesc().contains(CandleTagEnum.EVNT_GBUB.getName())
//		|| x.getBullishPatternDesc().contains(CandleTagEnum.EVNT_GAP_DOWN_AND_STAND_UP.getName())
//		|| x.getBullishPatternDesc().contains(CandleTagEnum.EVNT_BIG_DROP_AND_STAND_UP.getName())
//		|| x.getBullishPatternDesc().contains(CandleTagEnum.EVNT_BULLISH_ABANDONED_BABY.getName())
//		|| x.getBullishPatternDesc().contains(CandleTagEnum.EVNT_BULLISH_ENGULFING_2.getName());
//		return b;
//		
//		
//	}

	private boolean checkStrongIn2Days(InstantPerformanceResult x) {
		return this.isTodayWeak(x) == false && this.isPrevDayStrong(x);								
	}
	
	private boolean checkStrongIn3Days(InstantPerformanceResult x) {
		return this.isTodayWeak(x) == false && this.isPrevDayStrong(x) && this.isPrev2DayStrong(x);								
	}
	
	private boolean isTodayBullishPattern(InstantPerformanceResult x) {
		if(
			x.getBullishPattern() != null 
			&& x.getBullishPattern().getTxnDateInt() == x.getCurrentStockBean().getTxnDateInt()	
			&&
				(x.getBullishPattern().getSignSet().contains(CandleEventTagEnum.EVNT_GAP_DOWN_AND_STAND_UP) //good
				|| x.getBullishPattern().getSignSet().contains(CandleEventTagEnum.EVNT_BIG_DROP_AND_STAND_UP) //good
				|| x.getBullishPattern().getSignSet().contains(CandleEventTagEnum.EVNT_BULLISH_ENGULFING_2) //good
				|| x.getBullishPattern().getSignSet().contains(CandleEventTagEnum.EVNT_DOWNWARD_BREAKOUT_FAILURE)//good
				|| x.getBullishPattern().getSignSet().contains(CandleEventTagEnum.EVNT_BULLISH_ABANDONED_BABY) //good
				|| x.getBullishPattern().getSignSet().contains(CandleEventTagEnum.EVNT_TWO_WHITE_WITH_BLACK) //good
				|| x.getBullishPattern().getSignSet().contains(CandleEventTagEnum.EVNT_GBUB) //good
				|| x.getBullishPattern().getSignSet().contains(CandleEventTagEnum.EVNT_GAPUP_AND_GO)//good
				)
			){
			return true;
		}
		return false;
				
	}

	public String toDescription() {
		return StockPickTagger.LONG_1;
	}
}
