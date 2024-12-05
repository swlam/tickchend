
package com.sjm.test.yahdata.downloader.v2;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

import java.util.List;

//@Generated("net.hexar.json2pojo")
@Data
public class Quote {

    @SerializedName("close")
    private List<Double> mClose;
    @SerializedName("high")
    private List<Double> mHigh;
    @SerializedName("low")
    private List<Double> mLow;
    @SerializedName("open")
    private List<Double> mOpen;
    @SerializedName("volume")
    private List<Long> mVolume;

    public List<Double> getClose() {
        return mClose;
    }

    public void setClose(List<Double> close) {
        mClose = close;
    }

    public List<Double> getHigh() {
        return mHigh;
    }

    public void setHigh(List<Double> high) {
        mHigh = high;
    }

    public List<Double> getLow() {
        return mLow;
    }

    public void setLow(List<Double> low) {
        mLow = low;
    }

    public List<Double> getOpen() {
        return mOpen;
    }

    public void setOpen(List<Double> open) {
        mOpen = open;
    }

    public List<Long> getVolume() {
        return mVolume;
    }

    public void setVolume(List<Long> volume) {
        mVolume = volume;
    }

}
