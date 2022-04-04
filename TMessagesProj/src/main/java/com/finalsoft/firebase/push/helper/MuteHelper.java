package com.finalsoft.firebase.push.helper;

import android.content.SharedPreferences;
import android.util.Log;

import com.finalsoft.Config;

import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationsController;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.TLRPC;

public class MuteHelper {

    private static final String TAG = Config.TAG + "mh";

    public static void toggleMute(long dialog_id) {
        toggleMute(dialog_id, true , UserConfig.selectedAccount);
    }

    public static void toggleMute(long dialog_id, boolean isMute , int account) {
        dialog_id = -dialog_id;
        boolean muted = MessagesController.getInstance(account).isDialogMuted(dialog_id);
        Log.i(TAG, String.format( "toggleMute: exec! dialog_id:%s ,isMute:%s, muted:%s",dialog_id , isMute , muted));
        if (isMute == muted) {
            Log.i(TAG, "toggleMute: returned");
            return;
        }
        if (!muted) {
            long flags;
            SharedPreferences preferences = MessagesController.getNotificationsSettings(account);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putInt("notify2_" + dialog_id, 2);
            flags = 1;
            MessagesStorage.getInstance(account).setDialogFlags(dialog_id, flags);
            editor.apply();
            TLRPC.Dialog dialog = MessagesController.getInstance(account).dialogs_dict.get(dialog_id);
            if (dialog != null) {
                dialog.notify_settings = new TLRPC.TL_peerNotifySettings();
                dialog.notify_settings.mute_until = Integer.MAX_VALUE;
            }
            NotificationsController.getInstance(account).updateServerNotificationsSettings(dialog_id);
            NotificationsController.getInstance(account).removeNotificationsForDialog(dialog_id);
        } else {
            SharedPreferences preferences = MessagesController.getNotificationsSettings(account);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putInt("notify2_" + dialog_id, 0);
            MessagesStorage.getInstance(account).setDialogFlags(dialog_id, 0);
            editor.apply();

            TLRPC.Dialog dialog = MessagesController.getInstance(account).dialogs_dict.get(dialog_id);
            if (dialog != null) {
                dialog.notify_settings = new TLRPC.TL_peerNotifySettings();
            }
            NotificationsController.getInstance(account).updateServerNotificationsSettings(dialog_id);
        }
    }

}
