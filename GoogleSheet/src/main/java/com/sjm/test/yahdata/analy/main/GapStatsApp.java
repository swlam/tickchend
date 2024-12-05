package com.sjm.test.yahdata.analy.main;

import java.util.Arrays;
import java.util.List;


import com.sjm.test.yahdata.analy.bean.VolumePriceBean;
import com.sjm.test.yahdata.analy.bean.raw.StockBean;
import com.sjm.test.yahdata.analy.cfg.GlobalConfig;
import com.sjm.test.yahdata.analy.conts.Const;
import com.sjm.test.yahdata.analy.conts.ta.CandleEventTagEnum;
import com.sjm.test.yahdata.analy.helper.CFGHelper;
import com.sjm.test.yahdata.analy.helper.StreamTransformHelper;
import com.sjm.test.yahdata.analy.ta.EventRuleHandler;
import com.sjm.test.yahdata.backtest.gap.GapInfo;
import com.sjm.test.yahdata.backtest.gap.GapStatsHandler;
import com.sjm.test.yahdata.backtest.gap.ProjectionHandler;

import lombok.extern.slf4j.Slf4j;
@Slf4j
public class GapStatsApp extends BaseApp {

	public static List<String> TICKER_POOL = Arrays.asList("TSM");// USStockListConfig.ETF;
	public static String startDate = "2000-08-20";
	public static String endDate = "2024-02-01";

	public static String gapType = GapStatsHandler.TYPE_GAPUP;

	public GapStatsApp() {
	}

	public static void main(String[] args) {
		String interval = Const.INTERVAL_D;

		List<StockBean> fullTrunkList = loadStockData(TICKER_POOL, interval, startDate, endDate);
		CFGHelper.loadMetaInfo(GlobalConfig.DEFAULT_DEFAULT_PATH);

		EventRuleHandler ruleHandler = new EventRuleHandler();

		GapStatsHandler gapStatsHandler = new GapStatsHandler();
		ProjectionHandler projectionHandler = new ProjectionHandler();
		
		for (String code : TICKER_POOL) {
			List<StockBean> stockList = StreamTransformHelper.extractByStockCode(fullTrunkList, code);
			
			List<CandleEventTagEnum> BACKTEST_PATTERN_ARY = null;
			if(GapStatsHandler.TYPE_GAPDOWN.equalsIgnoreCase(gapType)) {
				BACKTEST_PATTERN_ARY= Arrays.asList(CandleEventTagEnum.EVNT_GAP_DOWN );
			}else if(GapStatsHandler.TYPE_GAPUP.equalsIgnoreCase(gapType)) {
				BACKTEST_PATTERN_ARY= Arrays.asList(CandleEventTagEnum.EVNT_GAP_UP );
			}else {
				log.warn("Incorrect Gap TYP: "+gapType + ". Please input "+GapStatsHandler.TYPE_GAPUP + " or " + GapStatsHandler.TYPE_GAPDOWN);
//				return gapDownInfoResultList;
			}
			

			List<VolumePriceBean> vpStockList = ruleHandler.goThruRules(stockList, BACKTEST_PATTERN_ARY);
			
			List<GapInfo> gapInfoResultList = gapStatsHandler.handle(gapType, stockList, vpStockList);

			gapStatsHandler.exportDetail(gapInfoResultList);
			gapStatsHandler.exportGapResultSummary(code, gapInfoResultList);

			projectionHandler.handle(stockList, vpStockList);
			System.out.println("\n" + code + " From " + startDate + " to " + endDate + ", Total " + gapType + "(filled) : " + gapInfoResultList.size() + ", total GapUp: "+vpStockList.size());
		}
	}

}
