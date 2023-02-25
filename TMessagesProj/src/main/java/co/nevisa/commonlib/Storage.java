package co.nevisa.commonlib;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

public class Storage {
    //region init
    private static SharedPreferences pref;
    private static SharedPreferences.Editor editor;
    @SuppressLint("StaticFieldLeak")
    private static Context mContext;

    public static SharedPreferences.Editor getEditor() {
        init();
        return editor;
    }

    public static SharedPreferences getPref() {
        init();
        return pref;
    }

    public static void init(Context _context) {
        mContext = _context;
    }

    public static void init() {
        if (pref == null) {
            if (mContext == null) {
                mContext = AdmobApplicationLoader.getContext();
            }
            pref = mContext.getSharedPreferences(mContext.getPackageName() + "_admob" + (BuildVars.DEBUG_VERSION ? "_debug" : ""), 0);
            editor = pref.edit();
        }
    }

    private static void remove(String key) {
        getEditor().remove(key);
        getEditor().commit();
    }

    private static void putInt(String key, int value) {
        getEditor().putInt(key, value);
        getEditor().commit();
    }

    private static int getInt(String key, int def) {
        return getPref().getInt(key, def);
    }

    private static void putLong(String key, int value) {
        getEditor().putLong(key, value);
        getEditor().commit();
    }

    private static Long getLong(String key, long def) {
        return getPref().getLong(key, def);
    }

    private static void putString(String key, String value) {
        getEditor().putString(key, value);
        getEditor().commit();
    }

    private static String getString(String key, String def) {
        return getPref().getString(key, def);
    }

    private static void putBoolean(String key, boolean status) {
        getEditor().putBoolean(key, status);
        getEditor().commit();
    }

    private static boolean getBoolean(String key, boolean def) {
        return getPref().getBoolean(key, def);
    }


    //endregion

    public static void showAdmob(Boolean status) {
        putBoolean("showAdmob", status);
    }

    public static boolean showAdmob() {
        return getPref().getBoolean("showAdmob", true);
    }

    public static void admobKeys(String data) {
        if (data == null) {
            remove("admobKeys");
        } else {
            putString("admobKeys", data);
        }
    }

    public static String admobKeys() {
        return getString("admobKeys", "");
    }
    //endregion

    public static void admobCounter(int value, String name) {
        putInt("admobCounter_" + name, value);
    }

    public static int admobCounter(String name) {
        return getInt("admobCounter_" + name, 0);
    }

    public static void admobTargets(String name, String json) {
        putString("admobTargets_" + name, json);
    }

    public static String admobTargets(String name) {
        return getString("admobTargets_" + name, "");
    }

    public static void admobRetryOnFail(int attempt) {
        putInt("admobRetryOnFail", attempt);
    }

    public static int admobRetryOnFail() {
        return getInt("admobRetryOnFail", 2);
    }


    public static void setNewVersionInfo(String result) {
        putString("setNewVersionInfo", result);
    }

    public static String getNewVersionInfo() {
        return getString("setNewVersionInfo", "");
    }

    public static void admobPreServe(boolean status) {
        putBoolean("admobPreServe", status);
    }

    public static boolean admobPreServe() {
        return getBoolean("admobPreServe", false);
    }


    //region Cache

    public static void cache(String key, long timeInMillis) {
        if (timeInMillis == 0 && is(key)) {
            editor.remove("cache_" + key);
        } else {
            editor.putLong("cache_" + key, timeInMillis);
        }
        editor.commit();
    }

    public static long cache(String key) {
        return getLong("cache_" + key, 0);
    }

    public static boolean is(String key) {
        return getPref().contains(key);
    }

    public static void cacheStatus(boolean status) {
        putBoolean("cacheStatus", status);
    }

    public static boolean cacheStatus() {
        return getBoolean("cacheStatus", Config.getBaseConfig().volleyDataCacheStatus());
    }


    public static void cacheData(String key, String json) {
        if (json.isEmpty() && is(key)) {
            remove("cacheData_" + key);
        } else {
            putString("cacheData_" + key, json);
        }
    }

    public static String cacheData(String key) {
        return getString("cacheData_" + key, "");
    }


    //endregion

    public static void loadSingleNativeAd(boolean status) {
        putBoolean("loadSingleNativeAd", status);
    }

    public static boolean loadSingleNativeAd() {
        return getBoolean("loadSingleNativeAd", Config.getAdConfig().isLoadSingleNative());
    }

    public static void admobNativeRefreshTime(int minutes) {
        putInt("admobNativeRefreshTime", minutes);
    }

    public static int admobNativeRefreshTime() {
        return getInt("admobNativeRefreshTime", Config.getAdConfig().nativeRefreshTime());
    }

}
