package com.sjm.test.yahdata.analy.ta;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.beanutils.BeanUtils;

import com.sjm.test.yahdata.analy.bean.VolumePriceBean;
import com.sjm.test.yahdata.analy.bean.raw.StockBean;
import com.sjm.test.yahdata.analy.cfg.BacktestConfig;
import com.sjm.test.yahdata.analy.conts.ta.CandleEventTagEnum;
import com.sjm.test.yahdata.analy.conts.ta.RuleConst;
import com.sjm.test.yahdata.analy.stats.rule.VolatileCloseUPRule;
import com.sjm.test.yahdata.analy.ta.rule.event.BearishAbandonedBabyRule;
import com.sjm.test.yahdata.analy.ta.rule.event.BearishEngulfingRule;
import com.sjm.test.yahdata.analy.ta.rule.event.BearishEngulfingStrongRule;
import com.sjm.test.yahdata.analy.ta.rule.event.BullishAbandonedBabyRule;
import com.sjm.test.yahdata.analy.ta.rule.event.BullishEngulfingPast2DaysTopRule;
import com.sjm.test.yahdata.analy.ta.rule.event.BullishEngulfingRule;
import com.sjm.test.yahdata.analy.ta.rule.event.BullishEngulfingStrongRule;
import com.sjm.test.yahdata.analy.ta.rule.event.BullishPriceVolPushUpRule;
import com.sjm.test.yahdata.analy.ta.rule.event.CloseAboveStarRule;
import com.sjm.test.yahdata.analy.ta.rule.event.CloseBelowStarRule;
import com.sjm.test.yahdata.analy.ta.rule.event.DayBeforeThanksgivingRule;
import com.sjm.test.yahdata.analy.ta.rule.event.DownwardBreakoutFailureRule;
import com.sjm.test.yahdata.analy.ta.rule.event.Engulfing21WithNormallVolRule;
import com.sjm.test.yahdata.analy.ta.rule.event.EngulfingWithNormallVolRule;
import com.sjm.test.yahdata.analy.ta.rule.event.EveningStarRule;
//import com.sjm.test.yahdata.analy.ta.rule.event.FakeBlackTrueRedRule;
//import com.sjm.test.yahdata.analy.ta.rule.event.FakeRedTrueBlackRule;
import com.sjm.test.yahdata.analy.ta.rule.event.FallBodyLowerLowRule;
import com.sjm.test.yahdata.analy.ta.rule.event.FallBodyVolatileLv2VolRule;
import com.sjm.test.yahdata.analy.ta.rule.event.FallBodyVolatileVolRule;
import com.sjm.test.yahdata.analy.ta.rule.event.GBUBRule;
import com.sjm.test.yahdata.analy.ta.rule.event.GapDownHammerRule;
import com.sjm.test.yahdata.analy.ta.rule.event.GapDownLargeRule;
import com.sjm.test.yahdata.analy.ta.rule.event.GapDownRule;
import com.sjm.test.yahdata.analy.ta.rule.event.GapUpBearishShootingRule;
import com.sjm.test.yahdata.analy.ta.rule.event.GapUpLargeRule;
import com.sjm.test.yahdata.analy.ta.rule.event.GapUpRule;
import com.sjm.test.yahdata.analy.ta.rule.event.GapupAndGoRule;
import com.sjm.test.yahdata.analy.ta.rule.event.GoStrongRule;
import com.sjm.test.yahdata.analy.ta.rule.event.GoWeakRule;
import com.sjm.test.yahdata.analy.ta.rule.event.MonthlyHighCloseRule;
import com.sjm.test.yahdata.analy.ta.rule.event.MonthlyHighRule;
import com.sjm.test.yahdata.analy.ta.rule.event.MonthlyLowVolumeThenRaiseRule;
import com.sjm.test.yahdata.analy.ta.rule.event.MonthlyLowestCloseRule;
import com.sjm.test.yahdata.analy.ta.rule.event.MorningStarRule;
import com.sjm.test.yahdata.analy.ta.rule.event.OpenHighCloseHighRule;
import com.sjm.test.yahdata.analy.ta.rule.event.OpenHighCloseHighVolumeIncrementalRule;
import com.sjm.test.yahdata.analy.ta.rule.event.OpenHighCloseLowRule;
import com.sjm.test.yahdata.analy.ta.rule.event.OpenHighCloseLowVolumeIncrementalRule;
import com.sjm.test.yahdata.analy.ta.rule.event.OpenLowCloseHighInRedBodyRule;
import com.sjm.test.yahdata.analy.ta.rule.event.PriceHalfEngulfingWithSmallVolRule;
import com.sjm.test.yahdata.analy.ta.rule.event.PriceOverNDoubleVolRule;
import com.sjm.test.yahdata.analy.ta.rule.event.PriceOverNNormalVolRule;
import com.sjm.test.yahdata.analy.ta.rule.event.QuarterlyHighCloseRule;
import com.sjm.test.yahdata.analy.ta.rule.event.QuarterlyLowestCloseRule;
import com.sjm.test.yahdata.analy.ta.rule.event.RaiseBodyVolatileVolRule;
import com.sjm.test.yahdata.analy.ta.rule.event.TwoWhiteWithDarkinTheMiddleRule;
import com.sjm.test.yahdata.analy.ta.rule.event.UpBreakoutFailureRule;
import com.sjm.test.yahdata.analy.ta.rule.event.VolDecreaseHalfRule;
import com.sjm.test.yahdata.analy.ta.rule.event.VolDecreaseMostRule;
import com.sjm.test.yahdata.analy.ta.rule.event.VolIncreaseDoubleRule;
import com.sjm.test.yahdata.analy.ta.rule.event.VolIncreaseOver2XRule;
import com.sjm.test.yahdata.analy.ta.rule.event.WeeklyHighCloseFirstTimeRule;
import com.sjm.test.yahdata.analy.ta.rule.event.WeeklyHighCloseRule;
import com.sjm.test.yahdata.analy.ta.rule.event.WeeklyHighFirstTimeRule;
import com.sjm.test.yahdata.analy.ta.rule.event.WeeklyHighRule;
import com.sjm.test.yahdata.analy.ta.rule.event.WeeklyLowestCloseFirstTimeRule;
import com.sjm.test.yahdata.analy.ta.rule.event.WeeklyLowestCloseRule;
import com.sjm.test.yahdata.analy.ta.rule.event.WeeklyLowestFirstTimeRule;
import com.sjm.test.yahdata.analy.ta.rule.event.WeeklyLowestRule;
//import com.sjm.test.yahdata.analy.ta.rule.event.occur.LongBoomRule;
import com.sjm.test.yahdata.analy.ta.rule.event.pattern.BigDropAndStandUpFormRule;
import com.sjm.test.yahdata.analy.ta.rule.event.pattern.GapDownAndStandUpRule;
import com.sjm.test.yahdata.analy.ta.rule.ma.event.CrossDown10D50DRule;
import com.sjm.test.yahdata.analy.ta.rule.ma.event.CrossDown2D19DRule;
import com.sjm.test.yahdata.analy.ta.rule.ma.event.CrossDown3D18DRule;
import com.sjm.test.yahdata.analy.ta.rule.ma.event.CrossDown50D200DRule;
import com.sjm.test.yahdata.analy.ta.rule.ma.event.CrossDown50D250DRule;
import com.sjm.test.yahdata.analy.ta.rule.ma.event.CrossDown5D10DRule;
import com.sjm.test.yahdata.analy.ta.rule.ma.event.CrossUp10D20DRule;
import com.sjm.test.yahdata.analy.ta.rule.ma.event.CrossUp10D50DRule;
import com.sjm.test.yahdata.analy.ta.rule.ma.event.CrossUp150D200DRule;
import com.sjm.test.yahdata.analy.ta.rule.ma.event.CrossUp2D19DRule;
import com.sjm.test.yahdata.analy.ta.rule.ma.event.CrossUp3D18DRule;
import com.sjm.test.yahdata.analy.ta.rule.ma.event.CrossUp3MALinesRule;
import com.sjm.test.yahdata.analy.ta.rule.ma.event.CrossUp50D200DRule;
import com.sjm.test.yahdata.analy.ta.rule.ma.event.CrossUp50D250DRule;
import com.sjm.test.yahdata.analy.ta.rule.ma.event.CrossUp5D10DRule;
import com.sjm.test.yahdata.analy.ta.rule.ma.event.FullLongArrangeRule;
import com.sjm.test.yahdata.analy.ta.rule.ma.event.MABullishCorssingRule;
import com.sjm.test.yahdata.analy.ta.rule.ma.event.MidTermLongArrangeRule;
import com.sjm.test.yahdata.analy.ta.rule.ma.event.ShortArrangeRule;
import com.sjm.test.yahdata.analy.ta.rule.ma.event.SmallLongArrangeRule;
import com.sjm.test.yahdata.analy.ta.rule.ma.event.UpBreak50DIn3DaysRule;
import com.sjm.test.yahdata.analy.ta.rule.ma.event.UpBreak50DRule;

