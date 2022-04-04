package com.finalsoft.controller;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;
import android.view.Gravity;
import android.widget.FrameLayout;

import com.finalsoft.Config;
import com.finalsoft.SharedStorage;
import com.finalsoft.models.AdmobKeys;
import com.finalsoft.ui.admob.DialogAddCell;
import com.finalsoft.ui.admob.NativeAddCell;
import com.finalsoft.ui.admob.NativeList;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.formats.NativeAdOptions;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.LaunchActivity;

import java.util.ArrayList;
import java.util.Calendar;

public class AdmobController {
    private static final int DEBUG_COUNT = 20;

    public interface AdmobControllerDelegate {
        public void onResponse();
    }

    private boolean keysExist = true;
    private final String TAG = Config.TAG + "ac";
    Context context;
    //region Instance
    private static AdmobController admobController;

    public static AdmobController getInstance() {
        if (admobController == null) {
            admobController = new AdmobController();
        }
        return admobController;
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
        SharedStorage.admobKeys(data);
        if (data != null) {
            init();
        }
    }

    public void init() {
        keysExist = true;
        JSONObject admob_keys;
        try {
            if (BuildVars.DEBUG_VERSION) {
                admob_keys = new JSONObject()
                        .put("app", "ca-app-pub-3940256099942544~3347511713")
                        .put("interstitial", "ca-app-pub-3940256099942544/1033173712")
                        .put("interstitial_donate", "ca-app-pub-3940256099942544/1033173712")
                        .put("interstitial_reward", "ca-app-pub-3940256099942544/1033173712")
                        .put("banner", "ca-app-pub-3940256099942544/6300978111")
                        .put("reward", "ca-app-pub-3940256099942544/5224354917")
                        .put("video_donate", "ca-app-pub-3940256099942544/5224354917")
                        .put("video_reward", "ca-app-pub-3940256099942544/5224354917")
                        .put("native", "ca-app-pub-3940256099942544/2247696110")
                        .put("native_video", "ca-app-pub-3940256099942544/1044960115");
            } else {
                String json = SharedStorage.admobKeys();
                if (json == null || json.isEmpty()) {
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

            show_admob = SharedStorage.showAdmob();

            initNativeTabs();

            interstitialCountOnOpenApp = SharedStorage.admobInt(ON_OPEN_COUNT);
            interstitialCounterOnOpenApp = SharedStorage.admobInt(ON_OPEN_COUNTER);

            interstitialCountOnProxy = SharedStorage.admobInt(ON_PROXY_COUNT);
            interstitialCounterOnProxy = SharedStorage.admobInt(ON_PROXY_COUNTER);

            interstitialCountOnDialog = SharedStorage.admobInt(ON_DIALOG_COUNT);
            interstitialCounterOnDialog = SharedStorage.admobInt(ON_DIALOG_COUNTER);

            interstitialCountOnGhost = SharedStorage.admobInt(ON_GHOST_COUNT);
            interstitialCounterOnGhost = SharedStorage.admobInt(ON_GHOST_COUNTER);

        } catch (JSONException e) {
            Log.e(TAG, "admobKeys error: ", e);
            if (keys == null) {
                keysExist = false;
                keys = new AdmobKeys();
            }
        }

    }

    public void initAdmob(LaunchActivity launchActivity, AdmobControllerDelegate admobControllerDelegate) {
        context = launchActivity;
        if (getKeys() != null && getKeys().getApp_id() != null && !getKeys().getApp_id().isEmpty()) {
            Log.i(TAG, "initAdmob: deprecated mode");
            MobileAds.initialize(launchActivity, getKeys().getApp_id());
            admobControllerDelegate.onResponse();
            return;
        } else {
            Log.i(TAG, "initAdmob: new mode");
            MobileAds.initialize(launchActivity, initializationStatus -> {
                admobControllerDelegate.onResponse();
                Log.i(TAG, "AdmobController > initAdmob > initialize successfully :)");
            });
        }

/*        if (BuildVars.DEBUG_VERSION) {
            List<String> testDeviceIds = Arrays.asList("33BE2250B43518CCDA7DE426D04EE231", AdRequest.DEVICE_ID_EMULATOR);
            RequestConfiguration configuration =
                    new RequestConfiguration.Builder().setTestDeviceIds(testDeviceIds).build();
            MobileAds.setRequestConfiguration(configuration);
        }*/


    }

    //endregion

    //region Active ADMOB
    private boolean show_admob;

    public void setShowAdmob(boolean status) {
        SharedStorage.showAdmob(status);
        show_admob = status;
    }

    public boolean getShowAdmob() {
        if (!Config.ADMOB_FEATURE) {
            return false;
        }
        if (!keysExist) {
            Log.i(TAG, "getShowAdmob > keys is empty!");
            return false;
        }
        long cache = SharedStorage.turnOffAdsTime();//some of users off admob with sent telegram messeges to hes contact
        if (cache > 0) {
            if (cache >= Calendar.getInstance().getTimeInMillis()) {
                return false;
            }
        }

        if (getKeys() == null) {
            Log.e(TAG, "AdmobController > getShowAdmob > keys is null");
            return false;
        }

        return show_admob;
    }
    //endregion

    //region Native
    private ArrayList<Integer> native_items = new ArrayList<>();
    AdLoader adLoader;
    public ArrayList<NativeList> nativeLists = new ArrayList<>();

    public void setNativeTabs(String list) {
        SharedStorage.nativeAdmobTabs(list);
        initNativeTabs();
    }

    private ArrayList<Integer> getNativeTabs() {
        if (native_items == null) {
            initNativeTabs();
        }
        return native_items;
    }

    private void initNativeTabs() {
        Log.i(TAG, "initNativeTabs exec!");
        native_items.clear();
        try {
            String s;
            if (BuildVars.DEBUG_VERSION) {
                s = String.format("%s,%s,%s",
                        0,
                        1,
                        Integer.MAX_VALUE);
            } else {
                s = SharedStorage.nativeAdmobTabs();
            }
            Log.i(TAG, "AdmobController > initNativeTabs > get: " + s);
            if (!s.isEmpty()) {
                String[] list = s.split(",");
                for (String item : list) {
                    if (item != null && !item.isEmpty()) {
                        native_items.add(Integer.parseInt(item));
                        /*if (Integer.parseInt(item) == -1) {
                            native_items.add(MessagesController.getInstance(UserConfig.selectedAccount).selectedDialogFilter[0].id);
                        } else {
                            if (!TabMenuHiddenController.is(Integer.parseInt(item))) {
                                Log.i(TAG, "AdmobController > initNativeTabs > native: tab is active!");
                            }
                        }*/
                    }
                }
            } else {
                Log.e(TAG, "AdmobController > initNativeTabs > tab place is empty! ");
            }
        } catch (NumberFormatException e) {
            Log.e(TAG, "AdmobController > initNativeTabs > error: ", e);
        }
    }

    public boolean getShowNative(String from) {
        if (BuildVars.DEBUG_VERSION) {
            Log.i(TAG, String.format("getShowNative > from %s > native_items.size():%s , BuildVars.ADMOB_NATIVE_FEATURE:%s, getShowAdmob():%s ", from, native_items.size(), BuildVars.ADMOB_NATIVE_FEATURE, getShowAdmob()));
        }
        return /*native_items.size() > 0 &&*/ BuildVars.ADMOB_NATIVE_FEATURE && getShowAdmob();
    }

    public void loadNative(LaunchActivity launchActivity) {
        //region options
        Log.i(TAG, "loadNative: exec!");
        if (!getShowNative("loadNative")) {
            Log.e(TAG, "AdmobController >  loadNative > inactive!");
            return;
        }
        nativeLists.clear();

        Calendar calendar = Calendar.getInstance();
        if (!BuildVars.DEBUG_VERSION) {
            long savedTime = SharedStorage.nativeAdmobSavedCacheTime();
            long now = calendar.getTimeInMillis();
            if (savedTime >= now) {
                Log.i(TAG, String.format("loadNative: time cached! now:%s  saved:%s , dif=%s", now, savedTime, (savedTime - now)));
                return;
            }
            Log.i(TAG, String.format("loadNative: time %s", now - savedTime));
        }

        String unitId = getKeys().getNative();
        if (unitId.isEmpty()) {
            Log.e(TAG, "AdmobController >  loadNative > unit id is empty ");
            return;
        }
        //endregion

        ArrayList<Integer> integers = getNativeTabs();
        int requestSize = integers.size();
        adLoader = new AdLoader.Builder(launchActivity, unitId)
                .forUnifiedNativeAd(ad -> {
                    try {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                            if (launchActivity.isDestroyed()) {
                                ad.destroy();
                                return;
                            }
                        }

                        NativeList nativeList = new NativeList();
                        nativeList.unifiedNativeAd = ad;
                        nativeList.tabIndex = integers.get(0);
                        integers.remove(0);
                        nativeLists.add(nativeList);
                        Log.i(TAG, "loadNative > add new item");


                        refreshDialogs(integers.size(), calendar);


                    } catch (Exception e) {
                        Log.e(TAG, "loadNative > forUnifiedNativeAd error: ", e);
                    }
                })
                .withAdListener(new AdListener() {

                    @Override
                    public void onAdFailedToLoad(LoadAdError e) {
                        super.onAdFailedToLoad(e);
                        Log.e(TAG, "loadNative > onAdFailedToLoad native: " + e);
                        Log.i(TAG, "loadNative > onAdFailedToLoad native > dialogs.size():" + nativeLists.size());


                        if (nativeLists.size() > 0) {
                            integers.remove(0);
                            refreshDialogs(integers.size(), calendar);
                      /*      if (nativeLists.size() != 1 || nativeLists.get(0).dialogType <= 100) {
                                MessagesController.getInstance(UserConfig.selectedAccount).sortDialogs(null);
                                MessagesController.getInstance(UserConfig.selectedAccount).loadDialogs(0, 0, 20, true);
                            }*/
                        }
                    }

                    @Override
                    public void onAdClicked() {
                        super.onAdClicked();
                        Log.i(TAG, "loadNative > onAdClicked");
                    }

                    @Override
                    public void onAdClosed() {
                        super.onAdClosed();
                        Log.i(TAG, "loadNative > onAdClosed");
                    }
                })
                .withNativeAdOptions(new NativeAdOptions.Builder().build())
                .build();
        try {
            new Thread(() -> {
                Log.i(TAG, "AdmobController > loadNative: adLoader.loadAd  > size:" + requestSize);

                adLoader.loadAds(new AdRequest.Builder().build(), Math.min(requestSize, 5));
//                Log.i(TAG, "loadNative nativeList size: " + MessagesController.getInstance(currentAccount).nativeLists.size());
            }).start();


        } catch (Exception e) {
            Log.e(TAG, "AdmobController > loadNative: ", e);
        }
    }

    private void refreshDialogs(int size, Calendar calendar) {

        if (size == 0) {
            MessagesController.getInstance(UserConfig.selectedAccount).sortDialogs(null);
            MessagesController.getInstance(UserConfig.selectedAccount).loadDialogs(0, 0, 20, true);
            if (!BuildVars.DEBUG_VERSION) {
                calendar.add(Calendar.MINUTE, SharedStorage.nativeAdmobCacheTime());
                SharedStorage.nativeAdmobSavedCacheTime(calendar.getTimeInMillis());
            }
            Log.i(TAG, "loadNative > sort dialogs...");
        }
    }

    public void addNativeDialogs() {
        if (!getShowNative("addNativeDialogs")) {
            Log.i(TAG, "addNativeDialogs >  native not activated! , return: ");
            return;
        }

        if ( nativeLists.isEmpty()) {
            Log.i(TAG, "addNativeDialogs >  nativeLists is empty! , return: ");
            return;
        }

        ArrayList<TLRPC.Dialog> dialogs = null;
        Log.i(TAG, "addNativeDialogs exec! > nativeLists count:  " + nativeLists.size());
        for (NativeList item : nativeLists) {
            Log.i(TAG, "addNativeDialogs > for > id: " + item.tabIndex);
            int i = 0;

            //more than 100 to open native in top of the chat
            if (item.tabIndex > 100) {
                continue;
            }

            if (item.tabIndex == 0) {
                //all dialogs
                dialogs = MessagesController.getInstance(UserConfig.selectedAccount).getAllDialogs();
            } else {
                //other tabs
                try {
                    if (MessagesController.getInstance(UserConfig.selectedAccount).dialogFilters.size() >= item.tabIndex) {
                        dialogs = MessagesController.getInstance(UserConfig.selectedAccount).dialogFilters.get(item.tabIndex - 1).dialogs;
                    }
                } catch (Exception e) {
                    Log.e(TAG, "addNativeDialogs: ", e);
                }
            }


            if (dialogs == null) {
                continue;
            }
            Log.i(TAG, "addAdToList >  native , dialogtype:" + item.tabIndex);
            UnifiedNativeAd ad = item.unifiedNativeAd;
            if (MessagesController.getInstance(UserConfig.selectedAccount).getAllDialogs() != null) {
                for (int j = 0; j < 5; j++) {
                    if ((dialogs.size() - 1) >= j) {
                        if (dialogs.get(j) instanceof DialogAddCell) {
                            return;
                        }
                    }
                }

                DialogAddCell dialogAddCell = new DialogAddCell(ad);
                if (!dialogs.contains(dialogAddCell)) {
                    dialogs.add(i, dialogAddCell);
                    Log.i(TAG, "addAdToList native:  dialog.add(0, dialogAddCell)");

                } else {
                    if (BuildVars.DEBUG_VERSION) {
                        Log.i(TAG, "addNativeDialogs: dialogs contain ad!");
                    }
                }
            }

        }
    }

    public boolean initNativeInChat(FrameLayout contentView) {
        if (!getShowNative("initNativeInChat")) {
            return false;
        }
        for (NativeList item : nativeLists) {
            if (item.tabIndex > 100) {
                NativeAddCell nativeAddCell = new NativeAddCell(context);
                nativeAddCell.setBackgroundColor(Theme.getColor(Theme.key_actionBarDefault));
                UnifiedNativeAd ad = item.unifiedNativeAd;
                DialogAddCell nativeAdd = new DialogAddCell(ad);
                nativeAddCell.setAdd(nativeAdd.getAd());
                Log.i(TAG, "initNativeInChat: " + item.tabIndex);
                nativeAddCell.setBackgroundColor(Color.TRANSPARENT);
                contentView.addView(nativeAddCell, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, Gravity.TOP | Gravity.LEFT, 0, 0, 35, 0));
                nativeLists.remove(item);
                return true;
            }
        }
        return false;
    }

