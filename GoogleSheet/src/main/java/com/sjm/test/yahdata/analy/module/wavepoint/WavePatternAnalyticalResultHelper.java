package com.sjm.test.yahdata.analy.module.wavepoint;

import java.util.*;

import com.maas.util.GeneralHelper;
import com.sjm.test.yahdata.analy.bean.raw.StockBean;
import com.sjm.test.yahdata.analy.conts.Const;
import com.sjm.test.yahdata.analy.conts.KPatternConst;
import com.sjm.test.yahdata.analy.conts.ta.StockTrendStatus;
import com.sjm.test.yahdata.analy.conts.type.KBodyType;
import com.sjm.test.yahdata.analy.conts.type.VolumePriceType;
import com.sjm.test.yahdata.analy.conts.type.WaveType;
import com.sjm.test.yahdata.analy.helper.StreamTransformHelper;
import com.sjm.test.yahdata.analy.module.wavepoint.bean.WavePoint;
import com.sjm.test.yahdata.analy.module.wavepoint.bean.WavePointAnalyticalResult;
import com.sjm.test.yahdata.analy.pv.PriceVolDirectionHandler;
import com.sjm.test.yahdata.analy.pv.model.PriceVolResult;
import com.sjm.test.yahdata.analy.ta.KHelper;
import com.sjm.test.yahdata.analy.wavepattern.*;

public class WavePatternAnalyticalResultHelper {
	private PriceVolDirectionHandler handler = null;
	
	private DownBreakBotWavePattern downBreakWavePattern;
	private UpBreakTopWavePattern upBreakWavePattern;
	private UpBreakTriangleWavePattern upBreakTriangleWavePattern;
	private DownBreakTriangleWavePattern downBreakTriangleWavePattern;

	private TopReversalWavePattern topReversalWavePattern;
	private BottomReversalWavePattern bottomReversalWavePattern;

	private FlatBottomWaitingBreakWavePattern flatBottomWaitingBreakWavePattern;
	private FlatTopWaitingBreakWavePattern flatTopWaitingBreakWavePattern;


	public WavePatternAnalyticalResultHelper() {
		handler = new PriceVolDirectionHandler();

		downBreakWavePattern = new DownBreakBotWavePattern();
		upBreakWavePattern = new UpBreakTopWavePattern();

		upBreakTriangleWavePattern = new UpBreakTriangleWavePattern();
		downBreakTriangleWavePattern = new DownBreakTriangleWavePattern();

		topReversalWavePattern = new TopReversalWavePattern();
		bottomReversalWavePattern = new BottomReversalWavePattern();

		flatTopWaitingBreakWavePattern = new FlatTopWaitingBreakWavePattern();
		flatBottomWaitingBreakWavePattern = new FlatBottomWaitingBreakWavePattern();

	}
	
	//for wp=TOP AND (c < wp.H)
	public WavePointAnalyticalResult getAdjustmentResult(int periodDays, List<WavePoint> sortedTopBotList, List<StockBean> stockList, StockBean curStockBean) {
		WavePoint wp = sortedTopBotList.get(sortedTopBotList.size() - 1); //TOP 
		WavePoint wpPrev = sortedTopBotList.get(sortedTopBotList.size() - 2);//BOT
		WavePoint wpPrev2 = sortedTopBotList.get(sortedTopBotList.size() - 3);//TOP
		String lv = this.calcAdjustmentRatio(wp.getStockBean().getH(), wpPrev.getStockBean().getL(), curStockBean.getC());
		String falseBreakMsg = this.isTopReversal(stockList, sortedTopBotList);
		
		WavePointAnalyticalResult wpresult = new WavePointAnalyticalResult(periodDays);
		wpresult.setCode(curStockBean.getStockCode());
		wpresult.setReversalPattern(falseBreakMsg);
//		wpresult.setTrendWay("回調");
		wpresult.setStockTrendStatus(StockTrendStatus.PULLBACK);
		wpresult.setRemark(lv+" "+" ("+wp.toString() +", "+wpPrev.toString()+", "+wpPrev2.toString()+")");
		
		//(100 -90)* 0.618 + 90
//		double pullbackMaxValue = (wp.getH() - wpPrev.getL())*0.382 + wpPrev.getL();
//		boolean isBigPullback = curStockBean.getL() < pullbackMaxValue;
//		if(isBigPullback) {
			wpresult.setStockTrendRemark(lv);
//		}
			
		double differPct = (wp.getH() - curStockBean.getC()) / curStockBean.getC();
		boolean isSeemsUpBreak = differPct <= 0.02 && curStockBean.isRiseToday();
		if(isSeemsUpBreak) {
			wpresult.setStockTrendRemark("似"+Const.UP+"Break");
		}else {
			wpresult.setStockTrendRemark(lv);
		}
			
		return wpresult;
	}
	
	//for wp=TOP AND (c > wp.H)
	public WavePointAnalyticalResult getDirectUpBreak(int periodDays, List<WavePoint> sortedTopBotList, List<StockBean> stockList, StockBean curStockBean, String breakDegree, int breakDayCnt) {
		WavePoint wp = sortedTopBotList.get(sortedTopBotList.size() - 1);
		WavePoint wpPrev = sortedTopBotList.get(sortedTopBotList.size() - 2);
		WavePoint wpPrev2 = sortedTopBotList.get(sortedTopBotList.size() - 3);
		double breakPct = this.upBreakRatio(wp.getStockBean().getH(), curStockBean.getC());

		List<StockBean> subList= StreamTransformHelper.subListWithEndElement(stockList, wpPrev.getDate(), curStockBean.getTxnDate());
		StockBean maxHighBean = StreamTransformHelper.findMaxHighStock(subList);
		
		double maxBreakPct = this.upBreakRatio(wp.getStockBean().getH(), maxHighBean.getH());
		
		WavePointAnalyticalResult wpresult = new WavePointAnalyticalResult(periodDays);
		wpresult.setCode(curStockBean.getStockCode());
		
		wpresult.setBreakPct(breakPct);
		wpresult.setBreakExtreamPct(maxBreakPct);
		wpresult.setBreakDegree(breakDegree);
//		wpresult.setTrendWay("升破頂(直接)");
		wpresult.setStockTrendStatus(StockTrendStatus.BREAKOUT_DIRECT);

		if(breakDayCnt > 0) {
//			wpresult.setTrendWay(wpresult.getTrendWay()+ " ("+breakDayCnt+")");
			wpresult.setStockTrendRemark("D("+breakDayCnt+")");
		}
		wpresult.setPrevTopAndBot(wp);
		wpresult.setRemark(GeneralHelper.toPct(breakPct)+", to Highest:"+GeneralHelper.toPct(maxBreakPct)+" ("+wp.toString() +", "+wpPrev.toString()+", "+wpPrev2.toString()+")");
		
		return wpresult;
	}
	
	
	//for wp=TOP AND wpPrev = BOT AND (c < wp.H)
	public WavePointAnalyticalResult getDownBreakPrevBot(int periodDays, List<WavePoint> sortedTopBotList, List<StockBean> stockList, StockBean curStockBean, boolean isFirstDay) {
		WavePoint wp = sortedTopBotList.get(sortedTopBotList.size() - 1);
		WavePoint wpPrev = sortedTopBotList.get(sortedTopBotList.size() - 2);
		WavePoint wpPrev2 = sortedTopBotList.get(sortedTopBotList.size() - 3);
		
		double currBreakPct = this.downBreakRatio(wpPrev.getStockBean().getL(), curStockBean.getC());
		List<StockBean> subList= StreamTransformHelper.subListWithEndElement(stockList, wpPrev.getDate(), curStockBean.getTxnDate());
		StockBean minLowBean = StreamTransformHelper.findMinLowStock(subList);
		
		double minBreakPct = this.downBreakRatio(wpPrev.getStockBean().getL(), minLowBean.getL());
		
		WavePointAnalyticalResult wpresult = new WavePointAnalyticalResult(periodDays);
		wpresult.setCode(curStockBean.getStockCode());
		wpresult.setBreakPct(currBreakPct);
		wpresult.setBreakExtreamPct(minBreakPct);
//		wpresult.setTrendWay("跌破前底");
		wpresult.setStockTrendStatus(StockTrendStatus.BREAKDOWN);
		if(isFirstDay) {
//			wpresult.setTrendWay(wpresult.getTrendWay()+ " (今天)");
			wpresult.setStockTrendRemark(Const.D0);
		}
		wpresult.setPrevTopAndBot(wpPrev);
		wpresult.setRemark(GeneralHelper.toPct(currBreakPct)+", Lowest :"+ minBreakPct + " ("+wp.toString() +", "+wpPrev.toString()+", "+wpPrev2.toString()+")");
		
		return wpresult;
	}
	
	//for wp=BOT AND wpPrev=TOP AND (c < wpPrev.H) 
	public WavePointAnalyticalResult getReboundResult(int periodDays, List<WavePoint> sortedTopBotList, List<StockBean> stockList, StockBean curStockBean) {
		WavePoint wp = sortedTopBotList.get(sortedTopBotList.size() - 1);
		WavePoint wpPrev = sortedTopBotList.get(sortedTopBotList.size() - 2);
		WavePoint wpPrev2 = sortedTopBotList.get(sortedTopBotList.size() - 3);
		
		String pdfFound = this.isBottomReversal(stockList, sortedTopBotList);
		
		WavePointAnalyticalResult wpresult = new WavePointAnalyticalResult(periodDays);
		wpresult.setCode(curStockBean.getStockCode());
		
		String lv = this.calcReboundRatio(wpPrev.getStockBean().getH(), wp.getStockBean().getL(), curStockBean.getC());
		wpresult.setReversalPattern(pdfFound);
//		wpresult.setTrendWay("反彈");
		wpresult.setStockTrendStatus(StockTrendStatus.REBOUND);
		wpresult.setRemark(lv+"  ("+wp.toString() +", "+wpPrev.toString()+", "+wpPrev2.toString()+")");
		
		
		/// check further
		
		double differPct = (wpPrev.getH() - curStockBean.getC()) / curStockBean.getC();
		boolean isSeemsUpBreak = differPct <= 0.02 && curStockBean.isRiseToday();
		if(isSeemsUpBreak) {
			wpresult.setStockTrendRemark("似"+Const.UP+"Break");

		}else {
			wpresult.setStockTrendRemark(lv);
		}
		
		return wpresult;
	}
	