import lombok.extern.slf4j.Slf4j;
@Slf4j
public class EventRuleHandler {
	private Set<VolRuleBase> ruleBaseSet = null;
	
	public EventRuleHandler() {
		String msg = " ***   " +BacktestConfig.MA_VALIDATE_CFG +" ***";
		log.info("\n"+msg);
		
		ruleBaseSet = new HashSet<VolRuleBase>(50);
		
		ruleBaseSet.add(new Engulfing21WithNormallVolRule());
		ruleBaseSet.add(new BullishEngulfingPast2DaysTopRule());
		ruleBaseSet.add(new EngulfingWithNormallVolRule());
		ruleBaseSet.add(new PriceOverNDoubleVolRule());
		ruleBaseSet.add(new PriceOverNNormalVolRule());
		ruleBaseSet.add(new FallBodyVolatileVolRule());		
		ruleBaseSet.add(new FallBodyVolatileLv2VolRule());
		ruleBaseSet.add(new FallBodyLowerLowRule());
		ruleBaseSet.add(new GapUpBearishShootingRule());
		ruleBaseSet.add(new GapUpRule());
		ruleBaseSet.add(new GapDownRule());
		ruleBaseSet.add(new GapDownLargeRule());
		ruleBaseSet.add(new GapUpLargeRule(0.02));
		ruleBaseSet.add(new GapUpLargeRule(0.03));
		ruleBaseSet.add(new GapUpLargeRule(0.04));
		ruleBaseSet.add(new GapUpLargeRule(0.05));
		ruleBaseSet.add(new GapUpLargeRule(0.06));
		ruleBaseSet.add(new GapUpLargeRule(0.08));
		ruleBaseSet.add(new GapUpLargeRule(1));
		ruleBaseSet.add(new GapDownAndStandUpRule());
		ruleBaseSet.add(new BigDropAndStandUpFormRule());
		ruleBaseSet.add(new BearishEngulfingRule());
		ruleBaseSet.add(new BullishEngulfingRule());		
		ruleBaseSet.add(new MonthlyHighRule());
		ruleBaseSet.add(new MonthlyHighCloseRule());
		ruleBaseSet.add(new MonthlyLowestCloseRule());
		ruleBaseSet.add(new MonthlyLowVolumeThenRaiseRule());
		
		ruleBaseSet.add(new QuarterlyHighCloseRule());
		ruleBaseSet.add(new QuarterlyLowestCloseRule());
		
		ruleBaseSet.add(new OpenHighCloseHighVolumeIncrementalRule());
		ruleBaseSet.add(new OpenHighCloseHighRule());
		ruleBaseSet.add(new OpenHighCloseLowVolumeIncrementalRule());
		ruleBaseSet.add(new OpenHighCloseLowRule());
		ruleBaseSet.add(new OpenLowCloseHighInRedBodyRule());
		ruleBaseSet.add(new PriceHalfEngulfingWithSmallVolRule());
		ruleBaseSet.add(new RaiseBodyVolatileVolRule());		
		ruleBaseSet.add(new WeeklyHighCloseRule());
		ruleBaseSet.add(new WeeklyHighCloseFirstTimeRule());
		ruleBaseSet.add(new WeeklyHighRule());
		ruleBaseSet.add(new WeeklyHighFirstTimeRule());		
		ruleBaseSet.add(new WeeklyLowestRule());
		ruleBaseSet.add(new WeeklyLowestCloseRule());
		ruleBaseSet.add(new WeeklyLowestFirstTimeRule());
		ruleBaseSet.add(new WeeklyLowestCloseFirstTimeRule());
		
		
		ruleBaseSet.add(new CrossUp5D10DRule());
		ruleBaseSet.add(new CrossUp3D18DRule());
		ruleBaseSet.add(new CrossUp2D19DRule());
		ruleBaseSet.add(new CrossUp150D200DRule());
		ruleBaseSet.add(new CrossUp50D250DRule());
		ruleBaseSet.add(new CrossUp50D200DRule());
		ruleBaseSet.add(new CrossUp10D50DRule());
		ruleBaseSet.add(new CrossUp10D20DRule());
		ruleBaseSet.add(new CrossDown2D19DRule());
		
		ruleBaseSet.add(new CrossDown5D10DRule());
		ruleBaseSet.add(new CrossDown2D19DRule());
		ruleBaseSet.add(new CrossDown3D18DRule());
		ruleBaseSet.add(new CrossDown10D50DRule());
		ruleBaseSet.add(new CrossDown50D250DRule());
		ruleBaseSet.add(new CrossDown50D200DRule());	
		ruleBaseSet.add(new CrossUp3MALinesRule());
		ruleBaseSet.add(new FullLongArrangeRule());
		ruleBaseSet.add(new SmallLongArrangeRule());
		ruleBaseSet.add(new MidTermLongArrangeRule());
		ruleBaseSet.add(new ShortArrangeRule());
//		ruleBaseSet.add(new FakeBlackTrueRedRule());
//		ruleBaseSet.add(new FakeRedTrueBlackRule());
		
		ruleBaseSet.add(new BullishEngulfingStrongRule());		
		ruleBaseSet.add(new BearishEngulfingStrongRule());
		
		ruleBaseSet.add(new TwoWhiteWithDarkinTheMiddleRule());
		
		boolean isRed = true;
		boolean isBlack = false;
		ruleBaseSet.add(new VolIncreaseDoubleRule(isRed));
		ruleBaseSet.add(new VolIncreaseDoubleRule(isBlack));
		ruleBaseSet.add(new VolIncreaseOver2XRule(isRed));
		ruleBaseSet.add(new VolIncreaseOver2XRule(isBlack));
		ruleBaseSet.add(new VolDecreaseHalfRule(isRed));
		ruleBaseSet.add(new VolDecreaseHalfRule(isBlack));
		ruleBaseSet.add(new VolDecreaseMostRule(isRed));
		ruleBaseSet.add(new VolDecreaseMostRule(isBlack));
		ruleBaseSet.add(new VolatileCloseUPRule(isRed, 0.03));
		ruleBaseSet.add(new VolatileCloseUPRule(isRed, 0.04));
		ruleBaseSet.add(new VolatileCloseUPRule(isRed, 0.06));
		ruleBaseSet.add(new VolatileCloseUPRule(isRed, 0.08));
		ruleBaseSet.add(new VolatileCloseUPRule(isRed, 0.1));
		
		ruleBaseSet.add(new VolatileCloseUPRule(isBlack, -0.03));
		ruleBaseSet.add(new VolatileCloseUPRule(isBlack, -0.04));
		ruleBaseSet.add(new VolatileCloseUPRule(isBlack, -0.06));
		ruleBaseSet.add(new VolatileCloseUPRule(isBlack, -0.08));
		ruleBaseSet.add(new VolatileCloseUPRule(isBlack, -0.1));
		ruleBaseSet.add(new CloseAboveStarRule());
		ruleBaseSet.add(new CloseBelowStarRule());
		
		ruleBaseSet.add(new GapDownHammerRule());
		ruleBaseSet.add(new GapupAndGoRule());
		ruleBaseSet.add(new GBUBRule());


		ruleBaseSet.add(new BearishAbandonedBabyRule());
		ruleBaseSet.add(new BullishAbandonedBabyRule());
		ruleBaseSet.add(new BullishPriceVolPushUpRule());
		ruleBaseSet.add(new UpBreakoutFailureRule());
		ruleBaseSet.add(new DownwardBreakoutFailureRule());
		ruleBaseSet.add(new MorningStarRule());
		ruleBaseSet.add(new EveningStarRule());
		
		ruleBaseSet.add(new UpBreak50DRule());
		ruleBaseSet.add(new UpBreak50DIn3DaysRule());
		
		ruleBaseSet.add(new GoStrongRule());
		ruleBaseSet.add(new GoWeakRule());
		
		ruleBaseSet.add(new MABullishCorssingRule());
		
		ruleBaseSet.add(new DayBeforeThanksgivingRule());
//		ruleBaseSet.add(new StatsLowerShadowRule()); //too many		
//		ruleBaseSet.add(new WhiteBodyLowerShadowInYesterdayRule()); //too many
//		ruleBaseSet.add(new GapUpWhiteBodyLowerShadowRule()); //too many
		
		
//		ruleBaseSet.add(new LongBoomRule()); //too many
	}
	
