package com.finalsoft.helper;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.finalsoft.Config;
import com.finalsoft.SharedStorage;

import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BuildConfig;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.R;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.TLRPC;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ShareHelper {
    private static final String TAG = Config.TAG + "sh";

    public Intent getShareIntent(Context context) {
        if (context == null) {
            context = ApplicationLoader.applicationContext;
        }

        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("text/plain");

        // Add data to the intent, the receiving app will decide
        // what to do with it.
        share.putExtra(Intent.EXTRA_SUBJECT, LocaleController.getString("AppName", R.string.AppName));
        String str =
                String.format(LocaleController.getString("ShareAppContent", R.string.ShareAppContent)
                        , BuildVars.PLAY_STORE_SITE_URL + BuildConfig.APPLICATION_ID);

        share.putExtra(Intent.EXTRA_TEXT, str);

        return Intent.createChooser(share, "Share link!");
    }

    public static final int KEEP_ORIGINAL_FOR_PRIVATE = 0;
    public static final int TARGET_PUBLIC = 1;
    public static final int MY_SIGN_FOR_PRIVATE = 2;
    public static final int TOOLBAR_ACTION = 3;
    public static final int ACTIVE = 4;
    public static final int BOLD_SIGN = 5;

    boolean keepId4Private;
    boolean targetPublic;
    boolean mySign4Private;
    boolean boldSign;
    String mySign;

    public ShareHelper() {

    }

    public ShareHelper(Context context) {
        keepId4Private = SharedStorage.forwardSetting(KEEP_ORIGINAL_FOR_PRIVATE);
        targetPublic = SharedStorage.forwardSetting(TARGET_PUBLIC);
        mySign4Private = SharedStorage.forwardSetting(MY_SIGN_FOR_PRIVATE);
        boldSign = SharedStorage.forwardSetting(BOLD_SIGN);
        mySign = SharedStorage.smartForwardSign();
    }

    public String doSmartMessage(String msgTxt, long did) {
        try {
            String myId = "";
            Log.i(TAG, "doSmartMessage: did:" + did);

            TLRPC.Chat chat = MessagesController.getInstance(UserConfig.selectedAccount).getChat(-did);
            if (targetPublic && chat != null && chat.username != null) {
                myId = "@" + chat.username;
                Log.i(TAG, "doSmartMessage: user name : " + chat.username);
            } else {
                if (keepId4Private) {
                    return msgTxt;
                } else if (mySign4Private) {
                    myId = mySign.startsWith("@") ? mySign : "@" + mySign;
                }
            }
            if (myId.isEmpty()) {
                return msgTxt;
            }
            String s = msgTxt;
            for (String regix : new String[]{"@", ".me"}) {
                if (!msgTxt.contains(regix)) {
                    continue;
                }
                String[] arr = msgTxt.split(regix);
                List<String> list = new ArrayList<>();
                list.addAll(Arrays.asList(arr));
                list.remove(0);
                for (String item : list) {
                    if (item.trim().isEmpty()) {
                        continue;
                    }
                    String id;
                    Log.i(TAG, "doSmartMessage: item:" + item);
                    String[] iid = item.split("[ \n]");
                    if (iid.length > 0) {
                        id = iid[0];
                        String r = id.startsWith("/") ? regix + myId.replace("@", "/") : myId;
                        if (boldSign && !id.startsWith("/")) {
                            r = String.format("**%s**", r);
                        }
                        s = s.replace(regix + id, r);
                        Log.i(TAG, "doSmartMessage: id:" + id);
                    }

                }
            }
            return s;
        } catch (Exception e) {
            Log.e(TAG, "doSmartMessage > error:", e);
        }
        return msgTxt;
    }
}
