package com.sjm.test.yahdata.analy.ta.helper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import com.maas.util.DateHelper;
import com.sjm.test.yahdata.analy.bean.GapBean;
import com.sjm.test.yahdata.analy.bean.Island;
import com.sjm.test.yahdata.analy.bean.raw.StockBean;
import com.sjm.test.yahdata.analy.conts.Const;
import com.sjm.test.yahdata.analy.conts.KPatternConst;
import com.sjm.test.yahdata.analy.conts.VolConst;
import com.sjm.test.yahdata.analy.conts.ta.RuleConst;
import com.sjm.test.yahdata.analy.helper.StreamTransformHelper;
import com.sjm.test.yahdata.analy.ta.KHelper;

public class VolumePriceStructureHelper {
	public static String DAY3_H_CLOSE= "新高-3天收";
	public static String DAY3_H= "新高-3天";
	public static String DAY3_L_CLOSE = "新低-3天收";
	public static String DAY3_L = "新低-3天";
	public static String DAY3_BODY_TOP_H 	= "新高-3天(高於全部燭身頂)";
	public static String DAY3_BODY_BOTTOM_L = "新低-3天(低於全部燭身底)";

	///week and month 
		public static String WEEKLY_H_CLOSE = "新高-週收";
		public static String WEEKLY_H = "新高-週";
		public static String WEEKLY_L_CLOSE = "新低-週收";
		public static String WEEKLY_L = "新低-週";
		public static String WEEKLY_BODY_TOP_H = "新高-週(高於全部燭身頂)";
		public static String WEEKLY_BODY_BOTTOM_L = "新低-週(低於全部燭身底)";
		
		public static String MONTHLY_H_CLOSE = "新高-月收";
		public static String MONTHLY_H = "新高-月";
		public static String MONTHLY_L_CLOSE = "新低-月收";
		public static String MONTHLY_L = "新低-月";
		
		public static String MONTHLY_BODY_TOP_H 	= "新高-月(高於全部燭身頂)";
		public static String MONTHLY_BODY_BOTTOM_L = "新低-月(低於全部燭身底)";

		public static String DAY10_H_CLOSE= "新高-10天收";
		public static String DAY10_H= "新高-10天";
		public static String DAY10_L_CLOSE = "新低-10天收";
		public static String DAY10_L = "新低-10天";
		public static String DAY10_BODY_TOP_H 	= "新高-10天(高於全部燭身頂)";
		public static String DAY10_BODY_BOTTOM_L = "新低-10天(低於全部燭身底)";

		public static String DAY50_H_CLOSE= "新高-50天收";
		public static String DAY50_H= "新高-50天";
		public static String DAY50_L_CLOSE = "新低-50天收";
		public static String DAY50_L = "新低-50天";
		public static String DAY50_BODY_TOP_H 	= "新高-50天(高於全部燭身頂)";
		public static String DAY50_BODY_BOTTOM_L = "新低-50天(低於全部燭身底)";
		