    //endregion

    //region Interstitial
    public static int ON_DIALOG_INTERSTITIAL = 1;
    public static int DONATE = 2;
    public static int REWARD = 3;
    public static int ON_OPEN_INTERSTITIAL = 4;
    public static int ON_PROXY_INTERSTITIAL = 5;
    public static int ON_GHOST_INTERSTITIAL = 6;

    private static final int ON_OPEN_COUNT = 0;
    private static final int ON_OPEN_COUNTER = 1;
    private static final int ON_PROXY_COUNT = 2;
    private static final int ON_PROXY_COUNTER = 3;
    private static final int ON_DIALOG_COUNT = 4;
    private static final int ON_DIALOG_COUNTER = 5;
    private static final int ON_GHOST_COUNT = 6;
    private static final int ON_GHOST_COUNTER = 7;

    public String getInterstitialUnitId(int type) {
        String unitId;
        if (ON_DIALOG_INTERSTITIAL == type || type == ON_OPEN_INTERSTITIAL || type == ON_GHOST_INTERSTITIAL) {
            unitId = getKeys().getInterstitial();
        } else if (type == DONATE) {
            unitId = getKeys().getInterstitial_donate();
        } else {
            unitId = getKeys().getInterstitial_reward();
        }
        if (BuildVars.DEBUG_VERSION) {
            Log.i(TAG, "AdmobController > getInterstitialUnitId > unit id :" + unitId);
        }
        return unitId;
    }

