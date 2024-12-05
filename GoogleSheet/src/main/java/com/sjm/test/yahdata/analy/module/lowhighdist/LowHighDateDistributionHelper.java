package com.sjm.test.yahdata.analy.module.lowhighdist;

import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.maas.util.DateHelper;
import com.sjm.test.yahdata.analy.analystmodel.DateDistributionBean;
import com.sjm.test.yahdata.analy.conts.Const;
import com.sjm.test.yahdata.analy.main.MonthlyAnalysisReportApp;
import com.sjm.test.yahdata.analy.module.lowhighdist.bean.BaseLowHighDateDistrBean;
import com.sjm.test.yahdata.analy.ta.helper.SectorAnalystHelper;

import lombok.extern.slf4j.Slf4j;
@Slf4j
public class LowHighDateDistributionHelper {

	public LowHighDateDistributionHelper() {
		
	}

	public static BaseLowHighDateDistrBean exploreDateDistribution(String stockCode, List<String> periodDateList , String startDateMMDDStr, String endDateMMDDStr) {				
		String partOneDate = "";
		String partTwoDate = "";
		String partThreeDate = "";
				
		
		int numOfParts = 4;
		Map<Integer, DateDistributionBean> map = new HashMap<Integer, DateDistributionBean>(numOfParts);
		
		map.put(1, new DateDistributionBean());
		map.put(2, new DateDistributionBean());
		map.put(3, new DateDistributionBean());
		map.put(4, new DateDistributionBean());
		
		
		for (String testDateStr : periodDateList) {
			
			String yyyy= testDateStr.substring(0, 4); // result ==> "2021"
			String startDateStr= yyyy+"-"+startDateMMDDStr;
			String endDateStr= yyyy+"-"+endDateMMDDStr;
			int numOfDay = 0;
			try {
				numOfDay = DateHelper.dayBetween(startDateStr, endDateStr);
			}catch(DateTimeParseException e) {
				log.warn(stockCode + ": "+ e.getMessage());
				continue;
			}
			int d = numOfDay / numOfParts;
						
			partOneDate = DateHelper.plusDays(startDateStr, d, DateHelper.YYYY_MM_DD);		
			partTwoDate = DateHelper.plusDays(partOneDate, d, DateHelper.YYYY_MM_DD);
			partThreeDate = DateHelper.plusDays(partTwoDate, d, DateHelper.YYYY_MM_DD);
		
			boolean isBwnEarly = DateHelper.isBetweenEndDateExclusive(startDateStr, partOneDate, testDateStr);
			if(isBwnEarly) {
				
				map.get(1).getDateList().add(testDateStr.substring(5));
				continue;
			}
			
			boolean isBwnMid1 = DateHelper.isBetweenEndDateExclusive(partOneDate, partTwoDate, testDateStr);
			if(isBwnMid1) {
				
				map.get(2).getDateList().add(testDateStr.substring(5));
				continue;
			}
			
			boolean isBwnMid2 = DateHelper.isBetweenEndDateExclusive(partTwoDate, partThreeDate, testDateStr);
			if(isBwnMid2) {
				
				map.get(3).getDateList().add(testDateStr.substring(5));
				continue;
			}
			
			boolean isBwnEnd = DateHelper.isBetween(partThreeDate, endDateStr, testDateStr);
			if(isBwnEnd) {
				
				map.get(4).getDateList().add(testDateStr.substring(5));
				continue;
			}
		}
		

		int total = map.get(1).getDateList().size() + map.get(2).getDateList().size() +map.get(3).getDateList().size() +map.get(4).getDateList().size();
		
		double timeFrame1Ratio = (double)map.get(1).getDateList().size() / (double)total;
		double timeFrame2Ratio = (double)map.get(2).getDateList().size() / (double)total;
		double timeFrame3Ratio = (double)map.get(3).getDateList().size() / (double)total;
		double timeFrame4Ratio = (double)map.get(4).getDateList().size() / (double)total;
	
		List<String> belongEtfs = SectorAnalystHelper.getETFBelongsTo(stockCode);
		
		BaseLowHighDateDistrBean resultSumy = new BaseLowHighDateDistrBean();
		try {
		resultSumy.setStockCode(stockCode);
		resultSumy.setYears(periodDateList.size());
		if(belongEtfs.isEmpty())
			resultSumy.setBelongETF(Const.NA);
		else
			resultSumy.setBelongETF(belongEtfs.toString().substring(1,belongEtfs.toString().length()-1));
		
		
		resultSumy.setOccursTimeSlot1(timeFrame1Ratio);
		resultSumy.setOccursTimeSlot2(timeFrame2Ratio);
		resultSumy.setOccursTimeSlot3(timeFrame3Ratio);
		resultSumy.setOccursTimeSlot4(timeFrame4Ratio);
		resultSumy.setTimeSlot1(startDateMMDDStr+" - "+partOneDate.substring(5));
		resultSumy.setTimeSlot2(partOneDate.substring(5)+" - "+partTwoDate.substring(5));
		resultSumy.setTimeSlot3(partTwoDate.substring(5)+" - "+partThreeDate.substring(5));
		resultSumy.setTimeSlot4(partThreeDate.substring(5) + " - "+endDateMMDDStr);
		resultSumy.setTimeSlotDateList1(map.get(1).getDateList().parallelStream().sorted().collect(Collectors.toList()));
		resultSumy.setTimeSlotDateList2(map.get(2).getDateList().parallelStream().sorted().collect(Collectors.toList()));
		resultSumy.setTimeSlotDateList3(map.get(3).getDateList().parallelStream().sorted().collect(Collectors.toList()));
		resultSumy.setTimeSlotDateList4(map.get(4).getDateList().parallelStream().sorted().collect(Collectors.toList()));
		}catch(Exception e) {
			log.warn(stockCode+" "+e.getMessage());

		}
		return resultSumy;
	}
}
