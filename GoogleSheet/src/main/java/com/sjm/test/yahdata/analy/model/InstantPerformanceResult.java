package com.sjm.test.yahdata.analy.model;

import java.util.List;

import com.sjm.test.yahdata.analy.bean.Island;
import com.sjm.test.yahdata.analy.bean.PvrStockBean;
import com.sjm.test.yahdata.analy.bean.StrongWeakTypeBean;
import com.sjm.test.yahdata.analy.bean.VolumePriceBean;
import com.sjm.test.yahdata.analy.bean.raw.StockBean;
import com.sjm.test.yahdata.analy.module.wavepoint.bean.WaveShape;
import com.sjm.test.yahdata.analy.ta.indicator.rsi.DivergenceResult;
import com.sjm.test.yahdata.analy.ta.pattern.BaseMWShapeInfo;
import com.sjm.test.yahdata.analy.ta.rule.ma.situation.bean.PriceMASituationSummaryBean;
import com.sjm.test.yahdata.analy.ta.rule.ma.situation.bean.VolumeMASituationSummaryBean;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.FieldType;
import org.springframework.data.mongodb.core.mapping.MongoId;

@Accessors(chain = true)
@Document(collection = "stockTargetList")
@Setter
@Getter
public class InstantPerformanceResult {
	@MongoId(FieldType.OBJECT_ID)
	protected String id;
	private List<StockBean> stockList;
	
	private String sector;
	private String industry;
	private String belongETF;
	private String belongSouthbound;
	private String interval;
	
	private StockBean currentStockBean;
	private StockBean prevStockBean;
	private StockBean prev2StockBean;
	
	private double estTradeAmount;
	private String sysPickLongCategory;
	private String sysPickStagnantCategory;
	private String sysPickShortCategory;
	
	private String dailyImportantCandlestickTradingPattern;
	private String dailyCandleStatus;
	private String dailyVolDescription;
	private String prev1DayCandleStatus;
	private String prev1DayVolDescription;
	private String prev2DayVolDescription;
	private String priceStatus;
//	private String weeklyVolSummary;
	private String volumeStatus;
//	private String volumeExtreameShrankDate;
	private double rsi14;
	private String rsi14TrendIn3Days;
	private double rsi9;
	private String rsi9TrendIn5Days;
	private Double dailyChangePct= null;
	private Double dailyChangeHighestPct = null;
	private Double dailyChangeLowestPct = null;
	
	private Double threeDaysChangeO2CPct = null;	
	private Double threeDaysChangeO2HPct = null;
	private Double threeDaysChangeO2LPct = null;
	private Double weeksChangeO2CPct = null;	
	private Double weeksChangeO2PHPct = null;		
	private Double weeksChangeO2PLPct = null;
	
	private Double mtdChangeO2CPct = null;
	private Double mtdChangeO2PHPct = null;
	private Double mtdChangeO2PLPct = null;
	
	private Double mtdPctRange = null;
	
	
	private String mtdPeriodHDate = null;
	private String mtdPeriodLDate = null;
	
//	private Double highest2LowestPct = null;
//	private StrongWeakTypeBean currentStrongType = null;
//	private StrongWeakTypeBean currentWeakType = null;
	private StrongWeakTypeBean strongWeakTypeToday = null;
	private StrongWeakTypeBean strongWeakTypePrevDay = null;
	private StrongWeakTypeBean strongWeakTypePrev2Days = null;
	private StrongWeakTypeBean strongWeakTypePrev3Days = null;
	private StrongWeakTypeBean strongWeakTypePrev4Days = null;
	
//	@Deprecated
//	private WavePointAnalyticalResult waveTopBottomStatus = null;
	
//	private String waveShape = null;
	private WaveShape waveShape = null;

	private String downTreandReversalDate = null;
//	private double volatility5D;
//	private double volatility20D;
	private String todayMonthDaysVolumeStatus;
	private String recentMonthDaysVolumeStatus;
	private String recentQuarterDaysVolumeStatus;
	
//	private Double todayMonthDaysVolumeStatus;
//	private Double recentMonthDaysVolumeStatus;
//	private Double recentQuarterDaysVolumeStatus;
	
//	private String recentMonthDaysPricePctStatus;
	
