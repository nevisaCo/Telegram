package com.finalsoft.helper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.widget.Toast;

import com.finalsoft.Config;
import com.finalsoft.SharedStorage;
import com.finalsoft.admob.AdmobController;

import org.telegram.messenger.BuildVars;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;

public class Voice2TextHelper {
    private static final String TAG = Config.TAG + "v2th";
    private Context context;

    public Voice2TextHelper(Context context) {
        this.context = context;
    }

    public void show(int id) {
        try {
            int v2tCost = BuildVars.DEBUG_VERSION ? 1 : SharedStorage.v2tCost();
            int reward = SharedStorage.rewards();
            boolean showAd = BuildVars.DEBUG_VERSION || AdmobController.getInstance().getShowAdmob();
            boolean video_error = SharedStorage.admobVideoErrorList();
//            Log.i(TAG, "show: v2tCost:" + v2tCost + " ,reward:" + reward + " showad:" + showAd);

            if (showAd && reward < v2tCost && v2tCost > 0) {
                new AdDialogHelper(context).show(
                        null, String.format(LocaleController.getString("GetCoinsText", R.string.GetCoinsText),
                                reward,
                                v2tCost,
                                video_error ? 0 : SharedStorage.videoRewards(),
                                SharedStorage.interstitialRewards()
                        ),
                        param -> {
                            if (param == 1) {
                                //video
                                NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.showAdmobRewarded, AdmobController.VIDEO_USE_V2T, true/*reward*/);
                            } else {
                                //Interstitial
                                NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.showAdmobInterstitial, AdmobController.INTERSTITIAL_USE_V2T, true/*reward*/);
                            }
                        }, false);
                return;
            }

            if (showAd && v2tCost > 0) {
                SharedStorage.rewards(SharedStorage.rewards() - v2tCost);
                Toast.makeText(context, String.format(LocaleController.getString("ShowInventory", R.string.ShowInventory), v2tCost, reward), Toast.LENGTH_SHORT).show();
            }
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