	/*
	public Set<CandleTagEnum> fireRules(List<StockBean> prevList, StockBean curr) {
		
		Set<CandleTagEnum> signSet = new HashSet<CandleTagEnum>();
				
		for (VolRuleBase volRuleBase : ruleBaseSet) {
			boolean b = volRuleBase.detect(prevList, curr);
			
			if(b) {
				signSet.add(volRuleBase.getCandleTag());				
			}
		}
		
		return signSet;
		
	}
	*/
	private VolumePriceBean fireRules(List<StockBean> prevList, StockBean curr, int stockListIdx, List<CandleEventTagEnum> candlePatternArray) {
		if(prevList.size() < 10)
			return null;
		
		Set<CandleEventTagEnum> signSet = new HashSet<CandleEventTagEnum>();
		boolean isBenchmarkEvent = false;
		boolean isOccurenceEvent = false;
		for (VolRuleBase volRuleBase : ruleBaseSet) {
			
			if(candlePatternArray!=null &&
					(candlePatternArray.contains(volRuleBase.getBenchmarkCandleTag())==false
					&& candlePatternArray.contains(volRuleBase.getOccurCandleTag())==false )
					) {
				continue;
			}
			boolean b = volRuleBase.detect(prevList, curr);
			
			if(b) {
				if(volRuleBase.getBenchmarkCandleTag()!=null) {
					signSet.add(volRuleBase.getBenchmarkCandleTag());
					isBenchmarkEvent = true;
				}
				
				if(volRuleBase.getOccurCandleTag()!=null) {
					signSet.add(volRuleBase.getOccurCandleTag());
					isOccurenceEvent = true;
				}
			}
		}
		
		if(signSet.isEmpty())
			return null;
		
		VolumePriceBean tmp = new VolumePriceBean(curr.getStockCode());
		tmp.setSignSet(signSet);
		tmp.setChainIdx(stockListIdx);
		tmp.setBenchmarkEvent(isBenchmarkEvent);
		tmp.setOccurenceEvent(isOccurenceEvent);
		try {
			BeanUtils.copyProperties(tmp ,curr);			
		} catch (Exception e) {
			e.printStackTrace();
		} 		
		
		return tmp;
		
	}

//	private VolumePriceBean fireRules(List<StockBean> prevList, StockBean curr, int stockListIdx) {
//		
//		Set<CandleTagEnum> signSet = new HashSet<CandleTagEnum>();
//		boolean isBenchmarkEvent = false;
//		boolean isOccurenceEvent = false;
//		for (VolRuleBase volRuleBase : ruleBaseSet) {
//			boolean b = volRuleBase.detect(prevList, curr);
//			
//			if(b) {
//				if(volRuleBase.getBenchmarkCandleTag()!=null) {
//					signSet.add(volRuleBase.getBenchmarkCandleTag());
//					isBenchmarkEvent = true;
//				}
//				
//				if(volRuleBase.getOccurCandleTag()!=null) {
//					signSet.add(volRuleBase.getOccurCandleTag());
//					isOccurenceEvent = true;
//				}
//			}
//		}
//		
//		if(signSet.isEmpty())
//			return null;
//		
//		VolumePriceBean tmp = new VolumePriceBean(curr.getStockCode());
//		tmp.setSignSet(signSet);
//		tmp.setChainIdx(stockListIdx);
//		tmp.setBenchmarkEvent(isBenchmarkEvent);
//		tmp.setOccurenceEvent(isOccurenceEvent);
//		try {
//			BeanUtils.copyProperties(tmp ,curr);			
//		} catch (Exception e) {
//			e.printStackTrace();
//		} 		
//		
//		return tmp;
//		
//	}
	
	
	
