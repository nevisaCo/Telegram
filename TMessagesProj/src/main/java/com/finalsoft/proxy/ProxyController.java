package com.finalsoft.proxy;

import android.content.SharedPreferences;
import android.os.SystemClock;
import android.util.Log;

import com.finalsoft.Config;
import com.finalsoft.SharedStorage;
import com.finalsoft.helper.ca;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.ConnectionsManager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Random;

public class ProxyController {
    private static final String TAG = Config.TAG + "proxy";
    private long cache = 0;
    private boolean send = true;

    private static ProxyController proxyController;

    public static ProxyController getInstance() {
        if (proxyController == null) {
            proxyController = new ProxyController();
        }
        return proxyController;
    }

    //region Add
    public void add(JSONArray jsonArray, boolean myApi, String from) {
        if (!SharedStorage.proxyCustomStatus()){
            Log.i(TAG, "change: proxy custom status is false!");
            return;
        }

        try {
            clearInactiveProxies();

            clearOldProxies();


            for (int i = 0; i < jsonArray.length(); i++) {
                try {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    if (!myApi) {
                        jsonObject.put("encrypt", false);
                        jsonObject.put("l", true);
                        jsonObject.put("n", "Server_" + jsonObject.getInt("p"));
                    }
                    add(jsonObject);
                } catch (JSONException e) {
                    Log.e(TAG, "add1 > JSONException:", e);
                }
            }
            if (jsonArray.length() > 0) {
                if (SharedConfig.currentProxy == null || !SharedConfig.currentProxy.available) {
                    change("proxy > add1 + , " + from, true);
                }
                checkProxyList(param -> {
                    Log.i(TAG, "add: checkProxyList exec and callback returned:");
                    if (SharedConfig.currentProxy == null || !SharedConfig.currentProxy.available) {
                        change("proxy > add2 + , " + from, true);
                        Log.i(TAG, "add: change called , param " + param);
                    } else {
                        Log.i(TAG, "add > current proxy is active. , no need to change");
                    }
                });
                NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.proxySettingsChanged);
            }
        } catch (Exception e) {
            Log.e(TAG, "add: ", e);
        }
    }

    public void add(JSONObject o) {
        if (!SharedStorage.proxyCustomStatus()){
            Log.i(TAG, "change: proxy custom status is false!");
            return;
        }

        try {
            if (BuildVars.DEBUG_VERSION)
                Log.i(TAG, "add: " + o.toString());

            String address = "";
            String password = "";
            String secret = "";
            boolean encryptError = false;
            boolean isEncrypt = true;
            if ((o.has("encrypt") && !o.getBoolean("encrypt"))) {
                isEncrypt = false;
            }
            Log.i(TAG, "add: isEncrypt:" + isEncrypt);
            try {
                if (isEncrypt) {
                    address = ca.get(o.getString("a"), ApplicationLoader.APP_ID);
                    password = (o.has("pw") && !o.getString("pw").isEmpty()) ? ca.get(o.getString("pw"), ApplicationLoader.APP_ID) : "";
                    secret = (o.has("s") && !o.getString("s").isEmpty()) ? ca.get(o.getString("s"), ApplicationLoader.APP_ID) : "";
                }
            } catch (Exception e) {
                encryptError = true;
            }

            if (encryptError || !isEncrypt) {
                address = o.getString("a");
                password = (o.has("pw") && !o.getString("pw").isEmpty()) ? o.getString("pw") : "";
                secret = (o.has("s") && !o.getString("s").isEmpty()) ? o.getString("s") : "";
            }

            if (address.isEmpty()) {
                Log.i(TAG, "add: a is empty , returned!");
                return;
            }

            int port = o.has("p") ? o.getInt("p") : 1080;
            String user = o.has("u") ? o.getString("u") : "";
            boolean lock = o.has("l") && o.getBoolean("l");// l = limit boolean
            boolean show_sponsor = (o.has("ss") && o.getBoolean("ss")) || (o.has("sponser") && o.getBoolean("sponser"));
            int points = o.has("pt") ? o.getInt("pt") : 0;
            String name = o.has("n") ? o.getString("n") : "";
            Log.d(TAG, "add: ------------------------------lock" + lock);
            SharedConfig.addProxy(new SharedConfig.ProxyInfo(address, port, user, password, secret, lock, show_sponsor, points, name));
            Log.d(TAG, "added successful!");

        } catch (JSONException e) {
            Log.e(TAG, "add > JSONException:", e);
        } catch (Exception e) {
            Log.e(TAG, "add > Exception:", e);
        }
    }

    //endregion

    public void increaseCounter() {
        try {
            int r0 = SharedStorage.proxyRefreshCountDown(0) + 1;
            int r1 = SharedStorage.proxyRefreshCountDown(1);
            SharedStorage.proxyRefreshCountDown(0, r0);
            if (r0 >= r1) {
                Communication.getInstance().GetProxies("proxy > increaseCounter");
                Log.i(TAG, "increaseCounter: refresh proxy");
            }
            Log.i(TAG, "increaseCounter: r0:" + r0 + " , r1:" + r1);
        } catch (Exception e) {
            Log.e(TAG, "increaseCounter: ", e);
        }
    }

    private void clearOldProxies() {
        if (SharedConfig.proxyList.size() == 0) {
            return;
        }
        if (SharedStorage.proxyClearPerRefresh()){
            SharedConfig.proxyList.clear();
            SharedConfig.currentProxy = null;
        }
    }

    public void clearInactiveProxies() {
        try {
            if (SharedConfig.proxyList.size() == 0) {
                return;
            }
            
            //select inactive proxies
            ArrayList<SharedConfig.ProxyInfo> proxyList = new ArrayList<>();
            for (SharedConfig.ProxyInfo p : SharedConfig.proxyList) {
                if (!p.available) {
                    proxyList.add(p);
                }
            }

            //delete inactive proxies from share config
            for (SharedConfig.ProxyInfo p : proxyList) {
                SharedConfig.deleteProxy(p);
            }

            //if inactive list not empty , that is mean clear some inactive proxies , so must refresh proxy list round app
            if (proxyList.size() > 0) {
                NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.proxySettingsChanged);
            }
        } catch (Exception e) {
            Log.e(TAG, "clearInactiveProxies: ", e);
        }
    }

    //region Change
    public void change(String from) {
        change(from, false);
    }

    public void change(String from, boolean force) {
        if (!SharedStorage.proxyCustomStatus()){
            Log.i(TAG, "change: proxy custom status is false!");
            return;
        }
        Log.i(TAG, "change: from:" + from);
        //region cache
//        boolean connected = ConnectionsManager.getInstance(UserConfig.selectedAccount).getConnectionState()== ConnectionsManager.ConnectionStateConnected;
        if (!force) {
            Calendar calendar = Calendar.getInstance();
            if (cache > calendar.getTimeInMillis()) {
                Log.i(TAG, "change: cached  > returned");
                SharedStorage.proxyRefreshCountDown(0, SharedStorage.proxyRefreshCountDown(0) - 1);
                return;
            }
            calendar.add(Calendar.SECOND, 3);
            cache = calendar.getTimeInMillis();
        }

        //endregion

        if (SharedConfig.proxyList.size() == 0) {
            Communication.getInstance().GetProxies("proxy > change");
            return;
        }
        //region By Ping
        boolean succeeded = changeByPing();

        if (succeeded) {
            Log.i(TAG, "change by ping succeeded!");
            return;
        }
        //endregion

        //region Random
        Log.i(TAG, "change by ping fail! , random exec!");
        try {
            Random r = new Random();
            int i = r.nextInt(SharedConfig.proxyList.size());
            SharedConfig.ProxyInfo p = SharedConfig.proxyList.get(i);
            setProxy(p);
            Log.i(TAG, "change > random> i:" + i);
        } catch (Exception e) {
            Log.e(TAG, "change > random: ", e);
        }
        //endregion


    }

    private boolean changeByPing() {
        try {
            Log.i(TAG, "changeByPing exec.");
            Collections.sort(SharedConfig.proxyList, (t, t1) -> (t1.ping > t.ping) ? -1 : 1);

            for (SharedConfig.ProxyInfo proxy : SharedConfig.proxyList) {
                boolean currentP = SharedConfig.currentProxy != null && SharedConfig.currentProxy == proxy;
                if (proxy.available && !currentP) {
                    if (BuildVars.DEBUG_VERSION) {
                        Log.i(TAG, "change selected ip:" + proxy.address + "\tping:" + proxy.ping + "\tavail:" + proxy.available);
                    }
                    setProxy(proxy);
                    return true;
                }
            }

        } catch (Exception exception) {
            Log.e(TAG, "changeByPing: ", exception);
        }
        return false;
    }
    //endregion

    private void setProxy(SharedConfig.ProxyInfo proxy) {
        try {
            SharedPreferences preferences = MessagesController.getGlobalMainSettings();
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("proxy_ip", proxy.address);
            editor.putString("proxy_pass", proxy.password);
            editor.putString("proxy_user", proxy.username);
            editor.putInt("proxy_port", proxy.port);
            editor.putString("proxy_secret", proxy.secret);
            editor.putBoolean("proxy_limit", proxy.lock);
            editor.putBoolean("proxy_show_sponsor", proxy.show_sponsor);
            editor.putInt("proxy_points", proxy.points);
            editor.putString("proxy_name", proxy.name);
            editor.putBoolean("proxy_enabled", true);

            ConnectionsManager.setProxySettings(true, proxy.address, proxy.port, proxy.username, proxy.password, proxy.secret);
            editor.apply();
            SharedConfig.currentProxy = proxy;
            NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.proxySettingsChanged);
            if (BuildVars.DEBUG_VERSION) {
                Log.i(TAG, "change: seted:" + proxy.name);
            }
        } catch (Exception e) {
            Log.e(TAG, "setProxy: ", e);
        }
    }

    //region Check
    public void checkProxyList(MessagesStorage.BooleanCallback callback) {
        checkProxyList(callback, false);
    }

    public void checkProxyList(MessagesStorage.BooleanCallback callback, boolean set) {
        Log.i(TAG, "checkProxyList exec.");
        try {
            send = true;
            for (int a = 0, count = SharedConfig.proxyList.size(); a < count; a++) {
                final SharedConfig.ProxyInfo proxyInfo = SharedConfig.proxyList.get(a);
                if (BuildVars.DEBUG_VERSION) {
                    Log.i(TAG, "checkProxyList: " + proxyInfo.address);
                }

                if (proxyInfo.checking
                        || SystemClock.elapsedRealtime() - proxyInfo.availableCheckTime < 2 * 60 * 1000) {
                    Log.i(TAG, "checkProxyList: return, already checked!");
                    if (callback != null) {
                        callback.run(false);
                        return;
                    }
                    continue;
                }
                Log.i(TAG, "checkProxyList > pinging...");
                proxyInfo.checking = true;
                proxyInfo.proxyCheckPingId = ConnectionsManager.getInstance(UserConfig.selectedAccount)
                        .checkProxy(proxyInfo.address, proxyInfo.port, proxyInfo.username, proxyInfo.password,
                                proxyInfo.secret, time -> AndroidUtilities.runOnUIThread(() -> {
                                    if (BuildVars.DEBUG_VERSION) {
                                        Log.i(TAG, "checkProxyList > checked: " + proxyInfo.address);
                                    }
                                    proxyInfo.availableCheckTime = SystemClock.elapsedRealtime();
                                    proxyInfo.checking = false;
                                    if (time == -1) {
                                        proxyInfo.available = false;
                                        proxyInfo.ping = 0;
                                    } else {
                                        proxyInfo.ping = time;
                                        proxyInfo.available = true;
                                        if (send && callback != null) {
                                            send = false;
                                            Log.i(TAG, "checkProxyList: return callback");
                                            callback.run(true);

                                        }
                                    }
                                    NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.proxyCheckDone, proxyInfo);
                                })
                        );
            }
        } catch (Exception e) {
            Log.e(TAG, "checkProxyList: ", e);
        }
    }
    //endregion

}