		public static String DAY250_H_CLOSE= "新高-250天收";
		public static String DAY250_H= "新高-250天";
		public static String DAY250_L_CLOSE = "新低-250天收";
		public static String DAY250_L = "新低-250天";
		public static String DAY250_BODY_TOP_H 	= "新高-250天(高於全部燭身頂)";
		public static String DAY250_BODY_BOTTOM_L = "新低-250天(低於全部燭身底)";
		
		
		public static String getPriceStatus(List<StockBean> stockList) {
			
			if(stockList.size() <= RuleConst.QUARTER_NUM_OF_DAYS)
				return Const.SPACE;
			
			boolean isEnough10D = stockList.size()- RuleConst.DAYS_10 >= 0;
			boolean isEnough50D = stockList.size()- RuleConst.QUARTER_NUM_OF_DAYS >= 0;
			boolean isEnough250D = stockList.size()- RuleConst.YEAR_NUM_OF_DAYS >= 0;
			
			
			StockBean last = stockList.get(stockList.size()-1);
			List<StockBean>  prev3DList = stockList.subList(stockList.size()- RuleConst.DAYS_3, stockList.size()-1);
			List<StockBean>  prevWeeklyList = stockList.subList(stockList.size()- RuleConst.WEEK_NUM_OF_DAYS, stockList.size()-1);
			List<StockBean>  prev10DList = (!isEnough10D)?null:stockList.subList(stockList.size()- RuleConst.DAYS_10, stockList.size()-1);
			List<StockBean>  prevMonthlyList = stockList.subList(stockList.size()- RuleConst.MONTH_NUM_OF_DAYS, stockList.size()-1);
			List<StockBean>  prev50DList = (!isEnough50D)?null:stockList.subList(stockList.size()- RuleConst.QUARTER_NUM_OF_DAYS, stockList.size()-1);
			List<StockBean>  prev250DList = (!isEnough250D)?null:stockList.subList(stockList.size()- RuleConst.YEAR_NUM_OF_DAYS, stockList.size()-1);
			
			StockBean minBy50D = (!isEnough50D)?null:prev50DList.stream().min(Comparator.comparing(StockBean::getL)).orElseThrow(NoSuchElementException::new);
			StockBean maxBy50D = (!isEnough50D)?null:prev50DList.stream().max(Comparator.comparing(StockBean::getH)).orElseThrow(NoSuchElementException::new);
			StockBean minBodyBottomBy50D = (!isEnough50D)?null:prev50DList.stream().min(Comparator.comparing(StockBean::getBodyBottom)).orElseThrow(NoSuchElementException::new);
			StockBean maxBodyTopBy50D = (!isEnough50D)?null:prev50DList.stream().max(Comparator.comparing(StockBean::getBodyTop)).orElseThrow(NoSuchElementException::new);
			
			StockBean minByWeek = prevWeeklyList.stream().min(Comparator.comparing(StockBean::getL)).orElseThrow(NoSuchElementException::new);
			StockBean maxByWeek = prevWeeklyList.stream().max(Comparator.comparing(StockBean::getH)).orElseThrow(NoSuchElementException::new);
			
			StockBean minBodyBottomByWeek = prevWeeklyList.stream().min(Comparator.comparing(StockBean::getBodyBottom)).orElseThrow(NoSuchElementException::new);
			StockBean maxBodyTopByWeek = prevWeeklyList.stream().max(Comparator.comparing(StockBean::getBodyTop)).orElseThrow(NoSuchElementException::new);
			
			
			
			
			StockBean minBodyBottomByMonth = prevMonthlyList.stream().min(Comparator.comparing(StockBean::getBodyBottom)).orElseThrow(NoSuchElementException::new);
			StockBean maxBodyTopByMonth = prevMonthlyList.stream().max(Comparator.comparing(StockBean::getBodyTop)).orElseThrow(NoSuchElementException::new);
			StockBean minByMonth = prevMonthlyList.stream().min(Comparator.comparing(StockBean::getL)).orElseThrow(NoSuchElementException::new);
			StockBean maxByMonth = prevMonthlyList.stream().max(Comparator.comparing(StockBean::getH)).orElseThrow(NoSuchElementException::new);

			StockBean minBodyBottomBy10D = prev10DList.stream().min(Comparator.comparing(StockBean::getBodyBottom)).orElseThrow(NoSuchElementException::new);
			StockBean maxBodyTopBy10D = prev10DList.stream().max(Comparator.comparing(StockBean::getBodyTop)).orElseThrow(NoSuchElementException::new);
			StockBean minBy10D = prev10DList.stream().min(Comparator.comparing(StockBean::getL)).orElseThrow(NoSuchElementException::new);
			StockBean maxBy10D = prev10DList.stream().max(Comparator.comparing(StockBean::getH)).orElseThrow(NoSuchElementException::new);

			StockBean minBodyBottomBy3D = prev3DList.stream().min(Comparator.comparing(StockBean::getBodyBottom)).orElseThrow(NoSuchElementException::new);
			StockBean maxBodyTopBy3D = prev3DList.stream().max(Comparator.comparing(StockBean::getBodyTop)).orElseThrow(NoSuchElementException::new);
			StockBean minBy3D = prev3DList.stream().min(Comparator.comparing(StockBean::getL)).orElseThrow(NoSuchElementException::new);
			StockBean maxBy3D = prev3DList.stream().max(Comparator.comparing(StockBean::getH)).orElseThrow(NoSuchElementException::new);

			StockBean minBy250D = (!isEnough250D)?null:prev250DList.stream().min(Comparator.comparing(StockBean::getL)).orElseThrow(NoSuchElementException::new);
			StockBean maxBy250D = (!isEnough250D)?null:prev250DList.stream().max(Comparator.comparing(StockBean::getH)).orElseThrow(NoSuchElementException::new);
			StockBean minBodyBottomBy250D = (!isEnough250D)?null:prev250DList.stream().min(Comparator.comparing(StockBean::getBodyBottom)).orElseThrow(NoSuchElementException::new);
			StockBean maxBodyTopBy250D = (!isEnough250D)?null:prev250DList.stream().max(Comparator.comparing(StockBean::getBodyTop)).orElseThrow(NoSuchElementException::new);
			
//			double wLowest = sortedWeeklyPrevLowList.get(0).getL();		
//			double wHighest = sortedWeeklyPrevHighList.get(0).getH();
//			
//			double mLowest = sortedMonthlyPrevLowList.get(0).getL();		
//			double mHighest = sortedMonthlyPrevHighList.get(0).getH();
			
			boolean isOnDayPriceChangePositive =  last.getDayChgPct() > 0;
			if(isEnough250D && last.getC() > maxBy250D.getH() && isOnDayPriceChangePositive) {
				return DAY250_H_CLOSE; 
			}else if(isEnough250D && last.getC() > maxBodyTopBy250D.getBodyTop()) {
				return DAY250_BODY_TOP_H; 
			}else if(isEnough250D && last.getH() > maxBy250D.getH()) {
				return DAY250_H; 
			}
			else if(isEnough50D && last.getC() > maxBy50D.getH() && isOnDayPriceChangePositive) {
				return DAY50_H_CLOSE; 
			}else if(isEnough50D && last.getC() > maxBodyTopBy50D.getBodyTop()) {
				return DAY50_BODY_TOP_H; 
			}else if(isEnough50D && last.getH() > maxBy50D.getH() && isOnDayPriceChangePositive) {
				return DAY50_H; 
				
			}else if(last.getC() > maxByMonth.getH() && isOnDayPriceChangePositive) {
				return MONTHLY_H_CLOSE;
			}else if(last.getC() > maxBodyTopByMonth.getBodyTop()) {
				return MONTHLY_BODY_TOP_H; 
			}else if(last.getH() > maxByMonth.getH() && isOnDayPriceChangePositive) {
				return MONTHLY_H;
			}else if(last.getC() > maxBy10D.getH() && isOnDayPriceChangePositive) {
				return DAY10_H_CLOSE;
			}else if(last.getC() > maxBodyTopBy10D.getBodyTop()) {
				return DAY10_BODY_TOP_H;
			}else if(last.getH() > maxBy10D.getH() && isOnDayPriceChangePositive) {
				return DAY10_H;
			}else if(last.getC() > maxByWeek.getH()  && isOnDayPriceChangePositive) {
				return WEEKLY_H_CLOSE; 
			}else if(last.getC() > maxBodyTopByWeek.getBodyTop()) {
				return WEEKLY_BODY_TOP_H; 
			}else if(last.getH() > maxByWeek.getH() && isOnDayPriceChangePositive) {
				return WEEKLY_H; 
			}else if(last.getC() > maxBy3D.getH() && isOnDayPriceChangePositive) {
				return DAY3_H_CLOSE;
			}else if(last.getC() > maxBodyTopBy3D.getBodyTop()) {
				return DAY3_BODY_TOP_H;
			}else if(last.getH() > maxBy3D.getH() && isOnDayPriceChangePositive) {
				return DAY3_H;
			}
			boolean isOnDayPriceChangeNegative =  last.getDayChgPct() < 0;
			
			if(isEnough250D && last.getC() < minBy250D.getL() && isOnDayPriceChangeNegative) {
				return DAY250_L_CLOSE; 
			}else if(isEnough250D && last.getC() < minBodyBottomBy250D.getBodyBottom()) {
				return DAY250_BODY_BOTTOM_L;
			}else if(isEnough250D && last.getL() < minBy250D.getL() && isOnDayPriceChangeNegative) {
				return DAY250_L; 
				
			}else if(isEnough50D && last.getC() < minBy50D.getL() && isOnDayPriceChangeNegative) {
				return DAY50_L_CLOSE;								
			}else if(isEnough50D && last.getC() < minBodyBottomBy50D.getBodyBottom()) {
				return DAY50_BODY_BOTTOM_L;
			}else if(isEnough50D && last.getL() < minBy50D.getL() && isOnDayPriceChangeNegative) {
				return DAY50_L;
				
			}else if(last.getC() < minByMonth.getL() && isOnDayPriceChangeNegative) {
				return MONTHLY_L_CLOSE; 
			}else if(last.getC() < minBodyBottomByMonth.getBodyBottom()) {
				return MONTHLY_BODY_BOTTOM_L; 
			}else if(last.getL() < minByMonth.getL() && isOnDayPriceChangeNegative) {
				return MONTHLY_L;

			}else if(isEnough10D && last.getC() < minBy10D.getL() && isOnDayPriceChangeNegative) {
				return DAY10_L_CLOSE;
			}else if(isEnough10D && last.getC() < minBodyBottomBy10D.getBodyBottom()) {
				return DAY10_BODY_BOTTOM_L;
			}else if(isEnough10D && last.getL() < minBy10D.getL() && isOnDayPriceChangeNegative) {
				return DAY10_L;

			}else if(last.getC() < minByWeek.getL() && isOnDayPriceChangeNegative ) {
				return WEEKLY_L_CLOSE; 
			}else if(last.getC() < minBodyBottomByWeek.getBodyBottom()) {
				return WEEKLY_BODY_BOTTOM_L;
			}else if(last.getL() < minByWeek.getL() && isOnDayPriceChangeNegative) {
				return WEEKLY_L; 
			}else if(last.getC() < minBy3D.getL() && isOnDayPriceChangeNegative) {
				return DAY3_L_CLOSE;
			}else if(last.getC() < minBodyBottomBy3D.getBodyBottom()) {
				return DAY3_BODY_BOTTOM_L;
			}else if(last.getL() < minBy3D.getL() && isOnDayPriceChangeNegative) {
				return DAY3_L;
			}
			return Const.SPACE;
		}
		
		
		public static String getVolumeStatus(List<StockBean> stockList) {
			if(stockList.size()<=RuleConst.MONTH_NUM_OF_DAYS)
				return Const.SPACE;
			
			
			boolean isEnought50D = stockList.size()- RuleConst.QUARTER_NUM_OF_DAYS >= 0;
			boolean isEnought20D = stockList.size()- RuleConst.MONTH_NUM_OF_DAYS >= 0;
			
			
			StockBean last = stockList.get(stockList.size()-1);
			List<StockBean>  prevWeeklyList = stockList.subList(stockList.size()- RuleConst.WEEK_NUM_OF_DAYS, stockList.size()-1);
			List<StockBean>  prev50DList = 		(isEnought50D==false)?null:stockList.subList(stockList.size()- RuleConst.QUARTER_NUM_OF_DAYS, stockList.size()-1);
			List<StockBean>  prevMonthlyList = 	(isEnought20D==false)?null:stockList.subList(stockList.size()- RuleConst.MONTH_NUM_OF_DAYS, stockList.size()-1);
			
			StockBean minByWeek = prevWeeklyList.stream().min(Comparator.comparing(StockBean::getVolume)).orElseThrow(NoSuchElementException::new);
			StockBean maxByWeek = prevWeeklyList.stream().max(Comparator.comparing(StockBean::getVolume)).orElseThrow(NoSuchElementException::new);

			StockBean minByMonth = (isEnought20D==false)?null:prevMonthlyList.stream().min(Comparator.comparing(StockBean::getVolume)).orElseThrow(NoSuchElementException::new);
			StockBean maxByMonth = (isEnought20D==false)?null:prevMonthlyList.stream().max(Comparator.comparing(StockBean::getVolume)).orElseThrow(NoSuchElementException::new);
			
			StockBean minBy50D = (isEnought50D==false)?null:prev50DList.stream().min(Comparator.comparing(StockBean::getVolume)).orElseThrow(NoSuchElementException::new);
			StockBean maxBy50D = (isEnought50D==false)?null:prev50DList.stream().max(Comparator.comparing(StockBean::getVolume)).orElseThrow(NoSuchElementException::new);
			
			if(isEnought50D && last.getVolume() > maxBy50D.getVolume()) {
				return "50天最高量"; 
			}else if(isEnought20D && last.getVolume() > maxByMonth.getVolume()) {
				return "月最高量"; 
			}else if(last.getVolume() > maxByWeek.getVolume()) {
				return "週最高量"; 
			}
			
			if(isEnought50D && last.getVolume() < minBy50D.getVolume()) {
				return "50天最低量"; 
			}else if(isEnought20D && last.getVolume() < minByMonth.getVolume()) {
					return "月最低量"; 
			}else if(last.getVolume() < minByWeek.getVolume()) {
				return "週最低量"; 
			}
			
			return Const.SPACE;
		}
		