	public WavePointAnalyticalResult getFalseUpBreakthroughResult(int periodDays, List<WavePoint> sortedTopBotList, List<StockBean> stockList, StockBean curStockBean) {
		WavePoint wp = sortedTopBotList.get(sortedTopBotList.size() - 1);
		WavePoint wpPrev = sortedTopBotList.get(sortedTopBotList.size() - 2);
		WavePoint wpPrev2 = sortedTopBotList.get(sortedTopBotList.size() - 3);
		
		String falseBreakMsg = this.isTopReversal(stockList, sortedTopBotList);

		double downratio = this.downBreakRatio(wp.getStockBean().getBodyTop(), curStockBean.getC());
		
		WavePointAnalyticalResult wpresult = new WavePointAnalyticalResult(periodDays);
		wpresult.setCode(curStockBean.getStockCode());
		wpresult.setReversalPattern(falseBreakMsg);
//		wpresult.setTrendWay("反轉向下(假突破)");
		wpresult.setStockTrendStatus(StockTrendStatus.REVERSAL_DOWN);
//		wpresult.setTrendWay("反轉向下("+KPatternConst.KP_TOP_REVERSAL+")");
		wpresult.setStockTrendRemark(KPatternConst.KP_TOP_REVERSAL);
		wpresult.setRemark( GeneralHelper.toPct(downratio)+" "+wp.toString() +", "+wpPrev.toString()+", "+wpPrev2.toString()+")");
		
		return wpresult;
	}
	
	public WavePointAnalyticalResult getFalseDownBreakthroughResult(int periodDays, List<WavePoint> sortedTopBotList, List<StockBean> stockList, StockBean curStockBean) {
		WavePoint wp = sortedTopBotList.get(sortedTopBotList.size() - 1);
		WavePoint wpPrev = sortedTopBotList.get(sortedTopBotList.size() - 2);
		WavePoint wpPrev2 = sortedTopBotList.get(sortedTopBotList.size() - 3);
		
		String pdfMsg = this.isBottomReversal(stockList, sortedTopBotList);

		double l2CurrentPct = this.upBreakRatio(wp.getStockBean().getBodyBottom(), curStockBean.getC());
		
		WavePointAnalyticalResult wpresult = new WavePointAnalyticalResult(periodDays);
		wpresult.setCode(curStockBean.getStockCode());
		wpresult.setReversalPattern(pdfMsg);
//		wpresult.setTrendWay("反轉向上(破底翻)");
		wpresult.setStockTrendStatus(StockTrendStatus.REVERSAL_UP);
		wpresult.setStockTrendRemark(KPatternConst.KP_BOTTOM_REVERSAL);
		
		wpresult.setRemark(GeneralHelper.toPct(l2CurrentPct)+ " ("+wp.toString() +", "+wpPrev.toString()+", "+wpPrev2.toString()+")");
		return wpresult;
	}
	
	
	//for wp=BOT AND wpPrev=TOP AND (c > wpPrev.H) 
	public WavePointAnalyticalResult getUpBreakPrevTop(int periodDays, List<WavePoint> sortedTopBotList, List<StockBean> stockList, StockBean curStockBean, String breakDegree, int breakDayCnt) {
		WavePoint wp = sortedTopBotList.get(sortedTopBotList.size() - 1);
		WavePoint wpPrev = sortedTopBotList.get(sortedTopBotList.size() - 2);
		WavePoint wpPrev2 = sortedTopBotList.get(sortedTopBotList.size() - 3);
		
		WavePointAnalyticalResult wpresult = new WavePointAnalyticalResult(periodDays);
		wpresult.setCode(curStockBean.getStockCode());
		
		double breakPct = this.upBreakRatio(wpPrev.getStockBean().getH(), curStockBean.getC());
		
		List<StockBean> subList= StreamTransformHelper.subListWithEndElement(stockList, wpPrev.getDate(), curStockBean.getTxnDate());
		StockBean maxHighBean = StreamTransformHelper.findMaxHighStock(subList);
		
		double maxBreakPct = this.upBreakRatio(wpPrev.getStockBean().getH(), maxHighBean.getH());
		
		wpresult.setBreakPct(breakPct);
		wpresult.setBreakExtreamPct(maxBreakPct);
		wpresult.setBreakDegree(breakDegree);
//		wpresult.setTrendWay("升破前頂");
		wpresult.setStockTrendStatus(StockTrendStatus.BREAKOUT);
		if(breakDayCnt>0) {
//			wpresult.setTrendWay(wpresult.getTrendWay()+ " ("+breakDayCnt+")");
			wpresult.setStockTrendRemark("D("+breakDayCnt+")");
		}
		wpresult.setPrevTopAndBot(wpPrev);
		wpresult.setRemark(GeneralHelper.toPct(breakPct) +", to Highest:"+GeneralHelper.toPct(maxBreakPct) + " ("+wp.toString() +", "+wpPrev.toString()+", "+wpPrev2.toString()+")");
		
		return wpresult;
	}
	
	
	//for wp=BOT AND (c < wp.L) 
	public WavePointAnalyticalResult getDirectDownBreak(int periodDays, List<WavePoint> sortedTopBotList, List<StockBean> stockList, StockBean curStockBean, boolean isFirstDay) {
		WavePoint wp = sortedTopBotList.get(sortedTopBotList.size() - 1);
		WavePoint wpPrev = sortedTopBotList.get(sortedTopBotList.size() - 2);
		WavePoint wpPrev2 = sortedTopBotList.get(sortedTopBotList.size() - 3);
		
		WavePointAnalyticalResult wpresult = new WavePointAnalyticalResult(periodDays);
		wpresult.setCode(curStockBean.getStockCode());
		
		double breakPct = this.downBreakRatio(wp.getStockBean().getL(), curStockBean.getC());

		List<StockBean> subList= StreamTransformHelper.subList(stockList, wpPrev.getDate(), curStockBean.getTxnDate());
		StockBean minLowBean = StreamTransformHelper.findMinLowStock(subList);
		
		double minBreakPct = this.downBreakRatio(wp.getStockBean().getL(), minLowBean.getL());
		
		wpresult.setBreakPct(breakPct);
		wpresult.setBreakExtreamPct(minBreakPct);
//		wpresult.setTrendWay("跌破底(直接)");
		wpresult.setStockTrendStatus(StockTrendStatus.BREAKDOWN_DIRECT);
		if(isFirstDay) {
			wpresult.setStockTrendRemark(Const.D0);
		}
		wpresult.setPrevTopAndBot(wp);
		wpresult.setRemark(GeneralHelper.toPct(breakPct)+", Lowest :"+ minBreakPct + " ("+wp.toString() +", "+wpPrev.toString()+", "+wpPrev2.toString()+")");
		
		return wpresult;
	}
	
	public String calcAdjustmentRatio(Double top, Double bot, Double currentPrice) {		
		//e.g. (10-9)*0.5 +9
		double r05 = (top - bot)* 0.5 + bot;
		double r764 = (top - bot)* 0.764 + bot;
		double r618 = (top - bot)* 0.618 + bot;
		double r382 = (top - bot)* 0.382 + bot;
		double r236 = (top - bot)* 0.236 + bot;
		
		if(currentPrice <= r236)
			return "回0.764下";
		else if(currentPrice <= r382)
			return "回0.618下";
		else if(currentPrice <= r05)
			return "回0.5下";		
		else if(currentPrice <= r618)
			return "回0.382下";
		else if(currentPrice <= r764)
			return "回0.236下";
		else 
			return Const.SPACE;
		
	}
	
	public String calcReboundRatio(Double top, Double bot, Double currentPrice) {		
		//e.g. (10-9)*0.5 +9
				double r05 = (top - bot)* 0.5 + bot;
				double r618 = (top - bot)* 0.618 + bot;
				double r764 = (top - bot)* 0.764 + bot;
				double r382 = (top - bot)* 0.382 + bot;
				double r236 = (top - bot)* 0.236 + bot;

				if(currentPrice >= r764)
					return "彈0.764上";
				else if(currentPrice >= r618)
					return "彈0.618上";
				else if(currentPrice >= r05)
					return "彈0.5上";		
				else if(currentPrice >= r382)
					return "彈0.382上";
				else if(currentPrice >= r236)
					return "彈0.236上";
				else 
					return Const.SPACE;
		
	}
	
