package com.sjm.test.yahdata.analy.module.lowhighdist;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import com.maas.util.DateHelper;
import com.sjm.test.yahdata.analy.bean.raw.StockBean;
import com.sjm.test.yahdata.analy.module.lowhighdist.bean.BaseLowHighDateDistrBean;
import com.sjm.test.yahdata.analy.module.lowhighdist.bean.LowHighDateProbabResult;
import com.sjm.test.yahdata.analy.module.lowhighdist.bean.LowHighDateRangeBean;
import com.sjm.test.yahdata.analy.module.lowhighdist.bean.LowHighDateSimplifyResult;

public class LowHighDateHelper {
	
	public LowHighDateHelper() {
		// TODO Auto-generated constructor stub
	}
	
	public static LowHighDateSimplifyResult generate(List<StockBean> productStockList, String MMDD_START, String MMDD_END){
		LowHighDateDistributionModule sdr = new LowHighDateDistributionModule();
		
				 
				if(productStockList.isEmpty())
					System.out.println(LowHighDateHelper.class + "\tpause here");


				String productCode = productStockList.get(0).getStockCode();
				List<LowHighDateRangeBean> lowHighList = sdr.generateLowHighDateRangeList(productStockList, MMDD_START, MMDD_END);
				if(lowHighList.isEmpty())
					return null;
				
				List<String> periodLowDateList = lowHighList.parallelStream()
						.map(LowHighDateRangeBean::getPeriodLowestPriceDate).collect(Collectors.toList());
				List<String> periodHighDateList = lowHighList.parallelStream()
						.map(LowHighDateRangeBean::getPeriodHighestPriceDate).collect(Collectors.toList());
				

				BaseLowHighDateDistrBean resultSummaryL = LowHighDateDistributionHelper.exploreDateDistribution(productCode, periodLowDateList, MMDD_START, MMDD_END );

				BaseLowHighDateDistrBean resultSummaryH = LowHighDateDistributionHelper.exploreDateDistribution(productCode, periodHighDateList, MMDD_START, MMDD_END );

				
				LowHighDateProbabResult l = toLowHighDateSimplifyResult(resultSummaryL); 
				LowHighDateProbabResult h = toLowHighDateSimplifyResult(resultSummaryH);
				
				
				LowHighDateSimplifyResult result = new LowHighDateSimplifyResult();
				result.setHighProbabilityHDates(h.getHighProbabilityDates());
				result.setHighProbabilityLDates(l.getHighProbabilityDates());
				result.setProbabilityHDate(h.getProbability());
				result.setProbabilityLDate(l.getProbability());
				
				result.setDayBetweenHighest(h.getDaysBetween());
				result.setDayBetweenLowest(l.getDaysBetween());
				
				result.setStockCode(productCode);
				
				return result;

	}
	
	
	private static LowHighDateProbabResult toLowHighDateSimplifyResult(BaseLowHighDateDistrBean resultSummaryL) {
		List<Double> occursList = Arrays.asList(resultSummaryL.getOccursTimeSlot1(), resultSummaryL.getOccursTimeSlot2(), resultSummaryL.getOccursTimeSlot3(), resultSummaryL.getOccursTimeSlot4());
		Double max = occursList.stream().mapToDouble(x->x).max().orElseThrow(NoSuchElementException::new);

		String actualTimeSlot = "";
		double prob = 0.0;

		String startMMDD = "";
		String endMMDD = "";
		if(resultSummaryL.getOccursTimeSlot1()==max) { 
			startMMDD = resultSummaryL.getTimeSlotDateList1().get(0);
			endMMDD = resultSummaryL.getTimeSlotDateList1().get(resultSummaryL.getTimeSlotDateList1().size()-1);
			
			actualTimeSlot = "["+resultSummaryL.getTimeSlotDateList1().get(0) +"-"+ resultSummaryL.getTimeSlotDateList1().get(resultSummaryL.getTimeSlotDateList1().size()-1)+"]";

			prob = resultSummaryL.getOccursTimeSlot1();
		}else if(resultSummaryL.getOccursTimeSlot2()==max) {
			
			startMMDD = resultSummaryL.getTimeSlotDateList2().get(0);
			endMMDD = resultSummaryL.getTimeSlotDateList2().get(resultSummaryL.getTimeSlotDateList2().size()-1);
			
			actualTimeSlot = "["+resultSummaryL.getTimeSlotDateList2().get(0) +"-"+ resultSummaryL.getTimeSlotDateList2().get(resultSummaryL.getTimeSlotDateList2().size()-1)+"]";

			prob = resultSummaryL.getOccursTimeSlot2();
		}else if(resultSummaryL.getOccursTimeSlot3()==max) {
			startMMDD = resultSummaryL.getTimeSlotDateList3().get(0);
			endMMDD = resultSummaryL.getTimeSlotDateList3().get(resultSummaryL.getTimeSlotDateList3().size()-1);
			
			
			actualTimeSlot = "["+resultSummaryL.getTimeSlotDateList3().get(0) +"-"+ resultSummaryL.getTimeSlotDateList3().get(resultSummaryL.getTimeSlotDateList3().size()-1)+"]";

			prob = resultSummaryL.getOccursTimeSlot3();
		}else if(resultSummaryL.getOccursTimeSlot4()==max) {
			
			startMMDD = resultSummaryL.getTimeSlotDateList4().get(0);
			endMMDD = resultSummaryL.getTimeSlotDateList4().get(resultSummaryL.getTimeSlotDateList4().size()-1);
			
			actualTimeSlot = "["+resultSummaryL.getTimeSlotDateList4().get(0) +"-"+ resultSummaryL.getTimeSlotDateList4().get(resultSummaryL.getTimeSlotDateList4().size()-1)+"]";

			prob = resultSummaryL.getOccursTimeSlot4();
		}
		
		LowHighDateProbabResult result = new LowHighDateProbabResult();

//		int daysBetween = (int)DateRangeHelper.calculateDaysBetween(startMMDD, endMMDD);
		int daysBetween = DateHelper.dayBetween(startMMDD, endMMDD);
		result.setHighProbabilityDates(actualTimeSlot);
		result.setProbability(prob);
		result.setDaysBetween(daysBetween);
		return result;
	}

}
