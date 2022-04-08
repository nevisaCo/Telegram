package com.finalsoft.admob;

import android.app.Activity;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.finalsoft.Config;
import com.finalsoft.admob.models.AdCountItem;
import com.finalsoft.admob.ui.NativeAddCell;
import com.google.android.gms.ads.OnUserEarnedRewardListener;

import org.telegram.ui.LaunchActivity;

import java.util.ArrayList;

public class AdmobController extends AdmobBaseClass {
    private static final int DEBUG_COUNT = 20;

    public interface AdmobControllerDelegate {
        public void onResponse();
    }

    private boolean keysExist = true;
    private final String TAG = Config.TAG + "ac";
    Context context;
    //region Instance
    private static AdmobController admobController;

    public static AdmobController getInstance() {
        if (admobController == null) {
            admobController = new AdmobController();
        }
        return admobController;
    }
    //endregion


    public void init(@NonNull LaunchActivity launchActivity, @NonNull ICallback iCallback) {
        init(launchActivity, iCallback, null);
    }

    public void init(@NonNull LaunchActivity launchActivity, @NonNull ICallback iCallback, @Nullable IServeCallback nativeCallback) {
        init(launchActivity, iCallback, nativeCallback, null);
    }


    public void init(@NonNull LaunchActivity launchActivity
            , @NonNull ICallback iCallback
            , @Nullable IServeCallback nativeCallback
            , @Nullable IServeCallback interstitialCallback) {


        ICallback iCallback1 = new ICallback() {
            @Override
            public void before() {
                Interstitial.getInstance().init(launchActivity);

                Native.getInstance().init(launchActivity);

                Reward.getInstance().init(launchActivity);

                iCallback.before();

            }

            @Override
            public void onResponse() {
                iCallback.onResponse();

                Reward.getInstance().serve(launchActivity);

                Native.getInstance().serve(nativeCallback);

                Interstitial.getInstance().serve(interstitialCallback);

            }

        };


        super.init(launchActivity, iCallback1);

    }

    public void showInterstitial(String name) {
        Interstitial.getInstance().show(name);
    }

    public void showRewarded(Activity activity, String name, OnUserEarnedRewardListener listener) {
        Reward.getInstance().show(activity, name, listener);
    }

    public void setInterstitialTargets(ArrayList<AdCountItem> adCountItems) {
        Interstitial.getInstance().setTargets(adCountItems);
    }

    public void setRewardedTargets(ArrayList<AdCountItem> adCountItems) {
        Reward.getInstance().setTargets(adCountItems);
    }

    public void setNativeTargets(ArrayList<AdCountItem> adCountItems) {
        Native.getInstance().setTargets(adCountItems);
    }

    public void addNativeDialogs() {
        Native.getInstance().addNativeDialogs();
    }


