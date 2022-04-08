package com.finalsoft.admob;

import android.app.Activity;
import android.graphics.Color;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.NonNull;

import com.finalsoft.SharedStorage;
import com.finalsoft.admob.models.AdCountItem;
import com.finalsoft.admob.models.NativeServedItem;
import com.finalsoft.admob.ui.AdDialogCell;
import com.finalsoft.admob.ui.NativeAddCell;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdOptions;
import com.google.gson.Gson;

import org.telegram.messenger.BuildVars;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.TLRPC;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;

public class Native extends AdmobBaseClass {

    private static Native aNative;

    public static Native getInstance() {
        if (aNative == null) {
            aNative = new Native();
        }
        return aNative;
    }

    AdLoader adLoader;
    public ArrayList<NativeServedItem> nativeServedItems = new ArrayList<>();
    public ArrayList<NativeAd> nativeShownOnTabs = new ArrayList<>();
    private ArrayList<AdCountItem> nativeRequestItems = new ArrayList<>();

    private int getTarget(String name) {
        AdCountItem a = nativeRequestItems
                .stream()
                .filter(p -> p.getName().equals(name))
                .findAny()
                .orElse(null);

        if (a != null) {
            return a.getCount();
        }
        return 0;
    }

    //add from remote config
    void setTargets(ArrayList<AdCountItem> adCountItems) {
        SharedStorage.admobTargets("target_native_", new Gson().toJson(adCountItems));
    }

    private Activity context;

    void init(Activity context) {
        this.context = context;
        nativeRequestItems = getItems("target_native_");
    }


    private boolean isActive() {
        int i = nativeRequestItems.stream().mapToInt(AdCountItem::getCount).sum();
        return i > 0 && getShowAdmob();
    }


    private int retry = 0;

    private void serve() {
        serve(null);
    }

    public void serve(IServeCallback iCallback) {
        //return id adLoader is loading...
        if (adLoader != null && adLoader.isLoading()) {
            Log.i(TAG, "serve: return, adLoader is loading...");
            return;
        }

        //region options
        Log.i(TAG, "loadNative > serve > exec!");
        if (!isActive()) {
            Log.e(TAG, "AdmobController >  loadNative  > serve >  inactive!");
            return;
        }

        String unitId = getKeys().getNative();
        if (unitId.isEmpty()) {
            Log.e(TAG, "AdmobController >  loadNative  > serve >  unit id is empty ");
            return;
        }

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
        //endregion

        nativeServedItems.clear();

        //expire native ad if don't shown after 60 minutes
        calendar.add(Calendar.MINUTE, SharedStorage.admobNativeRefreshTime());
        int index = 0;

        adLoader = new AdLoader.Builder(context, unitId)
                .forNativeAd(ad -> {
                    if (context.isDestroyed()) {
                        ad.destroy();
                        return;
                    }

                    NativeServedItem nativeServedItem = new NativeServedItem();
                    nativeServedItem.nativeAd = ad;
                    nativeServedItem.time = calendar.getTimeInMillis();


                    nativeServedItems.add(nativeServedItem);
                    Log.i(TAG, "Native > Serve >  add new: " + nativeServedItems.size());

                    if (!adLoader.isLoading()) {
                        //load completed
                        if (iCallback != null) {
                            iCallback.onServe();
                        }

                        //set cache time
                        if (!BuildVars.DEBUG_VERSION) {
                            calendar.add(Calendar.MINUTE, SharedStorage.nativeAdmobCacheTime());
                            SharedStorage.nativeAdmobSavedCacheTime(calendar.getTimeInMillis());
                        }
                    }
                })
                .withAdListener(new AdListener() {
                    @Override
                    public void onAdLoaded() {
                        super.onAdLoaded();
                        retry = 0;
                        Log.i(TAG, "onAdLoaded: **********");
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError e) {
                        super.onAdFailedToLoad(e);
                        Log.e(TAG, "loadNative  > serve >  onAdFailedToLoad native > loaded native size:" + nativeServedItems.size() + " , error:" + e);
                        if (retry < getAttemptToFail()) {
                            if (nativeServedItems.isEmpty()) {
                                new Handler().postDelayed(() -> {
                                    if (!adLoader.isLoading()) {
                                        adLoader.loadAds(new AdRequest.Builder().build(), Math.min(nativeRequestItems.size(), 5));
                                        Log.i(TAG, "onAdFailedToLoad: retrying to load ad... " + retry + 1);
                                        retry++;
                                    } else {
                                        Log.e(TAG, "onAdFailedToLoad: can't attempt to the load, the adLoader is loading...");
                                    }
                                }, (retry + 1) * 10000L);

                            } else {
                                Log.e(TAG, "onAdFailedToLoad: the native served item is not empty:" + nativeServedItems.size());
                            }
                        }
                    }

                    @Override
                    public void onAdClicked() {
                        super.onAdClicked();
                        Log.i(TAG, "loadNative  > serve >  onAdClicked");
                    }

                    @Override
                    public void onAdClosed() {
                        super.onAdClosed();
                        Log.i(TAG, "loadNative  > serve >  onAdClosed");
                    }
                })
                .withNativeAdOptions(new NativeAdOptions.Builder().build())
                .build();

        try {

            Log.i(TAG, "AdmobController > loadNative > serve > native request Items size:" + nativeRequestItems.size());
            adLoader.loadAds(new AdRequest.Builder().build(), Math.min(nativeRequestItems.size(), 5));
//            adLoader.loadAd(new AdRequest.Builder().build());


        } catch (Exception e) {
            Log.e(TAG, "AdmobController > loadNative > serve >  ", e);
        }
    }

