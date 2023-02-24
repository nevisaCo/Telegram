package com.finalsoft.admob;

import android.app.Activity;
import android.util.Log;

import com.finalsoft.SharedStorage;
import com.finalsoft.admob.models.CountItem;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.gson.Gson;

import java.util.ArrayList;

class Banner extends AdmobBaseClass {

    private static Banner banner;
    private Activity context;
    private static final String KEY = "target_banner_";

    public static Banner getInstance() {
        if (banner == null) {
            banner = new Banner();
        }
        return banner;
    }


    ArrayList<CountItem> bannerItems = new ArrayList<>();

    void init(Activity activity) {
        this.context = activity;
        bannerItems = getItems(KEY);
        if (bannerItems.size() == 0) {
            Log.e(TAG, "initInterstitial:Can't show ad, interstitial items is 0.;");
        }
    }

    private int getTarget(String name) {

        CountItem a = getBannerTarget(name);
        if (a != null) {
            return a.getCount();
        }
        return 0;
    }

    public CountItem getBannerTarget(String key) {
        return bannerItems.stream().filter(p -> p.getName().equals(key)).findAny().orElse(null);
    }

    //add from remote config
    public void setTargets(ArrayList<CountItem> countItems) {
        SharedStorage.admobTargets(KEY, new Gson().toJson(countItems));
    }

    private boolean isShow() {
        int i = 0;
        for (CountItem countItem : bannerItems) {
            i += countItem.getCount();
        }
        return i > 0 && getShowAdmob();
    }

    private boolean isShow(String name) {
        return getTarget(name) > 0;
    }

    private int retry = 0;
    InterstitialAdLoadCallback interstitialAdLoadCallback;

    void serve(IServeCallback iCallback) {
        if (!isShow()) {
            Log.e(TAG, "serve banner: Can't serve the banner ads ,banner is disabled");
            return;
        }


    }

    public void show(String name) {
        if (isShow(name)) {

        }
    }


    //endregion


}
