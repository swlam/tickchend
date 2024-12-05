package com.sjm.test.yahdata.analy.ta.pattern;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;

import com.maas.util.DateHelper;
import com.maas.util.GeneralHelper;
import com.sjm.test.yahdata.analy.bean.raw.StockBean;
import com.sjm.test.yahdata.analy.conts.type.KType;
import com.sjm.test.yahdata.analy.helper.AnalyGeneralHelper;
import com.sjm.test.yahdata.analy.helper.StreamTransformHelper;
import com.sjm.test.yahdata.analy.model.DownBreakAndStandUpBean;
import com.sjm.test.yahdata.analy.module.wavepoint.WavePatternAnalyticalResultHelper;
import com.sjm.test.yahdata.analy.ta.KHelper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MPatternAnaly {


	private static final String M_LH ="M_LH";
	private static final String M_HL ="M_HL";
	
	private static final String W_LH ="W_LH";
	private static final String W_HL ="W_HL";
	
	private static final int DAYS_TO_CURRENT_MAX_DAY = 20;
	private static final int DAYS_TO_CURRENT_MIN_DAY = 5;
	
	private static final int DAYS_BTWN_2POINT_MAX = 70;
	private static final int DAYS_BTWN_2POINT_MIN = 7;
	
	
	private WaveHelper helper ;
	
	public MPatternAnaly() {
		this.helper = new WaveHelper();
	}

	public MPatternInfo doMPatternTest(List<StockBean> dateRangeList) {
		
		int maxIdx = helper.maxIndex(dateRangeList);
		StockBean b = this.helper.findBackwardHighToLow(dateRangeList, maxIdx);
		System.out.println(".. backward H to L ..");
		System.out.println(b);
		
		int minIdx = helper.minIndex(dateRangeList);
		StockBean hbean = this.helper.findBackwardLowToHigh(dateRangeList, minIdx);
		System.out.println(".. backward L to H ..");
		System.out.println(hbean);
		
		return null;
	}
	
	
	public MPatternInfo doMPatternHL(List<StockBean> dateRangeList) {
		//h1StockBean : the highest stock
		//h2StockBean : the 2nd highest stock
		
		boolean isMHL = helper.isMhl(dateRangeList);
		if(isMHL==false)
			return null;
		
		int maxIdx = helper.maxIndex(dateRangeList);
		List<StockBean> subList = dateRangeList.subList(maxIdx, dateRangeList.size());

		StockBean h1StockBean = subList.get(0);

		StockBean firstLowPoint = this.helper.findForwardHighToLow(subList);
//		log.debug("firstLowPoint: " + firstLowPoint);

		if(firstLowPoint== null || h1StockBean.getTxnDateInt()==firstLowPoint.getTxnDateInt()) 
			return null;

		StockBean last = dateRangeList.get(dateRangeList.size() - 1);
		StockBean last2 = dateRangeList.get(dateRangeList.size() - 2);
		StockBean last3 = dateRangeList.get(dateRangeList.size() - 3);
		List<StockBean> subList2 = StreamTransformHelper.extractData(dateRangeList, firstLowPoint.getTxnDate(), last.getTxnDate());

		int max2ndIdx = helper.maxIndex(subList2);
		StockBean h2StockBean = subList2.get(max2ndIdx);

//		int diff2ndMax_Last = DateHelper.dayBetween(secondMax.getTxnDate(), last.getTxnDate());
		int diff2ndMax_Last = DateHelper.countBusinessDaysBetween(h2StockBean.getTxnDate(), last.getTxnDate());
//		if(diff2ndMax_Last > DAYS_TO_CURRENT)
		if(diff2ndMax_Last > DAYS_TO_CURRENT_MAX_DAY || diff2ndMax_Last < DAYS_TO_CURRENT_MIN_DAY)
			return null;
		
		
		List<StockBean> twoMaxBtwnList = StreamTransformHelper.extractData(dateRangeList, h1StockBean.getTxnDate(), h2StockBean.getTxnDate());
		StockBean minInTwoMaxPoint = twoMaxBtwnList.stream().min(Comparator.comparing(StockBean::getL))
				.orElseThrow(NoSuchElementException::new);
		
		List<StockBean> secondMaxToEndList = StreamTransformHelper.extractData(dateRangeList, h2StockBean.getTxnDate(), last.getTxnDate());
		StockBean minAfter2ndMax = secondMaxToEndList.stream().min(Comparator.comparing(StockBean::getL))
				.orElseThrow(NoSuchElementException::new);

//		if(minAfter2ndMax.getTxnDateInt() == last.getTxnDateInt())
//			return null;
		
		boolean isMatch = true;
		int diffMax = twoMaxBtwnList.size();//max2ndIdx;//

		if (diffMax < DAYS_BTWN_2POINT_MIN || diffMax > DAYS_BTWN_2POINT_MAX)
			isMatch = false;
		
		double nearLowPointPct = (last.getC() - minInTwoMaxPoint.getL()) / minInTwoMaxPoint.getL(); 
		
		
		List<StockBean> subTargetList = StreamTransformHelper.extractData(dateRangeList, h2StockBean.getTxnDate(), last.getTxnDate());		
		String[] breakDetail = this.helper.getBreakDown(minInTwoMaxPoint, subTargetList);
		if(breakDetail[0].isEmpty()==true)
			isMatch = false;
		
		boolean isCandidateBreak = false;
		if(breakDetail[0].isEmpty()==true && KHelper.isBearishCandle(last) && nearLowPointPct <= 0.05) {
			isMatch = true;
			isCandidateBreak = true;
		}
		//Ignore 反彈到頸線上的
		if(breakDetail[0].isEmpty()==false) {
			if( last.getC() >minInTwoMaxPoint.getBodyTop())
				isMatch =false;
		}
		
		
		MPatternInfo info = null;
		if(isMatch == true || isCandidateBreak == true) {
//			double fallPct = (last.getC()-minInTwoMaxPoint.getL())/minInTwoMaxPoint.getL();

			String supportMsg = "";
			
			if(last3.getC() > minInTwoMaxPoint.getL() && last2.getC() > minInTwoMaxPoint.getL() 
					&& last.getL() < minInTwoMaxPoint.getL() && last.getC() < minInTwoMaxPoint.getBodyBottom() && KHelper.isBearishCandle(last))		
			{
				supportMsg ="(D+0) "+GeneralHelper.toPct(nearLowPointPct);
			}else  if(last3.getC() > minInTwoMaxPoint.getL() && last2.getC() <= minInTwoMaxPoint.getBodyBottom() && last2.getL() < minInTwoMaxPoint.getL() 
					&& last.getL() < minInTwoMaxPoint.getL() && last.getC() < minInTwoMaxPoint.getBodyBottom() && KHelper.isBearishCandle(last) )
			{
				supportMsg ="(D+1) "+GeneralHelper.toPct(nearLowPointPct);
			}else  if(last3.getC() <= minInTwoMaxPoint.getL() && last2.getC() <= minInTwoMaxPoint.getBodyBottom() && last.getC() < minInTwoMaxPoint.getBodyBottom() )		{
				supportMsg ="(>D+2) "+GeneralHelper.toPct(nearLowPointPct);
			}
			
//			PvrStockBean pvrbean = pvr.findPriceVolRelationship(dateRangeList, ANALYSIS_VOL_PRICE_DAYS);
			info = new MPatternInfo(M_HL);
			
			if(breakDetail[0].isEmpty()==false && breakDetail[1].equalsIgnoreCase("0")==false)
				supportMsg = supportMsg +" 隨後有"+breakDetail[1]+"天跌穿";
			
			if(isCandidateBreak)
				supportMsg = " 相差"+GeneralHelper.toPct(nearLowPointPct);
			
			info.setSupportMessage(supportMsg);
			info.setBreakingDate(breakDetail[0]);
			info.setMtdChangePct(AnalyGeneralHelper.getMTDPct(dateRangeList, KType.END2END_O2C));
			info.setDailyChangePct(AnalyGeneralHelper.getDailyChangePct(dateRangeList));
			info.setThreeDaysChangePct(AnalyGeneralHelper.getThreeDaysPct(dateRangeList, KType.END2END_O2C));
			
			info.setCurrentStockBean(last);
			info.setLeftHStockBean(h1StockBean);
			info.setRightHStockBean(h2StockBean);
			info.setCriticalPointBtwn(minInTwoMaxPoint);
			
			int calendarDayBtwn = DateHelper.dayBetween(h1StockBean.getTxnDate(), h2StockBean.getTxnDate());
			info.setCalendarDayBetweenCriticalPoint(calendarDayBtwn);
			
			//(15-20)/20
			double topDownRatio = (minInTwoMaxPoint.getL() - h1StockBean.getH()) / h1StockBean.getH();
			double topDownDistance = h1StockBean.getH() - minInTwoMaxPoint.getL();
			
			double maxDrawdownRatio = (minAfter2ndMax.getL() - minInTwoMaxPoint.getL())/minInTwoMaxPoint.getL();
			double currPriceToHighPointBtwnRatio = (last.getC() - minInTwoMaxPoint.getL())/minInTwoMaxPoint.getL();
			
			info.setMaxDrawdownRatio(maxDrawdownRatio);
			info.setCurrPriceToHighPointBtwnRatio(currPriceToHighPointBtwnRatio);
			info.setTopDownDistance(topDownDistance);
			info.setTopDownDistanceRatio(topDownRatio);
//			info.setPriceVolRelationship(pvrbean);
		}
		
		return info;
	}

	
	public MPatternInfo doMPatternLH(List<StockBean> dateRangeList) {
		
		//h1StockBean : the highest stock
		//h2StockBean : the 2nd highest stock
		
		boolean isMLH = helper.isMlh(dateRangeList);
		if(isMLH==false)
			return null;
		
		StockBean last = dateRangeList.get(dateRangeList.size() - 1);
		StockBean last2 = dateRangeList.get(dateRangeList.size() - 2);
		StockBean last3 = dateRangeList.get(dateRangeList.size() - 3);
		
		int maxIdx = helper.maxIndex(dateRangeList);
		List<StockBean> subList = dateRangeList.subList(maxIdx, dateRangeList.size());

		StockBean h1StockBean = subList.get(0);

		StockBean firstLowPointLeftSide = this.helper.findBackwardHighToLow(dateRangeList, maxIdx);
		if(firstLowPointLeftSide== null || firstLowPointLeftSide.getTxnDateInt()==h1StockBean.getTxnDateInt())
			return null;
		
		
		int firstLowPointLeftSideIdx = this.helper.findIndex(dateRangeList, firstLowPointLeftSide.getTxnDate());
		
		StockBean h2StockBean = this.helper.findBackwardLowToHigh(dateRangeList, firstLowPointLeftSideIdx);

//		int diffFirstMax_Last = DateHelper.dayBetween(firstMax.getTxnDate(), last.getTxnDate());
		
		int diffFirstMax_Last = DateHelper.countBusinessDaysBetween(h1StockBean.getTxnDate(), last.getTxnDate());
		if(diffFirstMax_Last > DAYS_TO_CURRENT_MAX_DAY || diffFirstMax_Last < DAYS_TO_CURRENT_MIN_DAY)
			return null;

//		int left2ndMaxHighIdx = this.helper.findIndex(dateRangeList, left2ndMaxHighStockBean.getTxnDate());
		
		List<StockBean> secondMaxToEndList = StreamTransformHelper.extractData(dateRangeList, h1StockBean.getTxnDate(), last.getTxnDate());
		StockBean minAfter2ndMax = secondMaxToEndList.stream().min(Comparator.comparing(StockBean::getL))
				.orElseThrow(NoSuchElementException::new);


		List<StockBean> twoMaxBtwnList = StreamTransformHelper.extractData(dateRangeList, h2StockBean.getTxnDate(), h1StockBean.getTxnDate());
		StockBean lowestBetwnTwoHStockBean = twoMaxBtwnList.stream().min(Comparator.comparing(StockBean::getL))
				.orElseThrow(NoSuchElementException::new);
		
		boolean isMatch = true;
		int diffMax = twoMaxBtwnList.size();// - left2ndMaxHighIdx;//
		if (diffMax < DAYS_BTWN_2POINT_MIN || diffMax > DAYS_BTWN_2POINT_MAX)
			isMatch = false;

		
		
		List<StockBean> subTargetList = StreamTransformHelper.extractData(dateRangeList, h1StockBean.getTxnDate(), last.getTxnDate());
		
		String[] breakDetail = this.helper.getBreakDown(lowestBetwnTwoHStockBean, subTargetList);
		if(breakDetail[0].isEmpty()==true)
			isMatch = false;

		
		if(last.getC() > h2StockBean.getBodyTop() ||  last.getC() > h1StockBean.getBodyTop())
			isMatch = false;
		
		double nearLowPointPct = (last.getC() - lowestBetwnTwoHStockBean.getL()) / lowestBetwnTwoHStockBean.getL(); 
		
//		if(nearLowPointRatio > 0.02)
//			isMatch = false;
		
		boolean isCandidateBreak = false;
		if(breakDetail[0].isEmpty()==true && KHelper.isBearishCandle(last) && nearLowPointPct <= 0.05) {
			isMatch = true;
			isCandidateBreak = true;
		}
		
		//Ignore 反彈到頸線上的
		if(breakDetail[0].isEmpty()==false) {
			if( last.getC() >lowestBetwnTwoHStockBean.getBodyTop())
				isMatch =false;
		}
		
		MPatternInfo info = null;
		if(isMatch == true || isCandidateBreak == true) {
//			double fallPct = (last.getC()-minInTwoMaxPoint.getL())/minInTwoMaxPoint.getL();

			String supportMsg = "";
			if(last3.getC() > lowestBetwnTwoHStockBean.getL() && last2.getC() > lowestBetwnTwoHStockBean.getL() 
					&& last.getL() < lowestBetwnTwoHStockBean.getL() && last.getC() < lowestBetwnTwoHStockBean.getBodyBottom() && KHelper.isBearishCandle(last) )		
			{
				supportMsg ="(D+0) "+GeneralHelper.toPct(nearLowPointPct);
			}else  if(last3.getC() > lowestBetwnTwoHStockBean.getL() && last2.getC() <= lowestBetwnTwoHStockBean.getBodyBottom() && last2.getL() < lowestBetwnTwoHStockBean.getL()
					&& last.getL() < lowestBetwnTwoHStockBean.getL() && last.getC() < lowestBetwnTwoHStockBean.getBodyBottom() && KHelper.isBearishCandle(last) )
			{
				supportMsg ="(D+1) "+GeneralHelper.toPct(nearLowPointPct);
			}else  if(last3.getC() <= lowestBetwnTwoHStockBean.getL() && last2.getC() <= lowestBetwnTwoHStockBean.getBodyBottom() && last.getC() < lowestBetwnTwoHStockBean.getBodyBottom() )		{
				supportMsg ="(>D+2) "+GeneralHelper.toPct(nearLowPointPct);
			}
			

			
//			PvrStockBean pvrbean = pvr.findPriceVolRelationship(dateRangeList, ANALYSIS_VOL_PRICE_DAYS);
			
			info = new MPatternInfo(M_LH);
			
			if(breakDetail[0].isEmpty()==false && breakDetail[1].equalsIgnoreCase("0")==false)
				supportMsg = supportMsg +" 隨後有"+breakDetail[1]+"天跌穿";
			
			if(isCandidateBreak)
				supportMsg = " 相差"+GeneralHelper.toPct(nearLowPointPct);
			
			info.setSupportMessage(supportMsg);
			info.setBreakingDate(breakDetail[0]);
			info.setCurrentStockBean(last);
			info.setLeftHStockBean(h2StockBean);
			info.setRightHStockBean(h1StockBean);
			info.setCriticalPointBtwn(lowestBetwnTwoHStockBean);
			
			info.setMtdChangePct(AnalyGeneralHelper.getMTDPct(dateRangeList, KType.END2END_O2C));
			info.setDailyChangePct(AnalyGeneralHelper.getDailyChangePct(dateRangeList));
			info.setThreeDaysChangePct(AnalyGeneralHelper.getThreeDaysPct(dateRangeList, KType.END2END_O2C));
			
			int calendarDayBtwn = DateHelper.dayBetween(h2StockBean.getTxnDate(), h1StockBean.getTxnDate());
			info.setCalendarDayBetweenCriticalPoint(calendarDayBtwn);
			
			//(15-20)/20
			double topDownRatio = (lowestBetwnTwoHStockBean.getL() - h1StockBean.getH()) / h1StockBean.getH();
			double topDownDistance = h1StockBean.getH() - lowestBetwnTwoHStockBean.getL();
			
			double maxDrawdownRatio = (minAfter2ndMax.getL() - lowestBetwnTwoHStockBean.getL())/lowestBetwnTwoHStockBean.getL();
			double currPriceToHighPointBtwnRatio = (last.getC() - lowestBetwnTwoHStockBean.getL())/lowestBetwnTwoHStockBean.getL();
			info.setMaxDrawdownRatio(maxDrawdownRatio);
			info.setCurrPriceToHighPointBtwnRatio(currPriceToHighPointBtwnRatio);
			info.setTopDownDistance(topDownDistance);
			info.setTopDownDistanceRatio(topDownRatio);
//			info.setPriceVolRelationship(pvrbean);
			
		}
		
		return info;
	}
	

	public WPatternInfo doWPatternLH(List<StockBean> dateRangeList) {

		int minIdx = helper.minIndex(dateRangeList);

		List<StockBean> subList = dateRangeList.subList(minIdx, dateRangeList.size());
		StockBean l1StockBean = subList.get(0);
		StockBean firstHighPoint = this.helper.findForwardLowToHigh(subList);

//		log.debug("firstHighPoint: " + firstHighPoint);

		if(firstHighPoint== null || firstHighPoint.getTxnDateInt()==l1StockBean.getTxnDateInt())
			return null;


		StockBean last = dateRangeList.get(dateRangeList.size() - 1);
		StockBean last2 = dateRangeList.get(dateRangeList.size() - 2);
		StockBean last3 = dateRangeList.get(dateRangeList.size() - 3);
		
		List<StockBean> subList2 = StreamTransformHelper.extractData(dateRangeList, firstHighPoint.getTxnDate(), last.getTxnDate());


		int min2ndIdx = helper.minIndex(subList2);

		StockBean l2StockBean = subList2.get(min2ndIdx);

		List<StockBean> rightMinToEndList = StreamTransformHelper.extractData(dateRangeList, l2StockBean.getTxnDate(), last.getTxnDate());
		
		if(rightMinToEndList.size() > DAYS_TO_CURRENT_MAX_DAY || rightMinToEndList.size() < DAYS_TO_CURRENT_MIN_DAY)
			return null;
		
		StockBean maxAfterRightMin = rightMinToEndList.stream().max(Comparator.comparing(StockBean::getH))
				.orElseThrow(NoSuchElementException::new);

		List<StockBean> wPatternBtwnList = StreamTransformHelper.extractData(dateRangeList, l1StockBean.getTxnDate(), l2StockBean.getTxnDate());
		StockBean maxInWPatternPoint = wPatternBtwnList.stream().max(Comparator.comparing(StockBean::getH)).orElseThrow(NoSuchElementException::new);

		boolean isMatch = true;
		
		
		List<StockBean> subTargetList = StreamTransformHelper.extractData(dateRangeList, l2StockBean.getTxnDate(), last.getTxnDate());
		
		String[] breakDetail = this.helper.getBreakUp(maxInWPatternPoint, subTargetList);
		if(breakDetail[0].isEmpty()==true)
			isMatch = false;
		

		int diffMin = wPatternBtwnList.size();//

		if (diffMin < DAYS_BTWN_2POINT_MIN || diffMin > DAYS_BTWN_2POINT_MAX)
			isMatch = false;

		if(last.getC()<l1StockBean.getBodyBottom() || last.getC() < l2StockBean.getBodyBottom())
			isMatch = false;
		
		double upPct = (last.getC()-maxInWPatternPoint.getH())/maxInWPatternPoint.getH();
		
//		String status = wtHelper.calcReboundRatio(maxInWPatternPoint.getH(), l2StockBean.getL(), last.getC());
		
		double r05 = (maxInWPatternPoint.getH() - l2StockBean.getL())* 0.5 + l2StockBean.getL();
		
		boolean isCandidateBreak = false;
		if(breakDetail[0].isEmpty()==true && KHelper.isBullishCandle(last) && last.getC() >r05) {
			isMatch = true;
			isCandidateBreak = true;
		}
		
		WPatternInfo info = null;
		if(isMatch == true || isCandidateBreak == true) {
			
			String supportMsg = "";
			if(last3.getC() < maxInWPatternPoint.getH() && last2.getC() < maxInWPatternPoint.getH() && last.getC() > maxInWPatternPoint.getBodyTop() && KHelper.isBullishCandle(last))		{
				supportMsg ="(D+0) "+GeneralHelper.toPct(upPct);
			}else  if(last3.getC() < maxInWPatternPoint.getH() && last2.getC() >= maxInWPatternPoint.getBodyTop() && last.getC() > maxInWPatternPoint.getBodyTop() && KHelper.isBullishCandle(last))		{
				supportMsg ="(D+1) "+GeneralHelper.toPct(upPct);
			}else  if(last3.getC() >= maxInWPatternPoint.getBodyTop() && last2.getC() >= maxInWPatternPoint.getBodyTop() && last.getC() > maxInWPatternPoint.getBodyTop() && KHelper.isBullishCandle(last))		{
				supportMsg ="(>D+2) "+GeneralHelper.toPct(upPct);
			}
			
//			PvrStockBean pvrbean = pvr.findPriceVolRelationship(dateRangeList, ANALYSIS_VOL_PRICE_DAYS);
			if(breakDetail[0].isEmpty()==false && breakDetail[1].equalsIgnoreCase("0")==false)
				supportMsg = supportMsg +" 隨後有"+breakDetail[1]+"天升穿";
			
			if(breakDetail[0].isEmpty()==false && breakDetail[1].equalsIgnoreCase("0")==true)
				supportMsg = " 未升穿 "+GeneralHelper.toPct(upPct);
			
			if(isCandidateBreak)
				supportMsg = " 相差"+GeneralHelper.toPct(upPct);
			
			info = new WPatternInfo(W_LH);
			info.setSupportMessage(supportMsg);
			info.setBreakingDate(breakDetail[0]);
			info.setCurrentStockBean(last);
			info.setLeftLStockBean(l1StockBean);
			info.setRightLStockBean(l2StockBean);
			info.setCriticalPointBtwn(maxInWPatternPoint);
			
			info.setMtdChangePct(AnalyGeneralHelper.getMTDPct(dateRangeList, KType.END2END_O2C));
			info.setDailyChangePct(AnalyGeneralHelper.getDailyChangePct(dateRangeList));
			info.setThreeDaysChangePct(AnalyGeneralHelper.getThreeDaysPct(dateRangeList, KType.END2END_O2C));
			
			int calendarDayBtwn = DateHelper.dayBetween(l1StockBean.getTxnDate(), l2StockBean.getTxnDate());
			info.setCalendarDayBetweenCriticalPoint(calendarDayBtwn);
			
			//(15-20)/20
			double downTopRatio = (maxInWPatternPoint.getH() - l1StockBean.getL()) / l1StockBean.getL();
			double downTopDistance = maxInWPatternPoint.getH() - l1StockBean.getL();
			
			double maxUpBreakRatio = (maxAfterRightMin.getH() - maxInWPatternPoint.getH())/maxInWPatternPoint.getH();
			double currPriceToHighPointBtwnRatio = (last.getC() - maxInWPatternPoint.getH())/maxInWPatternPoint.getH();
			
			info.setMaxUpBreakRatio(maxUpBreakRatio);
			info.setCurrPriceToHighPointBtwnRatio(currPriceToHighPointBtwnRatio);
			info.setDownTopDistance(downTopDistance);
			info.setDownTopDistanceRatio(downTopRatio);
//			info.setPriceVolRelationship(pvrbean);
			
		}
		return info;

	}

	public WPatternInfo doWPatternHL(List<StockBean> dateRangeList) {

		int minIdx = helper.minIndex(dateRangeList);

		List<StockBean> subList = dateRangeList.subList(minIdx, dateRangeList.size());
		StockBean l1StockBean = subList.get(0);
		StockBean firstHighPoint = this.helper.findForwardLowToHigh(subList);

		StockBean leftHighPoint = this.helper.findBackwardLowToHigh(dateRangeList, minIdx);
		List<StockBean> leftMinList = StreamTransformHelper.extractData(dateRangeList, dateRangeList.get(0).getTxnDate(), leftHighPoint.getTxnDate());
		StockBean l2StockBean = leftMinList.stream().min(Comparator.comparing(StockBean::getL)).orElseThrow(NoSuchElementException::new);

		if(firstHighPoint== null || firstHighPoint.getTxnDateInt()==l1StockBean.getTxnDateInt())
			return null;

		StockBean last = dateRangeList.get(dateRangeList.size() - 1);
		StockBean last2 = dateRangeList.get(dateRangeList.size() - 2);
		StockBean last3 = dateRangeList.get(dateRangeList.size() - 3);

		List<StockBean> leftRightMinBtwnList = StreamTransformHelper.extractData(dateRangeList, l2StockBean.getTxnDate(), l1StockBean.getTxnDate());
		
		int diff2ndMin_Last = DateHelper.countBusinessDaysBetween(l1StockBean.getTxnDate(), last.getTxnDate());

		if(diff2ndMin_Last > DAYS_TO_CURRENT_MAX_DAY || diff2ndMin_Last < DAYS_TO_CURRENT_MIN_DAY)
			return null;
		
		StockBean maxInWPatternPoint = leftRightMinBtwnList.stream().max(Comparator.comparing(StockBean::getH)).orElseThrow(NoSuchElementException::new);

		List<StockBean> rightMinToEndList = StreamTransformHelper.extractData(dateRangeList, l1StockBean.getTxnDate(), last.getTxnDate());

		StockBean maxAfterRightMin = rightMinToEndList.stream().max(Comparator.comparing(StockBean::getH))
				.orElseThrow(NoSuchElementException::new);

		boolean isMatch = true;

		
		List<StockBean> subTargetList = StreamTransformHelper.extractData(dateRangeList, l1StockBean.getTxnDate(), last.getTxnDate());
		
		String[] breakDetail = this.helper.getBreakUp(maxInWPatternPoint, subTargetList);
		if(breakDetail[0].isEmpty()==true)
			isMatch = false;
		
		int diffMin = leftRightMinBtwnList.size();//

		if (diffMin < DAYS_BTWN_2POINT_MIN || diffMin > DAYS_BTWN_2POINT_MAX)
			isMatch = false;


		if(last.getC() < l2StockBean.getBodyBottom() || last.getC() < l1StockBean.getBodyTop())
			isMatch = false;
		double upPct = (last.getC()-maxInWPatternPoint.getH())/maxInWPatternPoint.getH();

		
		double r05 = (maxInWPatternPoint.getH() - l2StockBean.getL())* 0.5 + l2StockBean.getL();

		boolean isCandidateBreak = false;
		if(breakDetail[0].isEmpty()==true && KHelper.isBearishCandle(last) && last.getC() > r05) {
			isMatch = true;
			isCandidateBreak = true;
		}
		
		WPatternInfo info = null;
		if(isMatch == true || isCandidateBreak == true) {

			String supportMsg = "";
			if(last3.getC() < maxInWPatternPoint.getH() && last2.getC() < maxInWPatternPoint.getH() && last.getC() > maxInWPatternPoint.getBodyTop() && KHelper.isBullishCandle(last))		{
				supportMsg ="(D+0) "+GeneralHelper.toPct(upPct);
			}else  if(last3.getC() < maxInWPatternPoint.getH() && last2.getC() >= maxInWPatternPoint.getBodyTop() && last.getC() > maxInWPatternPoint.getBodyTop() && KHelper.isBullishCandle(last))		{
				supportMsg ="(D+1) "+GeneralHelper.toPct(upPct);
			}else  if(last3.getC() >= maxInWPatternPoint.getBodyTop() && last2.getC() >= maxInWPatternPoint.getBodyTop() && last.getC() > maxInWPatternPoint.getBodyTop() && KHelper.isBullishCandle(last))		{
				supportMsg ="(>D+2) "+GeneralHelper.toPct(upPct);
			}
			
//			PvrStockBean pvrbean = pvr.findPriceVolRelationship(dateRangeList, ANALYSIS_VOL_PRICE_DAYS);
			if(breakDetail[0].isEmpty()==false && breakDetail[1].equalsIgnoreCase("0")==false)
				supportMsg = supportMsg +" 隨後有"+breakDetail[1]+"天升穿";
			
			if(breakDetail[0].isEmpty()==false && breakDetail[1].equalsIgnoreCase("0")==true)
				supportMsg = " 未升穿 "+GeneralHelper.toPct(upPct);
			
			if(isCandidateBreak)
				supportMsg = " 相差"+GeneralHelper.toPct(upPct);
			
			info = new WPatternInfo(W_HL);
			info.setSupportMessage(supportMsg);
			info.setBreakingDate(breakDetail[0]);
			info.setCurrentStockBean(last);
			info.setLeftLStockBean(l2StockBean);
			info.setRightLStockBean(l1StockBean);
			info.setCriticalPointBtwn(maxInWPatternPoint);
			
			info.setMtdChangePct(AnalyGeneralHelper.getMTDPct(dateRangeList, KType.END2END_O2C));
			info.setDailyChangePct(AnalyGeneralHelper.getDailyChangePct(dateRangeList));
			info.setThreeDaysChangePct(AnalyGeneralHelper.getThreeDaysPct(dateRangeList, KType.END2END_O2C));
			
			int calendarDayBtwn = DateHelper.dayBetween(l2StockBean.getTxnDate(), l1StockBean.getTxnDate());
			info.setCalendarDayBetweenCriticalPoint(calendarDayBtwn);
			
			//(15-20)/20
			double downTopRatio = (maxInWPatternPoint.getH() - l1StockBean.getL()) / l1StockBean.getL();
			double downTopDistance = maxInWPatternPoint.getH() - l1StockBean.getL();
			
			double maxUpBreakRatio = (maxAfterRightMin.getH() - maxInWPatternPoint.getH())/maxInWPatternPoint.getH();
			
			info.setMaxUpBreakRatio(maxUpBreakRatio);
			info.setDownTopDistance(downTopDistance);
			info.setDownTopDistanceRatio(downTopRatio);
//			info.setPriceVolRelationship(pvrbean);
			
		}
		return info;

	}

	
	public DownBreakAndStandUpBean findFallAndStandPattern(List<StockBean> orgstockList)  {
		int NUM_OF_DAYS = 60;
		if(NUM_OF_DAYS>=orgstockList.size()) {
			NUM_OF_DAYS = orgstockList.size();
		}
		List<StockBean> stockList = orgstockList.subList(orgstockList.size()-NUM_OF_DAYS, orgstockList.size());
		StockBean lowest = stockList.stream()
			      .min(Comparator.comparing(StockBean::getL))
			      .orElseThrow(NoSuchElementException::new);
		StockBean highest = stockList.stream()
			      .max(Comparator.comparing(StockBean::getH))
			      .orElseThrow(NoSuchElementException::new);
		try {
			WaveHelper waveHelper = new WaveHelper();
			int idxHighest = StreamTransformHelper.findIndex(stockList, highest.getTxnDate());						
			int idxLowest = StreamTransformHelper.findIndex(stockList, lowest.getTxnDate());
			
			
			//Left Side
			StockBean waveTop = waveHelper.findBackwardLowToHigh(stockList, idxLowest);
			
			int idxWaveTop = StreamTransformHelper.findIndex(stockList, waveTop.getTxnDate());
			
			if(idxHighest > idxWaveTop)
				return null;
			

			DownBreakAndStandUpBean returnBean = null;
			
			StockBean waveTopLeftLowBean =waveHelper.findBackwardHighToLow(stockList, idxWaveTop);
			
			//Right side
			StockBean waveLowest2TopRight = waveHelper.findForwardLowToHigh(stockList, idxLowest);
			if(waveLowest2TopRight!=null) {
				int idxWaveLowest2TopRight = StreamTransformHelper.findIndex(stockList, waveLowest2TopRight.getTxnDate());
				List<StockBean> lowest2RightSideStockList = stockList.subList(idxWaveLowest2TopRight, stockList.size());
				
				StockBean lowest2RightSideLowStockBean = lowest2RightSideStockList.stream()
					      .min(Comparator.comparing(StockBean::getL))
					      .orElseThrow(NoSuchElementException::new);
				
				if(waveTopLeftLowBean.getL()<=lowest2RightSideLowStockBean.getL()) {
					double diff = (lowest.getL() - waveTopLeftLowBean.getL())/waveTopLeftLowBean.getL();
					if(diff > -0.03) {
						returnBean = new DownBreakAndStandUpBean(lowest, waveTopLeftLowBean, lowest2RightSideLowStockBean, false);		
					}else {
						returnBean = new DownBreakAndStandUpBean(lowest, waveTopLeftLowBean, lowest2RightSideLowStockBean, true);
					}
						
				}
			}
			
			return returnBean;
		}catch(Exception e) {
			log.warn("Return null, "+lowest.getStockCode() +" "+ e.getMessage());
			return null;
		}
	}
	

}
