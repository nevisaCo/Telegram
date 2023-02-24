package com.finalsoft.admob;

import android.app.Activity;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.NonNull;

import com.finalsoft.SharedStorage;
import com.finalsoft.admob.models.CountItem;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.gson.Gson;

import org.telegram.messenger.BuildVars;

import java.util.ArrayList;


class Reward extends AdmobBaseClass {

    private static Reward reward;
    private Activity context;
    protected final String TAG = super.TAG + "r";

    private static final String KEY = "target_rewarded";
    private static final String KEY_COUNTER = "rewarded_";

    private RewardedAd mRewardedAd;
    RewardedAdLoadCallback rewardedAdLoadCallback;
    private int retry = 0;
    ArrayList<CountItem> rewardedItems = new ArrayList<>();

    public static Reward getInstance() {
        if (reward == null) {
            reward = new Reward();
        }
        return reward;
    }

    public String getRewardedUnitId() {
        String unitId = getKeys().getRewarded();
        if (BuildVars.DEBUG_VERSION) {
            Log.i(TAG, "AdmobController > getRewardedUnitId > unit id :" + unitId);
        }
        return unitId;
    }


    void init(Activity activity) {
        this.context = activity;
        rewardedItems = getItems(KEY);
        if (rewardedItems.size() == 0) {
            Log.e(TAG, "Can't show ad, rewarded items is 0.;");
        }
    }

    private int getTarget(String name) {
        CountItem countItem = rewardedItems.stream().filter(p -> p.getName().equals(name)).findAny().orElse(null);
        if (countItem != null) {
            return countItem.getCount();

        }
        return 0;
    }

    //add from remote config
    public void setTargets(ArrayList<CountItem> countItems) {
        SharedStorage.admobTargets(KEY, new Gson().toJson(countItems));
    }

    private boolean isActive() {
        int i = rewardedItems.stream().mapToInt(CountItem::getCount).sum();
        return i > 0 && getShowAdmob();
    }


    void serve(Activity activity) {
        if (!isActive()) {
            Log.e(TAG, "serve Rewarded: Can't serve the ads ,rewarded is disabled");
            return;
        }


        String unitId = getRewardedUnitId();
        if (unitId.isEmpty()) {
            Log.e(TAG, "Rewarded > unit id is empty! ");
            return;
        }

        AdRequest adRequest = new AdRequest.Builder().build();

        rewardedAdLoadCallback = new RewardedAdLoadCallback() {

            @Override
            public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
                mRewardedAd = rewardedAd;
                Log.d(TAG, "Ad was loaded.");
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                // Handle the error.
                Log.e(TAG, "Rewarded > onAdFailedToLoad > error:" + loadAdError);

                mRewardedAd = null;
                if (retry < getAttemptToFail()) {
                    new Handler().postDelayed(() -> {
                        Log.i(TAG, "onAdFailedToLoad: retrying to load rewarded ad... attempt:" + (retry + 1));
                        RewardedAd.load(activity,
                                unitId,
                                adRequest,
                                rewardedAdLoadCallback);

                        retry++;

                    }, (retry + 1) * 10000L);
                }
            }

        };

        RewardedAd.load(activity, unitId, adRequest, rewardedAdLoadCallback);


    }

    public void show(Activity activity, String name, @NonNull OnUserEarnedRewardListener listener) {
        int target = getTarget(name);
        Log.i(TAG, "rewarded > show: " + target);

        if (target > 0) {
            int i = getCounter(KEY_COUNTER + name) + 1;
            setCounter(KEY_COUNTER + name, i);
            Log.i(TAG, String.format("show Rewarded :name:%s ,i:%s , target:%s", name, i, target));

            if (i >= target) {
                if (mRewardedAd == null) {
                    Log.e(TAG, "showRewarded: rewardedAd is null");
                    return;
                }

                mRewardedAd.show(activity, listener);
                mRewardedAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                    @Override
                    public void onAdShowedFullScreenContent() {
                        // Called when ad is shown.
                        Log.d(TAG, "Ad was shown.");
                        setCounter(KEY_COUNTER + name, 0);
                    }

                    @Override
                    public void onAdFailedToShowFullScreenContent(AdError adError) {
                        // Called when ad fails to show.
                        Log.d(TAG, "Ad failed to show.");

                    }

                    @Override
                    public void onAdDismissedFullScreenContent() {
                        // Called when ad is dismissed.
                        // Set the ad reference to null so you don't show the ad a second time.
                        Log.d(TAG, "Ad was dismissed.");
                        mRewardedAd = null;
                        serve(activity);
                    }
                });
            }
        }

    }
    //endregion


}
