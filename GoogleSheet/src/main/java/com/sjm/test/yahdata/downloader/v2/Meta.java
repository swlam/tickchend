
package com.sjm.test.yahdata.downloader.v2;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

import java.util.List;

//@Generated("net.hexar.json2pojo")
@Data
public class Meta {

    @SerializedName("chartPreviousClose")
    private Double mChartPreviousClose;
    @SerializedName("currency")
    private String mCurrency;
    @SerializedName("currentTradingPeriod")
    private CurrentTradingPeriod mCurrentTradingPeriod;
    @SerializedName("dataGranularity")
    private String mDataGranularity;
    @SerializedName("exchangeName")
    private String mExchangeName;
    @SerializedName("exchangeTimezoneName")
    private String mExchangeTimezoneName;
    @SerializedName("fiftyTwoWeekHigh")
    private Double mFiftyTwoWeekHigh;
    @SerializedName("fiftyTwoWeekLow")
    private Double mFiftyTwoWeekLow;
    @SerializedName("firstTradeDate")
    private Long mFirstTradeDate;
    @SerializedName("fullExchangeName")
    private String mFullExchangeName;
    @SerializedName("gmtoffset")
    private Long mGmtoffset;
    @SerializedName("hasPrePostMarketData")
    private Boolean mHasPrePostMarketData;
    @SerializedName("instrumentType")
    private String mInstrumentType;
    @SerializedName("priceHint")
    private Long mPriceHint;
    @SerializedName("range")
    private String mRange;
    @SerializedName("regularMarketDayHigh")
    private Double mRegularMarketDayHigh;
    @SerializedName("regularMarketDayLow")
    private Double mRegularMarketDayLow;
    @SerializedName("regularMarketPrice")
    private Double mRegularMarketPrice;
    @SerializedName("regularMarketTime")
    private Long mRegularMarketTime;
    @SerializedName("regularMarketVolume")
    private Long mRegularMarketVolume;
    @SerializedName("shortName")
    private String mShortName;
    @SerializedName("symbol")
    private String mSymbol;
    @SerializedName("timezone")
    private String mTimezone;
    @SerializedName("validRanges")
    private List<String> mValidRanges;

    public Double getChartPreviousClose() {
        return mChartPreviousClose;
    }

    public void setChartPreviousClose(Double chartPreviousClose) {
        mChartPreviousClose = chartPreviousClose;
    }

    public String getCurrency() {
        return mCurrency;
    }

    public void setCurrency(String currency) {
        mCurrency = currency;
    }

    public CurrentTradingPeriod getCurrentTradingPeriod() {
        return mCurrentTradingPeriod;
    }

    public void setCurrentTradingPeriod(CurrentTradingPeriod currentTradingPeriod) {
        mCurrentTradingPeriod = currentTradingPeriod;
    }

    public String getDataGranularity() {
        return mDataGranularity;
    }

    public void setDataGranularity(String dataGranularity) {
        mDataGranularity = dataGranularity;
    }

    public String getExchangeName() {
        return mExchangeName;
    }

    public void setExchangeName(String exchangeName) {
        mExchangeName = exchangeName;
    }

    public String getExchangeTimezoneName() {
        return mExchangeTimezoneName;
    }

    public void setExchangeTimezoneName(String exchangeTimezoneName) {
        mExchangeTimezoneName = exchangeTimezoneName;
    }

    public Double getFiftyTwoWeekHigh() {
        return mFiftyTwoWeekHigh;
    }

    public void setFiftyTwoWeekHigh(Double fiftyTwoWeekHigh) {
        mFiftyTwoWeekHigh = fiftyTwoWeekHigh;
    }

    public Double getFiftyTwoWeekLow() {
        return mFiftyTwoWeekLow;
    }

    public void setFiftyTwoWeekLow(Double fiftyTwoWeekLow) {
        mFiftyTwoWeekLow = fiftyTwoWeekLow;
    }

    public Long getFirstTradeDate() {
        return mFirstTradeDate;
    }

    public void setFirstTradeDate(Long firstTradeDate) {
        mFirstTradeDate = firstTradeDate;
    }

    public String getFullExchangeName() {
        return mFullExchangeName;
    }

    public void setFullExchangeName(String fullExchangeName) {
        mFullExchangeName = fullExchangeName;
    }

    public Long getGmtoffset() {
        return mGmtoffset;
    }

    public void setGmtoffset(Long gmtoffset) {
        mGmtoffset = gmtoffset;
    }

    public Boolean getHasPrePostMarketData() {
        return mHasPrePostMarketData;
    }

    public void setHasPrePostMarketData(Boolean hasPrePostMarketData) {
        mHasPrePostMarketData = hasPrePostMarketData;
    }

    public String getInstrumentType() {
        return mInstrumentType;
    }

    public void setInstrumentType(String instrumentType) {
        mInstrumentType = instrumentType;
    }

    public Long getPriceHint() {
        return mPriceHint;
    }

    public void setPriceHint(Long priceHint) {
        mPriceHint = priceHint;
    }

    public String getRange() {
        return mRange;
    }

    public void setRange(String range) {
        mRange = range;
    }

    public Double getRegularMarketDayHigh() {
        return mRegularMarketDayHigh;
    }

    public void setRegularMarketDayHigh(Double regularMarketDayHigh) {
        mRegularMarketDayHigh = regularMarketDayHigh;
    }

    public Double getRegularMarketDayLow() {
        return mRegularMarketDayLow;
    }

    public void setRegularMarketDayLow(Double regularMarketDayLow) {
        mRegularMarketDayLow = regularMarketDayLow;
    }

    public Double getRegularMarketPrice() {
        return mRegularMarketPrice;
    }

    public void setRegularMarketPrice(Double regularMarketPrice) {
        mRegularMarketPrice = regularMarketPrice;
    }

    public Long getRegularMarketTime() {
        return mRegularMarketTime;
    }

    public void setRegularMarketTime(Long regularMarketTime) {
        mRegularMarketTime = regularMarketTime;
    }

    public Long getRegularMarketVolume() {
        return mRegularMarketVolume;
    }

    public void setRegularMarketVolume(Long regularMarketVolume) {
        mRegularMarketVolume = regularMarketVolume;
    }

    public String getShortName() {
        return mShortName;
    }

    public void setShortName(String shortName) {
        mShortName = shortName;
    }

    public String getSymbol() {
        return mSymbol;
    }

    public void setSymbol(String symbol) {
        mSymbol = symbol;
    }

    public String getTimezone() {
        return mTimezone;
    }

    public void setTimezone(String timezone) {
        mTimezone = timezone;
    }

    public List<String> getValidRanges() {
        return mValidRanges;
    }

    public void setValidRanges(List<String> validRanges) {
        mValidRanges = validRanges;
    }

}
