package com.sjm.test.yahdata.analy.conts.ta;

public enum StockTrendStatus {
    BREAKOUT("升破前頂"),
    BREAKOUT_DIRECT("升破頂(直接)"),
    PULLBACK("回調"),
    BREAKDOWN("跌破前底"),
    BREAKDOWN_DIRECT("跌破前底(直接)"),
    REBOUND("反彈"),
    REVERSAL_DOWN("反轉向下"),// 假突破
    REVERSAL_UP("反轉向上"), //  破底翻
    SEEKING_TOP("尋頂中"),
    SEEKING_BOTTOM("尋底中");

    private final String description;

    StockTrendStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
    public String toString() {
    	return description;
    }
}
