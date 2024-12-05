package com.sjm.test.yahdata.analy.module.wavepoint;

import java.util.*;
import java.util.stream.Collectors;

import com.maas.util.DateHelper;
import com.sjm.test.yahdata.analy.bean.raw.StockBean;
import com.sjm.test.yahdata.analy.conts.Const;
import com.sjm.test.yahdata.analy.conts.ta.StockTrendStatus;
import com.sjm.test.yahdata.analy.conts.type.WaveType;
import com.sjm.test.yahdata.analy.helper.StreamTransformHelper;
import com.sjm.test.yahdata.analy.module.wavepoint.bean.WavePoint;
import com.sjm.test.yahdata.analy.module.wavepoint.bean.WavePointAnalyticalResult;
import com.sjm.test.yahdata.analy.module.wavepoint.bean.WaveShape;

import lombok.extern.slf4j.Slf4j;
@Slf4j
public class PriceTopBotWavePointHandler {
	public static int NO_OF_DAYS = 60;
	
//	public static int WAVE_CHECK_START_POSITION = 5;
//	public static int WAVE_CHECK_END_POSITION = 10;	
	public static int WAVE_CHECK_PERIOD_DAYS = 10;
	
//	public static int WAVE_CHECK_START_POSITION = 3;
//	public static int WAVE_CHECK_END_POSITION = 7;	
//	public static int WAVE_CHECK_DAYS_PERIOD = 10;
	
	private WavePatternAnalyticalResultHelper wavePointResultHelper = null;
	
	public PriceTopBotWavePointHandler() {
		wavePointResultHelper = new WavePatternAnalyticalResultHelper();
	}
	
	//Default to get 100 records to analysis
	public WaveShape doTopBot(List<StockBean> stockList) {
			
		return this.doTopBot( WAVE_CHECK_PERIOD_DAYS, stockList);
	}
	
	public WaveShape doTopBot(int periodDays, List<StockBean> stockList) {
		Map<String, WavePoint> rawTopMap = new HashMap<String, WavePoint>();
		Map<String, WavePoint> rawBotMap = new HashMap<String, WavePoint>();

		int subListStartIdx = (stockList.size() - NO_OF_DAYS <0)?0:stockList.size() - NO_OF_DAYS;
		List<StockBean> targetStockList = stockList.subList(subListStartIdx, stockList.size());
		
		int len = targetStockList.size();

		StockBean la1 =stockList.get(stockList.size()-1);
		StockBean la2 =stockList.get(stockList.size()-2);
		StockBean la3TorB =stockList.get(stockList.size()-3);
		StockBean la4 =stockList.get(stockList.size()-4);
		StockBean la5 =stockList.get(stockList.size()-5);


		if( ( la1.getL() < la2.getL() && la1.getL() < la3TorB.getL() ) 
			&& (la3TorB.getH() > la1.getH() && la3TorB.getH() > la2.getH() && la3TorB.getH() > la4.getH() && la3TorB.getH() > la5.getH())
			) {
			WavePoint wp = new WavePoint(WaveType.TOP, la3TorB);
			rawTopMap.put(wp.getDate(), wp);
		}
		
		if(la1.getH() > la2.getH() && la1.getH() > la3TorB.getH() 
		&& la3TorB.getL() < la1.getL() && la3TorB.getL() < la2.getL() && la3TorB.getL() < la4.getL() && la3TorB.getL() < la5.getL()
		) {
			WavePoint wp = new WavePoint(WaveType.BOT, la3TorB);
			rawBotMap.put(wp.getDate(), wp);
		}

		for (int i = 0; i < len; i++) {

			int endIdx = i + periodDays;

			if(endIdx >= len)
				break;

			List<StockBean> subStockList = targetStockList.subList(i, endIdx);

			this.storeWaveHighAndLow(periodDays, subStockList, rawTopMap, rawBotMap);
		}
		/*
		///////////////////////// OLD version //////////////////////
		for (int i = len; i >= periodDays; i--) {

			int startIdx = i - periodDays;
			int endIdx = i;

			List<StockBean> subStockList = targetStockList.subList(startIdx, endIdx);
			this.fillWaveHighAndLow(periodDays, subStockList, rawTopMap, rawBotMap);

		}
		/////////////////////////
*/
		List<WavePoint> topbotList = new ArrayList<WavePoint>();
		topbotList.addAll(rawTopMap.values());
		topbotList.addAll(rawBotMap.values());
		
		List<WavePoint> srcSortedTopBotList = topbotList.stream().sorted(Comparator.comparing(WavePoint::getDateInt)).collect(Collectors.toList());

		List<WavePoint> sortedTopBotList = filterAlternatingWavePoints(srcSortedTopBotList);
		List<WavePoint> sortedTopList = sortedTopBotList.stream().filter(x-> WaveType.TOP==  x.getType()).collect(Collectors.toList());
		List<WavePoint> sortedBotList = sortedTopBotList.stream().filter(x-> WaveType.BOT==  x.getType()).collect(Collectors.toList());
//		List<WavePoint> sortedTopList = topMap.values().stream().sorted(Comparator.comparing(e -> e.getDateInt())).collect(Collectors.toList());
//		List<WavePoint> sortedBotList = botMap.values().stream().sorted(Comparator.comparing(e -> e.getDateInt())).collect(Collectors.toList());
		
		WaveShape ws = new WaveShape(periodDays);
		ws.setSortedTopList(sortedTopList);
		ws.setSortedBotList(sortedBotList);

		String waveShape = wavePointResultHelper.findShape(stockList, sortedTopBotList, sortedTopList, sortedBotList);
		String waveSituation = this.findSituation(stockList, sortedTopList, sortedBotList);
		waveShape = (waveShape==null)?"":waveShape;
		ws.setShapeResult(waveShape);
		ws.setWaveSituation(waveSituation);
//		PriceVolResult volPriceDivergenceResult = this.calcPriceVolumeChanges(stockList, sortedTopBotList);
//		ws.setRecentVolPriceDivergencyResult(volPriceDivergenceResult);
		return ws;
	}

