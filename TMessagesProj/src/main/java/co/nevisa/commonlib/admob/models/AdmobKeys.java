package co.nevisa.commonlib.admob.models;

import com.google.gson.annotations.SerializedName;

public class AdmobKeys {

    @SerializedName("app")
    public String app_id = "";

    @SerializedName("interstitial")
    public String interstitial = "";

/*    @SerializedName("interstitial_donate")
    public String interstitial_donate = "";

    @SerializedName("interstitial_reward")
    public String interstitial_reward = "";*/

    @SerializedName("banner")
    public String banner = "";

    @SerializedName("reward")
    public String reward = "";

/*    @SerializedName("video_donate")
    public String video_donate = "";

    @SerializedName("video_reward")
    public String video_reward = "";*/

    @SerializedName("native")
    public String native_ad = "";

    @SerializedName("native_video")
    public String native_video = "";

    public String getApp_id() {
        return app_id;
    }

    public String getInterstitial() {
        return interstitial;
    }

/*    public String getInterstitial_donate() {
        return interstitial_donate;
    }

    public String getInterstitial_reward() {
        return interstitial_reward;
    }*/

    public String getBanner() {
        return banner;
    }

    public String getRewarded() {
        return reward;
    }

/*
    public String getVideo_donate() {
        return video_donate;
    }

    public String getVideo_reward() {
        return video_reward;
    }
*/

    public String getNative() {
        return native_ad;
    }

    public String getNative_video() {
        return native_video;
    }

    @SerializedName("app_open")
    public String appOpen = "";

    public String getAppOpen() {
        return appOpen;
    }
}
