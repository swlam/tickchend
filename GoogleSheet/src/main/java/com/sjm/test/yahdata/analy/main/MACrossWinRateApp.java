package com.sjm.test.yahdata.analy.main;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.beanutils.BeanUtils;

import com.sjm.test.yahdata.analy.bean.raw.StockBean;
import com.sjm.test.yahdata.analy.conts.Const;
import com.sjm.test.yahdata.analy.conts.ta.RuleConst;
import com.sjm.test.yahdata.analy.ta.rule.ma.crossing.winrate.MACrossUpForWinRateRule;
import com.sjm.test.yahdata.analy.ta.rule.ma.crossing.winrate.MACrossWinRateHelper;
import com.sjm.test.yahdata.analy.ta.rule.ma.crossing.winrate.MACrossWinRateResultSummary;
import com.sjm.test.yahdata.analy.ta.rule.ma.crossing.winrate.MaTagStockBean;
import com.sjm.test.yahdata.analy.ta.rule.ma.crossing.winrate.RuleMACrossingHandler;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MACrossWinRateApp extends BaseApp{
	
	private static final String INTERAL = Const.INTERVAL_D;

	public static int NEXT_FEW_DAYS = 10;
	
	public static String STOCK_CODE = "CSX";
	
	public static final String HEADER = "\tPATTERN\tDays\tHit Ratio\tHit Cnt\tTotal\tLast Hit Date\tLast Miss Date\tHIT\tTo H%\tDays\tTo HC%\tDays\tTo L%\tDays\tTo LC%\tDays\tTo Period End C%\tMISS\tTo H%\tDays\tTo HC%\tDays\tTo L%\tDays\tTo LC%\tDays\tTo Period End C%";
	
	public MACrossWinRateApp() {
	}

	public static void main(String[] args) throws FileNotFoundException, IOException {
		List<StockBean> stockList = loadStockData(Arrays.asList(STOCK_CODE), INTERAL);
		List<MACrossUpForWinRateRule> maCrossUpList = MACrossWinRateHelper.generateMACrossUpRules(MACrossWinRateHelper.MA_LIST);

		MACrossWinRateHelper.getRuleHandler().addAllRules(maCrossUpList);
		
		List<MaTagStockBean> resultList = doBacktest(stockList, MACrossWinRateHelper.getRuleHandler());
		
		boolean isRequiredFixEndDate = true;
		exportWinRateSummaryReport(maCrossUpList, resultList, stockList, isRequiredFixEndDate);
		
		
		isRequiredFixEndDate = false;
		exportWinRateSummaryReport(maCrossUpList, resultList, stockList, isRequiredFixEndDate);
	}
	
	public static List<MACrossWinRateResultSummary> exportWinRateSummaryReport(List<MACrossUpForWinRateRule> maCrossUpList, List<MaTagStockBean> resultList, List<StockBean> stockList, boolean isRequiredFixEndDate) {
		
		List<MACrossWinRateResultSummary> backtestResultBeanList = new ArrayList<MACrossWinRateResultSummary>(50);
		
		for(MACrossUpForWinRateRule rule : maCrossUpList) {
			List<MaTagStockBean> mList = resultList.parallelStream().filter(x -> x.getTagSet().contains(rule.getTag())).collect(Collectors.toList());
			MACrossWinRateResultSummary backtestResultSumy = MACrossWinRateHelper.toMACrossWinRateResultSummary(stockList, mList, rule.getTag(), isRequiredFixEndDate, NEXT_FEW_DAYS);
			
			backtestResultBeanList.add(backtestResultSumy);
		}
		
		String header = "";
		if(isRequiredFixEndDate) {
			header = "\n\tBacktest Stock: "+STOCK_CODE+", Price status ON the "+NEXT_FEW_DAYS+"th trading days";	
		}else {
			header = "\n\tBacktest Stock: "+STOCK_CODE+", Price status WITHIN "+NEXT_FEW_DAYS+" trading days";
		}
				
		header += "\n"+HEADER;
//		log.info(header);
		StringBuilder sb = new StringBuilder();
		sb.append(header);
		
		List<MACrossWinRateResultSummary> sortedResultList = backtestResultBeanList.parallelStream().sorted(Comparator.comparingDouble(MACrossWinRateResultSummary::getHitRatio).reversed()).collect(Collectors.toList());
		for (MACrossWinRateResultSummary backtestResultBean : sortedResultList) {
			sb.append("\n"+backtestResultBean.getMessage());
		}
		log.info(sb.toString());
		return sortedResultList;
	}
	
	
	public static List<MaTagStockBean> doBacktest(List<StockBean> stockBeanList, RuleMACrossingHandler ruleMACrossingHandler) {
		int startIdx = RuleConst.YEAR_NUM_OF_DAYS ;
//		if(stockBeanList.size()<MIN_DATA_SIZE) {
//			log.debug("Return goThruRules() function , due to Hist data size < "+MIN_DATA_SIZE);
//			return null;
//		}
		if(startIdx > stockBeanList.size())
			startIdx = 1;
		
		List<MaTagStockBean> resultList = new ArrayList<MaTagStockBean>(10);
		for (int i = startIdx; i < stockBeanList.size(); i++) {
			
			int mRangeStartId = i - RuleConst.YEAR_NUM_OF_DAYS; //MONTHLY_SAMPLE_SIZE;
			if(mRangeStartId<0)
				mRangeStartId=0;
			
			List<StockBean> prevList = stockBeanList.subList(mRangeStartId, i);
			
			StockBean curr = stockBeanList.get(i);
			
			Set<String> signSet = ruleMACrossingHandler.fireRules(prevList, curr);
			
			if(signSet.isEmpty())
				continue;
			
			MaTagStockBean tmp = new MaTagStockBean(curr.getStockCode());
			tmp.setTagSet(signSet);
			tmp.setChainIdx(i);
			try {
				BeanUtils.copyProperties(tmp ,curr);			
			} catch (Exception e) {
				log.error(null, e);
			} 
			
			resultList.add(tmp);
		}
		
		return resultList;
	}

}