	public String findSituation(List<StockBean> stockList, List<WavePoint> sortedTopList, List<WavePoint> sortedBotList){
		if(sortedTopList.isEmpty() || sortedBotList.isEmpty()){
			return "BOT/TOP EMPTY";
		}
		StockBean last1StockBean = stockList.get(stockList.size()-1);
		StockBean last2StockBean = stockList.get(stockList.size()-2);

		WavePoint last1Top = sortedTopList.get(sortedTopList.size()-1);
		WavePoint last1Bot = sortedBotList.get(sortedBotList.size()-1);

//		int dayBetweenFromFirstTop = DateHelper.dayBetween(sortedTopList.get(0).getDate(), last1StockBean.getTxnDate());
		long countStandTop = sortedTopList.stream()
				.filter(wp -> last1StockBean.getBodyTop() >=wp.getH()
						&& last1StockBean.getC() > last1StockBean.getO()
						&& last1StockBean.getBodyTop() > last1Top.getStockBean().getBodyTop()
				)
				.count();
		int totalTop = sortedTopList.size();
		if(countStandTop > 0){
			return "在浪頂上 ("+countStandTop+"/"+totalTop+"個)";//"在"+countStandTop+"個浪頂上";
		}

//		int dayBetweenFromFirstBot = DateHelper.dayBetween(sortedBotList.get(0).getDate(), last1StockBean.getTxnDate());

		int totalBot = sortedBotList.size();
		long countUnderBottom = sortedBotList.stream()
				.filter(wp -> last1StockBean.getBodyBottom() <= wp.getL()
						&& last1StockBean.getC() < last1StockBean.getO()
						&& last1StockBean.getBodyBottom() <= last1Bot.getStockBean().getBodyBottom()
				)
				.count();
		if(countUnderBottom > 0){
			return "在浪底下 ("+countUnderBottom+"/"+totalBot+"個)";//"在"+countUnderBottom+"個浪底下";
		}


		WavePoint lastTop = sortedTopList.get(sortedTopList.size()-1);
		WavePoint lastBot = sortedBotList.get(sortedBotList.size()-1);

		boolean isSameTopBotLevel = (lastTop.getStockBean().getBodyBottom() <lastBot.getH() || lastBot.getStockBean().getBodyTop() >lastTop.getL());

		boolean isRebounding = (last1StockBean.getC() > last1StockBean.getO() && last1StockBean.getDayChgPct() > 0)
								&& last1StockBean.getC() > lastBot.getStockBean().getBodyTop()
								&& last1StockBean.getH() < lastTop.getStockBean().getBodyTop()
								&& last1StockBean.getBodyTop() > last2StockBean.getBodyTop()
								;

		boolean isAdjusting = (last1StockBean.getC() < last1StockBean.getO() && last1StockBean.getDayChgPct() < 0)
				&& last1StockBean.getC() < lastTop.getStockBean().getBodyBottom()
				&& last1StockBean.getL() > lastBot.getStockBean().getBodyBottom()
				&& last1StockBean.getBodyBottom() < last2StockBean.getBodyBottom();

		if(isRebounding && isAdjusting) {
			return "";
		}

		if(isSameTopBotLevel){
			if(isAdjusting){
				return "區間"+Const.WAIT+Const.DOWN;
			}
			if(isRebounding ){
				return "區間"+Const.WAIT+Const.UP;
			}
		}else{
			if(isAdjusting ){
				return "浪調整中";
			}
			if(isRebounding ){
				return "浪反彈中";
			}
		}

		return "";
	}


