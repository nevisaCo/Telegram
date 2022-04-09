package com.finalsoft.controller;

import android.content.res.Resources;
import android.util.Log;

import com.finalsoft.Config;
import com.finalsoft.SharedStorage;

import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.R;

public class LocalController {
    private static final String TAG = Config.TAG + "localc";
    private static final String URL_PRIVACY = SharedStorage.urls(SharedStorage.UrlType.PRIVACY);
    private static final String URL_ASK = SharedStorage.urls(SharedStorage.UrlType.ASK);
    private static final String URL_FAQ = SharedStorage.urls(SharedStorage.UrlType.FAQ);

    protected static boolean show_full_number = SharedStorage.showFullNumber();

    public void updateFullNumberStatus() {
        show_full_number = SharedStorage.showFullNumber();
    }

    protected static String getAppName(String s) {
        if (s == null) {
            return "";
        }
        String appName = "";
        try {
            appName = ApplicationLoader.applicationContext.getResources().getString(R.string.AppName);
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        }
        if (BuildVars.DEBUG_VERSION) {
            if (s.toLowerCase().contains("telegram")) {
                Log.d("finalsoftlc", "getAppName : " + s);
            }
        }
        if (!BuildVars.DOMAIN_URL.isEmpty()) {
            if (s.toLowerCase().contains("://telegram.org")) {
                if (s.endsWith("/privacy") && !URL_PRIVACY.isEmpty()) {
                    s = URL_PRIVACY;
                } else if (s.contains("/faq") && !URL_FAQ.isEmpty()) {
                    s = URL_FAQ;
                } else {
                    s = s.replace("telegram.org", BuildVars.DOMAIN_URL);
                }
            }
        }

        //change intro text
        if (s.contains("forever. No ads.")) {
            s = s.replace(". No ads.", ". Maybe Contain ads.");
        } else if (s.contains("It is **free** and **secure**")){
            s = s.replace("It is **free** and **secure**", "Use Telegram's API");
        }

        return s.replaceAll("Telegram|telegram|텔레그램|تلگرام|تيليجرام", appName);


    }

}
