
package com.sjm.test.yahdata.downloader.v2;

//import javax.annotation.Generated;
import com.google.gson.annotations.SerializedName;
import lombok.Data;

//@Generated("net.hexar.json2pojo")
@Data
public class YahooStockBean {

    @SerializedName("chart")
    private Chart mChart;

    public Chart getChart() {
        return mChart;
    }

    public void setChart(Chart chart) {
        mChart = chart;
    }

}
