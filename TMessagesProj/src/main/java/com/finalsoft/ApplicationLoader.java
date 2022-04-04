package com.finalsoft;

import android.app.Application;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.util.Log;

import com.finalsoft.controller.AdmobController;
import com.finalsoft.controller.HiddenController;
import com.finalsoft.helper.FireBaseHelper;
import com.finalsoft.helper.FlurryHelper;
import com.finalsoft.helper.ca;
import com.finalsoft.proxy.Communication;

import org.telegram.messenger.BuildConfig;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.UserConfig;

import java.util.Calendar;
import java.util.Locale;

public class ApplicationLoader extends Application {
    public static final int ADMOB_PER_DIALOG = 20;
    public static final int ADMOB_PER_MESSAGE = 10;
    public static final int DOWNLOAD_MANAGER_COST = 2;
    public static final int PROXY_REFRESH_COST = 10;
    public static final int V2T_COST = 1;
    public static final int IMAGE_EDITOR_COST = 2;

    public static final int INTERSTITIAL_REWARDS = 4;
    public static final int VIDEO_REWARDS = 10;
    public static final boolean GLOBAL_AUTO_DOWNLOAD = false;
    public static final boolean AUTO_NIGHT = false;

    protected static final String TAG = Config.TAG + "al";
    public static boolean ACTIONBAR_DONATE = false;

    public static String API_URL = "";
    public static String RSA = "";
    public static String APP_ID = "";
    public static boolean Lock_Mode = false;

    @Override
    public void onCreate() {

//        initLocal(this);

        SharedStorage.init(this);

        AdmobController.getInstance().init();

        HiddenController.getInstance().init();

        initCustomData((org.telegram.messenger.ApplicationLoader) this);

        //        CheshmakPlus.with(this);

        super.onCreate();


        com.finalsoft.firebase.FireBaseLog.init(this);


        new FlurryHelper().initialize(this);

        new FireBaseHelper().initialize((org.telegram.messenger.ApplicationLoader) this);

        getProxy(this);
 /*       offStatus = SharedStorage.turnOff();
        Log.i(TAG, "onCreate: isNetworkOnline offStatus:" + offStatus);*/

    }

    private void initLocal(ApplicationLoader applicationLoader) {
        String languageToLoad = "fa"; // your language
        Locale locale = new Locale(languageToLoad);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());
    }

    private void getProxy(ApplicationLoader applicationLoader) {
        if (!SharedStorage.proxyServer()) {
            return;
        }

        long now = Calendar.getInstance().getTimeInMillis();
        long lastUpdate = SharedStorage.getProxiesTime();
        if (now >= lastUpdate) {
            Log.i(TAG, "getProxy: geting...");
            Communication.getInstance().GetProxies("Application loader");
            return;
        }
        Log.i(TAG, "getProxy: cached > aborted!");
    }

    private void initCustomData(org.telegram.messenger.ApplicationLoader applicationLoader) {
        try {
            ApplicationInfo ai = applicationLoader.getPackageManager()
                    .getApplicationInfo(applicationLoader.getPackageName(), PackageManager.GET_META_DATA);
            APP_ID = (String) ai.metaData.get("REPOSITORY_ID");
            RSA = ai.metaData.get("REPOSITORY_RSA") + "";
            API_URL = ca.get(RSA, APP_ID + "/apiService/v1/pass/123456");

            if (BuildVars.DEBUG_VERSION) {
                String ad = ai.metaData.get("com.google.android.gms.ads.APPLICATION_ID") + "";
                String map = ai.metaData.get("com.google.android.maps.v2.API_KEY") + "";
                Log.i(TAG, "initCustomData > \nREPOSITORY_ID:" + APP_ID
                        + "\nREPOSITORY_RSA:"+RSA
                        + "\ncom.google.android.gms.ads.APPLICATION_ID:" + ad
                        + "\ncom.google.android.maps.v2.API_KEY:" + map
                        + "\nAPI_URL:" + API_URL
                );
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