    NativeAddCell getItem(String name, IServeCallback iCallback) {
        return getItem(name, false, iCallback);
    }

    NativeAddCell getItem(String name, boolean repeat, IServeCallback iCallback) {


        NativeAd ad = getAd(name);

        if (ad != null) {
            NativeAddCell nativeAddCell = new NativeAddCell(context, false, false);
            nativeAddCell.setAdd(ad);
            nativeAddCell.setBackgroundColor(Color.TRANSPARENT);
            if (iCallback != null) {
                iCallback.onServe();
            }
            return nativeAddCell;
        }
        return null;
    }

    private NativeAd getAd(String name/*, boolean repeat*/) {
        if (nativeServedItems.isEmpty()) {
            Log.i(TAG, "getAd > served items is empty , requested from : " + name);
            return null;
        }

        Log.i(TAG, "getAd > executed from : " + name);
        int target = getTarget(name);

        if (target == 0) {
            //target number is 0
            Log.i(TAG, "getAd > isShow false, not in target!: " + name);
            return null;
        }


        int i = 1;

        if (target > 1) {
            //set counter loop
            i = getCounter("native_" + name) + 1;
            setCounter("native_" + name, i);
        }

        Log.i(TAG, String.format("getAd > name:%s ,i:%s , target:%s", name, i, target));


        if (i >= target) {
            Log.i(TAG, "getAd > Counter filled and  get native item.");

            long now = Calendar.getInstance().getTimeInMillis();

            //sort native ads per usage
            Collections.sort(nativeServedItems, Comparator.comparingInt(a -> a.used));

            for (NativeServedItem item : nativeServedItems) {
                Log.i(TAG, "getAd: used:" + item.used);

                //prevent shown native if repeat has been false
//                if (repeat || item.used == 0) {

                //check native ad timeout for 60 minutes
                if (item.time >= now) {

                    if (target > 1) {
                        setCounter("native_" + name, 0);
                    }

                    item.used += 1;

                    return item.nativeAd;
                }
//                }
            }

            //reserve native if the ads item are out of date.
            Log.e(TAG, "getItem > the ads are out of date ,  server again...");
            serve();

        }

        return null;
    }


    public void addNativeDialogs() {
        if (nativeServedItems.isEmpty()) {
            Log.i(TAG, "addNativeDialogs >  nativeLists is empty! , return: ");
            return;
        }

        Log.i(TAG, "addNativeDialogs exec! > nativeLists count:  " + nativeServedItems.size());
        int index;
        for (AdCountItem item : nativeRequestItems) {
            ArrayList<TLRPC.Dialog> dialogs = null;

            //filter tab only
            if (!item.getName().startsWith("tab_")) {
                continue;
            }

            //get tab index from tab name
            try {
                index = Integer.parseInt(item.getName().replace("tab_", ""));
            } catch (NumberFormatException e) {
                Log.e(TAG, "addNativeDialogs: tab name is incorrect! tab name must be start via tab_ and finish via a int number , e.g tab_1");
                continue;
            }

            String tabName = "All";
            int position = 0;

            if (index <= 0) {
                //all dialogs
                dialogs = MessagesController.getInstance(UserConfig.selectedAccount).getDialogs(index == 0 ? 0 : 1);
            } else {
                //other tabs
                if (MessagesController.getInstance(UserConfig.selectedAccount).dialogFilters.size() >= index) {
                    dialogs = MessagesController.getInstance(UserConfig.selectedAccount).dialogFilters.get(index - 1).dialogs;
                    tabName = MessagesController.getInstance(UserConfig.selectedAccount).dialogFilters.get(index - 1).name;
                }/*else {
                    dialogs = MessagesController.getInstance(UserConfig.selectedAccount).getDialogs(0);
                }*/
            }
            Log.i(TAG, "addNativeDialogs > apply native for tab index: " + tabName + " , tabs:" + MessagesController.getInstance(UserConfig.selectedAccount).dialogFilters.size());


            if (dialogs == null) {
                continue;
            }
            Log.i(TAG, "addAdToList >  native  > tab index:" + item.getName());

            boolean adDialogExist = dialogs.stream().filter(p -> p instanceof AdDialogCell).findAny().orElse(null) != null;
            if (!adDialogExist) {
                //get a native ad
                NativeAd ad = getAd(item.getName());
                AdDialogCell adDialogCell = new AdDialogCell(ad);
                position = index == 0 ? 1 : 0;
                dialogs.add(position, adDialogCell);
                Log.i(TAG, String.format("addAdToList native:  dialog added on tab %s position:%s , dialog size:%s", tabName, position, dialogs.size()));
            }


        }

    }

    //endregion

}
