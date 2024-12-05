package com.sjm.test.yahdata.backtest.gap;

import java.util.ArrayList;
import java.util.List;

import com.maas.util.GeneralHelper;
import com.sjm.test.yahdata.analy.bean.VolumePriceBean;
import com.sjm.test.yahdata.analy.bean.raw.StockBean;
import com.sjm.test.yahdata.analy.helper.StreamTransformHelper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GapStatsHandler {

	public static final String TYPE_GAPUP = "GAPUP";
	public static final String TYPE_GAPDOWN = "GAPDOWN";
	
//	private EventRuleHandler ruleHandler = new EventRuleHandler();
	
	public GapStatsHandler() {
	}

	public List<GapInfo> handle(String gapType, List<StockBean> trunkList, List<VolumePriceBean> vpStockList) {
		List<GapInfo> gapDownInfoResultList = new ArrayList<GapInfo>(0);

		boolean isGapUp = true;
		for (VolumePriceBean vpinfo : vpStockList) {
			int idx = StreamTransformHelper.findIndex(trunkList, vpinfo.getTxnDate());
			List<StockBean> subStockList = trunkList.subList(idx, trunkList.size());
			
			StockBean filterLv1 = null;
			StockBean filterLv2 = null;
			if(TYPE_GAPDOWN.equalsIgnoreCase(gapType)) {
				isGapUp = false;
				filterLv1 = subStockList.stream()
						.filter(x -> vpinfo.getGapBean() != null && (x.getL() > vpinfo.getGapBean().getGapBottom()
								|| x.getH() > vpinfo.getGapBean().getGapBottom()))
						.findFirst().orElse(null);
				 filterLv2 = subStockList.stream().filter(x -> vpinfo.getGapBean() != null
						&& x.getH() >= vpinfo.getGapBean().getGapTop() && x.getL() <= vpinfo.getGapBean().getGapTop()
				).findFirst().orElse(null);

				
				
			}else if(TYPE_GAPUP.equalsIgnoreCase(gapType)) {
				isGapUp = true;
				filterLv1 = subStockList.stream()
						 .filter(x -> vpinfo.getGapBean()!=null 
						 && (x.getH() <  vpinfo.getGapBean().getGapTop() || x.getL() <  vpinfo.getGapBean().getGapTop()))
						 .findFirst().orElse(null);
				 filterLv2 = subStockList.stream()
						 .filter(x -> vpinfo.getGapBean()!=null 
						 && x.getH() >=  vpinfo.getGapBean().getGapBottom() && x.getL() <=vpinfo.getGapBean().getGapBottom())
						 .findFirst().orElse(null);
				 
				 
			}else {
				log.warn("Incorrect Gap TYP: "+gapType + ". Please input "+TYPE_GAPUP + " or " + TYPE_GAPDOWN);
				return gapDownInfoResultList;
			}
			
			int noOfTxnDaysToStartFill = (filterLv1 == null) ? 0 : StreamTransformHelper.txDaysBetween(subStockList, vpinfo.getTxnDate(), filterLv1.getTxnDate());
			int noOfTxnDaysToCompletelyFilled = (filterLv2 == null) ? 0 : StreamTransformHelper.txDaysBetween(subStockList, vpinfo.getTxnDate(), filterLv2.getTxnDate());
			
			GapInfo gapInfo = new GapInfo(vpinfo.getTxnDate());
			gapInfo.setUP(isGapUp);
			gapInfo.setFillStartDate((filterLv1 == null) ? null : filterLv1.getTxnDate());
			gapInfo.setCompletelyFilledDate((filterLv2 == null) ? null : filterLv2.getTxnDate());
			gapInfo.setNoOfTxnDaysToStartFill(noOfTxnDaysToStartFill);
			gapInfo.setNoOfTxnDaysToCompletelyFilled(noOfTxnDaysToCompletelyFilled);
			gapInfo.setCompletelyFilled(filterLv2 != null ? true : false);
			gapInfo.setDayChgPct(vpinfo.getDayChgPct());
			gapDownInfoResultList.add(gapInfo);
		}
		return gapDownInfoResultList;
	}
	
	
	public void exportGapResultSummary(String code, List<GapInfo> gapDownInfoResultList) {
		int[] watchingPeriod = new int[] { 1, 5, 10, 15, 20, 50, 100 };

		List<GapResultSummary> resultGapResultSumaryList = new ArrayList<GapResultSummary>();
		int dayStart = 0;
		int dayEnd = 0;
		for (int i = 0; i < watchingPeriod.length; i++) {
			dayStart = 0;
			dayEnd = watchingPeriod[i];

			if (i == 0) {
				dayStart = 0;
			} else {
				dayStart = watchingPeriod[i - 1];
			}

			int dStart = dayStart;
			int dEnd = dayEnd;

			Long cntTargetCnt = gapDownInfoResultList.stream()
					.filter(x -> x.getNoOfTxnDaysToStartFill() > dStart && x.getNoOfTxnDaysToStartFill() <= dEnd)
					.count();
			Long cntTargetCompletelyFilled = gapDownInfoResultList.stream().filter(
					x -> x.getNoOfTxnDaysToCompletelyFilled() > dStart && x.getNoOfTxnDaysToCompletelyFilled() <= dEnd)
					.count();

			GapResultSummary grs = new GapResultSummary();
			grs.setPeriodDays(dStart + 1 + " - " + dEnd);
			grs.setNoOfStartFilled(cntTargetCnt.intValue());
			grs.setNoOfCompletelyFilled(cntTargetCompletelyFilled.intValue());

			resultGapResultSumaryList.add(grs);
		}
		////
		int lastNoOfDay = 100;

		Long cntTargetCnt = gapDownInfoResultList.stream().filter(x -> x.getNoOfTxnDaysToStartFill() > lastNoOfDay)
				.count();
		Long cntTargetCompletelyFilled = gapDownInfoResultList.stream()
				.filter(x -> x.getNoOfTxnDaysToCompletelyFilled() > lastNoOfDay).count();

		GapResultSummary grs = new GapResultSummary();
		grs.setPeriodDays("Over " + lastNoOfDay);
		grs.setNoOfStartFilled(cntTargetCnt.intValue());
		grs.setNoOfCompletelyFilled(cntTargetCompletelyFilled.intValue());

		resultGapResultSumaryList.add(grs);

		//// print result

		StringBuffer msg = new StringBuffer();
		msg.append(code
				+ " - Fill Days\t No. of Gap to Fill\tProportion \tNo. of Gap Completely Filled\tProportion\n");
		for (GapResultSummary sumy : resultGapResultSumaryList) {
			msg.append(sumy.getPeriodDays());
			msg.append("\t" + sumy.getNoOfStartFilled());
			msg.append("\t" + GeneralHelper
					.toPct((double) sumy.getNoOfStartFilled() / (double) gapDownInfoResultList.size()));
			msg.append("\t" + sumy.getNoOfCompletelyFilled());
			msg.append("\t" + GeneralHelper
					.toPct((double) sumy.getNoOfCompletelyFilled() / (double) gapDownInfoResultList.size()));
			msg.append("\n");
		}

//		System.out.println("\n" + code + " From " + startDate + " to " + endDate + ", Total Gap Down:"
//				+ gapDownInfoResultList.size());
		log.info("\n"+msg);
		

	}

	public void exportDetail(List<GapInfo> gapInfoResultLis) {
		StringBuilder msg = new StringBuilder();
		msg.append("\nGap Date\tPct.\tFill_Start\tComplete_Filled\tDays to Fill\tDays to Fill complete");
		for (GapInfo gapinfo : gapInfoResultLis) {
			msg.append("\n"+gapinfo.getGapDate() + "\t" + GeneralHelper.toPct(gapinfo.getDayChgPct()) + "\t"
					+ gapinfo.getFillStartDate() + "\t" + gapinfo.getCompletelyFilledDate() + "\t"
					+ gapinfo.getNoOfTxnDaysToStartFill() + "\t" + gapinfo.getNoOfTxnDaysToCompletelyFilled());
		}
		
		log.info("\n"+msg);
	}
}