	public static List<WavePoint> filterAlternatingWavePoints(List<WavePoint> wavePoints) {
		List<WavePoint> result = new ArrayList<>();
		if (wavePoints.isEmpty()) {
			return result;
		}

		WavePoint currentBest = wavePoints.get(0);
		WaveType lastType = currentBest.getType();

		for (int i = 1; i < wavePoints.size(); i++) {
			WavePoint current = wavePoints.get(i);
			if (current.getType().equals(lastType)) {
				// 如果当前类型与上一个类型相同，选择最优的WavePoint
				if (lastType.equals(WaveType.BOT)) {
					if (current.getL() < currentBest.getL()) {
						currentBest = current;
					}
				} else if (lastType.equals(WaveType.TOP)) {
					if (current.getH() > currentBest.getH()) {
						currentBest = current;
					}
				}
			} else {
				// 如果当前类型与上一个类型不同，添加最优的WavePoint到结果列表
				result.add(currentBest);
				currentBest = current;
				lastType = current.getType();
			}
		}

		// 添加最后一个WavePoint
		result.add(currentBest);

		return result;
	}










	private void storeWaveHighAndLow(int periodDays, List<StockBean> stockList, Map<String, WavePoint> topMap, Map<String, WavePoint> botMap) {
//		if( stockList.get(0).getTxnDateInt() >= 20240918)
//			System.out.println("stockList.get(0).getTxnDateInt() = "+stockList.get(0).getTxnDateInt());

		int WAVE_CHECK_START_POSITION = periodDays/2 - 1;
		int WAVE_CHECK_END_POSITION = periodDays/2 + 1;

		int maxIdx = StreamTransformHelper.findMaxIndex(stockList);
		int minIdx = StreamTransformHelper.findMinIndex(stockList);
		// 3 , 7
		if (maxIdx >= WAVE_CHECK_START_POSITION && maxIdx <= WAVE_CHECK_END_POSITION) {
			WavePoint wp = new WavePoint(WaveType.TOP, stockList.get(maxIdx));
			topMap.put(wp.getDate(), wp);
		}

		// 3 , 7
		if (minIdx >= WAVE_CHECK_START_POSITION && minIdx <= WAVE_CHECK_END_POSITION) {
			WavePoint wp = new WavePoint(WaveType.BOT, stockList.get(minIdx));
			botMap.put(wp.getDate(), wp);
		}

	}

	@Deprecated
	private void fillWaveHighAndLow(int periodDays, List<StockBean> stockList, Map<String, WavePoint> topMap, Map<String, WavePoint> botMap) {
		if( stockList.get(0).getTxnDateInt() >= 20240918)
			System.out.println("stockList.get(0).getTxnDateInt() = "+stockList.get(0).getTxnDateInt());

		int WAVE_CHECK_START_POSITION = periodDays/2 - 2;
		int WAVE_CHECK_END_POSITION = periodDays/2 + 2;

		int maxIdx = StreamTransformHelper.findMaxIndex(stockList);
		int minIdx = StreamTransformHelper.findMinIndex(stockList);
		// 3 , 7
		if (maxIdx >= WAVE_CHECK_START_POSITION && maxIdx <= WAVE_CHECK_END_POSITION) {
			WavePoint wp = new WavePoint(WaveType.TOP, stockList.get(maxIdx));
			topMap.put(wp.getDate(), wp);
		}

		// 3 , 7
		if (minIdx >= WAVE_CHECK_START_POSITION && minIdx <= WAVE_CHECK_END_POSITION) {
			WavePoint wp = new WavePoint(WaveType.BOT, stockList.get(minIdx));
			botMap.put(wp.getDate(), wp);
		}

	}

