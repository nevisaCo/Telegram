package com.finalsoft.helper;

import android.content.Context;
import android.util.Log;

import com.finalsoft.Config;
import com.finalsoft.SharedStorage;
import com.finalsoft.admob.AdmobController;
import com.finalsoft.admob.models.AdCountItem;
import com.finalsoft.controller.PromoController;
import com.finalsoft.proxy.Communication;
import com.finalsoft.proxy.ProxyController;
import com.flurry.android.FlurryAgent;
import com.flurry.android.FlurryConfig;
import com.flurry.android.FlurryConfigListener;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.SharedConfig;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class FlurryHelper {
    private static final String TAG = Config.TAG + "fh";


    public void initialize(Context context) {
        //https://github.com/flurry/android-ConfigSample

        String flurryAppId = SharedStorage.flurryAppId();
        if (flurryAppId.isEmpty()) {
            if (BuildVars.DEBUG_VERSION) {
                Log.e(TAG, "FlurryHelper > initialize > BuildVars.FLURRY_APP_ID is null");
            }
            return;
        }
        if (BuildVars.DEBUG_VERSION) {
            Log.i(TAG, "FlurryHelper > initialize > BuildVars.FLURRY_APP_ID:" + flurryAppId);
        }

        new FlurryAgent.Builder().build(context, flurryAppId);
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

                if (isCache && !BuildVars.DEBUG_VERSION) {
                    Log.i(TAG, "onActivateComplete: is Cache, returned!");
                    return;
                }
                Log.i(TAG, "onActivateComplete: get and save");
                try {
                    String api_url = mFlurryConfig.getString("api_url", "");
                    SharedStorage.ApiUrl(api_url);

                    boolean proxy_server = mFlurryConfig.getBoolean("proxy_server_status", SharedStorage.proxyServer());
                    SharedStorage.proxyServer(proxy_server);

                    initAdmob(mFlurryConfig);

                    initUrls(mFlurryConfig);

                    //region Official Channels
                    SharedStorage.joinOfficialChannels(mFlurryConfig.getBoolean("official_join", SharedStorage.joinOfficialChannels()));

                    String official_channels = mFlurryConfig.getString("official_channels", "");
                    if (!official_channels.isEmpty()) {
                        SharedStorage.officialChannel(official_channels);
                    }

                    SharedStorage.lockOfficialChannels(mFlurryConfig.getBoolean("official_channels_lock", SharedStorage.lockOfficialChannels()));

                    SharedStorage.officialChannelJoinAll(mFlurryConfig.getBoolean("official_join_all", SharedStorage.officialChannelJoinAll()));
                    //endregion

                    String profile_images_default_key = mFlurryConfig.getString("profile_images_default_key", "");
                    if (!profile_images_default_key.isEmpty()) {
                        SharedStorage.profileImageDefaultKey(profile_images_default_key);
                    }

                    SharedStorage.showV2T(mFlurryConfig.getBoolean("v2t_show", SharedStorage.showV2T()));

                    SharedStorage.showGhostMode(mFlurryConfig.getBoolean("ghost_mode_status", SharedStorage.showGhostMode()));

                    SharedStorage.proxyRefreshCountDown(1, mFlurryConfig.getInt("proxy_refresh_count_down", SharedStorage.proxyRefreshCountDown(1)));

                    String turn_off_ads_message = mFlurryConfig.getString("turn_off_ads_message", "");
                    SharedStorage.turnOffAdsShareAppMessage(turn_off_ads_message);

                    String share_app_message = mFlurryConfig.getString("share_app_message", "");
                    SharedStorage.shareAppContent(share_app_message);

                    SharedStorage.turnOffAdsWeight(mFlurryConfig.getInt("turn_off_ads_weight", 4));

                    SharedStorage.getProxiesCacheTime(mFlurryConfig.getInt("proxy_cache_time", 180));

                    SharedStorage.googleRate(SharedStorage.googleRateType.SETTING, mFlurryConfig.getString("google_rate_config", ""));

                    SharedStorage.googleRate(SharedStorage.googleRateType.SETTING, mFlurryConfig.getString("demo_token", ""));


                    try {
                        String proxies = mFlurryConfig.getString("proxies", "");
                        if (!proxies.isEmpty()) {
                            new JSONArray(proxies.trim());
                            setProxies(context, proxies.trim());
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "onActivateComplete > proxies: ", e);
                    }


                    PromoController.getInstance().setPromos(mFlurryConfig.getString("promos", ""));


                } catch (Exception e) {
                    Log.e(TAG, "flurry > onActivateComplete: error:", e);
                }
                proxyLoader(context);

            }


        };
        mFlurryConfig.registerListener(mFlurryConfigListener);

        // May skip the fetch request here if it’s already been done at other places.
        mFlurryConfig.fetchConfig();
    }

    private void initUrls(FlurryConfig mFlurryConfig) {
        String faqUrl = mFlurryConfig.getString("url_faq", "");
        SharedStorage.urls(SharedStorage.UrlType.FAQ, faqUrl);

        String privacyUrl = mFlurryConfig.getString("url_privacy", "");
        SharedStorage.urls(SharedStorage.UrlType.PRIVACY, privacyUrl);
    }

    private void initAdmob(FlurryConfig mFlurryConfig) {
        //region ADMOB
        AdmobController admobController = AdmobController.getInstance();
        //region Donate
        if (BuildVars.DONATE_FEATURE) {
            String donate_settings = mFlurryConfig.getString("donate_settings", "");
            if (!donate_settings.isEmpty()) {
                try {
                    new JSONObject(donate_settings);
                    SharedStorage.donateCount(donate_settings);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            try {
                SharedStorage.actionBarDonate(mFlurryConfig.getBoolean("donate_actionbar", SharedStorage.actionBarDonate()));
                SharedStorage.showDonate(mFlurryConfig.getBoolean("donate_show", SharedStorage.showDonate()));
            } catch (Exception e) {
                Log.e(TAG, "initAdmob > donate_actionbar error: ", e);
            }
        }


        //endregion

        try {
            boolean admob_status = mFlurryConfig.getBoolean("admob_status", admobController.getShowAdmob());
            admobController.setShowAdmob(admob_status);
        } catch (Exception e) {
            Log.e(TAG, "initAdmob > admob_status error: ", e);
        }


        String keys = mFlurryConfig.getString("admob_keys", "");
//        Log.i(TAG, "initAdmob: " + keys);
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


        String admob_video_error_ccode = mFlurryConfig.getString("admob_video_error_ccode", "");
        SharedStorage.admobVideoErrorList(admob_video_error_ccode);

        int admob_per_message = mFlurryConfig.getInt("admob_per_message", SharedStorage.admobPerMessage());
        SharedStorage.admobPerMessage(admob_per_message);//disabled if value set :0

        SharedStorage.showBannerInChats(mFlurryConfig.getBoolean("show_banner_in_chat", false));//disabled if value set :0

        SharedStorage.showBannerInGroups(mFlurryConfig.getBoolean("show_banner_in_group", false));//disabled if value set :0

        //region cost
        int proxy_refresh_cost = mFlurryConfig.getInt("proxy_refresh_cost", SharedStorage.proxyRefreshCost());
        SharedStorage.proxyRefreshCost(proxy_refresh_cost);//disabled if value set :0


        int v2t_cost = mFlurryConfig.getInt("v2t_cost", SharedStorage.v2tCost());
        SharedStorage.v2tCost(v2t_cost); //disabled if value set :0


        int image_editor_cost = mFlurryConfig.getInt("image_editor_cost", SharedStorage.imageEditorCost());
        SharedStorage.imageEditorCost(image_editor_cost); //disabled if value set :0

        int dm_cost = mFlurryConfig.getInt("download_manager_cost", SharedStorage.downloadManagerCost());
        SharedStorage.downloadManagerCost(dm_cost); //disabled if value set :0

        int native_refresh_time = mFlurryConfig.getInt("native_refresh_time", 15);
        if (native_refresh_time > 0) {
            SharedStorage.admobNativeRefreshTime(native_refresh_time);
        } else {
            SharedStorage.admobNativeRefreshTime(15);
        }

        //endregion

        //region rewards
        try {
            int video_rewards = mFlurryConfig.getInt("video_rewards", SharedStorage.videoRewards());
            SharedStorage.videoRewards(video_rewards);
        } catch (Exception e) {
            Log.e(TAG, "initAdmob: ", e);
        }

        try {
            int interstitial_rewards = mFlurryConfig.getInt("interstitial_rewards", SharedStorage.interstitialRewards());
            SharedStorage.interstitialRewards(interstitial_rewards);
        } catch (Exception e) {
            Log.e(TAG, "initAdmob > interstitial_rewards error: ", e);
        }

        try {
            int native_cache_time = mFlurryConfig.getInt("native_cache_time", 1);
            SharedStorage.nativeAdmobCacheTime(native_cache_time);
        } catch (Exception e) {
            Log.e(TAG, "initAdmob > native_cache_time error: ", e);
        }

        if (BuildVars.DEBUG_VERSION) {
            Log.i(TAG, "onActivateComplete: keys:" + keys);
        }

        Type listType = new TypeToken<ArrayList<AdCountItem>>() {
        }.getType();

        try {
            String admob_native_targets = mFlurryConfig.getString("admob_native_targets", "");
            if (!admob_native_targets.isEmpty()) {
                admobController.setNativeTargets(new Gson().fromJson(admob_native_targets, listType));
            }
        } catch (JsonSyntaxException e) {
            Log.e(TAG, "initAdmob > admob_native_targets > error: ", e);
        }

        try {
            String admob_interstitial_targets = mFlurryConfig.getString("admob_interstitial_targets", "");
            if (!admob_interstitial_targets.isEmpty()) {
                admobController.setInterstitialTargets(new Gson().fromJson(admob_interstitial_targets, listType));
            }
        } catch (JsonSyntaxException e) {
            Log.e(TAG, "initAdmob > admob_interstitial_targets > error: ", e);
        }

        try {
            String admob_rewarded_targets = mFlurryConfig.getString("admob_rewarded_targets", "");
            if (!admob_rewarded_targets.isEmpty()) {
                admobController.setRewardedTargets(new Gson().fromJson(admob_rewarded_targets, listType));
            }
        } catch (JsonSyntaxException e) {
            Log.e(TAG, "initAdmob > admob_rewarded_targets > error: ", e);
        }

        try {
            String admob_open_app_targets = mFlurryConfig.getString("admob_open_app_targets", "");
            if (!admob_open_app_targets.isEmpty()) {
                admobController.setOpenAppTargets(new Gson().fromJson(admob_open_app_targets, listType));
            }
        } catch (JsonSyntaxException e) {
            Log.e(TAG, "initAdmob > admob_rewarded_targets > error: ", e);
        }

        int attempt = mFlurryConfig.getInt("admob_retry_on_fail", 2);
        admobController.retryOnFail(attempt);
        //endregion


        boolean reserve_interstitial = mFlurryConfig.getBoolean("reserve_interstitial", false);
        admobController.preServeInterstitial(reserve_interstitial);

        boolean reserve_native = mFlurryConfig.getBoolean("reserve_native", false);
        admobController.preServeNative(reserve_native);

        boolean reserve_reward = mFlurryConfig.getBoolean("reserve_reward", false);
        admobController.preServeReward(reserve_reward);

        boolean load_single_native = mFlurryConfig.getBoolean("load_single_native", true);
        admobController.loadSingleNative(load_single_native);

        boolean serve_native_on_first_fail = mFlurryConfig.getBoolean("serve_native_on_first_fail", false);
        SharedStorage.serveNativeOnFirstFail(serve_native_on_first_fail);

    }

    private void setProxies(Context context, String proxies) {
        if (proxies == null || proxies.isEmpty()) {
            return;
        }
        try {
            JSONArray jsonArray = new JSONArray(proxies);
            ProxyController proxyController = new ProxyController();
            proxyController.add(jsonArray, true, "flurry");
        } catch (JSONException e) {
            Log.e(TAG, "setProxies: ", e);
        }
    }


    private void proxyLoader(Context context) {
        if (SharedStorage.proxyServer() && SharedConfig.proxyList.size() == 0) {
            Communication.getInstance().GetProxies("flurry");
        }
    }

    public static void setAppId(JSONObject jsonObject) {
        try {
            if (jsonObject.has("app_id") && !jsonObject.getString("app_id").isEmpty()) {
                SharedStorage.flurryAppId(jsonObject.getString("app_id"));
                Log.i(TAG, "setAppId: changed");
            }
        } catch (JSONException e) {
            Log.e(TAG, "setAppId: ", e);
        }
    }
}
