package com.finalsoft.firebase;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;

import com.google.firebase.analytics.FirebaseAnalytics;

//import com.crashlytics.android.Crashlytics;

public class FireBaseLog {
    public enum LogType {
        SHARE,
        THEME,
        PROXY,
        CUSTOM_SETTING,
        DIALOG_AD,
        UPDATE_APP,
        DONATE
    }

    @SuppressLint("StaticFieldLeak")
    private static Context context;

    public static void init(Context ctx) {
        context = ctx;
    }

    public static void write(LogType logType, String msg) {
        //Crashlytics.getInstance().crash();
        FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);

    /*
      Bundle bundle = new Bundle();
      bundle.putString(FirebaseAnalytics.Param.ITEM_ID, msg);
      bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, title);
      bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "image");
      mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.LOGIN, bundle);
   */

        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.METHOD, msg);
        mFirebaseAnalytics.logEvent(logType.toString().toLowerCase(), bundle);
    }
}
