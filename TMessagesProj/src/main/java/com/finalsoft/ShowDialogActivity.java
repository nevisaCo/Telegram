package com.finalsoft;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.finalsoft.firebase.FireBaseLog;

import org.json.JSONException;
import org.json.JSONObject;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LayoutHelper;

import java.util.Objects;

//import com.squareup.picasso.Callback;
//import com.squareup.picasso.Picasso;

public class ShowDialogActivity extends Activity  {
    private static final String TAG = Config.TAG + "sda";
    private String KEY = "type";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View v = new View(this);
        v.setLayoutParams(new LayoutHelper().createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT
                , Gravity.LEFT | Gravity.RIGHT, 0, 0, 0, 0));
        v.setPadding(0, 0, 0, 0);

        this.setContentView(v);
        String data = Objects.requireNonNull(getIntent().getExtras()).getString("data");

        JSONObject object;
        try {
            object = new JSONObject(data);
            if (object.has("bg_transparent")) {
                v.setBackgroundColor(Color.TRANSPARENT);
            } else {
                v.setBackgroundColor(Theme.getColor(Theme.key_dialogBackground));
            }

            if ("dialog".equals(object.getString(KEY))) {
                createDialog(this, object);
            } /*else if ("admob".equals(object.getString(KEY))) {
                showAdmob(this, object);
            } else if ("video".equals(object.getString(KEY))) {
                showVideo(this, object);
            }*/

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private int i = 1;


    //region Customized: dialog
    private static void createDialog(
            final Activity parentActivity,
            JSONObject o
    ) throws JSONException {

        String url = o.has("url") ? o.getString("url") : "";
        String title = o.has("title") ? o.getString("title") : "";
        String content = o.has("content") ? o.getString("content") : "";
        String img_link = o.has("img_link") ? o.getString("img_link") : "";
        boolean show_header = !o.has("show_header") || o.getBoolean("show_header");

        String btn_ok_text = o.has("btn_ok_text") ? o.getString("btn_ok_text") :
                LocaleController.getString("ContactsPermissionAlertContinue",
                        R.string.ContactsPermissionAlertContinue);

        String btn_cancel_text = o.has("btn_cancel_text") ? o.getString("btn_cancel_text") :
                LocaleController.getString("ContactsPermissionAlertNotNow",
                        R.string.ContactsPermissionAlertNotNow);

        String packageName =
                (o.has("package") && !o.getString("package").isEmpty()) ?
                        o.getString("package") : "";

        AlertDialog.Builder builder = new AlertDialog.Builder(parentActivity);
        if (show_header) {
            builder.setTopImage(R.drawable.profile_info,
                    Theme.getColor(Theme.key_dialogTopBackground));
        }

        if (!title.isEmpty()) {
            builder.setTitle(title);
        }

        if (content.isEmpty()) {
            builder.setMessage(
                    AndroidUtilities.replaceTags(
                            LocaleController.getString("UpdateAppAlert",
                                    R.string.UpdateAppAlert)
                    )
            );
        } else {
            builder.setMessage(
                    AndroidUtilities.replaceTags(content)
            );
        }

        builder.setPositiveButton(btn_ok_text, (dialog, which) -> {
            if (url.isEmpty()) return;
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            if (!packageName.isEmpty()) {
                intent.setPackage(packageName);
            }

            try {
                intent.setData(Uri.parse(url));
                ApplicationLoader.applicationContext.startActivity(intent);
                FireBaseLog.write(FireBaseLog.LogType.DIALOG_AD, "1");
            } catch (ActivityNotFoundException anfe) {
                //intent.setData(Uri.parse(BuildVars.PLAYSTORE_APP_URL + BuildConfig.APPLICATION_ID));
            }
            parentActivity.finish();
        });

        builder.setNegativeButton(btn_cancel_text, (dialog, which) -> {
            parentActivity.finish();
        });

        if (!img_link.isEmpty()) {
            ImageView img = new ImageView(parentActivity);
            img.setScaleType(ImageView.ScaleType.CENTER_CROP);
            img.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT)
            );
//            Picasso.with(parentActivity)
//                    .load(img_link)
//                    .into(img, new Callback() {
//                        @Override
//                        public void onSuccess() {
//                            builder.setView(img);
//                            builder.create().show();
//                        }
//
//                        @Override
//                        public void onError() {
//                            builder.create().show();
//                        }
//                    });
        }
    }
    //endregion



}