	public String isTopReversal(List<StockBean> stockList, List<WavePoint> sortedTopBotList){
		StockBean current = stockList.get(stockList.size()-1);
		
		WavePoint wp = sortedTopBotList.get(sortedTopBotList.size() - 1);
		WavePoint wpPrev = sortedTopBotList.get(sortedTopBotList.size() - 2);
		WavePoint wpPrev2 = sortedTopBotList.get(sortedTopBotList.size() - 3);
		
		
		WavePoint manByH = sortedTopBotList
			      .stream()
			      .max(Comparator.comparing(WavePoint::getH))
			      .orElseThrow(NoSuchElementException::new);
		if( WaveType.TOP.equals(wp.getType()) 
				&& WaveType.BOT.equals(wpPrev.getType()) 
				&& WaveType.TOP.equals(wpPrev2.getType()) ) 
		{
			if(current.getC() < wpPrev2.getStockBean().getBodyTop() && manByH.getDateInt() == wp.getDateInt()) {
				List<StockBean> subList = StreamTransformHelper.subListWithEndElement(stockList, wp.getDate(), current.getTxnDate());
				String confirmDate = current.getTxnDate();

				for(int i=1; i<subList.size(); i++) 
				{
					StockBean elemt = subList.get(i);
					
					double achiveBodyLevel = wpPrev2.getStockBean().getBodyBottom() + (wpPrev2.getStockBean().getBodyTop() - wpPrev2.getStockBean().getBodyBottom()) / 2.0;
					
					if(elemt.getC() <= achiveBodyLevel) {
						confirmDate = elemt.getTxnDate();
						break;
					}
				}		
						
				return KPatternConst.KP_TOP_REVERSAL+"\t"+confirmDate;
			}
		}
		/*
		 String confirmDate = "";
		 subList(wpPrev2.getDate(), current.getTxnDate())
		 
		 for loop {
		  if (elem.getC() < wpPrev2.getBodyBottom() ){
		  	confirmDate = elem.getDate();
		  }
		 }
		 */
		return Const.NA+"\t";
	}
	
	
	
	
	
	public String isBottomReversal(List<StockBean> stockList, List<WavePoint> sortedTopBotList){
		StockBean last = stockList.get(stockList.size()-1);
		
		WavePoint wpLast = sortedTopBotList.get(sortedTopBotList.size() - 1);
		WavePoint wpPrev1 = sortedTopBotList.get(sortedTopBotList.size() - 2);
		WavePoint wpPrev2 = sortedTopBotList.get(sortedTopBotList.size() - 3);
		
//		WavePoint minByL = sortedTopBotList.stream()
//			      .min(Comparator.comparing(WavePoint::getL))
//			      .orElseThrow(NoSuchElementException::new);
		
		boolean condition1 = WaveType.BOT.equals(wpPrev2.getType()) && WaveType.TOP.equals(wpPrev1.getType()) && WaveType.BOT.equals(wpLast.getType()) ;
		boolean condition2 = WaveType.TOP.equals(wpPrev2.getType()) && WaveType.BOT.equals(wpPrev1.getType()) && WaveType.BOT.equals(wpLast.getType()) ;
		String confirmDate = last.getTxnDate(); //init the date
		
		boolean isHit = false;
		
		WavePoint secondBot = null;
		if( condition1 ) {
			secondBot = wpPrev2;
		}else if( condition2 ) {
			secondBot = wpPrev1;
		}
		
		
		if( condition1 || condition2) 
		{
			if(last.getC() > secondBot.getStockBean().getBodyBottom()) //&& minByL.getDateInt() == wpLast.getDateInt()) 
			{
				List<StockBean> subList = StreamTransformHelper.subListWithEndElement(stockList, wpLast.getDate(), last.getTxnDate());
				
				//e.g. 100 + (110-100)/2 
//				double achiveBodyLevel = secondBot.getStockBean().getBodyBottom() + (secondBot.getStockBean().getBodyTop() - wpPrev2.getStockBean().getBodyBottom()) / 2.0; 

				for(int i=1; i<subList.size(); i++) 
				{
					StockBean elemt = subList.get(i);
					if(elemt.getC() > secondBot.getStockBean().getBodyBottom() && elemt.isRiseToday()==true) {
						confirmDate = elemt.getTxnDate(); //first confirmDate
						
						boolean result = this.findAchiveBodyLevelRatio(subList, secondBot.getStockBean(), elemt, last.getTxnDate());
						if(result==true) {
							isHit = true;
							break;
						}else 
							continue;
					}
				}
			}
		}
		
		if(isHit) {	
			return KPatternConst.KP_BOTTOM_REVERSAL+"\t"+confirmDate;
		}else {
			return Const.NA+"\t";				
		}
		
	}

	private boolean findAchiveBodyLevelRatio(List<StockBean> stockList, StockBean botStock, StockBean firstConfirmedStock, String currentDate) {
		List<StockBean> subList = StreamTransformHelper.subListWithEndElement(stockList, firstConfirmedStock.getTxnDate(), currentDate);
//		long count = subList.stream()
//		            .filter(x -> x.getBodyBottom() >= achiveBodyLevel)
//		            .count();

		long count = subList.stream()
	            .filter(x -> x.getBodyBottom() > firstConfirmedStock.getBodyBottom())
	            .count();
		
		StockBean lastStock = subList.get(subList.size()-1);
		double ratio = (double) count / (double)subList.size();
		
		
		boolean isEngouthVol = false;
		if(Const.IS_INTRADAY==true) {
			isEngouthVol=true;
		}else {		
			isEngouthVol = ((double)firstConfirmedStock.getVolume() / (double)botStock.getVolume() >= 1) 
				|| firstConfirmedStock.getDayVolumeChgPct() > 1.2;
		}
		
		if(isEngouthVol==true && ratio >=0.7 && lastStock.getBodyBottom()> firstConfirmedStock.getBodyBottom())
			return true;
		
		return false;
	}
	
	public double upBreakRatio(Double prevTop, Double curprice) {
		//e.g. (10-9)/9
		double r = (curprice - prevTop )/prevTop;
		return r;
	}
	
	public double downBreakRatio(Double prevBot, Double curprice) {
		//e.g. (9-10)/10
		double r = (curprice - prevBot )/prevBot;
		return r;
	}
	
	
	
