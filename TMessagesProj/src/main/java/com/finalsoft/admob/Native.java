package com.finalsoft.admob;

import android.annotation.SuppressLint;
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
import java.util.List;
import java.util.stream.Collectors;

public class Native extends AdmobBaseClass {

    @SuppressLint("StaticFieldLeak")
    private static Native aNative;
    final String TAG = super.TAG + "na";
    private final String KEY = "target_native_";
    private final String KEY_COUNTER = "native_";
    boolean serveNativeOnFirstFail;

    public Native() {
        serveNativeOnFirstFail = SharedStorage.serveNativeOnFirstFail();
    }

    ;

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
        SharedStorage.admobTargets(KEY, new Gson().toJson(adCountItems));
    }

    private Activity context;

    void init(Activity context) {
        this.context = context;
        nativeRequestItems = (ArrayList<AdCountItem>) getItems(KEY).stream()
                .filter(p -> p.getCount() > 0)
                .collect(Collectors.toList());
    }

    private int getRepeatGap(String name) {
        AdCountItem a = nativeRequestItems
                .stream()
                .filter(p -> p.getName().equals(name))
                .findAny()
                .orElse(null);

        if (a != null) {
            return a.getRepeatGap();
        }
        return 0;
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
/*
        if (!BuildVars.DEBUG_VERSION) {
            long savedTime = SharedStorage.nativeAdmobSavedCacheTime();
            long now = calendar.getTimeInMillis();
            if (savedTime >= now) {
                Log.i(TAG, String.format("loadNative: time cached! now:%s  saved:%s , dif=%s", now, savedTime, ((savedTime - now) / 60000)));
                return;
            }
            Log.i(TAG, String.format("loadNative: time %s", now - savedTime));
        }
*/

        //endregion

        //expire native ad if don't shown after x minutes
        Calendar expireCalendar = Calendar.getInstance();
        expireCalendar.add(Calendar.MINUTE, SharedStorage.admobNativeRefreshTime());

        adLoader = new AdLoader.Builder(context, unitId)
                .forNativeAd(ad -> {
                    if (context.isDestroyed()) {
                        ad.destroy();
                        return;
                    }

                    NativeServedItem nativeServedItem = new NativeServedItem();
                    nativeServedItem.setNativeAd(ad);
                    nativeServedItem.setTime(expireCalendar.getTimeInMillis());


                    nativeServedItems.add(nativeServedItem);
                    Log.i(TAG, "Native > Serve >  add new: " + nativeServedItems.size());

                    if (!adLoader.isLoading()) {
                        clearExpiredItems();

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
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError e) {
                        super.onAdFailedToLoad(e);
                        Log.e(TAG, "loadNative  > serve >  onAdFailedToLoad native > loaded native size:" + nativeServedItems.size() + " , error:" + e);
                        if (retry < getAttemptToFail()) {
                            if (nativeServedItems.isEmpty()) {
                                emptyTry = 0;
                                new Handler().postDelayed(() -> {
                                    if (!adLoader.isLoading()) {
                                        adLoader.loadAds(new AdRequest.Builder().build(), Math.min(nativeRequestItems.size(), 5));
                                        Log.i(TAG, "onAdFailedToLoad: retrying to load ad... " + (retry + 1));
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
        } catch (Exception e) {
            Log.e(TAG, "AdmobController > loadNative > serve >  ", e);
        }
    }

    private void clearExpiredItems() {
        long now = Calendar.getInstance().getTimeInMillis();
        List<NativeServedItem> expiredItems = nativeServedItems.stream()
                .filter(p -> p.getTime() < now)
                .collect(Collectors.toList());
        if (expiredItems.size() > 0) {
            Log.i(TAG, "clearInactiveItems:the expired items has been removed:" + expiredItems.size());
            nativeServedItems.removeAll(expiredItems);
            expiredItems.clear();
        }
    }


    NativeAddCell getItem(String name, IServeCallback iCallback) {
        if (!nativeServedItems.isEmpty()) {
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
        }

        Log.i(TAG, "getAd > served items is empty , requested from : " + name);
        return null;
    }

    NativeAd getAd(String name) {
        Log.i(TAG, "getAd > executed from : " + name);
        int target = getTarget(name);

        //disabled items by admin from remote config e.g flurry / not exist the item
        if (target == 0) {
            //target number is 0
            Log.i(TAG, "getAd > isShow false, not in target!: " + name);
            return null;
        }


        int i = 1;

        //No need to get and set the counter for the items that are displayed each time
        if (target > 1) {
            //set counter loop
            i = getCounter(KEY_COUNTER + name) + 1;
            setCounter(KEY_COUNTER + name, i);
        }

        Log.i(TAG, String.format("getAd > name:%s ,i:%s , target:%s", name, i, target));


        if (i >= target) {
            Log.i(TAG, "getAd > Counter filled and  get native item.");

            long now = Calendar.getInstance().getTimeInMillis();

            //sort native ads per usage
            Collections.sort(nativeServedItems, Comparator.comparingInt(NativeServedItem::getUsed));

            //filter the already shown ad
            NativeServedItem item = nativeServedItems.stream()
                    .filter(p -> p.getTime() >= now && p.getName().equals(name))
                    .findAny()
                    .orElse(null);

            //if already used item not found , find a item via minimum used
            if (item == null) {
                //filter the native ad timeout for 60 minutes
                item = nativeServedItems.stream()
                        .filter(p -> p.getTime() >= now)
                        .findFirst()
                        .orElse(null);
            }

            //the ad item not found , get the expired ad item anyway
            if (item == null) {
                item = nativeServedItems.stream()
                        .filter(p -> p.getName().equals(name))
                        .findAny()
                        .orElse(nativeServedItems.stream()
                                .findFirst()
                                .orElse(null));

                if (adLoader != null && !adLoader.isLoading()) {
                    //reserve native if the ads item are out of date.
                    Log.e(TAG, "getItem > the ads are out of date ,  server again...");
                    serve();
                }
            }

            if (item != null) {
                Log.i(TAG, "getAd: used:" + item.getUsed() + ", time:" + ((item.getTime() - now) / 1000));

                //No need to save the counter for the items that are displayed each time
                if (target > 1) {
                    setCounter(KEY_COUNTER + name, 0);
                }

                item.increaseUsed();
                if (item.getName().isEmpty()) {
                    item.setName(name);
                }

                return item.getNativeAd();
            }
        }


        return null;
    }


    int emptyTry;

    public void addNativeDialogs() {
        if (nativeServedItems.isEmpty()) {
            Log.i(TAG, "addNativeDialogs >  nativeLists is empty! , return >  emptyTry : " + emptyTry);
            emptyTry++;
            if (emptyTry >= 100) {
                emptyTry = 0;
                //reserve native if the ads item are out of date.
                Log.e(TAG, "getItem > the ads are out of date ,  serve again...");
                if (serveNativeOnFirstFail) {
                    serve();
                }
            }
            return;
        }
        emptyTry = 0;

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
            int position;

            int dialogFilterSize = MessagesController.getInstance(UserConfig.selectedAccount).dialogFilters.size();
            if (index <= 0) {
                //all dialogs
                dialogs = MessagesController.getInstance(UserConfig.selectedAccount).getDialogs(index == 0 ? 0 : 1);
            } else {
                //other tabs
                if (dialogFilterSize >= index) {
                    dialogs = MessagesController.getInstance(UserConfig.selectedAccount).dialogFilters.get(index - 1).dialogs;
                    tabName = MessagesController.getInstance(UserConfig.selectedAccount).dialogFilters.get(index - 1).name;
                }
            }
            //            Log.i(TAG, "addNativeDialogs > apply native for tab index: " + tabName + " , tabs:" + MessagesController.getInstance(UserConfig.selectedAccount).dialogFilters.size());


            if (dialogs == null) {
                Log.i(TAG, "addNativeDialogs: dialogs is null , continue");
                continue;

            }

            boolean adDialogExist = dialogs.stream().filter(p -> p instanceof AdDialogCell).findAny().orElse(null) != null;
            if (!adDialogExist) {


                //get a native ad
                NativeAd ad = getAd(item.getName());
                if (ad != null) {
                    AdDialogCell adDialogCell = new AdDialogCell(ad);
                    position = index == 0 && dialogs.size() > 0 ? 1 : 0;
                    dialogs.add(position, adDialogCell);
                    Log.i(TAG, String.format("addAdToList native:  dialog added on tab %s position:%s , dialog size:%s", tabName, position, dialogs.size()));

                    //repeat ad on the lists
                    int repeatGap = item.getRepeatGap();
                    boolean repeatAd = repeatGap > 0 || (index == 0 && dialogFilterSize == 0 && repeatGap < 0);
                    if (repeatAd) {
                        if (repeatGap < 0) {
                            repeatGap *= -1;
                        }
                        int j = 1;
                        for (NativeServedItem n : nativeServedItems) {
                            if ((j * repeatGap) < dialogs.size()) {
                                adDialogCell = new AdDialogCell(n.getNativeAd());
                                dialogs.add(j * repeatGap, adDialogCell);
                                j++;
                            }
                        }

                    }
                } else {
                    Log.e(TAG, "addNativeDialogs: ad is null");
                }
            }
        }


    }

    //endregion

}