		public static Island getIsland(List<StockBean> stockList) {
//			StockBean last = stockList.get(stockList.size()-1);
			if(stockList.size() <= RuleConst.MONTH_NUM_OF_DAYS)
				return new Island(Const.SPACE, Const.SPACE, 0);
			
			List<StockBean>  pass10DaysList = stockList.subList(stockList.size()- RuleConst.MONTH_NUM_OF_DAYS, stockList.size());
			
			
			//find all Gap Info 
			List<GapBean> gapUpList = new ArrayList<GapBean>();
			List<GapBean> gapDownList = new ArrayList<GapBean>();
			
			for (int i = 1; i < pass10DaysList.size(); i++) {
				StockBean prev = pass10DaysList.get(i-1);
				StockBean curr = pass10DaysList.get(i);
				if(KHelper.isGapUp(prev, curr)) {
					GapBean gapBean = new GapBean(curr.getStockCode(), curr.getTxnDate());
					gapBean.setGapBottom(prev.getH());
					gapBean.setGapTop(curr.getL());
					gapBean.setUP(true);					
					double volRatio = (double)curr.getVolume() / (double)prev.getVolume();
					double gapPtc = (curr.getL() - prev.getH())/ prev.getH();
					if(volRatio < 1.5)
						gapBean.setEnoughtVolume(false);
					gapBean.setGapPct(gapPtc);
					gapUpList.add(gapBean);
				}
				if(KHelper.isGapDown(prev, curr)) {
					GapBean gapBean = new GapBean(curr.getStockCode(), curr.getTxnDate());
					gapBean.setGapBottom(curr.getH());
					gapBean.setGapTop(prev.getL());
					gapBean.setUP(false);
					
					double gapPtc = (curr.getH() - prev.getL())/ prev.getL();
					gapBean.setGapPct(gapPtc);
					
					gapDownList.add(gapBean);
				}
				
			}
			//validatioin
			if(gapUpList.isEmpty() || gapDownList.isEmpty()){
				return new Island(Const.SPACE, Const.SPACE, 0);
			}
			
			Island goDownIslandDate = doDownIsland(gapUpList, gapDownList, stockList);
			Island goUpIslandDate = doUpIsland(gapUpList, gapDownList, stockList);
			
//			if(goUpIslandDate==null && goDownIslandDate==null) {
//				return new Island(Const.NA, Const.NA, 0);
//			}
			
			if(goUpIslandDate==null && goDownIslandDate==null) {
				return new Island(Const.SPACE, Const.SPACE, 0);
			}else if(goUpIslandDate!=null) {
				return goUpIslandDate;
			}else if(goDownIslandDate!=null) {
				return goDownIslandDate;
			}else {
				return new Island(Const.SPACE, Const.SPACE, 0);
			}
		}
		
		
		private static Island doDownIsland(List<GapBean> gapUpList, List<GapBean> gapDownList, List<StockBean> stockList) {
			
			StockBean last = stockList.get(stockList.size()-1);
			StockBean last2nd = stockList.get(stockList.size()-2);
			StockBean last3rd = stockList.get(stockList.size()-3);
			if(KHelper.isGapUp(last3rd, last2nd) && KHelper.isGapDown(last2nd, last)) {
				new Island(KPatternConst.ISLAND_TOP_REVERSAL, last.getTxnDate(), 1);

//				return last.getTxnDate();
			}
			
			GapBean lastGapUpBean = gapUpList.get(gapUpList.size()-1);
			GapBean lastGapDownBean = gapDownList.get(gapDownList.size()-1);

			boolean isValidCondition = DateHelper.before(lastGapUpBean.getDate(), lastGapDownBean.getDate());
			if(isValidCondition==false)
				return null;
			
			GapBean downB = null;
			GapBean upB = null;
			boolean isAbsGap = false;
			
			for(int i=gapDownList.size()-1; i>=0; i--) 
			{
				downB = gapDownList.get(i);
				
				for(int j=gapUpList.size()-1; j>=0; j--) {
					upB = gapUpList.get(j);
			  		
			  		if (upB.getGapTop() < downB.getGapTop() && upB.getGapBottom() > downB.getGapBottom())
			  			isAbsGap = true;
			  		
			  		if( upB.getGapTop() > downB.getGapTop() && upB.getGapBottom() < downB.getGapBottom() )
			  			isAbsGap = true;
			  		if(upB.getGapTop() > downB.getGapTop() && upB.getGapBottom() > downB.getGapBottom()  )
			  			isAbsGap = true;
			  		if( upB.getGapTop() < downB.getGapTop() && upB.getGapBottom() < downB.getGapBottom())
			  			isAbsGap = true;
			  		
			  		if(isAbsGap)
			  			break;
			  	}	
				
				if(isAbsGap)
		  			break; 
		  }//end loop
			
			
			List<StockBean> subList = StreamTransformHelper.subList(stockList, upB.getDate(), downB.getDate());
			if(subList.isEmpty())
				return null;
			
			StockBean minLBean = subList.stream().min(Comparator.comparing(StockBean::getL))
		      .orElseThrow(NoSuchElementException::new);
			
			List<StockBean> subList2 = StreamTransformHelper.extractData(stockList, downB.getDate(), stockList.get(stockList.size()-1).getTxnDate());
			StockBean maxHBean = subList2.stream().max(Comparator.comparing(StockBean::getH))
				      .orElseThrow(NoSuchElementException::new);
			//Check island exist: NOT (minLBean.l <= upB.gapBottom || minLBean.l<=downB.getBottom)
			//Check island is filled maxHBean.h >=downB.gapTop
			
			boolean isExistIsland = !(minLBean.getL() <= upB.getGapBottom() || minLBean.getL()<=downB.getGapBottom() );
			 
			if(isExistIsland){
				if(maxHBean.getH()<downB.getGapTop()) {
					int islandSize = StreamTransformHelper.txDaysBetween(stockList,upB.getDate() ,downB.getDate());

					return new Island(KPatternConst.ISLAND_TOP_REVERSAL, downB.getDate(), islandSize);

//					return downB.getDate();
				}
			}
			
			return null;
			
			
		}
		
		
		private static Island doUpIsland(List<GapBean> gapUpList, List<GapBean> gapDownList, List<StockBean> stockList) {
			StockBean last = stockList.get(stockList.size()-1);
			StockBean last2nd = stockList.get(stockList.size()-2);
			StockBean last3rd = stockList.get(stockList.size()-3);
			if(KHelper.isGapDown(last3rd, last2nd) && KHelper.isGapUp(last2nd, last)) {
//				return last.getTxnDate();
				new Island(KPatternConst.ISLAND_BOTTOM_REVERSAL, last.getTxnDate(), 1);
			}
			
			
			GapBean lastGapUpBean = gapUpList.get(gapUpList.size()-1);
			GapBean lastGapDownBean = gapDownList.get(gapDownList.size()-1);

			boolean isValidCondition = DateHelper.before(lastGapDownBean.getDate(), lastGapUpBean.getDate());
			if(isValidCondition==false)
				return null;
			
			GapBean upBX = null;
			GapBean downBX = null;
			boolean isAbsGap = false;
			for(int i=gapUpList.size()-1; i>=0; i--) {
				upBX = gapUpList.get(i);
				
				for(int j=gapDownList.size()-1; j>=0; j--) {
					downBX = gapDownList.get(j);
			  		
			  		
			  		if (downBX.getGapTop() < upBX.getGapTop() && downBX.getGapBottom() > upBX.getGapBottom())
			  			isAbsGap = true;
			  		
			  		if( downBX.getGapTop() > upBX.getGapTop() && downBX.getGapBottom() < upBX.getGapBottom() )
			  			isAbsGap = true;
			  		if(downBX.getGapTop() > upBX.getGapTop() && downBX.getGapBottom() > upBX.getGapBottom() )
			  			isAbsGap = true;
			  		if( downBX.getGapTop() < upBX.getGapTop() && downBX.getGapBottom() < upBX.getGapBottom() )
			  			isAbsGap = true;
			  		
			  		if(isAbsGap)
			  			break;//return upBX.getDate();
			  	}	
				if(isAbsGap)
		  			break;
			  }
			
			
			List<StockBean> subList = StreamTransformHelper.subList(stockList, downBX.getDate(), upBX.getDate());
			if(subList.isEmpty())
				return null;
			
			StockBean maxHBean = subList.stream().max(Comparator.comparing(StockBean::getH))
		      .orElseThrow(NoSuchElementException::new);
			
			List<StockBean> subList2 = StreamTransformHelper.extractData(stockList, upBX.getDate(), stockList.get(stockList.size()-1).getTxnDate());
			StockBean minLBean = subList2.stream().min(Comparator.comparing(StockBean::getL))
				      .orElseThrow(NoSuchElementException::new);
			
			
			boolean isExistIsland = !(maxHBean.getH()>= downBX.getGapTop() || maxHBean.getH()>=upBX.getGapTop());
			if(isExistIsland) {
				
				if(minLBean.getL()> upBX.getGapBottom()) {
					int islandSize = StreamTransformHelper.txDaysBetween(stockList,downBX.getDate() ,upBX.getDate());

					return new Island(KPatternConst.ISLAND_BOTTOM_REVERSAL, upBX.getDate(), islandSize);
					
//					return upBX.getDate();
				}
					
			}
			return null;
			
		}
		
		
		
		
		
		
		
