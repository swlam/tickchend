package com.sjm.test.yahdata.analy.ta;

import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.sjm.test.yahdata.analy.bean.StrongWeakTypeBean;
import com.sjm.test.yahdata.analy.bean.VolumePriceBean;
import com.sjm.test.yahdata.analy.bean.raw.StockBean;
import com.sjm.test.yahdata.analy.conts.Const;
import com.sjm.test.yahdata.analy.conts.ta.CandleEventTagEnum;
import com.sjm.test.yahdata.analy.ta.rule.event.pattern.BigDropAndStandUpFormRule;
import com.sjm.test.yahdata.analy.ta.rule.event.pattern.GapDownAndStandUpRule;

public class PatternTrendHelper {
	public final static String WEAK ="弱";
	public final static String STRONG ="強";

	public final static String STRONG_2_WEAK_LV1 	= "S->"+WEAK+"(Lv1)";
	public final static String STRONG_2_WEAK_LV2 	= "S->"+WEAK+"(Lv2)";
	public final static String KEEP_WEAK			= "->"+WEAK;
	public final static String WEAK_2_WEAK_AGAIN	= "W->"+WEAK;
	public final static String WEAK_LOWER_SHADOW 	= "->"+WEAK+"(下影線下)";
	
	
	public final static String WEAK_2_STRONG_LV1 	= "W->"+STRONG+"(Lv1)";
	public final static String WEAK_2_STRONG_LV2 	= "W->"+STRONG+"(Lv2)";
	public final static String STRONG_UPPER_SHADOW 	= "->"+STRONG+"(上影線上)";
	public final static String WEAK_2_STRONG_LV3_1 	= "W->"+STRONG+"(Lv3.1)";
	public final static String WEAK_2_STRONG_LV3_2 	= "W->"+STRONG+"(Lv3.2)";
	public final static String KEEP_STRONG			= "->"+STRONG;
	public final static String STRONG_2_STRONG_AGAIN	= "S->"+STRONG;
	
	public final static String WEAK_2_STRONG_MA 	= "W->"+STRONG+"(MA)";
	

	
	public static StrongWeakTypeBean getStrongWeakType(List<StockBean> stockList, List<VolumePriceBean> vpStockList) {
		StrongWeakTypeBean strong = getGoStrongType(stockList, vpStockList);
		StrongWeakTypeBean weak = getGoWeakType(stockList, vpStockList);
		
		if(!Const.NA.equals(strong.getType()) && Const.NA.equals(weak.getType())) {
			return strong;
		
		}else if(Const.NA.equals(strong.getType()) && !Const.NA.equals(weak.getType())) {
			return weak;
		}else {		
			return new StrongWeakTypeBean();		
		}	
	}

