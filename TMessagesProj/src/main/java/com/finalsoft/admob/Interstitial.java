package com.finalsoft.admob;

import android.app.Activity;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.NonNull;

import com.finalsoft.SharedStorage;
import com.finalsoft.admob.models.AdCountItem;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.gson.Gson;

import java.util.ArrayList;

class Interstitial extends AdmobBaseClass {

    private static Interstitial interstitial;
    private Activity context;
    private static final String KEY = "target_interstitial_";
    private static final String KEY_COUNTER = "interstitial_";

    public static Interstitial getInstance() {
        if (interstitial == null) {
            interstitial = new Interstitial();
        }
        return interstitial;
    }


    InterstitialAd mInterstitialAd;
    ArrayList<AdCountItem> interstitialItems = new ArrayList<>();

    void init(Activity activity) {
        this.context = activity;
        interstitialItems = getItems(KEY);
        if (interstitialItems.size() == 0) {
            Log.e(TAG, "initInterstitial:Can't show ad, interstitial items is 0.;");
        }
    }

    private int getTarget(String name) {
        try {
            AdCountItem adCountItem = interstitialItems.stream().filter(p -> p.getName().equals(name)).findAny().orElse(null);
            if (adCountItem != null) {
                return adCountItem.getCount();

            }
        } catch (Exception e) {
            Log.e(TAG, "getTarget: ", e);
        }
        return 0;
    }

    //add from remote config
    public void setTargets(ArrayList<AdCountItem> adCountItems) {
        SharedStorage.admobTargets(KEY, new Gson().toJson(adCountItems));
    }

    private boolean isActive() {
        int i = 0;
        try {
            i = interstitialItems.stream().mapToInt(AdCountItem::getCount).sum();
        } catch (Exception e) {
            Log.e(TAG, "isActive: ", e);
        }
        return i > 0 && getShowAdmob();
    }

    private int retry = 0;
    InterstitialAdLoadCallback interstitialAdLoadCallback;

    void serve(IServeCallback iCallback) {
        if (!isActive()) {
            Log.e(TAG, "serveInterstitial: Can't serve interstitial ads ,interstitial is disabled");
            return;
        }


        //admob lib 20.6
        mInterstitialAd = null;
        AdRequest adRequest = new AdRequest.Builder().build();
        String unitId = getKeys().getInterstitial();
        interstitialAdLoadCallback = new InterstitialAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                // The mInterstitialAd reference will be null until
                // an ad is loaded.
                mInterstitialAd = interstitialAd;
                Log.i(TAG, "Interstitial > onAdLoaded");
                if (iCallback != null) {
                    iCallback.onServe();
                }
                retry = 0;
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                // Handle the error
                Log.e(TAG,"onAdFailedToLoad:" + loadAdError);
                if (mInterstitialAd == null && retry < getAttemptToFail()) {
                    new Handler().postDelayed(() -> {
                        Log.i(TAG, "onAdFailedToLoad: retrying to load ad... " + retry + 1);
                        InterstitialAd.load(context,
                                unitId,
                                adRequest,
                                interstitialAdLoadCallback);

                        retry++;

                    }, (retry + 1) * 10000L);


                }

            }
        };

        InterstitialAd.load(context, unitId, adRequest, interstitialAdLoadCallback);
    }

    public void show(String name) {
        int target = getTarget(name);
        if (target > 0) {

            int i = getCounter(KEY_COUNTER + name) + 1;
            setCounter(KEY_COUNTER + name, i);
            Log.i(TAG, String.format("showInterstitial:name:%s ,i:%s , target:%s", name, i, target));

            if (i >= target) {

                if (mInterstitialAd == null) {
                    serve(() -> show(name));
                    Log.i(TAG, "showAdmobInterstitial: mInterstitialAd is null, returned.");
                    return;
                }

                mInterstitialAd.show(context);
                mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                    @Override
                    public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                        super.onAdFailedToShowFullScreenContent(adError);
                    }

                    @Override
                    public void onAdShowedFullScreenContent() {
                        super.onAdShowedFullScreenContent();
                        setCounter(KEY_COUNTER + name, 0);
                        if (SharedStorage.preServeInterstitial()) {
                            serve(null);
                        } else {
                            mInterstitialAd = null;
                        }

                    }

                    @Override
                    public void onAdDismissedFullScreenContent() {
                        super.onAdDismissedFullScreenContent();

                    }
                });
            }
        }
    }
    //endregion


}