		//get Comparison of TwoDaysTradingVolumes
		public static String getComparisonOfTwoDaysTradingVolumes(StockBean prev, StockBean curr) {
			double ratio = KHelper.getVolumeIncreaseRatio(prev, curr);
			return getVolumeDescription(ratio);
		}
		
		public static String getRecentDaysVolumeDescription(List<StockBean> stockList, int days, int longDays) {
			
			
			if(stockList.size() <= longDays + days)
				return Const.NA;
			
			int DAYS_NORMAL = longDays + days;
			
			int startIdxA = stockList.size() - DAYS_NORMAL;

			List<StockBean> stockListSeveralDays = stockList.subList(startIdxA, stockList.size() - days+1);
			List<StockBean> stockListB = stockList.subList(stockList.size()-days, stockList.size());
			
			double avgAVol = stockListSeveralDays.stream().mapToDouble(StockBean::getVolume).average().orElse(Double.NaN);
			double avgBVol = stockListB.stream().mapToDouble(StockBean::getVolume).average().orElse(Double.NaN);
			
			double ratio = avgBVol/avgAVol;
			return getVolumeDescription(ratio);
		}
		
		/**
		 * @param stockList
		 * @param days
		 * @param longDays
		 * @return
		 */
		@Deprecated
		public static String getRecentDaysPctDescription(List<StockBean> stockList, int days, int longDays) {
			
			
			if(stockList.size() <= longDays + days)
				return Const.NA;
			
			int DAYS_NORMAL = longDays + days;
			
			int startIdxA = stockList.size() - DAYS_NORMAL;

			List<StockBean> stockListSeveralDays = stockList.subList(startIdxA, stockList.size() - days+1);
			List<StockBean> stockListB = stockList.subList(stockList.size()-days, stockList.size());
			
			double avgHLPct = stockListSeveralDays.stream().mapToDouble(StockBean::getHighestLowestPct).average().orElse(Double.NaN);
			//
			double targetPct = stockListB.stream()
	                .mapToDouble(stock -> Math.abs(stock.getHighestLowestPct())) // 将每个元素替换为其绝对值
	                .average()
	                .orElse(Double.NaN);
			
//			double maxPrice = stockListB.stream()
//	                .mapToDouble(StockBean::getH)
//	                .max()
//	                .orElse(Double.NaN);
//			double minPrice = stockListB.stream()
//	                .mapToDouble(StockBean::getL)
//	                .min()
//	                .orElse(Double.NaN);
//			double targetPct = (maxPrice-minPrice) / minPrice;
			
			double ratio = targetPct / avgHLPct;
			
			List<Double>  longDaysList = stockListSeveralDays.stream().map(x->Math.abs(x.getHighestLowestPct())).collect(Collectors.toList());
			int position = findNumberPosition(longDaysList, targetPct);
			return position+"/"+longDaysList.size();
			/*
			if(ratio <= 0.3)
				return "近期偏少";
			if(ratio <=0.5)
				return "近期较少";
			if(ratio <=0.9)
				return "稍微低于平均值";
			if(ratio > 0.9 && ratio < 1.1)
				return "在平均值";
			if(ratio >= 1.1 && ratio < 2)
				return "近期较多";
			if(ratio >= 2 )
				return "近期偏多";
			
			return GeneralHelper.to100Pct(ratio);
			*/
		}
		
