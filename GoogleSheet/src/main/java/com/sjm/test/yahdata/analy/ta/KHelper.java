package com.sjm.test.yahdata.analy.ta;


import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;

import com.maas.util.GeneralHelper;

//import org.apache.log4j.Logger;

import com.sjm.test.yahdata.analy.analyzer.LargeCandleStickAndHigherCloseAnalyzer;
import com.sjm.test.yahdata.analy.bean.GapBean;
import com.sjm.test.yahdata.analy.bean.raw.StockBean;
import com.sjm.test.yahdata.analy.conts.Const;
import com.sjm.test.yahdata.analy.conts.KPatternConst;
import com.sjm.test.yahdata.analy.conts.ta.RuleConst;
import com.sjm.test.yahdata.analy.conts.type.CandlestickTradingPatternType;
import com.sjm.test.yahdata.analy.conts.type.KBodyType;
import com.sjm.test.yahdata.analy.helper.StreamTransformHelper;
import com.sjm.test.yahdata.excp.AbsoluteGapException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class KHelper {

	public KHelper() {}

	public static boolean isGapUp(StockBean prev, StockBean curr) {
		return prev.getH()< curr.getL();
	}

	public static boolean isGapDown(StockBean prev, StockBean curr) {
		return prev.getL()> curr.getH();
	}


	/*
	 * 1. prev gap-down, curr open hight sustain, but the close still below the prev's low
	 * 2. prev gap-down, curr open hight sustain, close go-into prev's body or total cover the prev
	 */
	public static boolean isOpenHighSustain(StockBean prev, StockBean curr) {
		return (prev.getC() <= curr.getO() && prev.getO() <= curr.getO() && curr.getO() < curr.getC()) ;
	}
	public static boolean isOpenLowCloseHigh(StockBean prev, StockBean curr) {
		return (prev.getC() >= curr.getO() && curr.getO() < curr.getC()) ;
	}
	public static boolean isOpenLowCloseLow(StockBean prev, StockBean curr) {
		return (prev.getC() >= curr.getO() && curr.getO() > curr.getC()) ;
	}
	public static boolean isOpenHighCloseLow(StockBean prev, StockBean curr) {
		return (prev.getC() <= curr.getO() && curr.getO() > curr.getC()) ;
	}

	public static boolean isOpenHighCloseLowAbvBodyTop(StockBean prev, StockBean curr) {
		return (prev.getC() <= curr.getO() && prev.getO() < curr.getO() && curr.getC() < curr.getO()) ;
	}

	public static boolean isOpenLowCloseHighInWhiteBody(StockBean prev, StockBean curr) {
		return (prev.getC() >= curr.getO() && prev.getO() < curr.getO() && curr.getC() > prev.getC()) ;
	}

	public static boolean isOpenLowSustain(StockBean prev, StockBean curr) {
		return (prev.getC() >= curr.getO() && curr.getC()< curr.getO() );
	}

	public static double getGapTop(StockBean prev, StockBean curr) throws AbsoluteGapException{
		boolean b1 = isGapUp(prev, curr);
		boolean b2 = isGapDown(prev, curr);
		if(b1 || b2) {
			double topCurr = (prev.getH()< curr.getL())?curr.getL(): prev.getL();
			return topCurr;
		}else {
			throw new AbsoluteGapException("No Gap on "+curr.getTxnDate());
		}
	}

	public static boolean isHarami(StockBean prev, StockBean curr) {
		double bodyTopPrev = getBodyTopValue(prev);
		double bodyBottomPrev = getBodyBottomValue(prev);

		double bodyTopCurr = getBodyTopValue(curr);
		double bodyBottomCurr = getBodyBottomValue(curr);

		if(bodyTopPrev>bodyTopCurr && bodyBottomPrev<bodyBottomCurr)
			return true;
		return false;
	}

	public static boolean isBullishCandle(StockBean candle) {

		return candle.getC() > candle.getO();
	}

	public static boolean isBearishCandle(StockBean candle) {

		return candle.getC() < candle.getO();
	}

	public static boolean isWhite(StockBean curr, StockBean prevCandle) {

		return curr.getC() > prevCandle.getC();
	}
	public static boolean isBlack(StockBean curr, StockBean prevCandle) {

		return curr.getC() < prevCandle.getC();
	}


	public static double getGapBottom(StockBean prev, StockBean curr) throws AbsoluteGapException{
		boolean b1 = isGapUp(prev, curr);
		boolean b2 = isGapDown(prev, curr);
		if(b1 || b2) {
			double bottomCurr = (prev.getH()< curr.getL())?prev.getH(): curr.getH();
			return bottomCurr;
		}else {
			throw new AbsoluteGapException("No Gap on "+curr.getTxnDate());
		}

	}


	public static double getBodyTopValue(StockBean curr) {
		double topCurr = (curr.getO()>curr.getC())?curr.getO(): curr.getC();
		return topCurr;
	}
	public static double getBodyBottomValue(StockBean curr) {
		double bottomCurr = (curr.getO()<curr.getC())?curr.getO(): curr.getC();
		return bottomCurr;
	}

	public static double getBodyPrice21(StockBean prev) {
		double topPrev = KHelper.getBodyTopValue(prev);
		double bottomPrev = KHelper.getBodyBottomValue(prev);

		double diffPrev = topPrev - bottomPrev;
		double price21Prev = topPrev - diffPrev/2;

		return price21Prev;

	}

	public static boolean isUpOverPrice21(StockBean prev, StockBean curr) {
		double price21Prev = KHelper.getBodyPrice21(prev);
		double prevBodyTop = KHelper.getBodyTopValue(prev);
		boolean bRtn = curr.getC() > price21Prev && curr.getC() <= prevBodyTop ;
		return bRtn;
	}

	public static boolean isMeetVolumeChangePositiveRatio(StockBean prev, StockBean curr, double ratio1, double ratio2) {
		boolean volumneRequirement = false;
		//volume requirement
		try {
			if( curr.getVolume()> prev.getVolume() )
			{
				double changesRatio = (double)curr.getVolume() / (double)prev.getVolume();
				if(changesRatio >=ratio1 && changesRatio <=ratio2) {
					volumneRequirement = true;
				}
			}
		}catch(Exception e) {
			log.error("PREV: "+prev.getTxnDate()+", vol:"+prev.getVolume()+", CURR: "+curr.getTxnDate()+", vol:"+curr.getVolume(), e);
		}
		return volumneRequirement;
	}

	public static boolean isDoji(StockBean curr) {


		double upperLegLength = (curr.getH() - curr.getBodyTop());
		double lowerLegLength = (curr.getBodyBottom()-curr.getL());
		double bodyLength = curr.getBodyTop() - curr.getBodyBottom();

		double shadowDifference = 1.0;

		boolean isDoji = false;

		if(upperLegLength/bodyLength>shadowDifference && lowerLegLength/bodyLength>shadowDifference)
			isDoji = true;



		return isDoji;
	}

	public static boolean isShootingStar(StockBean curr) {
		return isShootingStar(curr, false);
	}

	public static boolean isShootingStar(StockBean curr, boolean requireStrong) {
		double diffHL = curr.getH() - curr.getL();

		double shootingLevel = 0.0;
		if(requireStrong) {
			shootingLevel = curr.getL() + diffHL / 3;
		}else {
			shootingLevel = curr.getL() + diffHL / 2;
		}


		if(shootingLevel!=0.0 && curr.getO() <= shootingLevel && curr.getC() <= shootingLevel )
			return true;
		return false;
	}

	public static boolean isHammer(StockBean curr) {
		return isHammer(curr, false);
	}

	public static boolean isHammer(StockBean curr, boolean requireStrong) {
		double diffHL = curr.getH() - curr.getL();
		double shootingLevel = curr.getH() - diffHL / 3;
		boolean isBasicHarmer = (curr.getO() >= shootingLevel && curr.getC() >= shootingLevel );
		if(requireStrong) {
//			double shootingLevel = curr.getH() - diffHL / 3;
			if(isBasicHarmer && isBullishCandle(curr) )
				return true;
		}else {
//			double shootingLevel = curr.getH() - diffHL / 2;
//			if(curr.getO() >= shootingLevel && curr.getC() >= shootingLevel )
//				return true;
			return isBasicHarmer;
		}
		return false;
	}

	public static boolean isAbove2XVolume(double prevVol, double curVol) {
		double ratio = curVol / prevVol;
		if(ratio > 2.5)
			return true;
		return false;
	}

	public static boolean isAboveVolume(double prevVol, double curVol, double minRatio) {
		double ratio = curVol / prevVol;
		if(ratio > minRatio)
			return true;
		return false;
	}

	public static boolean isBelowHalfVolume(double prevVol, double curVol) {
		double ratio = curVol / prevVol;
		if(0.5 > ratio)
			return true;
		return false;
	}

	public static double getVolumeIncreaseRatio(StockBean prev, StockBean curr) {
		return (double)curr.getVolume() / (double)prev.getVolume();
	}





	public static String getTodayImportantCandlestickTradingPattern(List<StockBean> stockList) {
		StockBean curr = stockList.get(stockList.size()-1);
		StockBean prev = stockList.get(stockList.size()-2);
		StockBean prev2 = stockList.get(stockList.size()-3);

		Set<CandlestickTradingPatternType> hashSet = new HashSet<CandlestickTradingPatternType>();


		boolean isOpenHighSustain = KHelper.isOpenHighSustain(prev, curr);
		boolean isOpenHighCloseLow = KHelper.isOpenHighCloseLow(prev, curr);
		boolean isOpenLowCloseHigh = KHelper.isOpenLowCloseHigh(prev, curr);
		boolean isOpenLowCloseLow = KHelper.isOpenLowCloseLow(prev, curr);


		double topPrev = KHelper.getBodyTopValue(prev);
		double bottomPrev =KHelper.getBodyBottomValue(prev);

		double topCurr =KHelper.getBodyTopValue(curr);
		double bottomCurr =KHelper.getBodyBottomValue(curr);

//		if(KHelper.isHarami(prev, curr)){
//			hashSet.add(CandlestickTradingPatternType.HARAMI);
//		}
		if(KHelper.isDoji(curr)){
			hashSet.add(CandlestickTradingPatternType.DOJI);
		}


		if(KHelper.isBullishCandle(prev) && KHelper.isBearishCandle(curr) && curr.getBodyTop() == prev.getBodyTop() && curr.getH()==curr.getBodyTop() && curr.getH() == prev.getH()) {
			hashSet.add(CandlestickTradingPatternType.TWEEZER_TOP);
		}

		if(KHelper.isBearishCandle(prev) && KHelper.isBullishCandle(curr) && curr.getBodyBottom() == prev.getBodyBottom() && curr.getL()==curr.getBodyBottom() && curr.getL() == prev.getL()) {
			hashSet.add(CandlestickTradingPatternType.TWEEZER_BOTTOM);
		}


		if( KHelper.isBullishCandle(curr) && curr.getO() == curr.getL() && curr.getC()==curr.getH() )
		{
			hashSet.add(CandlestickTradingPatternType.FULL_WHITE);
		}

		if( KHelper.isBearishCandle(curr) && curr.getC() == curr.getL() && curr.getO()==curr.getH() )
		{
			hashSet.add(CandlestickTradingPatternType.FULL_BLACK);
		}


		if(KHelper.isBearishCandle(curr) && topPrev< topCurr && bottomPrev>bottomCurr) {
			hashSet.add(CandlestickTradingPatternType.BEARISH_ENGULFING);
		}

//		if(KHelper.isBullishCandle(prev) && KHelper.isBearishCandle(curr)
//				&& prev.getH()< curr.getO() && prev.getL()>bottomCurr) {
//			hashSet.add(CandlestickTradingPatternType.BEARISH_ENGULFING_2);
//		}

//		if(KHelper.isBearishCandle(prev) && KHelper.isBearishCandle(curr)
//				&& topPrev< topCurr && bottomPrev>bottomCurr) {
//			hashSet.add(CandlestickTradingPatternType.BEARISH_BLACK_ENGULFING);
//		}

		if(KHelper.isBullishCandle(curr) && topPrev< topCurr && bottomPrev>bottomCurr) {
			hashSet.add(CandlestickTradingPatternType.BULLISH_ENGULFING);
		}
//		if(KHelper.isBearishCandle(prev) && KHelper.isBullishCandle(curr)
//				&& prev.getH() < curr.getC() && prev.getL()> curr.getO()) {
//			hashSet.add(CandlestickTradingPatternType.BULLISH_ENGULFING_2);
//		}

//		if(KHelper.isBullishCandle(prev) && KHelper.isBullishCandle(curr)
//				&& topPrev< topCurr && bottomPrev>bottomCurr) {
//			hashSet.add(CandlestickTradingPatternType.BULLISH_WHITE_ENGULFING);
//		}



		boolean isGapUp = KHelper.isGapUp(prev, curr);
		boolean isGapDown = KHelper.isGapDown(prev, curr);
		boolean isHammer= KHelper.isHammer(curr, true);
		boolean isShootingStar = KHelper.isShootingStar(curr, true);

		boolean isHammerPrev= KHelper.isHammer(prev, false);
		boolean isShootingStarPrev = KHelper.isShootingStar(prev, true);



		if(isGapUp && isShootingStar) {
			hashSet.add(CandlestickTradingPatternType.GAP_UP_BEARISH_SHOOTING);
		}
		if(isShootingStar) {
			hashSet.add(CandlestickTradingPatternType.INVERTED_HAMMER);
		}

		if(isGapDown && isHammer) {
			hashSet.add(CandlestickTradingPatternType.GAP_DOWN_HAMMER);
		}else if(isHammer) {
			hashSet.add(CandlestickTradingPatternType.HAMMER);
		}

		if(isGapUp ) {
			hashSet.add(CandlestickTradingPatternType.GAP_UP_BULLISH);
		}
		if(isGapDown) {
			hashSet.add(CandlestickTradingPatternType.GAP_DOWN_BEARISH);
		}

		if(isOpenHighSustain) {
			hashSet.add(CandlestickTradingPatternType.OPEN_HIGH_CLOSE_HIGH);
		}

		if(isOpenHighCloseLow) {
			hashSet.add(CandlestickTradingPatternType.OPEN_HIGH_CLOSE_LOW);
		}
		if(isOpenLowCloseHigh) {
			hashSet.add(CandlestickTradingPatternType.OPEN_LOW_CLOSE_HIGH);
		}

		if(isOpenLowCloseLow) {
			hashSet.add(CandlestickTradingPatternType.OPEN_LOW_CLOSE_LOW);
		}


		//三飛烏鴉
		boolean b1 =false;
		b1 = prev2.getBodyBottom()>prev.getBodyTop() && prev.getBodyBottom()>curr.getBodyTop()
			&& KHelper.isBearishCandle(prev2) && KHelper.isBearishCandle(prev) && KHelper.isBearishCandle(curr)
			&& prev2.getL()> prev.getL() && prev.getL() > curr.getL();
		b1 = b1 && prev2.getVolume() < prev.getVolume() && prev.getVolume() < curr.getVolume();

		if(b1)
			hashSet.add(CandlestickTradingPatternType.THREE_DARK_DROP);

		if(hashSet.isEmpty())
			return Const.NA;
		else
			return hashSet.toString();
	}


	public static int getBodySize(List<StockBean> list) {
		int noOfDay = 10;

		if(list.size()<=noOfDay)
			noOfDay = list.size()-1;

		//Extract the stock bean for the past 5 days (not including today).
		List<StockBean> subList = list.subList(list.size()- noOfDay, list.size()-1);

		Double avgBodyChgPct = subList.stream().mapToDouble(StockBean::getBodyDailyChgPct).average().orElse(0);
		Double maxBodyChgPct = subList.stream().mapToDouble(StockBean::getBodyDailyChgPct).max().orElse(0);

		StockBean last = list.get(list.size()-1);
		StockBean prev = list.get(list.size()-2);

		double ratio = last.getBodyDailyChgPct() / avgBodyChgPct;
		double ratioCounterMaxPct = last.getBodyDailyChgPct() / maxBodyChgPct;

		int bodyType = KBodyType.NONE.getValue();

		if(ratioCounterMaxPct >1 && ratio > 2) {
			bodyType = KBodyType.XL.getValue();
		}if(ratioCounterMaxPct >0.8 && ratio >= 1.8) {
				bodyType = KBodyType.L.getValue();
		}else if(last.getBodyDailyChgPct()>= prev.getBodyDailyChgPct() && ratio >=1.5 && ratio <=1.8) {
			bodyType = KBodyType.M.getValue();
		}else if(ratio >0.8 && ratio <1.5) {
			bodyType = KBodyType.GENERAL.getValue();
		}else if(ratio >0.5 && ratio <= 0.8) {
			bodyType = KBodyType.S.getValue();
		}else {
			bodyType = KBodyType.XS.getValue();
		}



		return bodyType;
	}

	public static String isBigBodyToday(List<StockBean> list) {
		List<StockBean> subList = list.subList(0, list.size());
		return convertKBodyName(subList);
	}
	public static String isBigBodyPrevDay(List<StockBean> list){
		List<StockBean> subList = list.subList(0, list.size()-1);
		return convertKBodyName(subList);
	}
	public static String convertKBodyName(List<StockBean> list)
	{
		int bodyLv = getBodySize(list);
		
		StockBean last = list.get(list.size()-1);
		StockBean prev = list.get(list.size()-2);
		double topPrev = KHelper.getBodyTopValue(prev);
		double bottomPrev =KHelper.getBodyBottomValue(prev);
		
		String candleMsgPrefix = "";

		if(KHelper.isDoji( last)){
			return CandlestickTradingPatternType.DOJI.toString();
		}

//		if(KHelper.isBearishCandle(last) && last.getC() > topPrev) {
//			candleMsgPrefix.add(CandlestickTradingPatternType.FAKE_BLACK_TRUE_WHITE_BODY.toString());
//		}
//		if(KHelper.isBullishCandle(last) && last.getC() < bottomPrev) {
//			candleMsgPrefix.add(CandlestickTradingPatternType.FAKE_RED_TRUE_BLACK.toString());
//		}
//		if(KHelper.isHarami(prev, last)){
//			candleMsgPrefix = ", "+ CandlestickTradingPatternType.HARAMI;
//		}

		
		// for white K
		if(bodyLv == KBodyType.XL.getValue() && last.isOpenLowCloseHigh()) {//&& last.getBodyTop()> prev.getC()) {
			return KPatternConst.XL_WHITE_K + candleMsgPrefix;
		}
		if(bodyLv == KBodyType.L.getValue() && last.isOpenLowCloseHigh()) {//&& last.getBodyTop()> prev.getC()) {
			return KPatternConst.L_WHITE_K + candleMsgPrefix;
		}
		if(bodyLv == KBodyType.M.getValue() && last.isOpenLowCloseHigh()) {//&& last.getBodyTop()> prev.getC()) {
			return KPatternConst.M_WHITE_K + candleMsgPrefix;
		}
		if(bodyLv == KBodyType.S.getValue() && last.isOpenLowCloseHigh()) {//&& last.getBodyTop()> prev.getC()) {
			return KPatternConst.S_WHITE_K + candleMsgPrefix;
		}
		if(bodyLv == KBodyType.GENERAL.getValue() && last.isOpenLowCloseHigh()) {//&& last.getBodyTop()> prev.getC()) {
			return KPatternConst.G_WHITE_K + candleMsgPrefix;
		}
		
		if(bodyLv == KBodyType.XS.getValue() && last.isOpenLowCloseHigh()) {//&& last.getBodyTop()> prev.getC()) {
			return KPatternConst.XS_WHITE_K + candleMsgPrefix;
		}
		
		
		// for dark K
		if(bodyLv == KBodyType.XL.getValue() && !last.isOpenLowCloseHigh()) {//&& last.getBodyBottom()< prev.getC()) {
			return KPatternConst.XL_DARK_K + candleMsgPrefix;
		}
		if(bodyLv == KBodyType.L.getValue() && !last.isOpenLowCloseHigh()) {//&& last.getBodyBottom()< prev.getC()) {
			return KPatternConst.L_DARK_K + candleMsgPrefix;
		}
		if(bodyLv == KBodyType.M.getValue() && !last.isOpenLowCloseHigh()) {//&& last.getBodyBottom()< prev.getBodyBottom()) {
			return KPatternConst.M_DARK_K + candleMsgPrefix;
		}
		if(bodyLv == KBodyType.S.getValue() && !last.isOpenLowCloseHigh()) {//&& last.getBodyBottom()< prev.getBodyBottom()) {
			return KPatternConst.S_DARK_K + candleMsgPrefix;
		}
		if(bodyLv == KBodyType.GENERAL.getValue() && !last.isOpenLowCloseHigh()) {//&& last.getBodyBottom()< prev.getBodyBottom()) {
			return KPatternConst.G_DARK_K + candleMsgPrefix;
		}
		if(bodyLv == KBodyType.XS.getValue() && !last.isOpenLowCloseHigh()) {//&& last.getBodyBottom()< prev.getBodyBottom()) {
			return KPatternConst.XS_DARK_K + candleMsgPrefix;
		}
		
		return Const.NA;
	}
	
	public static GapBean getGapBean(StockBean prev, StockBean curr) {
		if(prev==null)
			return null;
		
		GapBean lastGap = null;
		
		if(KHelper.isGapUp(prev, curr)) {
			GapBean gapBean = new GapBean(curr.getStockCode(), curr.getTxnDate());
			gapBean.setGapBottom(prev.getH());
			gapBean.setGapTop(curr.getL());
			gapBean.setUP(true);
			double volRatio = 0;
			if(prev.getVolume()!=0) {
				volRatio = curr.getVolume() / prev.getVolume();
			}
			double gapPtc = (curr.getL() - prev.getH())/ prev.getH();
			if(volRatio < 1.5)
				gapBean.setEnoughtVolume(false);
			gapBean.setGapPct(gapPtc);
			gapBean.setVolumeChgPct(curr.getDayVolumeChgPct());

			lastGap = gapBean;
		}else if(KHelper.isGapDown(prev, curr)) {
			GapBean gapBean = new GapBean(curr.getStockCode(), curr.getTxnDate());
			gapBean.setGapBottom(curr.getH());
			gapBean.setGapTop(prev.getL());
			gapBean.setUP(false);

			double gapPtc = (curr.getH() - prev.getL())/ prev.getL();
			gapBean.setGapPct(gapPtc);
			gapBean.setVolumeChgPct(curr.getDayVolumeChgPct());

			lastGap = gapBean;
		}
		return lastGap;
	}
	
	public static String getGapTypeDisplayMessage(List<StockBean> stockList) {
		StockBean last = stockList.get(stockList.size()-1);
		int days = RuleConst.DAYS_10;
		String defaultRtnString = Const.SPACE+"\t"+Const.SPACE+"\t"+Const.SPACE+"\t"+Const.SPACE;
		
		if(stockList.size() <= days)
			return defaultRtnString;
		
		List<StockBean>  prevMonthlyList = stockList.subList(stockList.size()- days, stockList.size());
		
		
		//find all Gap Info 
		GapBean lastGap = null;
		for (int i = prevMonthlyList.size()-1; i >0; i--) {
			StockBean prev = prevMonthlyList.get(i-1);
			StockBean curr = prevMonthlyList.get(i);
			
			GapBean gapBean = KHelper.getGapBean(prev, curr);
			if(gapBean != null) {
				lastGap = gapBean;
				break;
			}
			
		}
		
		if(lastGap ==null)
			return defaultRtnString;
		
		List<StockBean> subList = StreamTransformHelper.extractData(stockList, lastGap.getDate(), last.getTxnDate());

		StockBean maxHBean = subList.stream().max(Comparator.comparing(StockBean::getH)).orElseThrow(NoSuchElementException::new);		
		StockBean minLBean = subList.stream().min(Comparator.comparing(StockBean::getL)).orElseThrow(NoSuchElementException::new);
		
		
		String gapPct = GeneralHelper.toPct(lastGap.getGapPct());
		
		
		
		if(lastGap.isUP()==true) {
			if(minLBean.getL() <=lastGap.getGapBottom())
				return String.format("%s\t%s\t%s\t%s", "向上裂口(填)", gapPct, GeneralHelper.toPct(lastGap.getVolumeChgPct()), lastGap.getDate()); //return "向上裂口("+gapPct+") - 填\t"+lastGap.getDate();				
			else
				return String.format("%s\t%s\t%s\t%s", "向上裂口", gapPct, GeneralHelper.toPct(lastGap.getVolumeChgPct()), lastGap.getDate());//"向上裂口("+gapPct+")\t"+lastGap.getDate();
		}
		
		if(lastGap.isUP()==false) {
			if(maxHBean.getH() >= lastGap.getGapTop())				
				return String.format("%s\t%s\t%s\t%s", "向下裂口(填)", gapPct, GeneralHelper.toPct(lastGap.getVolumeChgPct()), lastGap.getDate());//"向下裂口("+gapPct+") - 填\t"+lastGap.getDate();
			else
				return String.format("%s\t%s\t%s\t%s", "向下裂口", gapPct, GeneralHelper.toPct(lastGap.getVolumeChgPct()), lastGap.getDate());//"向下裂口("+gapPct+")\t"+lastGap.getDate();				
		}
		return defaultRtnString;
	}
	
	
	public static String getGapTypeDisplayMessageV2(List<StockBean> stockList) {
		StockBean last = stockList.get(stockList.size()-1);

		String defaultRtnString = Const.SPACE+"\t"+Const.SPACE+"\t"+Const.SPACE+"\t"+Const.SPACE;
		
		if(stockList.size() <= RuleConst.MONTH_NUM_OF_DAYS)
			return defaultRtnString;
		
		List<StockBean>  prevMonthlyList = stockList.subList(stockList.size()- RuleConst.MONTH_NUM_OF_DAYS, stockList.size());
		
		
		//find all Gap Info 
		GapBean lastGap = null;
		List<GapBean> gapList = new ArrayList<GapBean>(5);
		
		for (int i = prevMonthlyList.size()-1; i >0; i--) {
			StockBean prev = prevMonthlyList.get(i-1);
			StockBean curr = prevMonthlyList.get(i);
			
			
			GapBean gapBean = KHelper.getGapBean(prev, curr);
			if(gapBean != null) {
				lastGap = gapBean;
				gapList.add(gapBean);
			}
			
		}
		
		if(lastGap ==null)
			return defaultRtnString;
		
		List<StockBean> subList = StreamTransformHelper.extractData(stockList, lastGap.getDate(), last.getTxnDate());

		StockBean maxHBean = subList.stream().max(Comparator.comparing(StockBean::getH)).orElseThrow(NoSuchElementException::new);		
		StockBean minLBean = subList.stream().min(Comparator.comparing(StockBean::getL)).orElseThrow(NoSuchElementException::new);
		
		
		String gapPct = GeneralHelper.toPct(lastGap.getGapPct());
		
		
		
		if(lastGap.isUP()==true) {
			if(minLBean.getL() <=lastGap.getGapBottom())
				return String.format("%s\t%s\t%s\t%s", "向上裂口(填)", gapPct, GeneralHelper.toPct(lastGap.getVolumeChgPct()), lastGap.getDate()); //return "向上裂口("+gapPct+") - 填\t"+lastGap.getDate();				
			else
				return String.format("%s\t%s\t%s\t%s", "向上裂口", gapPct, GeneralHelper.toPct(lastGap.getVolumeChgPct()), lastGap.getDate());//"向上裂口("+gapPct+")\t"+lastGap.getDate();
		}
		
		if(lastGap.isUP()==false) {
			if(maxHBean.getH() >= lastGap.getGapTop())				
				return String.format("%s\t%s\t%s\t%s", "向下裂口(填)", gapPct, GeneralHelper.toPct(lastGap.getVolumeChgPct()), lastGap.getDate());//"向下裂口("+gapPct+") - 填\t"+lastGap.getDate();
			else
				return String.format("%s\t%s\t%s\t%s", "向下裂口", gapPct, GeneralHelper.toPct(lastGap.getVolumeChgPct()), lastGap.getDate());//"向下裂口("+gapPct+")\t"+lastGap.getDate();				
		}
		return defaultRtnString;
	}
	
	@Deprecated
	public static String getBigDarkBodyWithinTheDays_OLD(List<StockBean> srcstockList, int noOfDay) {
		
		StockBean last = srcstockList.get(srcstockList.size()-1);
		
		List<StockBean> subList = srcstockList.subList(srcstockList.size()- noOfDay, srcstockList.size());

		Double avgBodyChgPct = subList.stream().mapToDouble(StockBean::getBodyDailyChgPct).average().orElse(0);		
		Double maxBodyChgPct = subList.stream().mapToDouble(StockBean::getBodyDailyChgPct).max().orElse(0);
		
		boolean isFound = false; //isBigBody
		int idx = subList.size()-1;
		for(; idx >0; idx--) 
		{
			StockBean prev = subList.get(idx-1);
			StockBean curr = subList.get(idx);
			
			double ratio = curr.getBodyDailyChgPct() / avgBodyChgPct;
			double ratioCounterMaxPct = curr.getBodyDailyChgPct() / maxBodyChgPct;
			if(curr.getDayChgPct()>=0 || curr.getC() > prev.getBodyBottom())
				continue;
			if(ratio >1.5 && (curr.getDayVolumeChgPct()>1.8  || ratioCounterMaxPct>=0.7) ) {
				isFound = true;
				break;
			}
			
			
		}// end loop
		
		String rtn = Const.SPACE;
		if(isFound) 
		{								
			StockBean target = subList.get(idx);
			rtn = target.getTxnDate();
			if(last.getC() > target.getBodyTop())
				rtn += " ABV";
			else
				rtn += " BLW";
		}
		
		return rtn;			
	}
	
	
	

	
	//find the changes in short-term moving averages
	public static String getShortTermMovingAverageChanges(List<StockBean> stockList) {
//		int ma5D = 5;
//		int ma10D = 10;
		StockBean curr= stockList.get(stockList.size()-1);
		StockBean prev= stockList.get(stockList.size()-2);
		
		if(curr.getPriceSma()==null)
			return Const.NA;
		
//		List<StockBean> prevList = stockList.subList(0, stockList.size()-1);
//		List<StockBean> prevPrevList = prevList.subList(0, prevList.size()-1);
		
		double ma5 = curr.getPriceSma().getMa5();	//MovingAvgHelper.getMAbyLength(prevList, curr, ma5D);
		double ma10 = curr.getPriceSma().getMa10();//MovingAvgHelper.getMAbyLength(prevList, curr, ma10D);
		
		double ma5Prev = prev.getPriceSma().getMa5();//MovingAvgHelper.getMAbyLength(prevPrevList, prev, ma5D);
		double ma10Prev = prev.getPriceSma().getMa10();//MovingAvgHelper.getMAbyLength(prevPrevList, prev, ma10D);
		
		if((curr.getC() > ma5 && curr.getC() > ma10) && (prev.getC() <= ma5Prev || prev.getC() <= ma10Prev)) 
			return "5D,10D之上 (NEW)";
		else if((curr.getC() < ma5 && curr.getC() < ma10)  && (prev.getC() >= ma5Prev || prev.getC() >= ma10Prev))
			return "5D,10D之下 (NEW)";
		else if(curr.getC() > ma10 && prev.getC() <= ma10Prev)
			return "10D之上 (NEW)";
		else if(curr.getC() < ma10 && prev.getC() >= ma10Prev)
			return "10D之下 (NEW)";
		else if(curr.getC() > ma5 && prev.getC() <= ma5Prev)
			return "5D之上 (NEW)";
		else if(curr.getC() < ma5 && prev.getC() >= ma5Prev)
			return "5D之下 (NEW)";
		else
			return Const.NA;
	}
	

}