	public String findShape(List<StockBean> stockList, List<WavePoint> sortedTopBotList, List<WavePoint> sortedTopList, List<WavePoint> sortedBotList) {
		
		Set<String> result = new LinkedHashSet<String>(); 
		
		result.addAll( upBreakTriangleWavePattern.find(		stockList, sortedTopList, sortedBotList ));
		result.addAll( downBreakTriangleWavePattern.find(	stockList, sortedTopList, sortedBotList ));
		result.addAll( findReadyUpBreakW(		stockList, sortedTopBotList));
		result.addAll( findReadyDownBreakM(		stockList, sortedTopBotList));
		result.addAll( downBreakWavePattern.find(		stockList, sortedTopList, sortedBotList));
		result.addAll( upBreakWavePattern.find(		stockList, sortedTopList, sortedBotList));
		int botQty = 2;
		int topQty = 2;
		result.addAll( flatTopWaitingBreakWavePattern.find(stockList, sortedTopList, sortedBotList) );
		result.addAll( flatBottomWaitingBreakWavePattern.find(stockList, sortedTopList, sortedBotList) );//findFlatBottomByBotQty(			stockList, sortedTopBotList, botQty));
		
//		botQty = 3;
//		topQty = 3;
//		result.addAll( findFlatTopByTopQty(			stockList, sortedTopBotList, topQty));
//		result.addAll( findFlatBottomByBotQty(			stockList, sortedTopBotList, botQty));
		
		
		result.addAll( topReversalWavePattern.find(			stockList, sortedTopList, sortedBotList));
		result.addAll( bottomReversalWavePattern.find(		stockList, sortedTopList, sortedBotList));
//		result.addAll( findWaveHigherHigh(		stockList, sortedTopBotList));
//		result.addAll( findWaveHigherHighWithUpBegin(		stockList, sortedTopBotList));
//		result.addAll( findWaveHeadAndShoulder(		stockList, sortedTopBotList));
		return result.isEmpty()?Const.SPACE: result.toString().replace("[", "").replace("]", "");
	}
	
//	public Set<String> findBottomReversal(List<StockBean> stockList, List<WavePoint> sortedTopBotList){
//		Set<String> msg = new LinkedHashSet<String>();
//		if(sortedTopBotList.size()<3 ||sortedTopBotList.size()<3)
//			return msg;
//		StockBean last = stockList.get(stockList.size()-1);
//		
//		WavePoint wpLast = sortedTopBotList.get(sortedTopBotList.size() - 1);
//		WavePoint wpPrev1 = sortedTopBotList.get(sortedTopBotList.size() - 2);
//		WavePoint wpPrev2 = sortedTopBotList.get(sortedTopBotList.size() - 3);
//		
////		WavePoint minByL = sortedTopBotList.stream()
////			      .min(Comparator.comparing(WavePoint::getL))
////			      .orElseThrow(NoSuchElementException::new);
//		boolean isBigBodyDark = (KHelper.isBidBody(stockList) >= KBodyType.GENERAL.getValue() && KHelper.isBearishCandle(last));
//		
//		boolean condition1 = WaveType.BOT.equals(wpPrev2.getType()) 
//				&& WaveType.TOP.equals(wpPrev1.getType()) && WaveType.BOT.equals(wpLast.getType()) 
//				&& wpLast.getStockBean().getBodyTop() < wpPrev2.getL()
//				&& last.getC() >= wpPrev2.getStockBean().getBodyTop()
//				&& isBigBodyDark==false;
//		boolean condition2 = WaveType.TOP.equals(wpPrev2.getType()) 
//				&& WaveType.BOT.equals(wpPrev1.getType()) 
//				&& WaveType.BOT.equals(wpLast.getType()) 
//				&& wpLast.getStockBean().getBodyTop() < wpPrev1.getL()
//				&& last.getC() >= wpPrev1.getStockBean().getBodyTop()
//				&& isBigBodyDark==false;
//		
//		
//		String confirmDate = last.getTxnDate(); //init the date
//		
//		boolean isHit = false;
//		String d0Txt = "";
//		
//		WavePoint secondBot = null;
//		if( condition1 ) {
//			secondBot = wpPrev2;
//		}else if( condition2 ) {
//			secondBot = wpPrev1;
//		}
//		
//		
//		if( condition1 || condition2) 
//		{
//			
//			List<WavePoint> sortedTopList = sortedTopBotList.stream().filter(x->x.getType()==WaveType.TOP).sorted(Comparator.comparing(e -> e.getDateInt())).collect(Collectors.toList());
//			WavePoint lastTop1 = sortedTopList.get(sortedTopList.size()-1);
//			
//			if(last.getC() > secondBot.getStockBean().getBodyBottom()) //&& minByL.getDateInt() == wpLast.getDateInt()) 
//			{
//				List<StockBean> subList = StreamTransformHelper.subListWithEndElement(stockList, wpLast.getDate(), last.getTxnDate());
//				
//				//e.g. 100 + (110-100)/2 
//				double achiveBodyLevel = secondBot.getStockBean().getBodyBottom() + Math.abs(secondBot.getStockBean().getBodyTop() - secondBot.getStockBean().getBodyBottom()) / 2.0; 
//
//				for(int i=1; i<subList.size(); i++) 
//				{
//					StockBean elemt = subList.get(i);
//					if(elemt.getC() >= achiveBodyLevel) {
//						confirmDate = elemt.getTxnDate(); //first confirmDate
//						
//						boolean result = this.findAchiveBodyLevelRatio(subList, secondBot.getStockBean(), elemt, last.getTxnDate());
//						if(result==true) 
//						{
//							isHit = true;
//							if(last.getTxnDateInt() == elemt.getTxnDateInt())
//								d0Txt = "D0";
//							if(last.getC() > (secondBot.getH()*1.1) )
//								isHit = false;
//						
//							//find  max high
//							StockBean highestSk = StreamTransformHelper.findMaxHighStock(subList);
//							if(highestSk.getH() >= lastTop1.getH())
//								d0Txt += "(真)";
//							break;
//						}else 
//							continue;
//					}
//				}
//			}
//		}
//		
//		if(isHit) {	
//			msg.add(KPatternConst.KP_BOTTOM_REVERSAL+d0Txt);
//		}
//		return msg;
//	}
	
	
//	public Set<String> findUpBreakTop(List<StockBean> stockList, List<WavePoint> sortedTopBotList){
//		Set<String> msg = new LinkedHashSet<String>();
//		if(sortedTopBotList.size()<3 ||sortedTopBotList.size()<3)
//			return msg;
//				
//		StockBean last1 = stockList.get(stockList.size()-1);
//		StockBean last2 = stockList.get(stockList.size()-2);
//		
//		WavePoint wp = sortedTopBotList.get(sortedTopBotList.size() - 1);
//		WavePoint wpPrev = sortedTopBotList.get(sortedTopBotList.size() - 2);
//		
//		
//		boolean b = WaveType.BOT.equals(wp.getType());
//		if(b) return msg;
//		
//		
//		
////		int wpLast1Idx= StreamTransformHelper.findIndex(stockList, wp.getDate());	
////		List<StockBean> tailSkList = stockList.subList(wpLast1Idx+1, stockList.size());
////		StockBean tailMaxH = StreamTransformHelper.findMaxHighStock(tailSkList);
////		StockBean tailMinL = StreamTransformHelper.findMinLowStock(tailSkList);
//		
//		boolean bMa = last1.getC() > last1.getPriceSma().getMa20() && last1.getPriceSma().getMa20() > last1.getPriceSma().getMa50() 
//				&& last1.getC() > last1.getPriceSma().getMa50()
//				&& last2.getPriceSma().getMa20() >= last2.getPriceSma().getMa50();
//		//require true
//		boolean b1 = last1.getC() > wp.getStockBean().getBodyBottom() && last1.getC() < wp.getH();
//		boolean b2 = last2.getC() > wp.getStockBean().getBodyBottom() && last2.getC() < wp.getH()  
//				 && last1.getC() < wp.getH() && last1.getC() > wp.getStockBean().getBodyBottom() ;
//		
//		
//		if(bMa && (b1 || b2)){
//			msg.add("待Up前TOP");
//		}
//		
//		
//		//boolean about wave
//		boolean bWaveUpnUp = wpPrev.getH()<wp.getH() && wpPrev.getL()<wp.getL();
//		boolean bAnother = last1.getL() >wpPrev.getStockBean().getBodyTop();
//		
//		boolean  bWave = bWaveUpnUp?(bWaveUpnUp && bAnother): true;
//		
//		boolean bbD0 = last2.getH()< wp.getH() && last1.getC() >= wp.getH() && last1.isRiseToday();
//		boolean bb = last2.getH()>= wp.getH() && last1.getC() >= wp.getH() ;
//		if(bWave) {
//			if(bMa && bbD0) {
//				msg.add("Up前TOP(D0)");
//			}else if(bMa && bb) {
//				msg.add("Up前TOP");
//			}
//		}
//
//		return msg;
//	}
	
//	public Set<String> findDownBreakBot(List<StockBean> stockList, List<WavePoint> sortedTopBotList){
//		Set<String> msg = new LinkedHashSet<String>();
//		if(sortedTopBotList.size()<3 ||sortedTopBotList.size()<3)
//			return msg;
//				
//		StockBean last1 = stockList.get(stockList.size()-1);
//		StockBean last2 = stockList.get(stockList.size()-2);
//		
//		WavePoint wp = sortedTopBotList.get(sortedTopBotList.size() - 1);
//		WavePoint wpPrev = sortedTopBotList.get(sortedTopBotList.size() - 2);
//		
//		
//		boolean b = WaveType.TOP.equals(wp.getType());
//		if(b) return msg;
//		
//		
//		
////		int wpLast1Idx= StreamTransformHelper.findIndex(stockList, wp.getDate());	
////		List<StockBean> tailSkList = stockList.subList(wpLast1Idx+1, stockList.size());
////		StockBean tailMaxH = StreamTransformHelper.findMaxHighStock(tailSkList);
////		StockBean tailMinL = StreamTransformHelper.findMinLowStock(tailSkList);
//		
//		boolean bMa = last1.getBodyTop() < last1.getPriceSma().getMa20() && last1.getPriceSma().getMa20() < last1.getPriceSma().getMa50()  
//				&& last1.getBodyTop() < last1.getPriceSma().getMa50()
//				&& last2.getPriceSma().getMa20() <= last2.getPriceSma().getMa50();
//
//		
//		//require true
//		boolean b1 = last1.getC() < wp.getStockBean().getBodyTop() && last1.getC() > wp.getL();
//		boolean b2 = last2.getC() < wp.getStockBean().getBodyTop() && last2.getC() > wp.getL() 
//				&& last1.getC() > wp.getL() && last1.getC() < wp.getStockBean().getBodyTop() ;
//		
//		
//		if(bMa && (b1 || b2)){
//			msg.add("待Dw前BOT");
//		}
//		
//		
//		//boolean about wave
//		boolean bWaveDwnDw = wpPrev.getH()>wp.getH() && wpPrev.getL()>wp.getL();
//		boolean bAnother = last1.getH()<wpPrev.getStockBean().getBodyBottom();
//		
//		boolean  bWave = bWaveDwnDw?(bWaveDwnDw && bAnother): true;
//		
//		
//		boolean bbD0 = last2.getL()> wp.getL() && last1.getC() <= wp.getL() && last1.isRiseToday()==false;
//		boolean bb = last2.getL()<= wp.getL() && last1.getC() <= wp.getL();
//		if(bWave) {
//			if(bMa && bbD0) {
//				msg.add("Dw前BOT(D0)");
//			}else if(bMa && bb){
//				msg.add("Dw前BOT");
//			}
//		}
//		return msg;
//	}
	
//	public Set<String> findTopReversal(List<StockBean> stockList, List<WavePoint> sortedTopBotList){
//		Set<String> msg = new LinkedHashSet<String>();
//		if(sortedTopBotList.size()<3 ||sortedTopBotList.size()<3)
//			return msg;
//
//		StockBean last1 = stockList.get(stockList.size()-1);
//
//		WavePoint wp = sortedTopBotList.get(sortedTopBotList.size() - 1);
//		WavePoint wpPrev = sortedTopBotList.get(sortedTopBotList.size() - 2);
//		WavePoint wpPrev2 = sortedTopBotList.get(sortedTopBotList.size() - 3);
//
//		WavePoint previouseTop = null;
//		if( WaveType.TOP.equals(wpPrev2.getType()) && WaveType.BOT.equals(wpPrev.getType()) && WaveType.TOP.equals(wp.getType())){
//			previouseTop = wpPrev2;
//		}
//		if( WaveType.TOP.equals(wpPrev.getType()) && WaveType.TOP.equals(wp.getType())){
//			previouseTop = wpPrev;
//		}
//
//		boolean isMeet = previouseTop!=null ;
//
//
//
//		WavePoint manByH = sortedTopBotList
//			      .stream()
//			      .max(Comparator.comparing(WavePoint::getH))
//			      .orElseThrow(NoSuchElementException::new);
//		if( isMeet )
//		{
//			boolean isLastSkLowerPrevTop = last1.getC() < previouseTop.getH();
//
//			if(isLastSkLowerPrevTop && manByH.getDateInt() == wp.getDateInt())
//			{
//				List<StockBean> subList = StreamTransformHelper.subListWithEndElement(stockList, wp.getDate(), last1.getTxnDate());
//
//				int confirmDateInt = 0;
//
//				for(int i=1; i<subList.size(); i++)
//				{
//					StockBean elemt = subList.get(i);
//
//					if(elemt.getC() < previouseTop.getStockBean().getBodyTop() && elemt.isRiseToday()==false) {
//						confirmDateInt = elemt.getTxnDateInt();
//						break;
//					}
//				}
//
//				if(confirmDateInt > 0 ) {
//					String firstDateBreakMsg = "";
//					if(last1.getTxnDateInt() == confirmDateInt)
//						firstDateBreakMsg = "(D0)";
//					msg.add(KPatternConst.KP_TOP_REVERSAL+firstDateBreakMsg);
//				}
//
//			}
//		}
//
//		return msg;
//		/*
//		 String confirmDate = "";
//		 subList(wpPrev2.getDate(), current.getTxnDate())
//
//		 for loop {
//		  if (elem.getC() < wpPrev2.getBodyBottom() ){
//		  	confirmDate = elem.getDate();
//		  }
//		 }
//		 */
////		return Const.NA+"\t";
//	}
	
//	private Set<String> findUpBreakTrangle(List<StockBean> stockList, List<WavePoint> sortedTopList, List<WavePoint> sortedBotList) {
//		Set<String> msg = new LinkedHashSet<String>();
//		if(sortedTopList.size()<3 ||sortedBotList.size()<3)
//			return msg;
//		
//		StockBean last = stockList.get(stockList.size()-1);
//		
//		WavePoint topLast1 = sortedTopList.get(sortedTopList.size()-1);
//		WavePoint topLast2 = sortedTopList.get(sortedTopList.size()-2);
//		WavePoint topLast3 = sortedTopList.get(sortedTopList.size()-3);
//		
//		WavePoint botLast1 = sortedBotList.get(sortedBotList.size()-1);
//		WavePoint botLast2 = sortedBotList.get(sortedBotList.size()-2);
//		WavePoint botLast3 = sortedBotList.get(sortedBotList.size()-3);
//		
//		
//		
//		
//		boolean ispass1 = false;
//		boolean ispass2 = false;
//		if(topLast3.getStockBean().getH()>=topLast2.getStockBean().getH() && topLast2.getStockBean().getH()>=topLast1.getStockBean().getH()) {
//			ispass1 = true;
//		}
//		
//		if(topLast3.getStockBean().getBodyTop()>=topLast2.getStockBean().getBodyTop() && topLast2.getStockBean().getBodyTop()>=topLast1.getStockBean().getBodyTop()) {
//			ispass1 = true;
//		}
//		
//		
//		if(botLast3.getStockBean().getL()<=botLast2.getStockBean().getL() && botLast2.getStockBean().getL()<=botLast1.getStockBean().getL()) {
//			ispass2 = true;
//		}
//		
//		if(botLast3.getStockBean().getBodyBottom()<=botLast2.getStockBean().getBodyBottom() && botLast2.getStockBean().getBodyBottom()<=botLast1.getStockBean().getBodyBottom()) {
//			ispass2 = true;
//		}
//		
//		
//		double thresholdMinRequired = topLast1.getStockBean().getH() * 0.95;
//		double thresholdMaxRequired = topLast1.getStockBean().getH() * 1.01;
//		
//		if(ispass1 && ispass2 && last.getC()>=thresholdMinRequired && last.getC()<=thresholdMaxRequired && KHelper.isBullishCandle(last)) {
//			msg.add("待"+Const.UP+"破小三角");
//			
//			boolean isAnotherTriangleShap = isUpTrendTriangle( sortedTopList, sortedBotList);
//			if(isAnotherTriangleShap)
//				msg.add("待"+Const.UP+"破向上三角");
//		}
//		
//		return msg;
//	}
	
	
//	private Set<String> findDownBreakTriangle(List<StockBean> stockList, List<WavePoint> sortedTopList, List<WavePoint> sortedBotList) {
//		Set<String> msg = new LinkedHashSet<String>();
//		
//		if(sortedTopList.size()<3 || sortedBotList.size()<3)
//			return msg;
//		
//		StockBean last = stockList.get(stockList.size()-1);
//		
//		WavePoint topLast1 = sortedTopList.get(sortedTopList.size()-1);
//		WavePoint topLast2 = sortedTopList.get(sortedTopList.size()-2);
//		WavePoint topLast3 = sortedTopList.get(sortedTopList.size()-3);
//		
//		WavePoint botLast1 = sortedBotList.get(sortedBotList.size()-1);
//		WavePoint botLast2 = sortedBotList.get(sortedBotList.size()-2);
//		WavePoint botLast3 = sortedBotList.get(sortedBotList.size()-3);
//		
//		
//		boolean ispass1 = false;
//		boolean ispass2 = false;
//		if(topLast3.getStockBean().getH()>=topLast2.getStockBean().getH() && topLast2.getStockBean().getH()>=topLast1.getStockBean().getH()) {
//			ispass1 = true;
//		}
//		
//		if(topLast3.getStockBean().getBodyTop()>=topLast2.getStockBean().getBodyTop() && topLast2.getStockBean().getBodyTop()>=topLast1.getStockBean().getBodyTop()) {
//			ispass1 = true;
//		}
//		
//		
//		if(botLast3.getStockBean().getL()<=botLast2.getStockBean().getL() && botLast2.getStockBean().getL()<=botLast1.getStockBean().getL()) {
//			ispass2 = true;
//		}
//		
//		if(botLast3.getStockBean().getBodyBottom()<=botLast2.getStockBean().getBodyBottom() && botLast2.getStockBean().getBodyBottom()<=botLast1.getStockBean().getBodyBottom()) {
//			ispass2 = true;
//		}
//		
//		
////		double thresholdMinRequired = topLast1.getStockBean().getL() * 0.95;
//		double thresholdRequired = botLast1.getStockBean().getL() * 1.03;
//		
//		if(ispass1 && ispass2 && last.getC()<=thresholdRequired && KHelper.isBearishCandle(last)) {
//			msg.add("待"+Const.DOWN+"破小三角");
//			
//		}
//		
//		return msg;
//	}
	
	
//	public boolean isUpTrendTriangle( List<WavePoint> sortedTopList, List<WavePoint> sortedBotList) {
//		if(sortedTopList.size()<3 ||sortedBotList.size()<3)
//			return false;
//		
//		
//		WavePoint topLast1 = sortedTopList.get(sortedTopList.size()-1);
//		WavePoint topLast2 = sortedTopList.get(sortedTopList.size()-2);
//		WavePoint topLast3 = sortedTopList.get(sortedTopList.size()-3);
//		
//		WavePoint botLast1 = sortedBotList.get(sortedBotList.size()-1);
//		WavePoint botLast2 = sortedBotList.get(sortedBotList.size()-2);
//		WavePoint botLast3 = sortedBotList.get(sortedBotList.size()-3);
//		
//		
//		double threshold = 0.03;
//		boolean isExceedThreshold = isDifferenceExceedThreshold(topLast3.getH(), topLast2.getH(), topLast1.getH(), threshold);
//		
//		if(isExceedThreshold ==false) {
//			
//			boolean isGoodBottom = false;
//			if(botLast3.getStockBean().getL()<botLast2.getStockBean().getL() && botLast2.getStockBean().getL()<botLast1.getStockBean().getL()) {
//				isGoodBottom = true;
//			}
//			
//			if(botLast3.getStockBean().getBodyBottom()<botLast2.getStockBean().getBodyBottom() && botLast2.getStockBean().getBodyBottom()<botLast1.getStockBean().getBodyBottom()) {
//				isGoodBottom = true;
//			}
//			
//			
//			if(isGoodBottom)
//				return true; //return "向上三角";
//		}
//		
//		return false;
//	}
//	
//	
//	 public  boolean isDifferenceExceedThreshold(double num1, double num2, double num3, double threshold) {
//	        double maxDifference = Math.max(Math.abs(num1 - num2), Math.max(Math.abs(num1 - num3), Math.abs(num2 - num3)));
//	        double maxPercentageDifference = (maxDifference / Math.max(Math.max(num1, num2), num3)) ;
//	        return maxPercentageDifference > threshold;
//	 }
	
