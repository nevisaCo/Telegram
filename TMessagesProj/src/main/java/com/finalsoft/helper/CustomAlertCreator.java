package com.finalsoft.helper;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.finalsoft.Config;
import com.finalsoft.SharedStorage;
import com.finalsoft.helper.forward.ForwardHelper;

import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.R;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.CheckBoxCell;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.SlideChooseView;
import org.telegram.ui.FiltersSetupActivity;
import org.telegram.ui.LaunchActivity;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class CustomAlertCreator {
    public static void showGoogleRateDialog(Context context, String title, String text, String package_name, SlideChooseView.Callback callback) {
        try {
            AlertDialog.Builder builder = AlertsCreator.createSimpleAlert(context, title, text);

/*            LinearLayout linearLayout = new LinearLayout(context);
            TextView textView = new TextView(context);
            textView.setText(text);
            linearLayout.addView(checkBoxCell);*/
            builder.setTopImage(R.drawable.stars, Theme.getColor(Theme.key_dialogTopBackground));

            CheckBoxCell checkBoxCell = new CheckBoxCell(context, 0, null);
            checkBoxCell.setText(LocaleController.getString("DontAskAgain", R.string.DontAskAgain), "", false, true);
            checkBoxCell.setOnClickListener(view -> {
                checkBoxCell.setChecked(!checkBoxCell.isChecked(), true);
                SharedStorage.googleRate(SharedStorage.googleRateType.DONT_SHOW_AGAIN, checkBoxCell.isChecked() ? "true" : "false");
            });

            builder.setView(checkBoxCell);

            builder.setNegativeButton(LocaleController.getString("GoogleRateLater", R.string.GoogleRateLater), (dialogInterface, i) -> callback.onOptionSelected(0));

            builder.setPositiveButton(LocaleController.getString("GoogleRateOk", R.string.GoogleRateOk), (dialog, which) -> {
                Uri uri = Uri.parse("market://details?id=" + context.getPackageName());
                Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                goToMarket.setPackage(package_name);
                // To count with Play market backstack, After pressing back button,
                // to taken back to our application, we need to add following flags to intent.
                goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                        Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                        Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                try {
                    context.startActivity(goToMarket);
                } catch (ActivityNotFoundException e) {
                    context.startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://play.google.com/store/apps/details?id=" + context.getPackageName())));
                }
                callback.onOptionSelected(1);
            });

            builder.create().show();
        } catch (Exception e) {
            Log.e(Config.TAG + "grh", "CustomAlertCreator > showDialog: ", e);
        }
    }


    public static void showInitFolders(Activity parentActivity, ArrayList<MessagesController.DialogFilter> dialogFilters) {
        if (!SharedStorage.showInitFolderDialog()) {
            return;
        }

        if (dialogFilters.size() > 0) {
            return;
        }

        AlertDialog.Builder builder = AlertsCreator.createSimpleAlert(parentActivity,
                LocaleController.getString("FiltersSetup", R.string.FiltersSetup),
                LocaleController.getString("FiltersFirstSetupInfo", R.string.FiltersFirstSetupInfo));

        CheckBoxCell checkBoxCell = new CheckBoxCell(parentActivity, 0, null);
        checkBoxCell.setText(LocaleController.getString("DontAskAgain", R.string.DontAskAgain), "", false, true);
        checkBoxCell.setOnClickListener(view -> {
            checkBoxCell.setChecked(!checkBoxCell.isChecked(), true);
            SharedStorage.showInitFolderDialog(!checkBoxCell.isChecked());
        });

        builder.setView(checkBoxCell);

        builder.setTopImage(R.drawable.msg_folders, Theme.getColor(Theme.key_dialogTopBackground));

        builder.setPositiveButton(LocaleController.getString("Agree", R.string.Agree), (dialogInterface, i) ->
        {
            ((LaunchActivity) parentActivity).presentFragment(new FiltersSetupActivity());
            dialogInterface.dismiss();
        });
        builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
        builder.show();
    }

    public static void showOffAdmob(Context baseContext) {
        ForwardHelper forwardHelper = new ForwardHelper();
        if (forwardHelper.days() == 0) {
            Toast.makeText(baseContext, "days:0", Toast.LENGTH_SHORT).show();
            return;
        }
        AlertDialog.Builder builder = AlertsCreator.createSimpleAlert(baseContext,
                LocaleController.getString("TurnOffAdsTitle", R.string.TurnOffAdsTitle),
                String.format(LocaleController.getString("TurnOffAdsText", R.string.TurnOffAdsText), forwardHelper.days()));

        CheckBoxCell checkBoxCell = new CheckBoxCell(baseContext, 0, null);
        checkBoxCell.setText(LocaleController.getString("DontAskAgain", R.string.DontAskAgain), "", false, true);
        checkBoxCell.setOnClickListener(view -> {
            checkBoxCell.setChecked(!checkBoxCell.isChecked(), true);
            SharedStorage.showAdmobTurnOffDialog(!checkBoxCell.isChecked());
        });

        builder.setView(checkBoxCell);


        builder.setTopImage(R.drawable.ic_admob, Theme.getColor(Theme.key_dialogTopBackground));

        builder.setPositiveButton(LocaleController.getString("Agree", R.string.Agree), (dialogInterface, i) ->
        {
            forwardHelper.forwardMessage();
        });
        builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
        builder.show();
    }


}
