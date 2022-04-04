package com.finalsoft.helper;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.finalsoft.Config;

import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.LaunchActivity;

public class RestartHelper {
    private static final String TAG = Config.TAG + "rh";

    public void restart(Context context) {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle(LocaleController.getString("RestartApp", R.string.RestartApp));
            builder.setMessage(LocaleController.getString("RestartAppAlert", R.string.RestartAppAlert));
            builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), (dialog, which) -> {
                        try {
                            Intent mStartActivity = new Intent(context, LaunchActivity.class);
                            int mPendingIntentId = 123456;
                            PendingIntent mPendingIntent = PendingIntent.getActivity(context, mPendingIntentId, mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
                            AlarmManager mgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                            mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 1000L, mPendingIntent);
                            System.exit(0);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
            );
            builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
            builder.show();
        } catch (Exception e) {
            Log.e(TAG, "restart: ", e);
        }
    }
    //endregion

}
