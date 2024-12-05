
package com.sjm.test.yahdata.downloader.v2;

//import javax.annotation.Generated;
import com.google.gson.annotations.SerializedName;
import lombok.Data;

//@Generated("net.hexar.json2pojo")
@Data
public class CurrentTradingPeriod {

    @SerializedName("post")
    private Post mPost;
    @SerializedName("pre")
    private Pre mPre;
    @SerializedName("regular")
    private Regular mRegular;

    public Post getPost() {
        return mPost;
    }

    public void setPost(Post post) {
        mPost = post;
    }

    public Pre getPre() {
        return mPre;
    }

    public void setPre(Pre pre) {
        mPre = pre;
    }

    public Regular getRegular() {
        return mRegular;
    }

    public void setRegular(Regular regular) {
        mRegular = regular;
    }

}