	public static StrongWeakTypeBean getGoStrongType(List<StockBean> stockList, List<VolumePriceBean> vpStockList) {
		StrongWeakTypeBean rtnBean = new StrongWeakTypeBean();
		
		if(stockList.size()<=5)
			return rtnBean;
		
		StockBean curr = stockList.get(stockList.size()-1);
		StockBean prev = stockList.get(stockList.size()-2);
		StockBean prev2 = stockList.get(stockList.size()-3);
		StockBean prev3 = stockList.get(stockList.size()-4);
		StockBean prev4 = stockList.get(stockList.size()-5);
		
		List<VolumePriceBean> lsWeekLow = vpStockList.parallelStream().filter(
				x->x.getSignSet().contains(CandleEventTagEnum.EVNT_WEEKLY_L) || x.getSignSet().contains(CandleEventTagEnum.EVNT_WEEKLY_L_CLOSE)
				).toList();

		VolumePriceBean lastWeeklyLowBean = null;
		boolean hasWeeklyL =false;
		if(!lsWeekLow.isEmpty()) {
			lastWeeklyLowBean = lsWeekLow.get(lsWeekLow.size()-1);
			if(lastWeeklyLowBean.getTxnDateInt()==curr.getTxnDateInt() || lastWeeklyLowBean.getTxnDateInt()== prev.getTxnDateInt() 
				|| lastWeeklyLowBean.getTxnDateInt()==prev2.getTxnDateInt() || lastWeeklyLowBean.getTxnDateInt()== prev3.getTxnDateInt() || lastWeeklyLowBean.getTxnDateInt()==prev4.getTxnDateInt()
				)
				hasWeeklyL = true;
		}
		
		List<VolumePriceBean> lsWeekHigh = vpStockList.parallelStream().filter(
				x->x.getSignSet().contains(CandleEventTagEnum.EVNT_WEEKLY_H) || x.getSignSet().contains(CandleEventTagEnum.EVNT_WEEKLY_H_CLOSE)
				).toList();
		boolean hasWeeklyH =false;
		if(!lsWeekHigh.isEmpty())
		{
			VolumePriceBean b = lsWeekHigh.get(lsWeekHigh.size()-1);
			if(b.getTxnDateInt()==curr.getTxnDateInt() || b.getTxnDateInt()==prev.getTxnDateInt() 
				|| b.getTxnDateInt()==prev2.getTxnDateInt() ||b.getTxnDateInt()==prev3.getTxnDateInt() || b.getTxnDateInt()==prev4.getTxnDateInt())
				hasWeeklyH = true;
		}
		
		
		boolean isCurrAboveAllDayH = curr.getC()> prev.getH() && curr.getC()> prev2.getH() && curr.getC()> prev3.getH();
		boolean isPrevAboveAllDayH = prev.getC()> prev2.getH() && prev.getC()> prev3.getH() && prev.getC()> prev4.getH();
		
		boolean isCurrAboveAllDayTop = curr.getC()> prev.getBodyTop() && curr.getC()> prev2.getBodyTop() && curr.getC()> prev3.getBodyTop();
		boolean isPrevAboveAllDayTop = prev.getC()> prev2.getBodyTop() && prev.getC()> prev3.getBodyTop() && prev.getC()> prev4.getBodyTop();
		
		boolean isGoStrongLv2 = (isCurrAboveAllDayH && !isPrevAboveAllDayH);
		boolean isGoStrongLv1 = (!isCurrAboveAllDayH && isCurrAboveAllDayTop && !isPrevAboveAllDayTop);
		
		boolean isStrongAgain = (isCurrAboveAllDayH && isPrevAboveAllDayH) ||(isCurrAboveAllDayTop && isPrevAboveAllDayTop);


		boolean isMoreVol = Const.IS_INTRADAY ?(curr.getDayVolumeChgPct() > 0.5): ( (curr.getVolume() / prev.getVolume()) > 1 );


		
		StockBean lowest = Stream.of(curr, prev, prev2, prev3, prev4).min(Comparator.comparing(StockBean::getL))
			      .orElseThrow(NoSuchElementException::new);
		

		BigDropAndStandUpFormRule bigDropStandUpRule = new BigDropAndStandUpFormRule();
		GapDownAndStandUpRule gapDownStandUpRule = new GapDownAndStandUpRule();
//		MA510DownAndUpBreakRule maDownAndUpBrealRule = new MA510DownAndUpBreakRule();
//		MACrossUpRule maCrossUpRule = new CrossUp5D10DRule();
		List<StockBean> prevStockList = stockList.subList(0, stockList.size()-1);
		boolean isBigDropStandUp = bigDropStandUpRule.detect(prevStockList, curr);
		boolean isGapDownStandUp = gapDownStandUpRule.detect(prevStockList, curr);
//		boolean isMaCrossDownAndUpBreak = maCrossUpRule.detect(prevStockList, curr);
		//check upper shadow pattern
		boolean isPrevValidUpperShadow = KHelper.isDoji(prev) || KHelper.isShootingStar(prev) ;
		
		
		
		double distance = (lowest.getL()-curr.getC())/ curr.getC();
		rtnBean.setMaxDistancePct(distance);

		boolean isPositivePriceChangePct = curr.getDayChgPct() > 0.0;
		
//		if(isMaCrossDownAndUpBreak) {
//			rtnBean.setType(WEAK_2_STRONG_MA);
//		}else
			
		if(isBigDropStandUp && isPositivePriceChangePct) {
			rtnBean.setType(WEAK_2_STRONG_LV3_1);
		}else if(isGapDownStandUp && isPositivePriceChangePct) {
			rtnBean.setType(WEAK_2_STRONG_LV3_2);
		}else if( hasWeeklyL && isGoStrongLv2 && isMoreVol && isPositivePriceChangePct){
			rtnBean.setType(WEAK_2_STRONG_LV2);		
		}else if( hasWeeklyL && isGoStrongLv1 && isMoreVol && isPositivePriceChangePct){
				rtnBean.setType(WEAK_2_STRONG_LV1);
		}else if(hasWeeklyH && isStrongAgain && isPositivePriceChangePct){
			rtnBean.setType(STRONG_2_STRONG_AGAIN);
		}else if(
				isPositivePriceChangePct && isMoreVol
				&& (lastWeeklyLowBean!=null && lastWeeklyLowBean.getTxnDateInt() != prev.getTxnDateInt() && lastWeeklyLowBean.getTxnDateInt() != curr.getTxnDateInt() ) 
				&& isPrevValidUpperShadow && KHelper.isBullishCandle(curr) && curr.getBodyTop() > prev.getBodyTop() && curr.getBodyTop() < prev.getH() && curr.getH()> prev.getH())
		{
			rtnBean.setType(STRONG_UPPER_SHADOW);
		}else if(isPositivePriceChangePct && (isCurrAboveAllDayTop || isGoStrongLv1 || isGoStrongLv2 )){
			rtnBean.setType(KEEP_STRONG);
		}else {
			rtnBean.setType(Const.NA);
			rtnBean.setMaxDistancePct(null);
		}
		return rtnBean;
	}
	
