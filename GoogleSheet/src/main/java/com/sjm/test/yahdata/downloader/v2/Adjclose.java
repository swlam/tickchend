
package com.sjm.test.yahdata.downloader.v2;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

import java.util.List;

//@Generated("net.hexar.json2pojo")
@Data
public class Adjclose {

    @SerializedName("adjclose")
    private List<Double> mAdjclose;

    public List<Double> getAdjclose() {
        return mAdjclose;
    }

    public void setAdjclose(List<Double> adjclose) {
        mAdjclose = adjclose;
    }

}