    public NativeAddCell getNativeItem(String name, IServeCallback callback) {
        return Native.getInstance().getItem(name, callback);
    }



/*    //region  Old Initialize
    private com.finalsoft.admob.models.AdmobKeys keys;

    public AdmobKeys getKeys() {
        if (keys == null) {
            init();
        }
        return keys;
    }

    public void saveKeys(String data) {
        //set from flurry
        Log.i(TAG, "saveKeys: " + data);
        SharedStorage.admobKeys(data);
        if (data != null) {
            init();
        }
    }

    public void init() {
        keysExist = true;
        JSONObject admob_keys;
        try {
            if (BuildVars.DEBUG_VERSION) {
                admob_keys = new JSONObject()
                        .put("app", "ca-app-pub-3940256099942544~3347511713")
                        .put("interstitial", "ca-app-pub-3940256099942544/1033173712")
                        .put("interstitial_donate", "ca-app-pub-3940256099942544/1033173712")
                        .put("interstitial_reward", "ca-app-pub-3940256099942544/1033173712")
                        .put("banner", "ca-app-pub-3940256099942544/6300978111")
                        .put("reward", "ca-app-pub-3940256099942544/5224354917")
                        .put("video_donate", "ca-app-pub-3940256099942544/5224354917")
                        .put("video_reward", "ca-app-pub-3940256099942544/5224354917")
                        .put("native", "ca-app-pub-3940256099942544/2247696110")
                        .put("native_video", "ca-app-pub-3940256099942544/1044960115");
            } else {
                String json = SharedStorage.admobKeys();
                if (json == null || json.isEmpty()) {
                    Log.e(TAG, "AdmobController > init > saved json is null or empty!");
                    if (keys == null) {
                        keysExist = false;
                        keys = new com.finalsoft.admob.models.AdmobKeys();
                    }
                    return;
                }
                admob_keys = new JSONObject(json);
            }
            Gson gson = new Gson();
            keys = gson.fromJson(admob_keys.toString(), com.finalsoft.admob.models.AdmobKeys.class);
            if (BuildVars.DEBUG_VERSION) {
                Log.i(TAG, "AdmobController > init > keys:" + new Gson().toJson(keys));
            }

            show_admob = SharedStorage.showAdmob();

            initNativeTabs("init()");

            interstitialCountOnOpenApp = SharedStorage.admobInt(ON_OPEN_COUNT);
            interstitialCounterOnOpenApp = SharedStorage.admobInt(ON_OPEN_COUNTER);

            interstitialCountOnProxy = SharedStorage.admobInt(ON_PROXY_COUNT);
            interstitialCounterOnProxy = SharedStorage.admobInt(ON_PROXY_COUNTER);

            interstitialCountOnDialog = SharedStorage.admobInt(ON_DIALOG_COUNT);
            interstitialCounterOnDialog = SharedStorage.admobInt(ON_DIALOG_COUNTER);

            interstitialCountOnGhost = SharedStorage.admobInt(ON_GHOST_COUNT);
            interstitialCounterOnGhost = SharedStorage.admobInt(ON_GHOST_COUNTER);

        } catch (JSONException e) {
            Log.e(TAG, "admobKeys error: ", e);
            if (keys == null) {
                keysExist = false;
                keys = new AdmobKeys();
            }
        }

    }

    public void initAdmob(LaunchActivity launchActivity, AdmobController.AdmobControllerDelegate admobControllerDelegate) {
        context = launchActivity;
        if (getKeys() != null && getKeys().getApp_id() != null && !getKeys().getApp_id().isEmpty()) {
            Log.i(TAG, "initAdmob: deprecated mode");
            MobileAds.initialize(launchActivity, getKeys().getApp_id());
            admobControllerDelegate.onResponse();
            return;
        } else {
            Log.i(TAG, "initAdmob: new mode");
            MobileAds.initialize(launchActivity, initializationStatus -> {
                admobControllerDelegate.onResponse();
                Log.i(TAG, "AdmobController > initAdmob > initialize successfully :)");
            });
        }

*//*        if (BuildVars.DEBUG_VERSION) {
            List<String> testDeviceIds = Arrays.asList("33BE2250B43518CCDA7DE426D04EE231", AdRequest.DEVICE_ID_EMULATOR);
            RequestConfiguration configuration =
                    new RequestConfiguration.Builder().setTestDeviceIds(testDeviceIds).build();
            MobileAds.setRequestConfiguration(configuration);
        }*//*


    }

    //endregion


    //region Active ADMOB
    private boolean show_admob;

    public void setShowAdmob(boolean status) {
        SharedStorage.showAdmob(status);
        show_admob = status;
    }

    public boolean getShowAdmob() {
        if (!Config.ADMOB_FEATURE) {
            return false;
        }
        if (!keysExist) {
            Log.i(TAG, "getShowAdmob > keys is empty!");
            return false;
        }
        long cache = SharedStorage.turnOffAdsTime();//some of users off admob with sent telegram messeges to hes contact
        if (cache > 0) {
            if (cache >= Calendar.getInstance().getTimeInMillis()) {
                return false;
            }
        }

        if (getKeys() == null) {
            Log.e(TAG, "AdmobController > getShowAdmob > keys is null");
            return false;
        }

        return show_admob;
    }
    //endregion


    //region Rewarded
    public String getRewardedUnitId() {
        String unitId = getKeys().getRewarded();
        if (BuildVars.DEBUG_VERSION) {
            Log.i(TAG, "AdmobController > getRewardedUnitId > unit id :" + unitId);
        }
        return unitId;
    }
    //endregion*/
}
