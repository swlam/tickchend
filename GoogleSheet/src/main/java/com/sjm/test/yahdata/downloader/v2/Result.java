
package com.sjm.test.yahdata.downloader.v2;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

import java.util.List;

//@Generated("net.hexar.json2pojo")
@Data
public class Result {

    @SerializedName("indicators")
    private Indicators mIndicators;
    @SerializedName("meta")
    private Meta mMeta;
    @SerializedName("timestamp")
    private List<Long> mTimestamp;

    public Indicators getIndicators() {
        return mIndicators;
    }

    public void setIndicators(Indicators indicators) {
        mIndicators = indicators;
    }

    public Meta getMeta() {
        return mMeta;
    }

    public void setMeta(Meta meta) {
        mMeta = meta;
    }

    public List<Long> getTimestamp() {
        return mTimestamp;
    }

    public void setTimestamp(List<Long> timestamp) {
        mTimestamp = timestamp;
    }

}
