package com.sjm.test.yahdata.analy.ta.indicator.macd;

import lombok.Data;

@Data
public class MACDData {

	private double macd;
    private double ema;
    private double divergence;

    public MACDData(double macd, double ema, double divergence) {
        this.macd = macd;
        this.ema = ema; //TODO: result seems not correct
        this.divergence = divergence;//TODO: result seems not correct
    }

}
