package com.finalsoft.admob.models;

import com.google.gson.annotations.SerializedName;

public class AdKeys {

    @SerializedName("app")
    public String appId = "";

    @SerializedName("app_open")
    public String appOpen = "";

    @SerializedName("interstitial")
    public String interstitial = "";

    @SerializedName("rewarded_interstitial")
    public String rewardedInterstitial = "";

    @SerializedName("banner")
    public String banner = "";

    @SerializedName("rewarded")
    public String rewarded = "";

    @SerializedName("native")
    public String nativeAd = "";

    public String getAppId() {
        return appId;
    }

    public String getInterstitial() {
        return interstitial;
    }

    public String getBanner() {
        return banner;
    }

    public String getNative() {
        return nativeAd;
    }

    public String getAppOpen() {
        return appOpen;
    }

    public String getRewardedInterstitial() {
        return rewardedInterstitial;
    }

    public String getRewarded() {
        return rewarded;
    }
}
