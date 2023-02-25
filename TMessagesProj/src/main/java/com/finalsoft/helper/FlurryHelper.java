package com.finalsoft.helper;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.finalsoft.Config;
import com.finalsoft.SharedStorage;
import com.finalsoft.controller.PromoController;
import com.finalsoft.proxy.Communication;
import com.finalsoft.proxy.ProxyController;
import com.flurry.android.FlurryConfig;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.SharedConfig;

import co.nevisa.commonlib.admob.AdmobController;
import co.nevisa.commonlib.flurry.IFlurryCallback;

public class FlurryHelper extends co.nevisa.commonlib.flurry.FlurryHelper implements IFlurryCallback {
    private static final String TAG = Config.TAG + "fh";
    private Context context;

    public void initialize(Context context) {
        this.context = context;
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
        super.initialize(flurryAppId,this);
    }
    @Override
    public void onFetched(boolean isCache, @NonNull FlurryConfig mFlurryConfig) {

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
    private void initUrls(FlurryConfig mFlurryConfig) {
        String faqUrl = mFlurryConfig.getString("url_faq", "");
        SharedStorage.urls(SharedStorage.UrlType.FAQ, faqUrl);

        String privacyUrl = mFlurryConfig.getString("url_privacy", "");
        SharedStorage.urls(SharedStorage.UrlType.PRIVACY, privacyUrl);
    }

    private void initAdmob(FlurryConfig mFlurryConfig) {
        AdmobController admobController = AdmobController.getInstance();
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
