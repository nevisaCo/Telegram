package co.nevisa.commonlib.admob;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.gson.Gson;

import java.util.ArrayList;

import co.nevisa.commonlib.AdmobApplicationLoader;
import co.nevisa.commonlib.Storage;
import co.nevisa.commonlib.admob.interfaces.IServedCallback;
import co.nevisa.commonlib.admob.models.CountItem;

class Interstitial extends AdmobBaseClass {

    private static Interstitial interstitial;
    private Activity context;
    private static final String KEY = "target_interstitial_";

    public static Interstitial getInstance() {
        if (interstitial == null) {
            interstitial = new Interstitial();
        }
        return interstitial;
    }


    InterstitialAd mInterstitialAd;
    ArrayList<CountItem> interstitialItems = new ArrayList<>();

    void init(Activity activity) {
        Log.i(TAG, "initInterstitial > init: ");
        this.context = activity;
        interstitialItems = getItems(KEY);
        if (interstitialItems.size() == 0) {
            Log.e(TAG, "initInterstitial:Can't show ad, interstitial items is 0.;");
        }
    }

    private int getTarget(String name) {
        for (CountItem countItem : interstitialItems) {
            if (name.equals(countItem.getName())) {
                return countItem.getCount();
            }
        }

        return 0;
    }

    //add from remote config
    public void setTargets(ArrayList<CountItem> countItems) {
        Storage.admobTargets(KEY, new Gson().toJson(countItems));
    }

    private boolean isShow() {
        int i = 0;
        for (CountItem countItem : interstitialItems) {
            i += countItem.getCount();
        }
        return i > 0 && getShowAdmob();
    }

    private boolean isShow(String name) {
        return getTarget(name) > 0;
    }

    private int retry = 0;


    void serve(@Nullable IServedCallback iCallback) {
        if (!isShow()) {
            Log.e(TAG, "serveInterstitial: Can't serve interstitial ads ,interstitial is disabled");
            return;
        }


        //admob lib 20.5
        mInterstitialAd = null;
        @SuppressLint("VisibleForTests") AdRequest adRequest = new AdRequest.Builder().build();

        String unitId = getKeys().getInterstitial();
        InterstitialAd.load(AdmobApplicationLoader.getContext(), unitId, adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
                        mInterstitialAd = interstitialAd;
                        Log.i(TAG, "onAdLoaded");
                        if (iCallback != null) {
                            iCallback.onServed(mInterstitialAd);
                        }
                        retry = 0;

                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error
                        Log.i(TAG, loadAdError.getMessage());
                        if (mInterstitialAd == null && retry < getAttemptToFail()) {
                            serve(iCallback);
                            retry++;
                        }

                    }
                });
    }

    public void show(String name) {
        if (isShow(name)) {
            int i = getCounter("interstitial_" + name) + 1;
            setCounter("interstitial_" + name, i);
            int target = getTarget(name);
            Log.i(TAG, String.format("showInterstitial:name:%s ,i:%s , target:%s", name, i, target));

            if (i >= target) {

                if (mInterstitialAd == null) {
                    Log.i(TAG, "showAdmobInterstitial: mInterstitialAd is null, returned.");
                    serve((object) -> {
                        if (!Storage.admobPreServe()) {
                            show(name);
                        }
                    });
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
                        setCounter("interstitial_" + name, 0);
                        if (Storage.admobPreServe()) {
                            serve(null);
                        }
                        mInterstitialAd = null;

                    }

                    @Override
                    public void onAdDismissedFullScreenContent() {
                        super.onAdDismissedFullScreenContent();

                    }
                });
            }
        }
    }


}