	private String gapType;
	private String shortTermMAchange;
	private Island island;
//	private String highVolumeDaySituation;
	private String breakPrevMonthStatus;
	private Double ytd = null;
	private Double q1 = null;
	private Double q2 = null;
	private Double q3 = null;
	private Double q4 = null;
	
//	private boolean isAbv2D;
//	private boolean isAbv19D;
	
//	private boolean isAbv5D;
	//private boolean isAbv10D;
	//private boolean isAbv20D;
	//private boolean isAbv50D;
	//private boolean isAbv100D;
	//private boolean isAbv200D;
	private double abv5D;
	private double abv10D;
	private double abv20D;
	private double abv50D;
	private double abv100D;
	private double abv200D;
	
	private String ma219DStatus;
	private String ma50200DStatus;
	private String ma50250DStatus;
//	private MovingAvgCrossResultBean ma50200CrossUpDate;
//	private MovingAvgCrossResultBean ma50250CrossUpDate;
//	private MovingAvgCrossResultBean ma50200CrossDownDate;
	
	private BaseMWShapeInfo reversalPatterns;
	
	private DownBreakAndStandUpBean downBreakStandUpBean;
	
	private PvrStockBean priceVolStockBean = new PvrStockBean();
	
//	private String maxVolDate = null;
//	private String maxVolDateDesc=null;
//	private String isCurrentAboveMaxVolStockBodyTop = null;
//	private String isCurrentBelowMaxVolStockBodyBottom = null;
//	private String isMonoPeakVolStockBean = null;
	
	
//	private String numOfShrinkageVolumeDate = null;
//	private String shrinkageVolumeDateMsg = null;
	
//	private String numOfDoubleVolumeDate = null;
//	private String doubleVolumeDateMsg = null;
	
	private StockBean lowestStockBean;
	private Double lowest2CurrentRatio;
	
	private String consecutive5days50DStatus = null;
	private String consecutive5days200DStatus = null;
	
	private String extremelyLowVolumeDate;
	private String extremelyLowVolumeLowAndHigh;
	
//	private String gapDownStandUpSummary;
//	private String bullishPatternDesc;
//	private String bearishPatternDesc;
	
	private VolumePriceBean bullishPattern;
	private VolumePriceBean bearishPattern;
	
	private Double stagnantMARatio;
	private Double stagnantPrev1MARatio;
	private Double stagnantPrev2MARatio;
	private Double stagnantPrev3MARatio;
	
	private Double stagnantShortTermMARatio;
	private Double stagnantMidTermMARatio;
	
	private PriceMASituationSummaryBean maSituation;
	private VolumeMASituationSummaryBean volumeMASituation;
	
//	private String monthCandlestickStatus;
	private DivergenceResult rsiDiverence;
//	private PriceVolResult pvDiverence;
	
//	private String yearHighAchievedThisMonth;
	private String movingAvgLongArrangementWithinTheMonth;
	private String triplePregnancyInPassFewDays;
//	private String upDownBreakThreeWavePointToday;
	private String largeVolumeWithinTheMonth;
	private String movingAverageLongSideSupport;
	private String bbUpBreakForATime;
	private String bigDarkBodyWithMoreVol;
	
	private String concentratedVolumePriceRange;
	private String closestVolumePriceRange;
	
	private String movingAverageCrossUpSummary;
	private String lastEngulfingInRecentDays;
//	private MACDData macdData;
	
	public InstantPerformanceResult(List<StockBean> stockList) {
		this.stockList= stockList;
	}
	
	public String toString() {
		return this.getCurrentStockBean().toString();
	}

	public String checkRsiStatus() {
		String msg = "";
		double rsi9ObLv1 = 0.7;
		double rsi14ObLv1 = 0.7;
		double rsi9Lv2 = 0.8;
		double rsi14Lv2 = 0.8;
		double rsiLv3 = 0.9;
		
		if( (getCurrentStockBean().getRsi9()>rsi9ObLv1 && getPrevStockBean().getRsi9()>rsi9ObLv1 ) || (getCurrentStockBean().getRsi14()>=rsi14ObLv1 && getPrevStockBean().getRsi14()>=rsi14ObLv1))
			msg = "OB";
		else if( (getCurrentStockBean().getRsi9()>rsi9Lv2 && getPrevStockBean().getRsi9()>rsi9Lv2 ) || (getCurrentStockBean().getRsi14()>=rsi14Lv2 && getPrevStockBean().getRsi14()>=rsi14Lv2))
			msg = "OB2";
		else if( (getCurrentStockBean().getRsi9()>rsiLv3 && getPrevStockBean().getRsi9()>rsiLv3 ) || (getCurrentStockBean().getRsi14()>=rsiLv3 && getPrevStockBean().getRsi14()>=rsiLv3))
			msg = "OB3";
		else
			msg = "";
		return msg;
	}
}