	@Deprecated
	private WavePointAnalyticalResult measure(int periodDays, List<StockBean> stockList ,StockBean curStockBean, Collection<WavePoint> topList, Collection<WavePoint> botList) {
		
		WavePointAnalyticalResult wpresult = new WavePointAnalyticalResult(periodDays);
		wpresult.setCode(curStockBean.getStockCode());
		wpresult.setTopList(topList.stream().sorted(Comparator.comparing(e -> e.getDateInt())).collect(Collectors.toList()));
		wpresult.setBotList(botList.stream().sorted(Comparator.comparing(e -> e.getDateInt())).collect(Collectors.toList()));
		
		List<WavePoint> topbotList = new ArrayList<WavePoint>();
		topbotList.addAll(topList);
		topbotList.addAll(botList);
		
		List<WavePoint> sortedTopBotList = topbotList.stream().sorted(Comparator.comparing(e -> e.getDateInt())).collect(Collectors.toList());
		
		if(sortedTopBotList.size()<3) {
			wpresult.setRemark(Const.NA);
			return wpresult;
		}
		
		StockBean prev1Stock = stockList.get(stockList.size()-2);
		StockBean prev2Stock = stockList.get(stockList.size()-3);
		
		WavePoint wp = sortedTopBotList.get(sortedTopBotList.size() - 1);
		WavePoint wpPrev = sortedTopBotList.get(sortedTopBotList.size() - 2);
		WavePoint wpPrev2 = sortedTopBotList.get(sortedTopBotList.size() - 3);
		
		WaveType type = wp.getType();
		
		String defaultRemark = "("+wp.toString() +", "+wpPrev.toString()+", "+wpPrev2.toString()+")";
		
		if(WaveType.TOP.equals(type)) {
			
			if (curStockBean.getBodyTop() > wp.getStockBean().getBodyTop() 
				&& curStockBean.getH() > wp.getStockBean().getH() 
				&& curStockBean.isRiseToday()) 
			{
				// "升破頂(直接)"
//				boolean isFirstDay = !(prev1Stock.getBodyTop() > wp.getStockBean().getH());
				boolean isFirstDay = (prev1Stock.getBodyTop() < wp.getStockBean().getH());
				//boolean isTwoDay = ( prev1Stock.getBodyTop() >= wp.getStockBean().getH() && prev1Stock.isRiseToday())&& ( prev2Stock.getBodyTop() < wp.getStockBean().getH() );
				boolean isSecondDay = (prev1Stock.getBodyTop() > wp.getStockBean().getBodyTop() && prev1Stock.getH() > wp.getStockBean().getH()) && prev2Stock.getBodyTop() < wp.getStockBean().getH();
				int dayCnt = 0;
				if(isFirstDay) dayCnt++;
				if(isSecondDay) dayCnt++;
				
				String breakDegree = Const.SPACE;
				if(curStockBean.getC() >=  wp.getStockBean().getH()) {
					breakDegree = WavePointAnalyticalResult.BREAK_DEGREE_FIRM;
				}
				
				wpresult = wavePointResultHelper.getDirectUpBreak(periodDays, sortedTopBotList, stockList, curStockBean, breakDegree, dayCnt );
				wpresult.setPrevTopAndBot(wp);
			} else if (curStockBean.getC() < wp.getStockBean().getH()) {
				
				if(WaveType.BOT.equals(wpPrev.getType()) && WaveType.TOP.equals(wpPrev2.getType()) 
						&& curStockBean.getC() < wpPrev2.getStockBean().getBodyTop() 
						//&& Const.NA.equalsIgnoreCase(helper.isFalseBreakthrough(stockList, sortedTopBotList))==false
						&& wavePointResultHelper.isTopReversal(stockList, sortedTopBotList).contains(Const.NA)==false
						) 
				{
					// False-UP-Break,反轉向下,假突破
					wpresult = wavePointResultHelper.getFalseUpBreakthroughResult(periodDays, sortedTopBotList, stockList, curStockBean);
					wpresult.setPrevTopAndBot(wpPrev);
				}else if(WaveType.BOT.equals(wpPrev.getType()) 
						&& WaveType.TOP.equals(wpPrev2.getType()) 
						&& curStockBean.getC() >= wpPrev.getStockBean().getBodyBottom()
						&& curStockBean.getL() > wpPrev.getStockBean().getL()
//						&& Const.NA.equalsIgnoreCase(helper.isFalseBreakthrough(stockList, sortedTopBotList))==true
						&& wavePointResultHelper.isTopReversal(stockList, sortedTopBotList).contains(Const.NA)==true
						) 
				{
					// 回調
					wpresult = wavePointResultHelper.getAdjustmentResult(periodDays, sortedTopBotList, stockList, curStockBean);

				}else if(WaveType.BOT.equals(wpPrev.getType()) && curStockBean.getC() < wpPrev.getStockBean().getL()) {
					
					boolean isFirstDay = !(prev1Stock.getC() < wpPrev.getStockBean().getL());
					//跌破前底"
					wpresult = wavePointResultHelper.getDownBreakPrevBot(periodDays, sortedTopBotList, stockList, curStockBean, isFirstDay);
					wpresult.setPrevTopAndBot(wpPrev);
				}else if(WaveType.TOP.equals(wpPrev.getType()) && WaveType.BOT.equals(wpPrev2.getType()) && curStockBean.getC() < wpPrev2.getStockBean().getBodyBottom()) {
					wpresult.setStockTrendStatus(StockTrendStatus.SEEKING_BOTTOM);
//					wpresult.setStockTrendRemark();
				}else {
					wpresult.setRemark(defaultRemark);
				}

			} else {
				wpresult.setRemark(defaultRemark);
			}
			
		}else if(WaveType.BOT.equals(type)) 
		{
			if ( curStockBean.getC() > wp.getStockBean().getL()) 
			{				
				
				//wpPrev should be TOP
				if(WaveType.TOP.equals(wpPrev.getType()) 
						&& curStockBean.getBodyTop()> wpPrev.getStockBean().getBodyTop() 
						&& curStockBean.getH() > wpPrev.getStockBean().getH() 
						&& curStockBean.isRiseToday()) 
				{					
					boolean isFirstDay = prev1Stock.getBodyTop() < wpPrev.getStockBean().getH();
//					boolean isTwoDay = (prev1Stock.getBodyTop()> wpPrev.getStockBean().getH() &&  prev1Stock.isRiseToday())  && (!(prev2Stock.getBodyTop() > wpPrev.getStockBean().getH()) ) ;

					boolean isSecondDay = (prev1Stock.getBodyTop() > wpPrev.getStockBean().getBodyTop() && prev1Stock.getH() > wpPrev.getStockBean().getH()) && prev2Stock.getBodyTop() < wpPrev.getStockBean().getH();

					
					
					int breakDayCnt = 0;
					if(isFirstDay) breakDayCnt++;
					if(isSecondDay) breakDayCnt++;

					String breakDegree = Const.SPACE;
					if(curStockBean.getC() >=  wpPrev.getStockBean().getH()) {
						breakDegree = WavePointAnalyticalResult.BREAK_DEGREE_FIRM;
					}
					//升破前頂
					wpresult = wavePointResultHelper.getUpBreakPrevTop(periodDays, sortedTopBotList, stockList, curStockBean, breakDegree, breakDayCnt);
					wpresult.setPrevTopAndBot(wpPrev);
				}else if(WaveType.TOP.equals(wpPrev.getType()) 
						&& curStockBean.getC()< wpPrev.getStockBean().getH() 
//						&& Const.NA.equalsIgnoreCase(helper.isFalseBreakthrough(stockList, sortedTopBotList)) == false
						&& wavePointResultHelper.isTopReversal(stockList, sortedTopBotList).contains(Const.NA)== false
						) 
				{
					//false breakthrough 反轉(假突破)
					wpresult = wavePointResultHelper.getFalseUpBreakthroughResult(periodDays, sortedTopBotList, stockList, curStockBean);
					wpresult.setPrevTopAndBot(wpPrev);
				}else if (
						(
						(WaveType.TOP.equals(wpPrev.getType()) && WaveType.BOT.equals(wpPrev2.getType()))
						|| (WaveType.BOT.equals(wp.getType()) && WaveType.BOT.equals(wpPrev.getType()))
						)
						&& wavePointResultHelper.isBottomReversal(stockList, sortedTopBotList).contains(Const.NA) == false
						) 
				{
					//反彈(破底翻)
					wpresult = wavePointResultHelper.getFalseDownBreakthroughResult(periodDays, sortedTopBotList, stockList, curStockBean);
					wpresult.setPrevTopAndBot(wpPrev);
				}else if(WaveType.TOP.equals(wpPrev.getType()) 
						&& curStockBean.getH()< wpPrev.getStockBean().getH() 
						&& prev1Stock.getH()<curStockBean.getC() ) 
				{
					//rebounding(反彈)
					wpresult = wavePointResultHelper.getReboundResult(periodDays, sortedTopBotList, stockList, curStockBean);
				}else if ( curStockBean.getC() > wp.getStockBean().getL() && curStockBean.getL() < wp.getStockBean().getL()) 
				{
					wpresult.setStockTrendStatus(StockTrendStatus.BREAKDOWN_DIRECT);
					wpresult.setStockTrendRemark("(D(0)");

//					if(isFirstDay)
//						wpresult.setTrendWay(wpresult.getTrendWay()+ " (今天)");
					wpresult.setPrevTopAndBot(wp);
					wpresult.setRemark(defaultRemark);
					
				}else {
					wpresult.setRemark(defaultRemark);
				}
			} else if ( curStockBean.getC() < wp.getStockBean().getL()) 
			{
				boolean isFirstDay = !(prev1Stock.getC() < wp.getStockBean().getL());

				wpresult = wavePointResultHelper.getDirectDownBreak(periodDays, sortedTopBotList, stockList, curStockBean, isFirstDay);
//				wpresult.setTrendWay("跌破底(直接)");
			}else {
				wpresult.setRemark(defaultRemark);
			}
		} else {
			wpresult.setRemark(defaultRemark);
		}

		//topbotList.addAll(topList);
//		topbotList.addAll(botList);
		
		
		wpresult.setTopList(topList.stream().sorted(Comparator.comparing(e -> e.getDateInt())).collect(Collectors.toList()));
		wpresult.setBotList(botList.stream().sorted(Comparator.comparing(e -> e.getDateInt())).collect(Collectors.toList()));
		
		String shape = wavePointResultHelper.findShape(stockList, sortedTopBotList, wpresult.getTopList(), wpresult.getBotList());
		
		wpresult.setShape(shape);
//		PriceVolResult volPriceDivergenceResult = this.calcPriceVolumeChanges(stockList, sortedTopBotList);
//		wpresult.setRecentVolPriceDivergencyResult(volPriceDivergenceResult);
		return wpresult;

	}

//	 public PriceVolResult calcPriceVolumeChanges(List<StockBean> stockList, List<WavePoint> sortedTopBotList) {
//		 return wavePointResultHelper.calcPriceVolumeChanges(stockList, sortedTopBotList);
//	 }
	
//	public void handleTopList(List<WavePoint> topList) {
//		int size = topList.size() ;
//		WavePoint wpLast1 = topList.get(size-1);
//		WavePoint wpLast2 = topList.get(size-2);
//		WavePoint wpLast3 = topList.get(size-3);
//		
//		
//		double rangeRatio = getStagnantRatio( Arrays.asList(wpLast1.getH(), wpLast2.getH(), wpLast3.getH()));)
//	}
//	
//	
//	
//	public Double getStagnantRatio(List<Double> maList)//STAGNANT
//	{
//		Double minValue = Collections.min(maList);
//		Double maxValue = Collections.max(maList);
//		Double ratio = (maxValue - minValue)/minValue;
//		return ratio;
//	}
	

}