package co.nevisa.commonlib.flurry;

import android.util.Log;

import androidx.annotation.NonNull;

import com.finalsoft.SharedStorage;
import com.flurry.android.FlurryAgent;
import com.flurry.android.FlurryConfig;
import com.flurry.android.FlurryConfigListener;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;

import co.nevisa.commonlib.AdmobApplicationLoader;
import co.nevisa.commonlib.Config;
import co.nevisa.commonlib.Storage;
import co.nevisa.commonlib.admob.AdmobController;
import co.nevisa.commonlib.admob.models.CountItem;

public class FlurryHelper {
    private static final String TAG = Config.TAG + "fh";

    /**
     * @param callback return fetched data as 'FlurryConfig' key value object
     */
    public void initialize(@NonNull IFlurryCallback callback) {
        initialize(Config.getFlurryId(), callback);
    }

    /**
     * @param id       set yor flurry app id , get it from flurry.com panel
     * @param callback return fetched data as 'FlurryConfig' key value object
     */
    public void initialize(String id, @NonNull IFlurryCallback callback) {
        //https://github.com/flurry/android-ConfigSample

        if (id.isEmpty()) {
            if (Config.isDebugMode()) {
                Log.e(TAG, "FlurryHelper > initialize > BuildVars.FLURRY_APP_ID is null");
            }
            throw new RuntimeException("Flurry id is null, please init the 'FlurryHelper' in your application loader and try again.");
        }

        if (Config.isDebugMode()) {
            Log.i(TAG, "FlurryHelper > initialize > BuildVars.FLURRY_APP_ID:" + id);
        }

        new FlurryAgent.Builder().build(AdmobApplicationLoader.getContext(), id);
        FlurryConfig mFlurryConfig = FlurryConfig.getInstance();

        FlurryConfigListener mFlurryConfigListener = new FlurryConfigListener() {
            @Override
            public void onFetchSuccess() {
                mFlurryConfig.activateConfig();
                Log.i(TAG, "onFetchSuccess: ");
            }

            @Override
            public void onFetchNoChange() {
                // Use the Config cached data if available
                Log.i(TAG, "onFetchNoChange: ");
            }

            @Override
            public void onFetchError(boolean isRetrying) {
                // Use the Config cached data if available
                Log.e(TAG, "onFetchError: " + isRetrying);
            }

            @Override
            public void onActivateComplete(boolean isCache) {
                Log.i(TAG, "onActivateComplete > current version: " + mFlurryConfig.getString("edit_version", "null"));
                callback.onFetched(isCache, mFlurryConfig);

                if (isCache && !Config.isDebugMode()) {
                    Log.i(TAG, "onActivateComplete: is Cache, returned!");
                    return;
                }
                Log.i(TAG, "onActivateComplete: get and save data from flurry");

                initAdmob(mFlurryConfig);

                String update = mFlurryConfig.getString("app_update", "");
                Storage.setNewVersionInfo(update);

                boolean volleyDataCacheStatus = mFlurryConfig.getBoolean("cache_status", Config.getBaseConfig().volleyDataCacheStatus());
                Storage.cacheStatus(volleyDataCacheStatus);
            }


        };


        mFlurryConfig.registerListener(mFlurryConfigListener);

        // May skip the fetch request here if it’s already been done at other places.
        mFlurryConfig.fetchConfig();
    }

    private void initAdmob(FlurryConfig mFlurryConfig) {
        AdmobController admobController = AdmobController.getInstance();

        try {
            boolean admob_status = mFlurryConfig.getBoolean("admob_status", admobController.getShowAdmob());
            admobController.setShowAdmob(admob_status);
        } catch (Exception e) {
            Log.e(TAG, "initAdmob > admob_status error: ", e);
        }


        String keys = mFlurryConfig.getString("admob_keys", "");
        if (!keys.isEmpty()) {
            try {
                new JSONObject(keys);
                admobController.saveKeys(keys);
            } catch (JSONException e) {
                Log.e(TAG, "onActivateComplete: ", e);
            }
        } else {
            admobController.saveKeys(null);
        }

        Type listType = new TypeToken<ArrayList<CountItem>>() {
        }.getType();

        //Native targets
        try {
            String admob_native_targets = mFlurryConfig.getString("admob_native_targets", "");
            admobController.setNativeTargets(new Gson().fromJson(admob_native_targets, listType));
        } catch (JsonSyntaxException e) {
            Log.e(TAG, "initAdmob > admob_native_targets > error: ", e);
        }

        //Interstitial Targets
        try {
            String admob_interstitial_targets = mFlurryConfig.getString("admob_interstitial_targets", "");
            admobController.setInterstitialTargets(new Gson().fromJson(admob_interstitial_targets, listType));
        } catch (JsonSyntaxException e) {
            Log.e(TAG, "initAdmob > admob_interstitial_targets > error: ", e);
        }

        //Reward Targets
        try {
            String admob_reward_targets = mFlurryConfig.getString("admob_reward_targets", "");
            admobController.setRewardTargets(new Gson().fromJson(admob_reward_targets, listType));
        } catch (JsonSyntaxException e) {
            Log.e(TAG, "initAdmob > admob_reward_targets > error: ", e);
        }

        //Banner Targets
        try {
            String admob_banner_targets = mFlurryConfig.getString("admob_banner_targets", "");
            admobController.setBannerTargets(new Gson().fromJson(admob_banner_targets, listType));
        } catch (JsonSyntaxException e) {
            Log.e(TAG, "initAdmob > admob_banner_targets > error: ", e);
        }

        //Banner Targets
        try {
            String admob_app_open_targets = mFlurryConfig.getString("admob_app_open_targets", "");
            admobController.setOpenAppTargets(new Gson().fromJson(admob_app_open_targets, listType));
        } catch (JsonSyntaxException e) {
            Log.e(TAG, "initAdmob > admob_open_app_targets > error: ", e);
        }

        int native_refresh_time = mFlurryConfig.getInt("native_refresh_time", 15);
        admobController.nativeRefreshTime(native_refresh_time);

        int attempt = mFlurryConfig.getInt("admob_retry_on_fail", Config.getAdConfig().getRetryOnFail());
        admobController.retryOnFail(attempt);

        boolean load_single_native = mFlurryConfig.getBoolean("load_single_native", Config.getAdConfig().isLoadSingleNative());
        admobController.loadSingleNative(load_single_native);

        boolean admob_serve_on_shown = mFlurryConfig.getBoolean("admob_pre_serve", Config.getAdConfig().isPreServe());
        admobController.preServe(admob_serve_on_shown);

/*        int native_cache_time = mFlurryConfig.getInt("native_cache_time", 1);
        Storage.nativeAdmobCacheTime(native_cache_time);*/

    }


}