    public void clearCounter(int type) {
        if (type == AdmobController.ON_DIALOG_INTERSTITIAL) {
            SharedStorage.admobInt(0, ON_DIALOG_COUNTER);
            this.interstitialCounterOnDialog = 0;
        } else if (type == AdmobController.ON_OPEN_INTERSTITIAL) {
            SharedStorage.admobInt(0, ON_OPEN_COUNTER);
            this.interstitialCounterOnOpenApp = 0;
        } else if (type == AdmobController.ON_PROXY_INTERSTITIAL) {
            SharedStorage.admobInt(0, ON_PROXY_COUNTER);
            this.interstitialCounterOnProxy = 0;
        } else if (type == AdmobController.ON_GHOST_INTERSTITIAL) {
            SharedStorage.admobInt(0, ON_GHOST_COUNTER);
            this.interstitialCounterOnGhost = 0;
        }
    }

    //region On App Open
    private int interstitialCountOnOpenApp = 0;
    private int interstitialCounterOnOpenApp = 0;


    private int getInterstitialCountOnOpenApp() {
        if (BuildVars.DEBUG_VERSION) {
            return DEBUG_COUNT;
        }
        return interstitialCountOnOpenApp;
    }

    private int getInterstitialCounterOnOpenApp() {

        return interstitialCounterOnOpenApp;
    }

