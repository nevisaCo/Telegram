package com.finalsoft.admob;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Preconditions;

import com.finalsoft.ApplicationLoader;
import com.finalsoft.Config;
import com.finalsoft.admob.models.CountItem;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.nativead.NativeAd;

import java.util.ArrayList;

public class AdmobController extends AdmobBaseClass {
    private int mobileAdInitializeStatus = 0;




    public interface AdmobControllerDelegate {
        void onResponse();
    }

    private final String TAG = Config.TAG + "ac";

    //region Instance
    private static AdmobController admobController;

    public static AdmobController getInstance() {
        if (admobController == null) {
            admobController = new AdmobController();
        }
        return admobController;
    }
    //endregion


    public void init(@NonNull Activity launchActivity, @NonNull ICallback iCallback) {
        init(launchActivity, iCallback, null);
    }

    public void init(@NonNull Activity launchActivity, @NonNull ICallback iCallback, @Nullable IServeCallback nativeCallback) {
        init(launchActivity, iCallback, nativeCallback, null);
    }

    ICallback iCallback;

    @SuppressLint("RestrictedApi")
    public void init(@NonNull Activity launchActivity
            , @NonNull ICallback iCallback
            , @Nullable IServeCallback nativeCallback
            , @Nullable IServeCallback interstitialCallback) {

        Preconditions.checkNotNull(launchActivity, "Activity can't be null!");
        Preconditions.checkNotNull(iCallback, "ICallback can't be null!");

        Log.i(TAG, "AdmobController init: ");

        this.iCallback = new ICallback() {
            @Override
            public void before() {
                iCallback.before();

                Interstitial.getInstance().init(launchActivity);

                Native.getInstance().init(launchActivity);

                Reward.getInstance().init(launchActivity);

                Banner.getInstance().init(launchActivity);

                Log.i(TAG, "AdmobController > before: ");
            }

            @Override
            public void onResponse() {
                if (preServeReward()) {
                    Reward.getInstance().serve(launchActivity);
                }
                if (preServeNative()) {
                    Native.getInstance().serve(nativeCallback);
                }

                if (preServeInterstitial()) {
                    Interstitial.getInstance().serve(interstitialCallback);
                }

                iCallback.onResponse();

                Log.i(TAG, " AdmobController > onResponse: ");
            }

        };

        Log.i(TAG, "AdmobController > init: mobileAdInitializeStatus:" + mobileAdInitializeStatus);
        if (mobileAdInitializeStatus == 1) {
            this.iCallback.before();
        } else if (mobileAdInitializeStatus == 2) {
            this.iCallback.before();
            this.iCallback.onResponse();
        } else {
            super.init(launchActivity, this.iCallback);
        }
    }

    AppOpen appOpen;

    public void initOpenAppAd(ApplicationLoader applicationLoader) {
        Log.i(TAG, "initOpenAppAd: ");
        super.init(applicationLoader, new ICallback() {
            @Override
            public void before() {
                mobileAdInitializeStatus = 1;
            }

            @Override
            public void onResponse() {
                mobileAdInitializeStatus = 2;
                if (iCallback != null) {
                    iCallback.onResponse();
                }
            }
        });
        appOpen = new AppOpen(applicationLoader);

    }

    public void showInterstitial(String name) {
        Interstitial.getInstance().show(name);
    }

    public void showRewarded(Activity activity, String name, OnUserEarnedRewardListener listener) {
        Reward.getInstance().show(activity, name, listener);
    }

    public void setInterstitialTargets(ArrayList<CountItem> countItems) {
        Interstitial.getInstance().setTargets(countItems);
    }

    public void setRewardedTargets(ArrayList<CountItem> countItems) {
        Reward.getInstance().setTargets(countItems);
    }

    public void setNativeTargets(ArrayList<CountItem> countItems) {
        Native.getInstance().setTargets(countItems);
    }

    public void setBannerTargets(ArrayList<CountItem> countItems) {
        Banner.getInstance().setTargets(countItems);
    }

    public void setOpenAppTargets(ArrayList<CountItem> countItems) {
        AppOpen.setTargets(countItems);
    }

    public void addNativeDialogs() {
        Native.getInstance().addNativeDialogs();
    }


    public void getUINativeItem(String name, Native.IGetNativeItem callback) {
         Native.getInstance().getItem(name, callback);
    }

    public NativeAd getNativeAd(String name) {
        return Native.getInstance().getAd(name);
    }

    public CountItem getNativeTarget(String nativeKey) {
        return Native.getInstance().getNativeTarget(nativeKey);
    }

    public CountItem getBannerTarget(String key) {
        return Banner.getInstance().getBannerTarget(key);
    }
}
