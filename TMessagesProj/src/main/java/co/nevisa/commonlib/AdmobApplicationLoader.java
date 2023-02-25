package co.nevisa.commonlib;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.multidex.MultiDexApplication;

import co.nevisa.commonlib.admob.AdmobController;
import co.nevisa.commonlib.config.AdConfig;
import co.nevisa.commonlib.config.BaseConfig;

public class AdmobApplicationLoader extends MultiDexApplication {

    @SuppressLint("StaticFieldLeak")
    protected static Context mContext;
    private SharedPreferences.Editor editor;
    private static SharedPreferences pref;
    public static volatile Handler applicationHandler;

    public static Context getContext() {
        return mContext;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        AdmobApplicationLoader.mContext = this;
        applicationHandler = new Handler(getContext().getMainLooper());

        //the open-app ad need application loader context to open
        AdmobController.getInstance().initOpenAppAd(this);
    }


    /**
     * @param flurryId flurry app id
     * @param adConfig set admob config
     */

    public void init(@NonNull BaseConfig baseConfig, @NonNull AdConfig adConfig, String flurryId) {
        Config.setBaseConfig(baseConfig);

        Config.setFlurryAppId(flurryId);

        Config.setAdConfig(adConfig);
    }



}