    private void increaseInterstitialCounterOnOpenApp() {
        int i = SharedStorage.admobInt(ON_OPEN_COUNTER) + 1;
        SharedStorage.admobInt(i, ON_OPEN_COUNTER);
        this.interstitialCounterOnOpenApp = i;
    }

    public void setInterstitialCountOnOpenApp(int value) {
        SharedStorage.admobInt(value, ON_OPEN_COUNT);
        interstitialCountOnOpenApp = value;
    }

    private boolean getShowInterstitialOnAppOpen() {
        if (BuildVars.DEBUG_VERSION) {
            return true;
        }
        return interstitialCountOnOpenApp > 0 && getShowAdmob();
    }

    public void showInterstitialOnAppOpen() {
        if (getShowInterstitialOnAppOpen()) {
            increaseInterstitialCounterOnOpenApp();
            if (getInterstitialCounterOnOpenApp() >= getInterstitialCountOnOpenApp()) {
                NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.showAdmobInterstitial, AdmobController.ON_OPEN_INTERSTITIAL);
            }
        }
    }
    //endregion

    // region On Dialog
    private int interstitialCountOnDialog = 0;
    private int interstitialCounterOnDialog = 0;


    private int getInterstitialCountOnDialog() {
        if (BuildVars.DEBUG_VERSION) {
            return 5;
        }
        return interstitialCountOnDialog;
    }

    private int getInterstitialCounterOnDialog() {

        return interstitialCounterOnDialog;
    }

    private void increaseInterstitialCounterOnDialog() {
        int i = SharedStorage.admobInt(ON_DIALOG_COUNTER) + 1;
        SharedStorage.admobInt(i, ON_DIALOG_COUNTER);
        this.interstitialCounterOnDialog = i;
    }

    public void setInterstitialCountOnDialog(int value) {
        SharedStorage.admobInt(value, ON_DIALOG_COUNT);
        interstitialCountOnDialog = value;
    }

    private boolean getShowInterstitialOnDialog() {
        if (BuildVars.DEBUG_VERSION) {
            return true;
        }
        return interstitialCountOnDialog > 0 && getShowAdmob();
    }

    public void showInterstitialOnDialog() {
        if (getShowInterstitialOnDialog()) {
            increaseInterstitialCounterOnDialog();
            if (getInterstitialCounterOnDialog() >= getInterstitialCountOnDialog()) {
                NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.showAdmobInterstitial, AdmobController.ON_DIALOG_INTERSTITIAL);
            }
        }
    }
    //endregion

    // region On Proxy Refresh
    private int interstitialCountOnProxy = 0;
    private int interstitialCounterOnProxy = 0;


    private int getInterstitialCountOnProxy() {
        if (BuildVars.DEBUG_VERSION) {
            return DEBUG_COUNT;
        }
        return interstitialCountOnProxy;
    }

    private int getInterstitialCounterOnProxy() {

        return interstitialCounterOnProxy;
    }

    private void increaseInterstitialCounterOnProxy() {
        int i = SharedStorage.admobInt(ON_PROXY_COUNTER) + 1;
        SharedStorage.admobInt(i, ON_PROXY_COUNTER);
        this.interstitialCounterOnProxy = i;
    }

    public void setInterstitialCountOnProxy(int value) {
        SharedStorage.admobInt(value, ON_PROXY_COUNT);
        interstitialCountOnProxy = value;
    }

    private boolean getShowInterstitialOnProxy() {
        if (BuildVars.DEBUG_VERSION) {
            return true;
        }
        return interstitialCountOnProxy > 0 && getShowAdmob();
    }

    public void showInterstitialOnProxyRefresh() {
        if (getShowInterstitialOnProxy()) {
            increaseInterstitialCounterOnProxy();
            if (getInterstitialCounterOnProxy() >= getInterstitialCountOnProxy()) {
                NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.showAdmobInterstitial, AdmobController.ON_PROXY_INTERSTITIAL);
            }
        }
    }

    //endregion

    // region On toggle Ghost
    private int interstitialCountOnGhost = 0;
    private int interstitialCounterOnGhost = 0;


    private int getInterstitialCountOnGhost() {
        if (BuildVars.DEBUG_VERSION) {
            return DEBUG_COUNT;
        }
        return interstitialCountOnGhost;
    }

    private int getInterstitialCounterOnGhost() {
        return interstitialCounterOnGhost;
    }

    private void increaseInterstitialCounterOnGhost() {
        int i = SharedStorage.admobInt(ON_GHOST_COUNTER) + 1;
        SharedStorage.admobInt(i, ON_GHOST_COUNTER);
        this.interstitialCounterOnGhost = i;
    }

    public void setInterstitialCountOnGhost(int value) {
        SharedStorage.admobInt(value, ON_GHOST_COUNT);
        interstitialCountOnGhost = value;
    }

    private boolean getShowInterstitialOnGhost() {
        if (BuildVars.DEBUG_VERSION) {
            return true;
        }
        return interstitialCountOnGhost > 0 && getShowAdmob();
    }

    public void showInterstitialOnGhost() {
        if (getShowInterstitialOnGhost()) {
            increaseInterstitialCounterOnGhost();
            if (getInterstitialCounterOnGhost() >= getInterstitialCountOnGhost()) {
                NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.showAdmobInterstitial, AdmobController.ON_GHOST_INTERSTITIAL);
            }
        }

    }

    //endregion

    //endregion

    //region Rewarded
    public String getRewardedUnitId() {
        String unitId = getKeys().getRewarded();
        if (BuildVars.DEBUG_VERSION) {
            Log.i(TAG, "AdmobController > getRewardedUnitId > unit id :" + unitId);
        }
        return unitId;
    }
    //endregion
}
