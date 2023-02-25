package co.nevisa.commonlib.admob;

import android.app.Activity;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.rewarded.RewardItem;

import java.util.ArrayList;

import co.nevisa.commonlib.AdmobApplicationLoader;
import co.nevisa.commonlib.Storage;
import co.nevisa.commonlib.admob.interfaces.IInitializeCallback;
import co.nevisa.commonlib.admob.interfaces.IServedCallback;
import co.nevisa.commonlib.admob.models.CountItem;


public class AdmobController extends AdmobBaseClass {
    private static AdmobController admobController;

    public static AdmobController getInstance() {
        if (admobController == null) {
            admobController = new AdmobController();
        }
        return admobController;
    }


    /**
     * @param activity        your activity for host ad
     * @param iCallback       return init callback result
     * @param iServedCallback return preserved ads status on app start
     */
    public void init(Activity activity, IInitializeCallback iCallback, IServedCallback iServedCallback) {
        super.init(activity, new IInitializeCallback() {
            @Override
            public void before() {
                iCallback.before();//first line

                Interstitial.getInstance().init(activity);

                Native.getInstance().init(activity);

                Reward.getInstance().init(activity);

                Banner.getInstance().init(activity);

            }

            @Override
            public void onComplete() {
                iCallback.onComplete();
                boolean preServe = Storage.admobPreServe();
                if (preServe) {
                    Native.getInstance().serve(iServedCallback);

                    Interstitial.getInstance().serve(iServedCallback);

                    Reward.getInstance().serve(iServedCallback);
                }else {
                    iServedCallback.onServed(true);
                }


            }
        });


    }

    AppOpen appOpen;

    public void initOpenAppAd(AdmobApplicationLoader admobApplicationLoader) {
        Log.i(TAG, "initOpenAppAd: ");
        super.init(admobApplicationLoader, new IInitializeCallback() {
            @Override
            public void before() {
                /*mobileAdInitializeStatus = 1;*/
            }

            @Override
            public void onComplete() {
              /*  mobileAdInitializeStatus = 2;
                if (iCallback != null) {
                    iCallback.onResponse();
                }*/
            }
        });
        appOpen = new AppOpen(admobApplicationLoader);

    }

    /**
     * @param name key name of interstitial location
     */
    public void showInterstitial(String name) {
        Interstitial.getInstance().show(name);
    }

    /**
     * @param countItems set interstitial item location and count before anything
     */
    public void setInterstitialTargets(ArrayList<CountItem> countItems) {
        Interstitial.getInstance().setTargets(countItems);
    }

    /**
     * @param countItems set native item location and count before anything
     */
    public void setNativeTargets(ArrayList<CountItem> countItems) {
        Native.getInstance().setTargets(countItems);
    }

    public void setBannerTargets(ArrayList<CountItem> countItems) {
        Banner.getInstance().setTargets(countItems);
    }

    /**
     * @param name     key name of the show location
     * @param callback return ad or serve status
     * @return
     */
    public void getNativeItem(String name, IServedCallback callback) {
        Native.getInstance().getItem(name, callback);
    }

    public NativeAd getNativeAd(String name) {
        return Native.getInstance().getAd(name);
    }

    public void applyNativeOnCollection(IServedCallback refreshNeedCallback) {
        Native.getInstance().addNative(refreshNeedCallback);
    }


    /**
     * @param name       key name of you location
     * @param customData customData custom data for get in in server side and check
     * @param callback   return reward item after show completed as OnUserEarnedRewardListener object.
     */
    public void showReward(String name, @Nullable String customData, @NonNull IRewardCallback callback) {
        Reward.getInstance().show(name, customData, callback);
    }

    /**
     * @param countItems set reward item location and count before anything
     */
    public void setRewardTargets(ArrayList<CountItem> countItems) {
        Reward.getInstance().setTargets(countItems);
    }

    public void setOpenAppTargets(ArrayList<CountItem> adCountItems) {
        AppOpen.setTargets(adCountItems);
    }

    public CountItem getNativeTarget(String nativeKey) {
        return Native.getInstance().getNativeTarget(nativeKey);
    }

    public CountItem getBannerTarget(String key) {
        return Banner.getInstance().getBannerTarget(key);
    }

    public void nativeRefreshTime(int native_refresh_time) {
        Native.getInstance().nativeRefreshTime(native_refresh_time);
    }
}
