
package com.sjm.test.yahdata.downloader.v2;

//import javax.annotation.Generated;
import com.google.gson.annotations.SerializedName;
import lombok.Data;

//@Generated("net.hexar.json2pojo")
@Data
public class Pre {

    @SerializedName("end")
    private Long mEnd;
    @SerializedName("gmtoffset")
    private Long mGmtoffset;
    @SerializedName("start")
    private Long mStart;
    @SerializedName("timezone")
    private String mTimezone;

    public Long getEnd() {
        return mEnd;
    }

    public void setEnd(Long end) {
        mEnd = end;
    }

    public Long getGmtoffset() {
        return mGmtoffset;
    }

    public void setGmtoffset(Long gmtoffset) {
        mGmtoffset = gmtoffset;
    }

    public Long getStart() {
        return mStart;
    }

    public void setStart(Long start) {
        mStart = start;
    }

    public String getTimezone() {
        return mTimezone;
    }

    public void setTimezone(String timezone) {
        mTimezone = timezone;
    }

}
