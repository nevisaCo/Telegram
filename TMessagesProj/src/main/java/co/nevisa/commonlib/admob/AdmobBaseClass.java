package co.nevisa.commonlib.admob;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import co.nevisa.commonlib.BuildVars;
import co.nevisa.commonlib.Config;
import co.nevisa.commonlib.Storage;
import co.nevisa.commonlib.admob.interfaces.IInitializeCallback;
import co.nevisa.commonlib.admob.models.AdmobKeys;
import co.nevisa.commonlib.admob.models.CountItem;

public class AdmobBaseClass {
    private int attemptToFail;

    public int getAttemptToFail() {
        return attemptToFail;
    }

    public AdmobBaseClass() {
    }

    public interface IRewardCallback extends OnUserEarnedRewardListener {
        void onFail(Object object);
    }

    public boolean loadSingleNative() {
        return Storage.loadSingleNativeAd();
    }
    public void loadSingleNative(boolean status) {
        Storage.loadSingleNativeAd(status);
    }
    private boolean keysExist = true;
    protected final String TAG = Config.TAG + "ac";
    private Context context;

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

    private AdmobKeys keys;

    public AdmobKeys getKeys() {
        if (keys == null) {
            init();
        }
        return keys;
    }

    public void saveKeys(String data) {
        Log.i(TAG, "saveKeys: " + data);
        Storage.admobKeys(data);
        if (data != null) {
            init();
        }
    }

    protected ArrayList<CountItem> getItems(String name) {
        ArrayList<CountItem> ci = new ArrayList<>();
        String s = Storage.admobTargets(name);
        Log.i(TAG, "getItems> " + name + ":" + s);
        if (s.trim().length() <= 0 || s.equals("null")) {
            Log.e(TAG, "getItems: is null :" + name);
            return ci;
        }

        Type listType = new TypeToken<ArrayList<CountItem>>() {
        }.getType();

        ci = new Gson().fromJson(s, listType);

        if (ci == null) {
            Log.e(TAG, "getItems: error in convert items :" + name);
            ci = new ArrayList<>();
        }

        return ci;
    }

    protected int getCounter(String name) {

        return Storage.admobCounter(name.toLowerCase());
    }

    protected void setCounter(String name, int value) {
        Storage.admobCounter(value, name.toLowerCase());
    }

    public void retryOnFail(int attempt) {
        Storage.admobRetryOnFail(attempt);
    }

    public void preServe(boolean admob_serve_on_shown) {
        Storage.admobPreServe(admob_serve_on_shown);
    }

    private void init() {
        keysExist = true;
        JSONObject admob_keys;

        attemptToFail = Storage.admobRetryOnFail();
        try {
            if (BuildVars.DEBUG_VERSION) {
                admob_keys = new JSONObject()
                        .put("app", "ca-app-pub-3940256099942544~3347511713")
                        .put("app_open", "ca-app-pub-3940256099942544/3419835294")
                        .put("interstitial", "ca-app-pub-3940256099942544/1033173712")
                        .put("rewarded_interstitial", "ca-app-pub-3940256099942544/5354046379")
                        .put("banner", "ca-app-pub-3940256099942544/6300978111")
                        .put("reward", "ca-app-pub-3940256099942544/5224354917")
                        .put("native", "ca-app-pub-3940256099942544/2247696110")
                        .put("native_video", "ca-app-pub-3940256099942544/1044960115");

            } else {
                String json = Storage.admobKeys();
                if (json.isEmpty()) {
                    Log.e(TAG, "AdmobController > init > saved json is null or empty!");
                    if (keys == null) {
                        keysExist = false;
                        keys = new AdmobKeys();
                    }
                    return;
                }
                admob_keys = new JSONObject(json);
            }
            Gson gson = new Gson();
            keys = gson.fromJson(admob_keys.toString(), AdmobKeys.class);
            if (BuildVars.DEBUG_VERSION) {
                Log.i(TAG, "AdmobController > init > keys:" + new Gson().toJson(keys));
            }

            show_admob = Storage.showAdmob();

        } catch (JSONException e) {
            Log.e(TAG, "admobKeys error: ", e);
            if (keys == null) {
                keysExist = false;
                keys = new AdmobKeys();
            }
        }

    }

    void init(Context activity, IInitializeCallback iCallback) {
        context = activity;
        Storage.init(context);
        iCallback.before();

        try {
            ApplicationInfo ai = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            Bundle bundle = ai.metaData;
            String myApiKey = bundle.getString("com.google.android.gms.ads.APPLICATION_ID");

            ai.metaData.putString("com.google.android.gms.ads.APPLICATION_ID", getKeys().getApp_id());


            Log.i(TAG, "initAdmob: new mode");
            MobileAds.initialize(activity, initializationStatus -> {

                iCallback.onComplete();

                Log.i(TAG, "AdmobController > initAdmob > initialize successfully :)");
            });

            if (BuildVars.DEBUG_VERSION) {
                Log.i(TAG, "initAdmobKey: Manifest Admob APPLICATION_ID:" + myApiKey);

                String apiKey = bundle.getString("com.google.android.gms.ads.APPLICATION_ID");
                Log.i(TAG, "initAdmobKey: Changed Admob APPLICATION_ID:" + apiKey);


                if (Config.getAdConfig().isTestDevice()) {
                    Log.i(TAG, "init: setup test device.");
                    List<String> testDeviceIds = Arrays.asList(Config.getAdConfig().getTestDeviceIds(), AdRequest.DEVICE_ID_EMULATOR);
                    RequestConfiguration configuration =
                            new RequestConfiguration.Builder().setTestDeviceIds(testDeviceIds).build();
                    MobileAds.setRequestConfiguration(configuration);
                }
            }

        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "initAdmob: ", e);
        }


    }

    //endregion

    //region Active Global Settings
    private boolean show_admob;

    public void setShowAdmob(boolean status) {
        Storage.showAdmob(status);
        show_admob = status;
    }

    public boolean getShowAdmob() {
        if (!keysExist) {
            Log.i(TAG, "getShowAdmob > keys is empty!");
            return false;
        }
/*
        long cache = Storage.turnOffAdsTime();//some of users off admob with sent telegram messeges to hes contact
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