	public Set<VolRuleBase> getRuleBaseSet(){
		if(ruleBaseSet == null)
			ruleBaseSet = new HashSet<VolRuleBase>(50);
		
		return ruleBaseSet;
	}
	
	
	
	public  List<VolumePriceBean> goThruRules(List<StockBean> trunkList, List<CandleEventTagEnum> candlePatternArray) {
		
		int startIdx = RuleConst.YEAR_NUM_OF_DAYS ;
//		if(trunkList.size()<MIN_DATA_SIZE) {
//			log.warn("Return goThruRules() function , due to Hist data size < "+MIN_DATA_SIZE);
//			return new ArrayList<VolumePriceBean>(0);
//		}
		
//		EventRuleHandler ruleHandler = new EventRuleHandler();
		if(startIdx > trunkList.size())
			startIdx = 1;
			
		List<VolumePriceBean> resultList = new ArrayList<VolumePriceBean>(10);
		
		for (int i = startIdx; i < trunkList.size(); i++) {
			
			int mRangeStartId = i - RuleConst.YEAR_NUM_OF_DAYS; //MONTHLY_SAMPLE_SIZE;
			if(mRangeStartId<0)
				mRangeStartId=0;
			List<StockBean> prevMonthlyList = trunkList.subList(mRangeStartId, i);

			
			StockBean curr = trunkList.get(i);
			/*
			Set<CandleTagEnum> signSet = ruleHandler.fireRules(prevMonthlyList, curr);
			if(signSet.isEmpty())
				continue;
			
			VolumePriceBean tmp = new VolumePriceBean(curr.getStockCode());
			tmp.setSignSet(signSet);
			tmp.setChainIdx(i);
			try {
				BeanUtils.copyProperties(tmp ,curr);			
			} catch (Exception e) {
				e.printStackTrace();
			} 
			*/
			
			VolumePriceBean tmp = this.fireRules(prevMonthlyList, curr, i, candlePatternArray);
			if(tmp!=null)
				resultList.add(tmp);
			
			if(BacktestConfig.isPrintCandleTag)
				tmp.printResult();
			
		}
		return resultList;
		
	}
	
	

