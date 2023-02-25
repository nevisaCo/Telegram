package co.nevisa.commonlib.admob;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.android.gms.ads.rewarded.ServerSideVerificationOptions;
import com.google.gson.Gson;

import java.util.ArrayList;

import co.nevisa.commonlib.BuildVars;
import co.nevisa.commonlib.Storage;
import co.nevisa.commonlib.admob.interfaces.IServedCallback;
import co.nevisa.commonlib.admob.models.CountItem;


class Reward extends AdmobBaseClass {

    @SuppressLint("StaticFieldLeak")
    private static Reward reward;
    private Activity context;

    private static final String KEY = "target_rewarded_";
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
        Log.i(TAG, "Reward > init: ");
        this.context = activity;
        rewardedItems = getItems(KEY);
        if (rewardedItems.size() == 0) {
            Log.e(TAG, "Can't show ad, rewarded items is 0.;");
        }
    }

    private int getTarget(String name) {
        Log.i(TAG, "getTarget: " + rewardedItems.size());
        for (CountItem countItem : rewardedItems) {
            if (name.equals(countItem.getName())) {
                return countItem.getCount();
            }
        }

        return 0;
    }

    //add from remote config
    public void setTargets(ArrayList<CountItem> adCountItems) {
        Storage.admobTargets(KEY, new Gson().toJson(adCountItems));
    }

    private boolean isShow() {
        int i = 0;
        for (CountItem countItem : rewardedItems) {
            i += countItem.getCount();
        }
        return i > 0 && getShowAdmob();
    }

    private boolean isShow(String name) {
        return getTarget(name) > 0;
    }


    /**
     * @param iCallback return serve result.
     */
    void serve(@NonNull IServedCallback iCallback) {
        if (!isShow()) {
            iCallback.onServed(null);
            Log.e(TAG, "serve Rewarded: Can't serve the ads ,rewarded is disabled");
            return;
        }


        String unitId = getRewardedUnitId();
        if (unitId.isEmpty()) {
            iCallback.onServed(null);
            Log.e(TAG, "Rewarded > unit id is empty! ");
            return;
        }


        @SuppressLint("VisibleForTests") AdRequest adRequest = new AdRequest.Builder().build();
        rewardedAdLoadCallback = new RewardedAdLoadCallback() {

            @Override
            public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
                mRewardedAd = rewardedAd;
                Log.d(TAG, "Ad was loaded.");
                iCallback.onServed(rewardedAd);
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                iCallback.onServed(null);
                // Handle the error.
                Log.e(TAG, "Rewarded > onAdFailedToLoad > error:" + loadAdError);
                mRewardedAd = null;
                if (retry < getAttemptToFail()) {
                    new Handler().postDelayed(() -> {
                        Log.i(TAG, "onAdFailedToLoad: retrying to load rewarded ad... attempt:" + (retry + 1));
                        RewardedAd.load(context,
                                unitId,
                                adRequest,
                                rewardedAdLoadCallback);

                        retry++;

                    }, (retry + 1) * 10000L);
                }
            }

        };

        RewardedAd.load(context, unitId, adRequest, rewardedAdLoadCallback);


    }


    /**
     * @param name                       key name of you location
     * @param customData                 customData custom data for get in in server side and check
     * @param callback return reward item after show completed.
     */
    public void show(String name, @Nullable String customData, @NonNull IRewardCallback callback) {
        int target = getTarget(name);
        if (target <= 0) {
            Log.e(TAG, "rewarded > show > target is 0 for '" + name + "' . return");
            callback.onFail("target is 0");
            return;
        }

        Log.i(TAG, "rewarded > show > " + name + ":" + target);

        int i = getCounter(KEY_COUNTER + name) + 1;
        setCounter(KEY_COUNTER + name, i);
        Log.i(TAG, String.format("show Rewarded :name:%s ,i:%s , target:%s", name, i, target));

        if (i >= target) {
            if (mRewardedAd == null) {
                Log.e(TAG, "showRewarded: rewardedAd is null");
                serve(ad -> {
                    if (ad==null){
                        callback.onFail("ad null and retry failed.");
                        return;
                    }
                    show(name, customData, callback);
                });

                return;
            }

            if (customData != null && !customData.isEmpty()) {
                ServerSideVerificationOptions options = new ServerSideVerificationOptions
                        .Builder()
                        .setCustomData(customData)
                        .build();
                mRewardedAd.setServerSideVerificationOptions(options);
            }

            mRewardedAd.show(context, callback);
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
                    callback.onFail(adError);
                }

                @Override
                public void onAdDismissedFullScreenContent() {
                    // Called when ad is dismissed.
                    // Set the ad reference to null so you don't show the ad a second time.
                    Log.d(TAG, "Ad was dismissed.");
                    mRewardedAd = null;
                    if (Storage.admobPreServe()) {
                        serve(ad -> {});
                    }
                }
            });
        }


    }
    //endregion


}
