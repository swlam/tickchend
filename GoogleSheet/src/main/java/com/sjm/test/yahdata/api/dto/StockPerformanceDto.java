package com.sjm.test.yahdata.api.dto;

import com.sjm.test.yahdata.analy.bean.Island;
import com.sjm.test.yahdata.analy.bean.PvrStockBean;
import com.sjm.test.yahdata.analy.bean.StrongWeakTypeBean;
import com.sjm.test.yahdata.analy.bean.VolumePriceBean;
import com.sjm.test.yahdata.analy.bean.raw.StockBean;
import com.sjm.test.yahdata.analy.model.DownBreakAndStandUpBean;
import com.sjm.test.yahdata.analy.module.wavepoint.bean.WaveShape;
import com.sjm.test.yahdata.analy.ta.indicator.rsi.DivergenceResult;
import com.sjm.test.yahdata.analy.ta.pattern.BaseMWShapeInfo;
import com.sjm.test.yahdata.analy.ta.rule.ma.situation.bean.PriceMASituationSummaryBean;
import com.sjm.test.yahdata.analy.ta.rule.ma.situation.bean.VolumeMASituationSummaryBean;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Setter
@Getter
public class StockPerformanceDto {

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
//    private String sysPickLongCategory;
//    private String sysPickStagnantCategory;
//    private String sysPickShortCategory;

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
//    private StrongWeakTypeBean strongWeakTypeToday = null;
//    private StrongWeakTypeBean strongWeakTypePrevDay = null;
//    private StrongWeakTypeBean strongWeakTypePrev2Days = null;
//    private StrongWeakTypeBean strongWeakTypePrev3Days = null;
//    private StrongWeakTypeBean strongWeakTypePrev4Days = null;


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


    private double abv5D;
    private double abv10D;
    private double abv20D;
    private double abv50D;
    private double abv100D;
    private double abv200D;

    private String ma219DStatus;
    private String ma50200DStatus;
    private String ma50250DStatus;


    private BaseMWShapeInfo reversalPatterns;

    private DownBreakAndStandUpBean downBreakStandUpBean;

//    private PvrStockBean priceVolStockBean = new PvrStockBean();



//    private StockBean lowestStockBean;
//    private Double lowest2CurrentRatio;

    private String consecutive5days50DStatus = null;
    private String consecutive5days200DStatus = null;

//    private String extremelyLowVolumeDate;
//    private String extremelyLowVolumeLowAndHigh;



//    private VolumePriceBean bullishPattern;
//    private VolumePriceBean bearishPattern;

    private Double stagnantMARatio;
    private Double stagnantPrev1MARatio;
    private Double stagnantPrev2MARatio;
    private Double stagnantPrev3MARatio;

    private Double stagnantShortTermMARatio;
    private Double stagnantMidTermMARatio;

    private PriceMASituationSummaryBean maSituation;
//    private VolumeMASituationSummaryBean volumeMASituation;

    //	private String monthCandlestickStatus;
    private DivergenceResult rsiDiverence;
//	private PriceVolResult pvDiverence;

    //	private String yearHighAchievedThisMonth;
    private String movingAvgLongArrangementWithinTheMonth;
    //	private String upDownBreakThreeWavePointToday;
    private String largeVolumeWithinTheMonth;
    private String movingAverageLongSideSupport;
    private String bbUpBreakForATime;
    private String bigDarkBodyWithMoreVol;

    private String concentratedVolumePriceRange;
    private String closestVolumePriceRange;

//    private String movingAverageCrossUpSummary;
    private String lastEngulfingInRecentDays;

    public StockPerformanceDto(List<StockBean> stockList) {
        this.stockList= stockList;

    }

}
