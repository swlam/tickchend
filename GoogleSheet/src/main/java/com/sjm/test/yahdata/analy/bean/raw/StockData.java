package com.sjm.test.yahdata.analy.bean.raw;

import java.time.LocalDate;

import lombok.Data;
@Data
public class StockData {
	private String symbol;
	private String frequency; //D, W, M
	private String dayOneDate;
	private double o;
	private double l;
	private double h;
	private double c;
    private double volume;

    public StockData(String symbol, String frequency, String dayOneDate, double weeklyOpenValue, double weeklyCloseValue,
                       double weeklyHighValue, double weeklyLowValue, double weeklyVolume) {
    	this.symbol = symbol;
    	this.frequency = frequency;
        this.dayOneDate = dayOneDate;
        this.o = weeklyOpenValue;
        this.c = weeklyCloseValue;
        this.h = weeklyHighValue;
        this.l = weeklyLowValue;
        this.volume = weeklyVolume;
    }

    @Override
    public String toString() {
        return this.frequency+ " starting from: " + dayOneDate +
                "\nOpen Value: " + o +
                "\nClose Value: " + c +
                "\nHigh Value: " + h +
                "\nLow Value: " + l +
                "\nVolume: " + volume +
                "\n";
    }
}
