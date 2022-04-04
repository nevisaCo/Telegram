package com.finalsoft.firebase.push;


import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.Layout;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.ui.Components.LayoutHelper;


public class PushDialogActivity extends Activity {
    TextView txtTitle;
    TextView btnOk;
    TextView txtDetail;
    TextView btnCancel;
    ImageView btnClose;
    ImageView imgIcon;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        View v = initView(this);


        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        Intent intent = getIntent();

        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);

        int width = displaymetrics.widthPixels;
        getWindow().setLayout((int) (width * 0.85), ViewGroup.LayoutParams.WRAP_CONTENT);


        this.setFinishOnTouchOutside(false);


        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(intent.getStringExtra("json"));
            boolean isInstall = jsonObject.getString("key").equals("install");
            String packageName = jsonObject.has("package-name") ? jsonObject.getString("package-name") : "";
            String uri = jsonObject.getString("url");
            String dialogTitle = jsonObject.has("dialog-title") ? jsonObject.getString("dialog-title") : "";
            String dialogContent = jsonObject.has("dialog-content") ? jsonObject.getString("dialog-content") : "";
            String dialogImageUrl = jsonObject.has("dialog-image-url") ? jsonObject.getString("dialog-image-url") : "";
            String btnOkText = jsonObject.has("btn-ok-text") ? jsonObject.getString("btn-ok-text") : LocaleController.getString("OK",R.string.OK);
            String btnCancelText =jsonObject.has("btn-cancel-text")? jsonObject.getString("btn-cancel-text"):"";


            new DisplayImage(imgIcon).execute(dialogImageUrl);

            if (!dialogTitle.isEmpty()) {
                txtTitle.setVisibility(View.VISIBLE);
                txtTitle.setText(dialogTitle);
            }
            btnClose.setOnClickListener(arg0 -> finish());


            imgIcon.setOnClickListener(arg0 -> {
//                if (isInstall) {
//                    new HandleReceivedJson(PushDialogActivity.this, null).download(uri, packageName);
//                    finish();
//                } else {
//
//                }
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                startActivity(browserIntent);
            });

            if (!dialogContent.isEmpty()) {
                txtDetail.setVisibility(View.VISIBLE);
                txtDetail.setText(dialogContent);
            }

            btnOk.setText(btnOkText);
            btnOk.setOnClickListener(arg0 -> imgIcon.callOnClick());

            if (!btnCancelText.isEmpty()) {
                btnCancel.setVisibility(View.VISIBLE);
                btnCancel.setText(btnCancelText);
                btnCancel.setOnClickListener(arg0 -> finish());
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        setContentView(v);

    }



    private View initView(PushDialogActivity pushDialogActivity) {
        //region init elements
        LinearLayout l = new LinearLayout(this);
        l.setOrientation(LinearLayout.VERTICAL);

        LinearLayout titleLinear = new LinearLayout(this);
        titleLinear.setOrientation(LinearLayout.HORIZONTAL);

        txtTitle = new TextView(this);
        titleLinear.addView(txtTitle, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT,
                LayoutHelper.MATCH_PARENT, Gravity.LEFT | Gravity.CENTER_VERTICAL, 0, 0, 22, 0));
        txtTitle.setVisibility(View.GONE);

        btnClose = new ImageView(this);
        btnClose.setImageResource(R.drawable.ic_close_white);
        btnClose.setBackgroundResource(R.drawable.circle);
        titleLinear.addView(btnClose, LayoutHelper.createFrame(20, 20,
                Gravity.RIGHT, 0, 0, 0, 0));

        l.addView(titleLinear);


        imgIcon = new ImageView(this);
        l.addView(imgIcon, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT,
                Gravity.CENTER, 0, 0, 0, 0));
        imgIcon.setVisibility(View.GONE);

        txtDetail = new TextView(this);
        l.addView(txtDetail, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT,
                Gravity.CENTER, 3, 3, 3, 3));
        txtDetail.setVisibility(View.GONE);

        LinearLayout bottomLinear = new LinearLayout(this);
        titleLinear.setOrientation(LinearLayout.HORIZONTAL);

        btnOk = new TextView(this);
        bottomLinear.addView(btnOk, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT,
                Gravity.CENTER, 3, 3, 3, 3));

        btnCancel = new TextView(this);
        bottomLinear.addView(btnCancel, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT,
                Gravity.CENTER, 3, 3, 3, 3));
        btnCancel.setVisibility(View.GONE);

        l.addView(bottomLinear);

        //endregion

        return l;
    }


}