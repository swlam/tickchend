//package com.sjm.test.yahdata.analy.ta.rule.event.occur;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import com.sjm.test.yahdata.analy.bean.raw.StockBean;
//import com.sjm.test.yahdata.analy.helper.StreamTransformHelper;
//import com.sjm.test.yahdata.analy.module.wavepoint.WavePointAnalyticalResult;
//import com.sjm.test.yahdata.analy.module.wavepoint.WavePriceTopBotHandler;
//import com.sjm.test.yahdata.analy.module.wavepoint.bean.WavePoint;
//import com.sjm.test.yahdata.analy.ta.VolRuleBase;
//import com.sjm.test.yahdata.analy.ta.conts.CandleTagEnum;
//import com.sjm.test.yahdata.analy.ta.conts.RuleConst;
//import com.sjm.test.yahdata.analy.ta.helper.MovingAvgHelper;
//
//import lombok.extern.slf4j.Slf4j;
//
//@Slf4j
//public class LongBoomRule  extends VolRuleBase{
//	
//	private CandleTagEnum SIGN = CandleTagEnum.EVNT_LONG_BOOM;
//	private WavePriceTopBotHandler waveTopBotHandler = new WavePriceTopBotHandler();
//	
//	//short-term moving average
//	public LongBoomRule() {
//		
//	}
//	
//	public boolean detect(List<StockBean> prevList, StockBean curr) {
//		boolean b = validate(prevList, curr);
//		if(b==false)
//			return false;
//		
//		boolean bRtn = false;
//		
////		ArrayList<StockBean> cloneList = (ArrayList)((ArrayList<StockBean>)prevList).clone();
//		
//		List<StockBean> copy = new ArrayList<>();
//		copy.addAll(prevList);
//		copy.add(curr);
//		WavePointAnalyticalResult wpResult = waveTopBotHandler.doTopBot(copy);
//		
//		
//		
//		try {
//			bRtn = func(prevList, wpResult);
//			if(bRtn==true)
//				System.out.println("");
//		}catch(Exception e) {
//			log.error(curr.getTxnDate(), e);
//			
//		}
//		
//		return bRtn;
//	}	
//	
//	
//	
//	public boolean func(List<StockBean> stockList, WavePointAnalyticalResult wavePointList) throws Exception {
//		int stockSize = stockList.size();
//		
//		StockBean last = stockList.get(stockSize-1);
//		StockBean prev1 = stockList.get(stockSize-2);
//		StockBean prev2 = stockList.get(stockSize-3);
//		
////		StockBean maxByMonth = prevMonthlyList.stream().max(Comparator.comparing(StockBean::getH)).orElseThrow(NoSuchElementException::new);
//		StockBean mHBean = StreamTransformHelper.getPeriodHighest(stockList, RuleConst.MONTH_NUM_OF_DAYS);
//		
//		List<StockBean> prev1List = stockList.subList(0, stockSize-1);
//		List<StockBean> prev2List = stockList.subList(0, stockSize-2);
//		List<StockBean> prev3List = stockList.subList(0, stockSize-3);
//		
//		StockBean mHBeanPrevList = StreamTransformHelper.getPeriodHighest(prev1List, RuleConst.MONTH_NUM_OF_DAYS);
//		
//		
//		
//		boolean isFirstBreakMonthHighDate = false;
//		boolean isFirstBreakWaveTopDate = false;
//		boolean isPrev1BetweenMa10AndMa20 = false;
//		boolean isPrev2BetweenMa10AndMa20 = false;
//		boolean isTodayAboveMa10AndMa20 = false;
//		boolean isDoubleVolToday = false;
//		if(mHBean.getTxnDateInt() == last.getTxnDateInt() && mHBeanPrevList.getTxnDateInt()!=prev1.getTxnDateInt() ) {
//			isFirstBreakMonthHighDate = true;
//		}
//		
//		if(wavePointList==null || wavePointList.getTopList().size() ==0) {
//			isFirstBreakWaveTopDate = false;
//		}else {
//			WavePoint lastTopWavePoint = wavePointList.getTopList().get(wavePointList.getTopList().size()-1);
//			if(last.getC()>lastTopWavePoint.getH() && prev1.getH()<lastTopWavePoint.getH()) {
//				isFirstBreakWaveTopDate = true;
//			}
//		}
//		
//		double prev1MA20 = MovingAvgHelper.getMAbyLength(prev2List, prev1, 20)	;
//		double prev1MA10 = MovingAvgHelper.getMAbyLength(prev2List, prev1, 10)	;
//		double prev1TopBandMa = prev1MA10>prev1MA20? prev1MA10:prev1MA20;
//		double prev1BottomBandMa = prev1MA10<prev1MA20? prev1MA10:prev1MA20;
//		prev1TopBandMa = prev1TopBandMa * 1.01; // add up buffer
//		prev1BottomBandMa = prev1BottomBandMa * 0.99; // add bottom buffer
//		
//		
//		double prev2MA20 = MovingAvgHelper.getMAbyLength(prev3List, prev2, 20)	;
//		double prev2MA10 = MovingAvgHelper.getMAbyLength(prev3List, prev2, 10)	;
//		double prev2TopBandMa = prev2MA10>prev2MA20? prev2MA10:prev2MA20;
//		double prev2BottomBandMa = prev2MA10<prev2MA20? prev2MA10:prev2MA20;
//		prev2TopBandMa = prev2TopBandMa * 1.01; // add up buffer
//		prev2BottomBandMa = prev2BottomBandMa * 0.99; // add bottom buffer
//		
//		
//		if(prev1.getBodyTop()<=prev1TopBandMa && prev1.getBodyBottom()>=prev1BottomBandMa) {
//			isPrev1BetweenMa10AndMa20 = true;
//		}
//		
//		if(prev2.getBodyTop()<=prev2TopBandMa && prev2.getBodyBottom()>=prev2BottomBandMa) {
//			isPrev2BetweenMa10AndMa20 = true;
//		}
//		
//		if(last.getBodyTop() > prev1TopBandMa && last.getC() > prev1TopBandMa) { 
//			isTodayAboveMa10AndMa20 = true;
//		}
//
//		if( last.getVolume()/prev1.getVolume() >= 2.0 ) {
//			isDoubleVolToday = true;
//		}
//		
//		boolean rtn = isFirstBreakMonthHighDate && isFirstBreakWaveTopDate && isPrev1BetweenMa10AndMa20 && isPrev2BetweenMa10AndMa20 && isTodayAboveMa10AndMa20 && isDoubleVolToday;
//		
////		isDoubleVolToday = (last.getVolume()/prev.getVolume()>2.0);)
//		
////		for(int idx=stockSize ; idx>=0; idx--) {
////			List<StockBean> tmpSubList = stockList.subList(0, idx);
////			StockBean mHBean = StreamTransformHelper.getPeriodHighest(stockList, RuleConst.MONTH_NUM_OF_DAYS);	
////		}
//		
//		
//		return rtn;
//		
//	}
//	
//	
//	@Override
//	public CandleTagEnum getBenchmarkCandleTag() {
//		return SIGN;
//	}
//
//}