		@Deprecated
		   public static int findNumberPosition(List<Double> numberList, Double targetNumber) {
		        // 排序整数列表
		        Collections.sort(numberList);
		        
		        // 在排序后的列表中查找目标数字的位置
		        int position = Collections.binarySearch(numberList, targetNumber);
		        
		        // 由于binarySearch方法返回的位置可能为负数，需要进行处理
		        if (position < 0) {
		            position = -position - 1;
		        }
		        
		        return position;
		    }
		
		public static String getVolumeDescription(double ratio) {
			double range1Ratio1 = 0.3;
			double range1Ratio2 = 0.6;
			
			if(ratio > 0.1 && ratio <range1Ratio1) {
				return VolConst.VOL_EXTREMELY_SMALL;
			}		
			if(range1Ratio1 <= ratio && ratio <=range1Ratio2 ) {
				return VolConst.VOL_DECREASE_HALF;
			}
			
			if(range1Ratio2 < ratio && ratio <0.9 ) {
				return VolConst.VOL_FLAT_MINUS;
			}
			if(0.9 <= ratio && ratio <=1.1 ) {
				return VolConst.VOL_FLAT;
			}if(ratio>1.1 && ratio <=1.2 ) {
				return VolConst.VOL_FLAT_PLUS;
			}			

			
			//////////
			 range1Ratio1 = 1.75;
			 range1Ratio2 = 2.2;
			 if(range1Ratio1 <= ratio && ratio <=range1Ratio2) {
				 return VolConst.VOL_INCREASE_2X;
			 }
			 
			 ////////
			 if(ratio > 4.8)
				 return VolConst.VOL_INCREASE_HUGE;
			 if(ratio > 3.9)
				 return VolConst.VOL_INCREASE_OVER_4X;
			 if(ratio > 2.9)
				 return VolConst.VOL_INCREASE_OVER_3X;
			 
			 if(ratio > 1.8)
				 return VolConst.VOL_INCREASE_OVER_2X;
			 
			 if(ratio > 1.2)
				 return VolConst.VOL_INCREASE_NORMAL;
			 
			 return Const.NA;
		}

