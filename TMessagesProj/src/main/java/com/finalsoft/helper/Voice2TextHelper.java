package com.finalsoft.helper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.widget.Toast;

import com.finalsoft.Config;
import com.finalsoft.SharedStorage;

import org.telegram.messenger.BuildVars;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;

import co.nevisa.commonlib.admob.AdLocation;
import co.nevisa.commonlib.admob.AdmobController;

public class Voice2TextHelper {
    private static final String TAG = Config.TAG + "v2th";
    private Context context;

    public Voice2TextHelper(Context context) {
        this.context = context;
    }

    public void show(int id) {
        //video
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.showAdmobRewarded, AdLocation.REWARD_USE_V2T, true);
        //Interstitial
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.showAdmobInterstitial, AdLocation.INTERSTITIAL_USE_V2T, true);

        try {

            String local = SharedStorage.v2tLocalShortName();
            if (local == null || local.isEmpty()) {
                local = LocaleController.getCurrentLanguageShortName();
            }

            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT, LocaleController.getString("Voice2TextLabel", R.string.Voice2TextLabel));
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, local);

            ((Activity) context).startActivityForResult(intent, id);
        } catch (Exception e) {
            Log.i(TAG, "show: ", e);
        }
    }
}
