package com.finalsoft.admob;

import static androidx.lifecycle.Lifecycle.Event.ON_START;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ProcessLifecycleOwner;

import com.finalsoft.ApplicationLoader;
import com.finalsoft.SharedStorage;
import com.finalsoft.admob.models.AdCountItem;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.appopen.AppOpenAd;
import com.google.gson.Gson;

import org.telegram.messenger.BuildVars;

import java.util.ArrayList;
import java.util.Date;

public class AppOpen extends AdmobBaseClass implements LifecycleObserver, Application.ActivityLifecycleCallbacks {
    private final String TAG = super.TAG + "ao";
    private AppOpenAd appOpenAd = null;
    private AppOpenAd.AppOpenAdLoadCallback loadCallback;
    private Activity currentActivity;
    private final ApplicationLoader myApplication;
    private static final String KEY = "target_open_app";
    private static final String KEY_COUNTER = "open_app_";

    ArrayList<AdCountItem> openAppItems;

    /**
     * Constructor
     */
    public AppOpen(ApplicationLoader myApplication) {
        this.myApplication = myApplication;
        this.myApplication.registerActivityLifecycleCallbacks(this);
        ProcessLifecycleOwner.get().getLifecycle().addObserver(this);
        openAppItems = getItems(KEY);
        if (openAppItems.size() == 0) {
            Log.e(TAG, "AppOpen > Can't show ad, app_open items is 0.;");
        }
    }


    private int getTarget(String name) {
        AdCountItem adCountItem = openAppItems.stream().filter(p -> p.getName().equals(name)).findAny().orElse(null);
        if (adCountItem != null) {
            return adCountItem.getCount();
        }

        return 0;
    }

    //add from remote config
    public static void setTargets(ArrayList<AdCountItem> adCountItems) {
        SharedStorage.admobTargets(KEY, new Gson().toJson(adCountItems));
    }

    private boolean isActive() {
        int i = openAppItems.stream().mapToInt(AdCountItem::getCount).sum();
        return i > 0 && getShowAdmob();
    }

    public String getUnitId() {
        String unitId = getKeys().getAppOpen();
        if (BuildVars.DEBUG_VERSION) {
            Log.i(TAG, "AppOpen > unit id :" + unitId);
        }
        return unitId;
    }


    /**
     * Creates and returns ad request.
     */
    private AdRequest getAdRequest() {
        return new AdRequest.Builder().build();
    }

    /**
     * Utility method to check if ad was loaded more than n hours ago.
     */
    private boolean wasLoadTimeLessThanNHoursAgo() {
        long dateDifference = (new Date()).getTime() - this.loadTime;
        long numMilliSecondsPerHour = 3600000;
        return (dateDifference < (numMilliSecondsPerHour * (long) 4));
    }


    /**
     * Utility method that checks if ad exists and can be shown.
     */
    public boolean isAdAvailable() {
        return appOpenAd != null && wasLoadTimeLessThanNHoursAgo();

    }

    private long loadTime = 0;

    /**
     * Request an ad
     */
    public void serve(IServeCallback iServeCallback) {
        if (!isActive()) {
            Log.i(TAG, "AppOpen > fetchAd: not activated");
            return;
        }

        // Have unused ad, no need to fetch another.
        if (isAdAvailable()) {
            return;
        }

        loadCallback = new AppOpenAd.AppOpenAdLoadCallback() {
            /**
             * Called when an app open ad has loaded.
             *
             * @param ad the loaded app open ad.
             */
            @Override
            public void onAdLoaded(@NonNull AppOpenAd ad) {
                AppOpen.this.appOpenAd = ad;
                AppOpen.this.loadTime = (new Date()).getTime();
                Log.i(TAG, "AppOpen > onAdLoaded > ad load done.");
                if (iServeCallback != null) {
                    iServeCallback.onServe();
                }
            }

            /**
             * Called when an app open ad has failed to load.
             *
             * @param loadAdError the error.
             */
            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                // Handle the error.
                Log.e(TAG, "AppOpen > onAdFailedToLoad > error : " + loadAdError);
            }

        };
        AdRequest request = getAdRequest();
        AppOpenAd.load(
                myApplication, getUnitId(), request,
                AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT, loadCallback);
    }

    private static boolean isShowingAd = false;

    /**
     * Shows the ad if one isn't already showing.
     */
    public void showAdIfAvailable() {
        String name = OPEN_APP;
        int target = getTarget(name);
        if (target > 0) {
            int i = getCounter(KEY_COUNTER + name) + 1;
            setCounter(KEY_COUNTER + name, i);
            Log.i(TAG, String.format("AppOpen > name:%s ,i:%s , target:%s", name, i, target));

            if (i >= target) {

                // Only show ad if there is not already an app open ad currently showing
                // and an ad is available.
                if (!isShowingAd && isAdAvailable()) {
                    Log.d(TAG, "Will show ad.");

                    FullScreenContentCallback fullScreenContentCallback =
                            new FullScreenContentCallback() {
                                @Override
                                public void onAdDismissedFullScreenContent() {
                                    // Set the reference to null so isAdAvailable() returns false.
                                    appOpenAd = null;
                                    isShowingAd = false;
                                    serve(null);
                                }

                                @Override
                                public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                                }

                                @Override
                                public void onAdShowedFullScreenContent() {
                                    isShowingAd = true;
                                    setCounter(KEY_COUNTER + name, 0);
                                }
                            };

                    appOpenAd.setFullScreenContentCallback(fullScreenContentCallback);
                    appOpenAd.show(currentActivity);

                } else {
                    Log.d(TAG, "Can not show ad.");
                    serve(this::showAdIfAvailable);
                }
            }
        } else {
            Log.i(TAG, "showAdIfAvailable: target is 0");
        }
    }

    /**
     * ActivityLifecycleCallback methods
     */
    @Override
    public void onActivityCreated(@NonNull Activity activity, Bundle savedInstanceState) {
    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {
        currentActivity = activity;
    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {
        currentActivity = activity;
    }

    @Override
    public void onActivityStopped(@NonNull Activity activity) {
    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {
    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle bundle) {
    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {
        currentActivity = null;
    }

    @OnLifecycleEvent(ON_START)
    public void onStart() {
        showAdIfAvailable();
        Log.d(TAG, "onStart");
    }
}