	 private static final double DEVIATION = 0.02;
	 
	 private Set<String> findReadyUpBreakW(List<StockBean> stockList, List<WavePoint> sortedTopBotList) {
			Set<String> attributes = new LinkedHashSet<String>();

			
			StockBean last1 = stockList.get(stockList.size()-1);
			StockBean last2 = stockList.get(stockList.size()-2);

		 	if(sortedTopBotList.size()<4 )
				 return attributes;

			WavePoint last1Bot = sortedTopBotList.get(sortedTopBotList.size()-1); //BOT
			WavePoint last2Top = sortedTopBotList.get(sortedTopBotList.size()-2); //TOP ,need to break this 
			WavePoint last3Bot = sortedTopBotList.get(sortedTopBotList.size()-3); //BOT
			WavePoint last4Top = sortedTopBotList.get(sortedTopBotList.size()-4);//TOP
			
//			double topHighDiffPct = Math.abs( (last2Top.getH() - last4Top.getH()) / last4Top.getH() );
//			boolean isSameTopHigh = (topHighDiffPct <= DEVIATION);
			
//			double botLowDiff = Math.abs( (last1Bot.getL() - last3Bot.getL()) / last3Bot.getL() );
//			boolean isSameBottom = (botLowDiff <= DEVIATION);
			
			int daysDiffInTop = StreamTransformHelper.txDaysBetween(stockList, last4Top.getDate(), last2Top.getDate());
			int daysDiffInBottom = StreamTransformHelper.txDaysBetween(stockList, last3Bot.getDate(), last1Bot.getDate());
			
			
			boolean isValidKBody = true;
			
			if(KHelper.getBodySize(stockList)>=KBodyType.L.getValue() && last1.isOpenLowCloseHigh()==false )
				isValidKBody = false;
			
//			Set<String> attributes = new HashSet<String>();
			if(WaveType.BOT==last1Bot.getType() && WaveType.TOP==last2Top.getType() && WaveType.BOT==last3Bot.getType() && WaveType.TOP==last4Top.getType()) 
			{

				boolean isSameTop = (last2Top.getStockBean().getH() >= last4Top.getStockBean().getBodyBottom() && last2Top.getStockBean().getH() < last4Top.getStockBean().getH() ) || (last4Top.getStockBean().getH() >=last2Top.getStockBean().getBodyBottom() && last4Top.getStockBean().getH() < last2Top.getStockBean().getH() );
				boolean isSameBottom = (last1Bot.getStockBean().getL() > last3Bot.getStockBean().getL() && last1Bot.getStockBean().getL() < last3Bot.getStockBean().getBodyTop()) ||
						(last3Bot.getStockBean().getL() > last1Bot.getStockBean().getL() && last3Bot.getStockBean().getL() < last1Bot.getStockBean().getBodyTop());

				boolean b1 = last2Top.getStockBean().getC() > last1Bot.getStockBean().getH() && last2Top.getStockBean().getC() > last3Bot.getStockBean().getH();
				boolean b2 = last4Top.getStockBean().getC() > last1Bot.getStockBean().getH() && last4Top.getStockBean().getC() > last3Bot.getStockBean().getH();
				
				
				boolean b3 = last1.getDayChgPct()>0 && last1.getC() > last1Bot.getStockBean().getBodyTop() && last1.getC() > last3Bot.getStockBean().getBodyTop();


				boolean bActualBreak = last1.getBodyTop() >= last2Top.getStockBean().getH();				
				boolean bReadyBreak = last1.getH() <= last2Top.getStockBean().getH() && last1.getC() > last2Top.getL();
							
				
//				String bottomShap = "";
//				double toleranceRatio = 0.03;
//				double diff = (last1Bot.getL() - last3Bot.getL())/last3Bot.getL();
//				if(diff >= toleranceRatio) {
//					bottomShap = "-LH";//lh
//				}else if(diff <= -toleranceRatio) {
//					bottomShap = "-HL";//hl
//				}
//				isBidBody
						
				
				
				if(b1 && b2 && b3 && isSameBottom && bActualBreak) {

					if(isValidKBody && last2.getH() <= last2Top.getStockBean().getH()) {
						attributes.add(Const.UP+Const.D0+"破小W");

					}else{
						attributes.add(Const.UP+"破小W");

					}
					

				}
				
				if(b1 && b2 && b3 && isSameBottom && bReadyBreak) {
					attributes.add(Const.WAIT+Const.UP+"破小W");
				}
				
				
				//adding other attributes about the W pattern
				if(!attributes.isEmpty() && isSameTop && daysDiffInTop >10) {
					attributes.add("平頂");
				}
				
				if(!attributes.isEmpty() && isSameBottom && daysDiffInBottom >10) {
					attributes.add("平底");
				}
				
//				double toleranceRatio = 0.03;
				double diff = (last1Bot.getL() - last3Bot.getL())/last3Bot.getL();
				if(!attributes.isEmpty() && diff >= DEVIATION) {
					attributes.add("LH");//lh
				}else if(!attributes.isEmpty() && diff <= -DEVIATION) {
					attributes.add("HL");//hl
				}
				
			}
			
			return attributes;
		}
	
