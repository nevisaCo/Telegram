package com.finalsoft.admob;

import android.app.Activity;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import com.finalsoft.Config;
import com.finalsoft.SharedStorage;
import com.finalsoft.admob.models.AdCountItem;
import com.finalsoft.admob.models.AdKeys;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;
import org.telegram.messenger.BuildVars;
import org.telegram.ui.LaunchActivity;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class AdmobBaseClass {

    public static final String INTERSTITIAL_REFRESH_PROXY = "refresh_proxy";
    public static final String INTERSTITIAL_OPEN_APP = "open_app";
    public static final String INTERSTITIAL_OPEN_DIALOG = "open_dialog";
    public static final String INTERSTITIAL_TOGGLE_GHOST = "toggle_ghost";
    public static final String INTERSTITIAL_DONATE = "donate";
    public static final String INTERSTITIAL_USE_DOWNLOAD_MANAGER = "use_download_manager";
    public static final String INTERSTITIAL_USE_V2T = "use_v2t";
    public static final String INTERSTITIAL_USE_PHOTO_PICKER = "use_photo_picker";

    public static final String VIDEO_USE_V2T = "use_v2t";
    public static final String VIDEO_REFRESH_PROXY = "refresh_proxy";
    public static final String VIDEO_USE_PHOTO_PICKER = "use_photo_picker";

    public static final String NATIVE_TOP_CHAT = "top_chat";
//    public static final String NATIVE_TOP_CHAT="top_chat";

    //region vars
    private static final int DEBUG_COUNT = 2;
    private int attemptToFail;

    public int getAttemptToFail() {
        return attemptToFail;
    }

    public AdmobBaseClass() {
    }


    public interface ICallback {
        void before();

        void onResponse();
    }

    public interface IServeCallback {
        void onServe();
    }

    private boolean keysExist = true;
    protected final String TAG = Config.TAG + "ac";
    private Activity context;

    //endregion

    //region Instance
    private static AdmobBaseClass admobBaseClass;

    public static AdmobBaseClass getInstance() {
        if (admobBaseClass == null) {
            admobBaseClass = new AdmobBaseClass();
        }
        return admobBaseClass;
    }
    //endregion

    //region Initialize

    private AdKeys keys;

    public AdKeys getKeys() {
        if (keys == null) {
            init();
        }
        return keys;
    }

    public void saveKeys(String data) {
        Log.i(TAG, "saveKeys: " + data);
        SharedStorage.admobKeys(data);
        if (data != null) {
            init();
        }
    }

    protected ArrayList<AdCountItem> getItems(String name) {
        ArrayList<AdCountItem> ci = new ArrayList<>();
        String s = SharedStorage.admobTargets(name);
        if (s.isEmpty()) {
            return ci;
        }

        Type listType = new TypeToken<ArrayList<AdCountItem>>() {
        }.getType();

        ci = new Gson().fromJson(s, listType);

        return ci;
    }

    protected int getCounter(String name) {

        return SharedStorage.admobCounter(name.toLowerCase());
    }

    protected void setCounter(String name, int value) {
        SharedStorage.admobCounter(value, name.toLowerCase());
    }

    public void retryOnFail(int attempt) {
        SharedStorage.admobRetryOnFail(attempt);
    }

    private void init() {
        keysExist = true;
        JSONObject admob_keys;

        attemptToFail = SharedStorage.admobRetryOnFail();
        try {
            if (BuildVars.DEBUG_VERSION) {
                admob_keys = new JSONObject()
                        .put("app", "ca-app-pub-3940256099942544~3347511713")
                        .put("interstitial", "ca-app-pub-3940256099942544/1033173712")
                        .put("banner", "ca-app-pub-3940256099942544/6300978111")
                        .put("rewarded", "ca-app-pub-3940256099942544/5224354917")
                        .put("native", "ca-app-pub-3940256099942544/2247696110")
                        .put("native_video", "ca-app-pub-3940256099942544/1044960115");
            } else {
                String json = SharedStorage.admobKeys();
                if (json == null || json.isEmpty()) {
                    Log.e(TAG, "AdmobController > init > saved json is null or empty!");
                    if (keys == null) {
                        keysExist = false;
                        keys = new AdKeys();
                    }
                    return;
                }
                admob_keys = new JSONObject(json);
            }
            Gson gson = new Gson();
            keys = gson.fromJson(admob_keys.toString(), AdKeys.class);
            if (BuildVars.DEBUG_VERSION) {
                Log.i(TAG, "AdmobController > init > keys:" + new Gson().toJson(keys));
            }

            show_admob = SharedStorage.showAdmob();

        } catch (JSONException e) {
            Log.e(TAG, "admobKeys error: ", e);
            if (keys == null) {
                keysExist = false;
                keys = new AdKeys();
            }
        }

    }

    protected void init(LaunchActivity launchActivity, ICallback iCallback) {
        context = launchActivity;
        iCallback.before();

        try {
            ApplicationInfo ai = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            Bundle bundle = ai.metaData;
            String myApiKey = bundle.getString("com.google.android.gms.ads.APPLICATION_ID");

            ai.metaData.putString("com.google.android.gms.ads.APPLICATION_ID", getKeys().getAppId());


            Log.i(TAG, "initAdmob: new mode");
            MobileAds.initialize(launchActivity, initializationStatus -> {

                iCallback.onResponse();


                Log.i(TAG, "AdmobController > initAdmob > initialize successfully :)");
            });

            if (BuildVars.DEBUG_VERSION) {
                Log.i(TAG, "initAdmobKey: Manifest Admob APPLICATION_ID:" + myApiKey);

                String apiKey = bundle.getString("com.google.android.gms.ads.APPLICATION_ID");
                Log.i(TAG, "initAdmobKey: Changed Admob APPLICATION_ID:" + apiKey);


                RequestConfiguration configuration = new RequestConfiguration.Builder().setTestDeviceIds(Collections.singletonList("F1D59E70AFFA00AA61A3AA360C3CC535")).build();
                MobileAds.setRequestConfiguration(configuration);
            }

        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "initAdmob: ", e);
        }


    }

    //endregion

    //region Active Global Settings
    private boolean show_admob;

    public void setShowAdmob(boolean status) {
        SharedStorage.showAdmob(status);
        show_admob = status;
    }

    public boolean getShowAdmob() {
        if (!keysExist) {
            Log.i(TAG, "getShowAdmob > keys is empty!");
            return false;
        }
/*
        long cache = SharedStorage.turnOffAdsTime();//some of users off admob with sent telegram messeges to hes contact
        if (cache > 0) {
            if (cache >= Calendar.getInstance().getTimeInMillis()) {
                return false;
            }
        }
*/

        if (getKeys() == null) {
            Log.e(TAG, "AdmobController > getShowAdmob > keys is null");
            return false;
        }

        return show_admob;
    }
    //endregion

}
