package com.sjm.test.yahdata.analy.conts.type;

import lombok.Getter;

@Getter
public enum CandlestickTradingPatternType {

    DOJI("十字星"),

//    HARAMI("身懷六甲"),

    OPEN_HIGH_CLOSE_LOW("高開低收"),
    BEARISH_ENGULFING("穿頭破腳"),
    LAST_BEARISH_ENGULFING("LAST穿頭破腳(預告見底)"),
//    BEARISH_ENGULFING_2("向淡鯨吞"),
    BEARISH_BLACK_ENGULFING("穿黑頭破腳"),
//    FAKE_BLACK_TRUE_WHITE_BODY("假陰真陽"),
    CLOSE_ABV_UPPER_SHADOW("上影線上收"),


    OPEN_LOW_CLOSE_HIGH("低開高收"),
    BULLISH_ENGULFING("破腳穿頭"),
    LAST_BULLISH_ENGULFING("LAST破腳穿頭(預告見頂)"),
//    BULLISH_ENGULFING_2("向好鯨吞"),
//    BULLISH_WHITE_ENGULFING("破白腳穿頭"),
//    FAKE_RED_TRUE_BLACK("假陽真陰"),
    CLOSE_BELOW_LOWER_SHADOW("下影線下收"),
    OPEN_LOW_CLOSE_LOW("低開低收"),
    INVERTED_HAMMER ("倒轉的鎚頭"),
    HAMMER ("鎚頭"),

    OPEN_HIGH_CLOSE_HIGH("高開高收"),
    GAP_UP_BEARISH_SHOOTING("裂口上升射擊"),
    GAP_UP_BULLISH("裂口上升"),
    GAP_DOWN_HAMMER("裂口下跌鎚頭"),
    GAP_DOWN_BEARISH("裂口下跌"),

    TWEEZER_TOP("雙平頂"),
    TWEEZER_BOTTOM("雙平底"),

    FULL_WHITE("光頭白K"),
    FULL_BLACK("光頭黑K"),


    THREE_DARK_DROP("增量三飛烏鴉");


    private final String displayName;

    CandlestickTradingPatternType(String displayName) {
        this.displayName = displayName;
    }
    public String toString() {
        return displayName;
    }

}