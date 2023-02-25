package co.nevisa.commonlib.admob;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdOptions;
import com.google.gson.Gson;

import org.telegram.messenger.MessagesController;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.TLRPC;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import co.nevisa.commonlib.Storage;
import co.nevisa.commonlib.admob.cells.AdDialogCell;
import co.nevisa.commonlib.admob.cells.NativeAddCell;
import co.nevisa.commonlib.admob.interfaces.IServedCallback;
import co.nevisa.commonlib.admob.models.CountItem;
import co.nevisa.commonlib.admob.models.NativeObject;
import co.nevisa.commonlib.admob.models.NativeServedItem;

class Native extends AdmobBaseClass {

    private static final String KEY = "target_native_";
    private final String KEY_COUNTER = "native_";

    @SuppressLint("StaticFieldLeak")
    private static Native aNative;

    public static Native getInstance() {
        if (aNative == null) {
            aNative = new Native();
        }
        return aNative;
    }

    AdLoader adLoader;
    public ArrayList<NativeServedItem> nativeServedItems = new ArrayList<>();
    private ArrayList<CountItem> nativeRequestItems = new ArrayList<>();

    private int getTarget(String name) {
        CountItem a = getNativeTarget(name);
        if (a == null) {
            return 0;
        }
        return a.getCount();
    }