	 private Set<String> findReadyDownBreakM(List<StockBean> stockList, List<WavePoint> sortedTopBotList) {
			Set<String> msg = new LinkedHashSet<String>();

			
			StockBean last1 = stockList.get(stockList.size()-1);
			StockBean last2 = stockList.get(stockList.size()-2);

			 if(sortedTopBotList.size()<4 )
				 return msg;
			WavePoint last1Top = sortedTopBotList.get(sortedTopBotList.size()-1);
			WavePoint last2Bot = sortedTopBotList.get(sortedTopBotList.size()-2);
			WavePoint last3Top = sortedTopBotList.get(sortedTopBotList.size()-3);
			WavePoint last4Bot = sortedTopBotList.get(sortedTopBotList.size()-4);
			
			
			boolean isValidKBody = true;
			if(KHelper.getBodySize(stockList)>=KBodyType.L.getValue() && last1.isOpenLowCloseHigh()==true ) {
				isValidKBody = false;
			}
//			double topHdiff = Math.abs( (last1Top.getH() - last3Top.getH()) / last3Top.getH() );
//			boolean isSameTop = (topHdiff <= DEVIATION);
			
//			double botLowDiff = Math.abs( (last2Bot.getL() - last4Bot.getL()) / last4Bot.getL() );
//			boolean isSameBottom = (botLowDiff <= DEVIATION);
			
			int daysDiffInTop = StreamTransformHelper.txDaysBetween(stockList, last3Top.getDate(), last1Top.getDate());
			int daysDiffInBottom = StreamTransformHelper.txDaysBetween(stockList, last4Bot.getDate(), last2Bot.getDate());
			
			if(WaveType.TOP==last1Top.getType() && WaveType.BOT==last2Bot.getType() && WaveType.TOP==last3Top.getType() && WaveType.BOT==last4Bot.getType()) 
			{

				boolean isSameBottom = (last2Bot.getStockBean().getL() > last4Bot.getStockBean().getL() && last2Bot.getStockBean().getL() < last2Bot.getStockBean().getBodyTop()) ||
						(last4Bot.getStockBean().getL() > last2Bot.getStockBean().getL() && last4Bot.getStockBean().getL() < last2Bot.getStockBean().getBodyTop());


				boolean b1 = last1Top.getStockBean().getC() > last2Bot.getStockBean().getBodyTop() && last1Top.getStockBean().getC() > last4Bot.getStockBean().getBodyTop();
				boolean b2 = last3Top.getStockBean().getC() > last2Bot.getStockBean().getBodyTop() && last3Top.getStockBean().getC() > last4Bot.getStockBean().getBodyTop();				
				boolean b3 = last1.getDayChgPct()<0 && last1.getC() < last1Top.getStockBean().getBodyBottom() && last1.getC() <last3Top.getStockBean().getBodyBottom();
				boolean isSameTop = (last1Top.getStockBean().getH() >= last3Top.getStockBean().getBodyBottom() && last1Top.getStockBean().getH() < last3Top.getStockBean().getH() ) || (last3Top.getStockBean().getH() >=last1Top.getStockBean().getBodyBottom() && last3Top.getStockBean().getH() < last1Top.getStockBean().getH() );
				
				boolean bActualBreak = last1.getBodyBottom() <= last2Bot.getStockBean().getL();				
				boolean bReadyBreak = last1.getL() >= last2Bot.getStockBean().getL();

				
//				String topShap = "";
//				double toleranceRatio = 0.03;
//				
//				double diff = (last1Top.getH() - last3Top.getH())/last3Top.getH();
//				if(diff >= toleranceRatio) {
//					topShap = "-LH";//lh
//				}else if(diff <= -toleranceRatio) {
//					topShap = "-HL";//hl
//				}
				
				if(b1 && b2 && b3 && isSameTop && bActualBreak) {

					if(isValidKBody && last2.getL() >= last2Bot.getStockBean().getL()) {
						msg.add(Const.DOWN+Const.D0+"破小M");
					}else{
						msg.add(Const.DOWN+"破小M");
					}

				}
				if(b1 && b2 && b3 && isSameTop && bReadyBreak) {
					msg.add(Const.WAIT+Const.DOWN+"破小M");
				}
				
				//adding other attributes about the W pattern
//				if(msg.isEmpty()==false && isSameTop==true) {
//					msg.add("平頂");
//				}
//				if(msg.isEmpty()==false && isSameBottom==true) {
//					msg.add("平底");
//				}
				
				if(!msg.isEmpty() && isSameTop && daysDiffInTop >=20) {
					msg.add("平頂");
				}
				
				if(!msg.isEmpty() && isSameBottom && daysDiffInBottom >=20) {
					msg.add("平底");
				}
				
				
//				double toleranceRatio = 0.03;
				double diff = (last1Top.getH() - last3Top.getH())/last3Top.getH();
				if(!msg.isEmpty() && diff >= DEVIATION) {
					msg.add("-LH");//lh
				}else if(!msg.isEmpty() && diff <= -DEVIATION) {
					msg.add("-HL");//hl
				}
				
				return msg;
				
			}
			
			return msg;
		}
	 
	 
	 
//	 private Set<String> findFlatTopByTopQty(List<StockBean> stockList, List<WavePoint> sortedTopBotList, int topQty) {
//			Set<String> attributes = new LinkedHashSet<String>();
//
//			int windowSize = topQty;
//
//			List<WavePoint> sortedTopList = sortedTopBotList.stream().filter(x-> WaveType.TOP== x.getType()).toList();
//			List<WavePoint> sortedBotList = sortedTopBotList.stream().filter(x-> WaveType.BOT== x.getType()).toList();
//			if(sortedTopList.size() <= windowSize || sortedBotList.size() <= windowSize)
//				return attributes;
//
//			StockBean last1 = stockList.get(stockList.size()-1);
//			StockBean last2 = stockList.get(stockList.size()-2);
//
//			WavePoint last1Top = sortedTopList.get(sortedTopList.size()-1);
//			WavePoint last1Bot = sortedBotList.get(sortedBotList.size()-1);
//
//			List<WavePoint> targetWpList = sortedTopList.subList(sortedTopList.size()-windowSize, sortedTopList.size());
//			boolean isFlatTop = this.isFlatTop(targetWpList);
////			   for(WavePoint data: targetWpList) {
////				   List<WavePoint> othersList = targetWpList.stream().filter(x-> !x.getDate().equalsIgnoreCase(data.getDate())).toList();
////				   boolean b1 = this.isTopValueHigherThanOthersLowPrice(data.getStockBean().getBodyTop(), othersList);
////				   boolean b2 = this.isBottomValueLowerThanOthersHightPrice(data.getStockBean().getBodyBottom(), othersList);
//////				   boolean b2 = isValueHigherThanAllStockDataBodyBottom(data.getH(), othersList);
////				   if(b1==false || b2==false) {
////					   return attributes;
////				   }
////			   }
//
//			boolean b1 = last1.getC() < last1Top.getH() && last1.getC() >= last1Bot.getH();
//			boolean b2 = KHelper.isBullishCandle(last1);
//			boolean b3 = last1.getBodyTop() >= last2.getBodyTop() && last1.getH() > last2.getH() && last1.getVolume() >= last2.getVolume();
//			if(isFlatTop && b1 && b2 && b3) {
//				attributes.add(windowSize+"平頂待"+Const.UP);
//			}
//
//			return attributes;
//		}
//
//	 public boolean isFlatTop(List<WavePoint> targetWpList) {
//		 for(WavePoint data: targetWpList) {
//			   List<WavePoint> othersList = targetWpList.stream().filter(x-> !x.getDate().equalsIgnoreCase(data.getDate())).toList();
//			   boolean b1 = this.isTopValueHigherThanOthersLowPrice(data.getStockBean().getBodyTop(), othersList);
//			   boolean b2 = this.isBottomValueLowerThanOthersHightPrice(data.getStockBean().getBodyBottom(), othersList);
////			   boolean b2 = isValueHigherThanAllStockDataBodyBottom(data.getH(), othersList);
//			   if(b1==false || b2==false) {
//				   return false;
//			   }
//		   }
//		 return true;
//	 }
	 
//	 //checkTopValueLessThanOthersLow
//	 private boolean isTopValueHigherThanOthersLowPrice(double bodyTopValue, List<WavePoint> stockDataList) {
//		    for (WavePoint stockData : stockDataList) {
//		        if (bodyTopValue <= stockData.getL()) {
//		            return false;
//		        }
//		    }
//		    return true;
//		}
//
//	 private boolean isBottomValueLowerThanOthersHightPrice(double bodyBotValue, List<WavePoint> stockDataList) {
//		    for (WavePoint stockData : stockDataList) {
//		        if (bodyBotValue >= stockData.getH()) {
//		            return false;
//		        }
//		    }
//		    return true;
//		}
	 
//	 //checkHvalue
//	 private boolean isValueHigherThanAllStockDataBodyBottom(double hValue, List<WavePoint> stockDataList) {
//		    for (WavePoint stockData : stockDataList) {
//		        if (hValue <= stockData.getStockBean().getBodyBottom()) {
//		            return false;
//		        }
//		    }
//		    return true;
//		}
	 
//
//	 private boolean isValueLowerThanAllStockDataHight(double bodyTopValue, List<WavePoint> stockDataList) {
//		    for (WavePoint stockData : stockDataList) {
//		        if (bodyTopValue >= stockData.getH()) {
//		            return false;
//		        }
//		    }
//		    return true;
//		}
	 
	 
//	 private Set<String> findFlatBottomByBotQty(List<StockBean> stockList, List<WavePoint> sortedTopBotList, int numOfBottom) {
//			Set<String> attributes = new LinkedHashSet<String>();
//			int windowSize = numOfBottom;
//
//			List<WavePoint> sortedTopList = sortedTopBotList.stream().filter(x-> WaveType.TOP== x.getType()).toList();
//			List<WavePoint> sortedBotList = sortedTopBotList.stream().filter(x-> WaveType.BOT== x.getType()).toList();
//
//			if(sortedTopList.size() <= windowSize || sortedBotList.size() <= windowSize)
//				return attributes;
//
//
//			StockBean last1 = stockList.get(stockList.size()-1);
//			StockBean last2 = stockList.get(stockList.size()-2);
//
//			WavePoint last1Top = sortedTopList.get(sortedTopList.size()-1);
//			WavePoint last1Bot = sortedBotList.get(sortedBotList.size()-1);
//
//			List<WavePoint> targetWpList = sortedBotList.subList(sortedBotList.size()-windowSize, sortedBotList.size());
//
////			boolean isFound = true;
//		   for(WavePoint data: targetWpList)
//		   {
//			   List<WavePoint> othersList = targetWpList.stream().filter(x-> !x.getDate().equalsIgnoreCase(data.getDate())).toList();
//			   boolean isF = this.isValueLowerThanAllStockDataHight(data.getStockBean().getBodyBottom(), othersList);
//			   if(isF==false) {
//				   return attributes;
//			   }
//		   }
//
//			boolean b1 = last1.getC() > last1Bot.getL() && last1.getC() <= last1Top.getL() ;
//			boolean b2 = KHelper.isBearishCandle(last1);
//			boolean b3 = last1.getBodyBottom() <= last2.getBodyBottom() && last1.getL() < last2.getL() ;
//
//			if(b1 && b2 && b3)
//			{
//				attributes.add(windowSize+"平底待"+Const.DOWN);
//			}
//
//			return attributes;
//		}
//
//
//
	 
//	 
//	 private Set<String> findWaveHigherHigh(List<StockBean> stockList, List<WavePoint> sortedTopBotList) {
//			Set<String> attributes = new LinkedHashSet<String>();
//			int windowSize = 3;
//			
//			if(sortedTopBotList.size() <= windowSize )
//				return attributes;
//			
//			StockBean last1 = stockList.get(stockList.size()-1);
//			StockBean last2 = stockList.get(stockList.size()-2);
//			
//			WavePoint wpLast1 = sortedTopBotList.get(sortedTopBotList.size()-1);
//			WavePoint wpLast2 = sortedTopBotList.get(sortedTopBotList.size()-2);
//			WavePoint wpLast3 = sortedTopBotList.get(sortedTopBotList.size()-3);
//			WavePoint wpLast4 = sortedTopBotList.get(sortedTopBotList.size()-4);
//			
//			WavePoint latestBot = null;
//			WavePoint latestTop = null;
//			
//			//last1 may lower than  TOP
//			if(WaveType.BOT== wpLast4.getType() && WaveType.TOP== wpLast3.getType() && WaveType.BOT== wpLast2.getType() && WaveType.TOP== wpLast1.getType()) {
//				boolean isMeet1 = wpLast2.getL()< last1.getL() &&  wpLast3.getH() < wpLast1.getH() && wpLast4.getL() < wpLast2.getL();
//				boolean isMeet2 = wpLast4.getH() < wpLast3.getH() && wpLast4.getL() < wpLast3.getL() // BOT4 -> TOP3 
//								&& wpLast3.getL()> wpLast2.getL() && wpLast3.getH()> wpLast2.getH()// TOP3 -> BOT2
//								&& wpLast2.getH() < wpLast1.getH() && wpLast2.getL() < wpLast1.getL() //BOT2 -> TOP1
//								&& wpLast3.getH() < wpLast1.getStockBean().getBodyTop(); 
//								;
//				if(isMeet1 && isMeet2) {
//					latestTop = wpLast1;
//					latestBot = wpLast2;	
//				}
//			}
//			
//			// last1 may higher than  TOP
//			if(WaveType.TOP== wpLast4.getType() && WaveType.BOT== wpLast3.getType() && WaveType.TOP== wpLast2.getType() && WaveType.BOT== wpLast1.getType()) {
//				boolean isMeet1 = wpLast2.getH()< last1.getH() && wpLast3.getL() < wpLast1.getL();
//				boolean isMeet2 = wpLast4.getH() > wpLast3.getH() && wpLast4.getL() > wpLast3.getL() // T4 -> B3 
//						&& wpLast3.getL()< wpLast2.getL() && wpLast3.getH()< wpLast2.getH()// B3 -> T2
//						&& wpLast2.getH()> wpLast1.getH() && wpLast2.getL() > wpLast1.getL() //T2 -> B1
//						&& wpLast4.getH() < wpLast2.getStockBean().getBodyTop(); 
//						;
//				if(isMeet1 && isMeet2) {
//					latestTop = wpLast2;
//					latestBot = wpLast1;
//				}
//			}
//			
//			
//			if(latestBot!=null && latestTop !=null && last1.getL() > latestBot.getL() &&  last1.getH() < latestTop.getH()) {
//				attributes.add(KPatternConst.KP_WAVE_HIGHER_HIGH);
//			}
//			
//			if(latestBot!=null && latestTop !=null && last1.getL() > latestBot.getL() 
//					&& last1.getH() >= latestTop.getH()) {
//				String msg = "(尋頂中)";
//				if(last2.getH() <latestTop.getH())
//					msg = "(尋新頂D0)";
//				attributes.add(KPatternConst.KP_WAVE_HIGHER_HIGH + msg);
//				
//			}
//			
//				
//			
//			return attributes;
//		}
	 
	 
//	 private Set<String> findWaveHigherHighWithUpBegin(List<StockBean> stockList, List<WavePoint> sortedTopBotList) {
//			Set<String> attributes = new LinkedHashSet<String>();
//			int windowSize = 3;
//
//			if(sortedTopBotList.size() <= windowSize )
//				return attributes;
//
//			StockBean last1 = stockList.get(stockList.size()-1);
//			StockBean last2 = stockList.get(stockList.size()-2);
//
//			WavePoint wpLast1 = sortedTopBotList.get(sortedTopBotList.size()-1);
//			WavePoint wpLast2 = sortedTopBotList.get(sortedTopBotList.size()-2);
//			WavePoint wpLast3 = sortedTopBotList.get(sortedTopBotList.size()-3);
//			WavePoint wpLast4 = sortedTopBotList.get(sortedTopBotList.size()-4);
//
//			WavePoint latestBot = null;
//			WavePoint latestTop = null;
//
//
//			int wpLast1Idx= StreamTransformHelper.findIndex(stockList, wpLast1.getDate());
//			List<StockBean> tailSkList = stockList.subList(wpLast1Idx+1, stockList.size());
//			StockBean tailMaxH = StreamTransformHelper.findMaxHighStock(tailSkList);
//			StockBean tailMinL = StreamTransformHelper.findMinLowStock(tailSkList);
//
//			if(tailMaxH==null || tailMinL==null)
//				return attributes;
//
//			int tailMinLIdx = StreamTransformHelper.findMinIndex(tailSkList);
//			boolean isDistanceCLose = (tailSkList.size() - tailMinLIdx) <=3;
//
//			boolean isWaveHigherHighAtBegining = false;
//			//last1 may lower than  TOP
//			if(WaveType.BOT== wpLast4.getType() && WaveType.TOP== wpLast3.getType() && WaveType.BOT== wpLast2.getType() && WaveType.TOP== wpLast1.getType()) {
//				boolean isMeet1 = wpLast2.getL()< last1.getL() &&  wpLast3.getH() < wpLast1.getH() && wpLast4.getL() < wpLast2.getL();
//				boolean isMeet2 = wpLast4.getH() < wpLast3.getH() && wpLast4.getL() < wpLast3.getL() // BOT4 -> TOP3
//								&& wpLast3.getL()> wpLast2.getL() && wpLast3.getH()> wpLast2.getH()// TOP3 -> BOT2
//								&& wpLast2.getH() < wpLast1.getH() && wpLast2.getL() < wpLast1.getL() //BOT2 -> TOP1
//								&& wpLast3.getH() < wpLast1.getStockBean().getBodyTop();
//								;
//				if(isMeet1 && isMeet2) {
//					latestTop = wpLast1;
//					latestBot = wpLast2;
//				}
//
//
//				boolean isHigherLowPrice = tailMinL.getL() >wpLast2.getL();
//				boolean isLast1Higher = tailMinL.getTxnDateInt() < last1.getTxnDateInt() && last1.getC() >  tailMinL.getBodyTop() && last1.getH() >  tailMinL.getH();
//				boolean bCombineResult = isDistanceCLose && isHigherLowPrice && isLast1Higher;
//
//				if(isMeet1 && isMeet2 && bCombineResult) {
//					isWaveHigherHighAtBegining = true;
////					attributes.add(KPatternConst.KP_WAVE_HIGHER_HIGH_BEGAIN_AGAIN);
//				}
//			}
//
//			// last1 may higher than  TOP
//			if(WaveType.TOP== wpLast4.getType() && WaveType.BOT== wpLast3.getType() && WaveType.TOP== wpLast2.getType() && WaveType.BOT== wpLast1.getType()) {
//				boolean isMeet1 = wpLast2.getH()< last1.getH() && wpLast3.getL() < wpLast1.getL();
//				boolean isMeet2 = wpLast4.getH() > wpLast3.getH() && wpLast4.getL() > wpLast3.getL() // T4 -> B3
//						&& wpLast3.getL()< wpLast2.getL() && wpLast3.getH()< wpLast2.getH()// B3 -> T2
//						&& wpLast2.getH()> wpLast1.getH() && wpLast2.getL() > wpLast1.getL() //T2 -> B1
//						&& wpLast4.getH() < wpLast2.getStockBean().getBodyTop();
//						;
//				if(isMeet1 && isMeet2) {
//					latestTop = wpLast2;
//					latestBot = wpLast1;
//				}
//
//				boolean isNotUpBreakPrevTop = tailMaxH.getH() < wpLast2.getH();
//				boolean isLast1Higher = last1.getC() >  tailMinL.getBodyTop() && last1.getH() >  tailMinL.getH();
//				boolean bCombineResult = isDistanceCLose && isNotUpBreakPrevTop && isLast1Higher;
//				if(isMeet1 && isMeet2 && bCombineResult) {
//					isWaveHigherHighAtBegining = true;
////					attributes.add(KPatternConst.KP_WAVE_HIGHER_HIGH_BEGAIN_AGAIN);
//				}
//			}
//
//			if(isWaveHigherHighAtBegining) {
//				attributes.add(KPatternConst.KP_WAVE_HIGHER_HIGH_BEGAIN_AGAIN);
//			}else if(latestBot!=null && latestTop !=null && last1.getL() > latestBot.getL() &&  last1.getH() < latestTop.getH()) {
//				attributes.add(KPatternConst.KP_WAVE_HIGHER_HIGH);
//			}
//
//			if(latestBot!=null && latestTop !=null && last1.getL() > latestBot.getL() && last1.getH() >= latestTop.getH() && last1.getC() >= latestTop.getStockBean().getBodyTop()) {
//				String msg = "(尋頂中)";
//				if(last2.getH() <latestTop.getH())
//					msg = "(尋新頂D0)";
//				attributes.add(KPatternConst.KP_WAVE_HIGHER_HIGH + msg);
//
//			}
//
//
//
//			return attributes;
//		}
	 
	 
//	 private Set<String> findWaveHeadAndShoulder(List<StockBean> stockList, List<WavePoint> sortedTopBotList) {
//			Set<String> attributes = new LinkedHashSet<String>();
//			int windowSize = 3;
//
//			if(sortedTopBotList.size() <= windowSize )
//				return attributes;
//
//			StockBean last1 = stockList.get(stockList.size()-1);
//			StockBean last2 = stockList.get(stockList.size()-2);
//
//			List<WavePoint> sortedTopList = sortedTopBotList.stream().filter(x-> WaveType.TOP== x.getType()).toList();
//
//
//			WavePoint wpLast1 = sortedTopBotList.get(sortedTopBotList.size()-1);//BOT
//			WavePoint wpLast2 = sortedTopBotList.get(sortedTopBotList.size()-2);//TOP
//			WavePoint wpLast3 = sortedTopBotList.get(sortedTopBotList.size()-3);//BOT
//			WavePoint wpLast4 = sortedTopBotList.get(sortedTopBotList.size()-4);//TOP
//			WavePoint wpLast5 = sortedTopBotList.get(sortedTopBotList.size()-4);//BOT
//
//			if(WaveType.BOT== wpLast5.getType() && WaveType.TOP== wpLast4.getType() && WaveType.BOT== wpLast3.getType() && WaveType.TOP== wpLast2.getType() && WaveType.BOT== wpLast1.getType())
//			{
//				boolean isMeetBot = wpLast5.getL()> wpLast3.getStockBean().getBodyBottom()
//									&&   wpLast1.getL()> wpLast3.getStockBean().getBodyBottom() ;
//
//				List<WavePoint> targetWpList = sortedTopList.subList(sortedTopList.size()-windowSize, sortedTopList.size());
//				boolean isFlatTop = this.isFlatTop(targetWpList);
//
//				boolean isTurnStrong = last1.getC() > last2.getBodyTop();
//
//				if(isMeetBot && isFlatTop && isTurnStrong) {
//					attributes.add(KPatternConst.KP_HEAD_N_SHOULDER);
//				}
//			}
//
//			return attributes;
//		}
	 
