
package com.sjm.test.yahdata.downloader.v2;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

import java.util.List;

//@Generated("net.hexar.json2pojo")
@Data
public class Indicators {

    @SerializedName("adjclose")
    private List<Adjclose> mAdjclose;
    @SerializedName("quote")
    private List<Quote> mQuote;

    public List<Adjclose> getAdjclose() {
        return mAdjclose;
    }

    public void setAdjclose(List<Adjclose> adjclose) {
        mAdjclose = adjclose;
    }

    public List<Quote> getQuote() {
        return mQuote;
    }

    public void setQuote(List<Quote> quote) {
        mQuote = quote;
    }

}