    public CountItem getNativeTarget(String name) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return nativeRequestItems
                    .stream()
                    .filter(p -> p.getName().equals(name))
                    .findAny()
                    .orElse(null);
        } else {
            for (CountItem countItem : nativeRequestItems) {
                if (name.equals(countItem.getName())) {
                    return countItem;
                }
            }
        }
        return null;
    }


    //add from remote config
    void setTargets(ArrayList<CountItem> countItems) {
        Storage.admobTargets(KEY, new Gson().toJson(countItems));
    }

    private Activity context;

    void init(Activity context) {
        Log.i(TAG, "Native > init: ");
        this.context = context;
        nativeRequestItems = getItems(KEY);
    }


    private boolean isShow() {
        int i = 0;
        for (CountItem countItem : nativeRequestItems) {
            i += countItem.getCount();
        }
        return i > 0 && getShowAdmob();
    }


    private boolean isShow(String name) {
        return getTarget(name) > 0;
    }

    private int retry = 0;

    ArrayList<CallbackQueue> callbackQueues = new ArrayList<>();

    public void nativeRefreshTime(int native_refresh_time) {
        Storage.admobNativeRefreshTime(native_refresh_time);
    }

    private static class CallbackQueue {
        private final String name;
        private final IServedCallback iServedCallback;

        public String getName() {
            return name;
        }

        public IServedCallback getIServedCallback() {
            return iServedCallback;
        }

        public CallbackQueue(String name, IServedCallback iServedCallback) {
            this.name = name;
            this.iServedCallback = iServedCallback;
        }
    }

    public void serve(@Nullable IServedCallback iCallback) {
        //region options
        Log.i(TAG, "loadNative > serve > exec!");
        String unitId = getKeys().getNative();
        if (unitId.isEmpty()) {
            Log.e(TAG, "AdmobController >  loadNative  > serve >  unit id is empty ");
            return;
        }

        if (!isShow()) {
            Log.e(TAG, "AdmobController >  loadNative  > serve >  inactive!");
            return;
        }

        //endregion
        if (adLoader != null && adLoader.isLoading()) {
            Log.i(TAG, "serve: ad loader is loading..., return ");
            if (iCallback != null) {
                for (CallbackQueue cq : callbackQueues) {
                    if (cq.getName().equals(iCallback.getClass().getSimpleName())) {
                        return;
                    }
                }
                Log.i(TAG, "serve: add to callback queue! > size:" + callbackQueues.size());
                callbackQueues.add(new CallbackQueue(iCallback.getClass().getSimpleName(), iCallback));
            }
            return;
        }

        //expire native ad if don't shown after x minutes
        Calendar expireCalendar = Calendar.getInstance();
        expireCalendar.add(Calendar.MINUTE, Storage.admobNativeRefreshTime());

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
                    Log.i(TAG, "loadNative  > serve >  add new: " + nativeServedItems.size());

                    if (!adLoader.isLoading()) {
                        if (!nativeServedItems.isEmpty()) {
                            clearExpiredItems();
                            //show
                            boolean loaderCallbackNotInQueue = true;
                            for (CallbackQueue cq : callbackQueues) {
                                Log.i(TAG, "serve: notif on other callback request to : " + cq.getName());
                                cq.getIServedCallback().onServed(ad);
                                if (iCallback != null) {
                                    if (cq.getName().equals(iCallback.getClass().getSimpleName())) {
                                        loaderCallbackNotInQueue = false;
                                    }
                                }
                            }

                            if (loaderCallbackNotInQueue) {
                                if (iCallback != null) {
                                    iCallback.onServed(ad);
                                }
                            }
                            callbackQueues.clear();

                        } else {
                            Log.e(TAG, "serve: nativeServedItems is empty");
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
                        if (nativeServedItems.isEmpty()) {
                            if (retry < getAttemptToFail()) {
                                serve(iCallback);
                                retry++;
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
                        .

                withNativeAdOptions(new NativeAdOptions.Builder().

                        build())
                        .

                build();

        try {

            Log.i(TAG, "AdmobController > loadNative > serve >  adLoader.loadAd  > size:" + nativeRequestItems.size());
            @SuppressLint("VisibleForTests")
            AdRequest adRequest = new AdRequest.Builder().build();
            boolean single = loadSingleNative();
            if (single) {
                adLoader.loadAd(adRequest);
            } else {
                adLoader.loadAds(adRequest, Math.min(nativeRequestItems.size(), 5));
            }
            Log.i(TAG, "serve: " + (single ? "single mode" : "batch mode"));
        } catch (Exception e) {
            Log.e(TAG, "AdmobController > loadNative > serve >  ", e);
        }

    }

    public void getItem(String name, IServedCallback iCallback) {
        if (!nativeServedItems.isEmpty()) {
            NativeAd ad = getAd(name);

            if (ad != null) {
                NativeAddCell nativeAddCell = new NativeAddCell(context, false, false);
                nativeAddCell.setAd(ad);
                nativeAddCell.setBackgroundColor(Color.TRANSPARENT);
                iCallback.onServed(new NativeObject(ad, nativeAddCell));
            }
        } else {
            serve((a) -> getItem(name, iCallback));
        }

        Log.i(TAG, "getAd > served items is empty , requested from : " + name);
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
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Collections.sort(nativeServedItems, Comparator.comparingInt(NativeServedItem::getUsed));
            } else {
                Collections.sort(nativeServedItems, (NativeServedItem t, NativeServedItem t1) -> {
                    if (t.getUsed() > t1.getUsed()) return -1;
                    else return 1;
                });
            }

            //filter the already shown ad
            NativeServedItem item = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                item = nativeServedItems.stream()
                        .filter(p -> p.getTime() >= now && p.getName().equals(name))
                        .findAny()
                        .orElse(null);
            } else {
                for (NativeServedItem a : nativeServedItems) {
                    if (a.getTime() >= now && a.getName().equals(name)) {
                        item = a;
                        break;
                    }
                }
            }

            //if already used item not found , find a item via minimum used
            if (item == null) {
                //filter the native ad timeout for 60 minutes
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    item = nativeServedItems.stream()
                            .filter(p -> p.getTime() >= now)
                            .findFirst()
                            .orElse(null);
                } else {
                    for (NativeServedItem a : nativeServedItems) {
                        if (a.getTime() >= now) {
                            item = a;
                            break;
                        }
                    }
                }
            }

            //the ad item not found , get the expired ad item anyway
            if (item == null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    item = nativeServedItems.stream()
                            .filter(p -> p.getName().equals(name))
                            .findAny()
                            .orElse(nativeServedItems.stream()
                                    .findFirst()
                                    .orElse(null));
                } else {
                    for (NativeServedItem a : nativeServedItems) {
                        if (a.getName().equals(name)) {
                            item = a;
                            break;
                        }
                    }
                    if (item == null && nativeServedItems.size() > 0) {
                        item = nativeServedItems.get(0);
                    }
                }

                //re-serve native if the ads item are out of date.
                Log.e(TAG, "getItem > the ads are out of date ,  server again...");
                serve(ad -> {
                });

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

    public void addNative(IServedCallback refreshNeedCallback) {

        Log.i(TAG, "addNativeDialogs: start...");
        if (nativeServedItems.isEmpty()) {
            serve(refreshNeedCallback);
            return;
        }

        Log.i(TAG, "addNativeDialogs exec! > nativeLists count:  " + nativeServedItems.size());
        for (CountItem item : nativeRequestItems) {
            //filter tab only
            if (!item.getName().startsWith("tab_")) {
                continue;
            }


            ArrayList<TLRPC.Dialog> dialogs = null;
            int position = 0;

            if (item.getName().equals("tab_all") || item.getName().equals("tab_0")) {
                //all dialogs
                dialogs = MessagesController.getInstance(UserConfig.selectedAccount).getDialogs(0);
                position = dialogs.size() > 0 ? 1 : 0;
            } else if (item.getName().equals("tab_archive")|| item.getName().equals("tab_-1")) {
                //Archive dialogs
                dialogs = MessagesController.getInstance(UserConfig.selectedAccount).getDialogs(1);
            } else {
                //other tabs

                try {
                    //get tab index from tab name
                    int index = Integer.parseInt(item.getName().replace("tab_", ""));
                    int dialogFilterSize = MessagesController.getInstance(UserConfig.selectedAccount).dialogFilters.size();
                    if (dialogFilterSize >= index) {
                        dialogs = MessagesController.getInstance(UserConfig.selectedAccount).dialogFilters.get(index - 1).dialogs;
                    }
                } catch (NumberFormatException e) {
                    Log.e(TAG, "addNativeDialogs: tab name is incorrect! tab name must be start via tab_ and finish via a int number , e.g tab_1");
                    continue;
                }

            }

            if (dialogs == null) {
                Log.i(TAG, "addNativeDialogs: dialogs is null , continue");
                continue;
            }

            //get a native ad
            NativeAd ad = getAd(item.getName());
            if (ad != null) {
                AdDialogCell adDialogCell = new AdDialogCell(ad);
                dialogs.add(position, adDialogCell);
                Log.i(TAG, "addNative: add on :" + item.getName());

                int adSize = nativeServedItems.size();

                //repeat ad on the lists
                int repeatGap = item.getRepeatGap();
                if (repeatGap > 0 && adSize > 0) {
                    int j = adSize > 1 ? 1 : 0;
                    for (int i = repeatGap; i < dialogs.size(); i = i + repeatGap) {
                        adDialogCell = new AdDialogCell(nativeServedItems.get(j).getNativeAd());
                        dialogs.add(i, adDialogCell);
                        j++;
                        if (j >= adSize) {
                            j = 0;
                        }
                    }
                }
            } else {
                Log.e(TAG, "addNativeDialogs: ad is null");
            }

        }


    }

    private void clearExpiredItems() {
        long now = Calendar.getInstance().getTimeInMillis();
        List<NativeServedItem> expiredItems = new ArrayList<>();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            expiredItems = nativeServedItems.stream()
                    .filter(p -> p.getTime() < now)
                    .collect(Collectors.toList());
        } else {
            for (NativeServedItem a : nativeServedItems) {
                if (a.getTime() < now) {
                    expiredItems.add(a);
                }
            }
        }
        if (expiredItems.size() > 0) {
            Log.i(TAG, "clearInactiveItems:the expired items has been removed:" + expiredItems.size());
            nativeServedItems.removeAll(expiredItems);
            expiredItems.clear();
        }
    }
}
