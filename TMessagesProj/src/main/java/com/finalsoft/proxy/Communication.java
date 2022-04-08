package com.finalsoft.proxy;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.finalsoft.Config;
import com.finalsoft.SharedStorage;
import com.finalsoft.VolleySingleton;
import com.finalsoft.helper.CryptLib;
import com.finalsoft.helper.ca;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BuildVars;

import java.util.Calendar;

public class Communication extends Application {
    private static final String TAG = Config.TAG + "cc";
    private Context context;
    private boolean getProxies = false;
    private String alreadyFrom = "";
    @SuppressLint("StaticFieldLeak")
    private static Communication communication;

    public static Communication getInstance() {
        if (communication == null) {
            communication = new Communication(ApplicationLoader.applicationContext);
        }
        return communication;
    }

    public Communication(Context context) {
        this.context = context;
    }

    public void GetRemoteConfiguration() {
        String url = SharedStorage.ApiUrl();

        try {
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url,
                    fillUserData(),
                    response -> {
                        try {

                            boolean status = response.getBoolean(BuildVars.RESPONSE_STATUS);
                            String data = response.getString(BuildVars.RESPONSE_DATA);
                            if (status) {
                                final String key = getDeviceUniqueId();// getPackageName() + Conf.RSA_PUSH;
                                String decrypt = ca.get(data, key);
                                JSONObject jsonObject = new JSONObject(decrypt);
                                //                            boolean permission = jsonObject.getBoolean("Permission");

                                String dataCheck = jsonObject.getString("Data");

                                String rsa = ca.get(dataCheck, key);
                                if (rsa.equals("Conf.RSA")) {

                                } else {
                                    Log.e(TAG,
                                            "Communication > GetRemoteConfiguration > onResponse: rsa not valid");
                                }
                                //                            boolean useProxy = jsonObject.getBoolean("UseProxy");

                                return;
                            } else {
                                Log.i(TAG,
                                        "Communication > GetRemoteConfiguration > onResponse: status false, data:"
                                                + data);
                                if (data.contains("request not secure!")) {
                                    Toast.makeText(context, "اپ شما غیر مجاز است!", Toast.LENGTH_LONG).show();
                                    Log.e(TAG, "Communication > GetRemoteConfiguration > permission is refused :)");
                                    android.os.Process.killProcess(android.os.Process.myPid());
                                    System.exit(1);
                                } else {
                                    Log.i(TAG, "Communication > GetRemoteConfiguration >onResponse data : " + data);
                                }
                            }
                        } catch (JSONException e) {
                            Log.e(TAG, "Communication > GetRemoteConfiguration > onResponse: JSONException :", e);
                        } catch (Exception e) {
                            Log.e(TAG, "Communication > GetRemoteConfiguration > onResponse: Exception:", e);
                        }
                    },
                    ex -> Log.e(TAG,
                            "Communication > GetRemoteConfiguration > onErrorResponse:" + ex.getMessage()));

            request.setRetryPolicy(new DefaultRetryPolicy(
                    15000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            request.setShouldCache(false);
            VolleySingleton.getInstance(context).addToRequestQueue(request);
        } catch (Exception e) {
            Log.e(TAG, "GetRemoteConfiguration: volley error:", e);
        }
    }

    public void GetProxies(String from) {
        GetProxies(false, from);
    }

    public void GetProxies(boolean refresh, String from) {
        if (!SharedStorage.proxyCustomStatus()) {
            Log.i(TAG, "GetProxies: proxy custom status is false!");
            return;
        }

        if (getProxies) {
            Log.i(TAG, "GetProxies > " + alreadyFrom + " called already! , wait for response.");
            return;
        }

        getProxies = true;
        alreadyFrom = from;
        String uri = SharedStorage.ApiUrl();
        boolean myApi = uri.endsWith("api") || uri.endsWith("api/");
        String url = myApi ? uri + "/proxies" : uri;
        if (BuildVars.DEBUG_VERSION)
            Log.i(TAG, "GetProxies: exec! : from : " + from + ", url:" + url);

        try {
            JsonObjectRequest request = new JsonObjectRequest(myApi ? Request.Method.POST : Request.Method.GET, url,
                    myApi ? fillUserData() : null,
                    response -> {
                        try {
                            if (BuildVars.DEBUG_VERSION) {
                                Log.i(TAG, "GetProxies: response:" + response.toString());
                            }
                            boolean status = response.has(BuildVars.RESPONSE_STATUS) && response.getBoolean(BuildVars.RESPONSE_STATUS);
                            String data = response.getString(BuildVars.RESPONSE_DATA);
                            if (status) {
                                if (!myApi) {
                                    String key = data.substring(data.length() - 16);
                                    data = data.replace(key, "");
                                    data = new CryptLib().decryptCipherTextWithRandomIV(data, key);

                                    if (BuildVars.DEBUG_VERSION) {
                                        Log.i(TAG, "GetProxies > key: " + key);
                                        Log.i(TAG, "GetProxies ?> data: " + data);
                                        Log.i(TAG, "GetProxies > list: " + data);
                                    }
                                }


                                JSONArray jsonArray = new JSONArray(data);

                                if (jsonArray.length() > 0) {
                                    ProxyController.getInstance().add(jsonArray, myApi, "getProxies");
                                    if (refresh) {
                                        SharedStorage.rewards(SharedStorage.rewards() - ApplicationLoader.PROXY_REFRESH_COST);
                                    }
                                    SharedStorage.proxyRefreshCountDown(0, 0);

                                    setLastUpdateTime();
                                }

                            } else {
                                Toast.makeText(context, "fail!", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            if (BuildVars.DEBUG_VERSION)
                                Log.e(TAG, "Communication > GetProxies> JSONException: ", e);
                        } catch (Exception e) {
                            if (BuildVars.DEBUG_VERSION)
                                Log.e(TAG, "Communication > GetProxies > Exception: ", e);
                        }
                        getProxies = false;
                    },
                    ex -> {
                        if (BuildVars.DEBUG_VERSION) {
                            Log.e(TAG, "Communication > GetProxies > onErrorResponse:" + new Gson().toJson(ex));
                        }
                        getProxies = false;

                    });

            request.setRetryPolicy(new DefaultRetryPolicy(
                    15000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            request.setShouldCache(false);
            VolleySingleton.getInstance(context).addToRequestQueue(request);
        } catch (Exception e) {
            if (BuildVars.DEBUG_VERSION) Log.e(TAG, "GetProxies: volley error:", e);
            getProxies = false;
        }

        new Handler().postDelayed(() -> getProxies = false, 10000);
    }

    private void setLastUpdateTime() {
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.MINUTE, SharedStorage.getProxiesCacheTime());
            SharedStorage.getProxiesTime(calendar.getTimeInMillis());
        } catch (Exception e) {
            Log.e(TAG, "setLastUpdateTime: ", e);
        }
    }

    private JSONObject fillUserData() {
        UserData userData = new UserData();
        userData.deviceId = getDeviceUniqueId();
        userData.packageName = context.getPackageName();

        try {
            ApplicationInfo ai = context.getPackageManager()
                    .getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            userData.uniqueId = (String) ai.metaData.get("com.google.android.gms.ads.APPLICATION_ID");
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return userData.getJson();
    }

    //region private functions

    private String getDeviceUniqueId() {
        @SuppressLint("HardwareIds")
        String s = String.format("%s",
                Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID));
        return s;
    }

    //endregion
}