		public static double getRecentDaysVolumeRatio(List<StockBean> stockList, int days) {

			List<StockBean> stockListSeveralDays = stockList.subList(0, stockList.size()-days+1);
			List<StockBean> stockListB = stockList.subList(stockList.size()-days, stockList.size());
			
			double avgAVol = stockListSeveralDays.stream().mapToDouble(StockBean::getVolume).average().orElse(Double.NaN);
			double avgBVol = stockListB.stream().mapToDouble(StockBean::getVolume).average().orElse(Double.NaN);
			
			double ratio = avgBVol/avgAVol;
			return ratio;
		}
		
		public static double getVolatility(List<StockBean> stockList, int days) {
			List<StockBean> subList = stockList.subList(stockList.size()-days, stockList.size());
			List<Double> volatilityList = new ArrayList<Double>();
			
			for (StockBean stockBean : subList) {
//				stockBean.getH()
				double h2lVolatility = (stockBean.getH() - stockBean.getL())/stockBean.getH();
				volatilityList.add(h2lVolatility);
			}
			
			double avgVolatility = volatilityList.stream().mapToDouble(d -> d).average().orElse(0.0);
			return avgVolatility;
		}
		
		/*
		public static String getHighVolumeDaySituation(List<StockBean> orgStockList, int daysLen) {
			List<StockBean> stockList = orgStockList.subList(orgStockList.size()- daysLen, orgStockList.size());
			StockBean last = orgStockList.get(orgStockList.size()-1);
					
//			StockBean maxBodyChgPctStock = stockList.stream().max(Comparator.comparingDouble(StockBean::getBodyDailyChgPct)).get();
//			StockBean minVolStock = stockList.stream().min(Comparator.comparingDouble(StockBean::getVolume)).get();
			StockBean maxVolStock = stockList.stream().max(Comparator.comparingDouble(StockBean::getVolume)).get();
			
			double avgVol = stockList.stream().mapToDouble(StockBean::getVolume).average().orElse(Double.NaN);
			double maxVolRatio = (double)maxVolStock.getVolume()/avgVol;
//			double minVolRatio = (double)minVolStock.getVolume()/avgVol;
			
			if((maxVolRatio< 1.5) || (maxVolStock.getTxnDateInt() == last.getTxnDateInt()))
				return Const.NA;
			
			
			
			int idxMaxVolStock = StreamTransformHelper.findIndex(stockList, maxVolStock.getTxnDate());
			List<StockBean>  mySubList = stockList.subList(idxMaxVolStock+1, stockList.size());
			double minClose = mySubList.stream().mapToDouble(StockBean::getC).min().orElse(Double.NaN);
			double maxClose = mySubList.stream().mapToDouble(StockBean::getC).max().orElse(Double.NaN);
			
			if(minClose > maxVolStock.getBodyTop()) {
				return "價高於高量日頂(持續數日)";
			}else if(last.getC() > maxVolStock.getBodyTop()) {
				return "價高於高量日燭頂(現價)";
			}
			
			
			if(maxClose < maxVolStock.getBodyBottom()) {
				return "價低於高量日燭底(持續數日)";
			}else if(last.getC() < maxVolStock.getBodyBottom()) {
				return "價低於高量日燭底(現價)";
			}
			
			return Const.NA;
		}
		*/
		
}
