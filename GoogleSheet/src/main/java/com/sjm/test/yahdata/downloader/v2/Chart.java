
package com.sjm.test.yahdata.downloader.v2;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

import java.util.List;

//@Generated("net.hexar.json2pojo")
@Data
public class Chart {

    @SerializedName("error")
    private Object mError;
    @SerializedName("result")
    private List<Result> mResult;

    public Object getError() {
        return mError;
    }

    public void setError(Object error) {
        mError = error;
    }

    public List<Result> getResult() {
        return mResult;
    }

    public void setResult(List<Result> result) {
        mResult = result;
    }

}