	public  List<VolumePriceBean> goThruRules(List<StockBean> trunkList, int MIN_DATA_SIZE,  List<CandleEventTagEnum> candlePatternArray) {
		
		
		if(trunkList==null || trunkList.size()<MIN_DATA_SIZE) {
			log.warn("Return goThruRules() function , due to Hist data size < "+MIN_DATA_SIZE);
			return new ArrayList<VolumePriceBean>(0);
		}
		
		
		List<VolumePriceBean> resultList = new ArrayList<VolumePriceBean>(10);
		int startIdxWindow = 0;

		try {
		//case 1
		if(trunkList.size() >= RuleConst.YEAR_NUM_OF_DAYS) {
			startIdxWindow = trunkList.size() - MIN_DATA_SIZE;
//			startIdxLoop = startIdxWindow - RuleConst.DEFAULT_NUM_OF_DAYS;
//		}else if(trunkList.size() == MIN_DATA_SIZE) {
//			startIdxLoop = 0;
		}else {
			startIdxWindow = trunkList.size() - MIN_DATA_SIZE ;
//			startIdxLoop = 0;
		}
		
		for (int i = 0; i < MIN_DATA_SIZE; i++) {		

			List<StockBean> prevList = trunkList.subList(0, startIdxWindow) ;
			
			StockBean curr = trunkList.get(startIdxWindow);
			
//			startIdxLoop++;
			startIdxWindow++;
			
			
			VolumePriceBean tmp = this.fireRules(prevList, curr, i, candlePatternArray);
			if(tmp!=null)
				resultList.add(tmp);
			
			if(BacktestConfig.isPrintCandleTag)
				tmp.printResult();
			
		}
		}catch(Exception e) {
			e.printStackTrace();
		}
		return resultList;
		
	}

}