	 /*
	  * 價跌量縮 9988.HK case (2024-05-03 to 2024-05-09)
	  	Price Rule:
		1. find TOP
		2. TOP to last, last.L must Lowest
		3. last.H < TOP.L
		4. TOP.date - Last.date <= 8
		
		Volume Rule:
		1. TOP.vol is the max during(TOP to last)
		2. last.dailyVol<1.05 or <1
		3. last.vol < TOP.vol*0.7
	  
	  * 
	  */
	 //價升量升 , 價跌量升, 價升量縮, 價跌量縮

	public PriceVolResult calcPriceVolumeChanges(List<StockBean> stockList, List<WavePoint> sortedTopBotList) {
		 if(sortedTopBotList.size()<4 )
				return null;
			int stockSize = stockList.size();
			StockBean last1 = stockList.get(stockList.size()-1);
			
			WavePoint lastWavePoint = sortedTopBotList.get(sortedTopBotList.size()-1);
			
			List<StockBean> subList = StreamTransformHelper.extractData(stockList, lastWavePoint.getDate(), last1.getTxnDate());
			
			if(subList==null || subList.isEmpty())
				return null;
			
			
			
			//lastWavePoint.
			VolumePriceType vpType = null;
			if(WaveType.TOP == lastWavePoint.getType()) {
				vpType = handler.findUpTrendPriceDownVolDown(subList);	
			}else if(WaveType.BOT == lastWavePoint.getType()) {
				vpType = handler.findUpTrendBottomPriceVolRebound(subList);
			}
			
			VolumePriceType vpType2 = handler.findUpTrendPriceUpVolDown(subList);
			
			int startPos = stockSize-100;
			if(startPos<0)
				startPos = 0;
			VolumePriceType vpType3 = handler.findExtremelyShrinkingVolume(stockList.subList(startPos, stockSize));
			
			PriceVolResult diverResult = null;
			if(vpType!=null) {
				diverResult = new PriceVolResult();
				diverResult.setDates(Arrays.asList(lastWavePoint.getDate()));
				if(vpType!=null)
					diverResult.getDivergenceType().add(vpType);
				if(vpType2!=null)
					diverResult.getDivergenceType().add(vpType2);
				if(vpType3!=null)
					diverResult.getDivergenceType().add(vpType3);
			}
			return diverResult;

	 }




}
