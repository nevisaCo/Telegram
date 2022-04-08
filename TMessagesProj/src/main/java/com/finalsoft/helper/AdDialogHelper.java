package com.finalsoft.helper;

import android.content.Context;

import com.finalsoft.SharedStorage;

import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.R;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.Components.AlertsCreator;

public class AdDialogHelper {
    private Context context;

    public AdDialogHelper(Context context) {
        this.context = context;
    }

    public void show(MessagesStorage.IntCallback callback) {
        show(null, null, callback, false);
    }

    public void show(String title, String message, MessagesStorage.IntCallback callback, boolean donate) {
        boolean video_error = SharedStorage.admobVideoErrorList();
        if (video_error && donate) {
            callback.run(2);
            return;
        }

        AlertDialog.Builder builder = AlertsCreator.createSimpleAlert(context,
                title == null ? LocaleController.getString("GetCoins", R.string.GetCoins) : title,
                message == null ? String.format(LocaleController.getString("GetCoinsText", R.string.GetCoinsText),
                        SharedStorage.rewards(),
                        SharedStorage.proxyRefreshCost(),
                        video_error ? 0 : SharedStorage.videoRewards(),
                        SharedStorage.interstitialRewards()
                ) : message
        );

        if (!video_error)
            builder.setNegativeButton("VIDEO", (dialog, which) -> {
                callback.run(1);
            });

        builder.setPositiveButton("BANNER", (dialog, which) -> {
            callback.run(2);
        });
        builder.create().show();

    }

}
