package com.finalsoft;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.finalsoft.firebase.FireBaseLog;

import org.json.JSONException;
import org.json.JSONObject;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BuildConfig;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.R;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.Theme;

public class UpdateApp {
    private static String TAG = Config.TAG + "ua";

    public void checkUpdate(Activity activity) {
        if (SharedStorage.getNewVersionInfo().isEmpty()) {
            return;
        }
        try {
            JSONObject o = new JSONObject(SharedStorage.getNewVersionInfo());

            int verCode = 0;
            if (o.has("version")) {
                verCode = o.getInt("version");
            }

            boolean force = false;
            if (o.has("force")) {
                force = o.getBoolean("force");
            }

            String text = o.has("text") ? o.getString("text") : "";

//      final String appPackageName =          ApplicationLoader.applicationContext.getPackageName();
            String url = (o.has("link") && !o.getString("link").isEmpty()) ? o.getString("link") : BuildVars.PLAYSTORE_APP_URL/* + appPackageName*/;

            String packageName =
                    (o.has("package") && !o.getString("package").isEmpty()) ?
                            o.getString("package") : "";

            if (verCode > BuildConfig.VERSION_CODE) {
                //AlertsCreator.showUpdateAppAlert(activity, "sssssssssssssssssssss", true);

                AlertDialog builder = createDialog(activity, text, force,
                        param -> {
                            if (param == 1) {

                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                if (!packageName.isEmpty()) {
                                    intent.setPackage(packageName);
                                }

                                try {
                                    intent.setData(Uri.parse(url));
                                    Log.i(TAG, "checkUpdate > url : " + url);
                                } catch (ActivityNotFoundException anfe) {
                                    intent.setData(Uri.parse(BuildVars.PLAY_STORE_SITE_URL + BuildConfig.APPLICATION_ID));
                                }
                                ApplicationLoader.applicationContext.startActivity(intent);

                                FireBaseLog.write(FireBaseLog.LogType.UPDATE_APP, "1");
                            } else {
                                Toast.makeText(activity, "2", Toast.LENGTH_SHORT).show();
                            }
                        }).create();
                builder.setCancelable(!force);
                builder.show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //region Customized: dialog
    private static AlertDialog.Builder createDialog(final Activity parentActivity, String text,
                                                    boolean isForce,
                                                    final MessagesStorage.IntCallback callback) {
        AlertDialog.Builder builder = new AlertDialog.Builder(parentActivity);
        builder.setTopImage(R.drawable.app_update,
                Theme.getColor(Theme.key_dialogTopBackground));
        if (text.isEmpty()) {
            builder.setMessage(
                    AndroidUtilities.replaceTags(
                            LocaleController.getString("UpdateAppAlert",
                                    R.string.UpdateAppAlert)
                    )
            );
        } else {
            builder.setMessage(
                    AndroidUtilities.replaceTags(text)
            );
        }

        builder.setPositiveButton(
                LocaleController.getString("UpdateApp",
                        R.string.UpdateApp),
                (dialog, which) -> callback.run(1)
        );
        if (!isForce) {
            builder.setNegativeButton(
                    LocaleController.getString("ContactsPermissionAlertNotNow",
                            R.string.ContactsPermissionAlertNotNow), (dialog, which) -> callback.run(0));
        }

        return builder;
    }
    //endregion
}