	public static StrongWeakTypeBean getGoWeakType(List<StockBean> stockList, List<VolumePriceBean> vpStockList) {
		StrongWeakTypeBean rtnBean = new StrongWeakTypeBean();
		
		if(stockList.size()<=5)
			return rtnBean;
		
		StockBean curr = stockList.get(stockList.size()-1);
		StockBean prev = stockList.get(stockList.size()-2);
		StockBean prev2 = stockList.get(stockList.size()-3);
		StockBean prev3 = stockList.get(stockList.size()-4);
		StockBean prev4 = stockList.get(stockList.size()-5);
		
		List<VolumePriceBean> lsWeekHigh = vpStockList.parallelStream().filter(
				x->x.getSignSet().contains(CandleEventTagEnum.EVNT_WEEKLY_H) || x.getSignSet().contains(CandleEventTagEnum.EVNT_WEEKLY_H_CLOSE)
				).toList();

		List<VolumePriceBean> lsWeekLow = vpStockList.parallelStream().filter(
				x->x.getSignSet().contains(CandleEventTagEnum.EVNT_WEEKLY_L) || x.getSignSet().contains(CandleEventTagEnum.EVNT_WEEKLY_L_CLOSE)
				).toList();

		
		boolean hasWeeklyH =false;
		VolumePriceBean lastWeekHighBean = null;
		if(!lsWeekHigh.isEmpty())
		{
			lastWeekHighBean = lsWeekHigh.get(lsWeekHigh.size()-1);
			if(lastWeekHighBean.getTxnDateInt()==curr.getTxnDateInt() || lastWeekHighBean.getTxnDateInt()==prev.getTxnDateInt() 
					|| lastWeekHighBean.getTxnDateInt()==prev2.getTxnDateInt() ||lastWeekHighBean.getTxnDateInt()==prev3.getTxnDateInt() || lastWeekHighBean.getTxnDateInt()==prev4.getTxnDateInt())
				hasWeeklyH = true;
		}
		
		boolean hasWeeklyL =false;
		if(!lsWeekLow.isEmpty() && lsWeekLow.size()>=2) {
			VolumePriceBean b = lsWeekLow.get(lsWeekLow.size()-2);
			if( b.getTxnDateInt()==prev.getTxnDateInt() 
					|| b.getTxnDateInt()==prev2.getTxnDateInt() ||b.getTxnDateInt()==prev3.getTxnDateInt() || b.getTxnDateInt()==prev4.getTxnDateInt())
				hasWeeklyL = true;
		}
		
		
		boolean isCurrBelowThreeDayLow = curr.getC()< prev.getL() && curr.getC()< prev2.getL() && curr.getC()< prev3.getL();
		boolean isPrevBelowThreeDayLow = prev.getC()< prev2.getL() && prev.getC()< prev3.getL() && prev.getC()< prev4.getL();
		
		boolean isCurrBelowThreeDayBottom = curr.getC()< prev.getBodyBottom() && curr.getC()< prev2.getBodyBottom() && curr.getC()< prev3.getBodyBottom();
		boolean isPrevBelowThreeDayBottom = prev.getC()< prev2.getBodyBottom() && prev.getC()< prev3.getBodyBottom() && prev.getC()< prev4.getBodyBottom();
		
		boolean isGoWeakLv2 = (isCurrBelowThreeDayLow && !isPrevBelowThreeDayLow);
		boolean isGoWeakLv1 = (!isCurrBelowThreeDayLow && isCurrBelowThreeDayBottom && !isPrevBelowThreeDayBottom);
//		boolean isOtherGoWeak = curr.getL()< prev.getL() && curr.getL()< prev2.getL() && curr.getL()< prev3.getL() && curr.getL()< prev4.getL(); 
		
		
		boolean isWeakAgain = (isCurrBelowThreeDayLow && isPrevBelowThreeDayLow) ||(isCurrBelowThreeDayBottom && isPrevBelowThreeDayBottom);
		
		StockBean highest = Stream.of(curr, prev, prev2, prev3, prev4).max(Comparator.comparing(StockBean::getH)).orElseThrow(NoSuchElementException::new);
		

		boolean isPrevValidLowerShadow = KHelper.isDoji(prev) || KHelper.isHammer(curr, isWeakAgain);
		
		double distance = (highest.getH()-curr.getC())/curr.getC();
		boolean isNegativeChangePct = curr.getDayChgPct() < 0.0;
		
		
		if(hasWeeklyH && isGoWeakLv2 && isNegativeChangePct) {
			rtnBean.setType(STRONG_2_WEAK_LV2);
			rtnBean.setMaxDistancePct(distance);
		}else if(hasWeeklyH && isGoWeakLv1 && isNegativeChangePct) {
			rtnBean.setType(STRONG_2_WEAK_LV1);
			rtnBean.setMaxDistancePct(distance);
		}else if(hasWeeklyL && isWeakAgain && isNegativeChangePct){
			rtnBean.setType(WEAK_2_WEAK_AGAIN);
			rtnBean.setMaxDistancePct(distance);
		}else if( isNegativeChangePct 
				&& (lastWeekHighBean!=null && lastWeekHighBean.getTxnDateInt() != prev.getTxnDateInt() && lastWeekHighBean.getTxnDateInt() != curr.getTxnDateInt() )
				&& isPrevValidLowerShadow && KHelper.isBearishCandle(curr) && curr.getBodyBottom() < prev.getBodyBottom() && curr.getBodyBottom() > prev.getL() && curr.getL()< prev.getL()) {
			rtnBean.setType(WEAK_LOWER_SHADOW);
		}else if( isNegativeChangePct && (isCurrBelowThreeDayBottom || isGoWeakLv1 || isGoWeakLv2)){
			rtnBean.setType(KEEP_WEAK);
			rtnBean.setMaxDistancePct(distance);		
		}else {
			rtnBean.setType(Const.NA);
			rtnBean.setMaxDistancePct(null);
		}
		
		return rtnBean;
		
	}
}
