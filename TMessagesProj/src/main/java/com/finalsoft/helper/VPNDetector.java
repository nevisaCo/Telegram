package com.finalsoft.helper;

import static com.finalsoft.Config.TAG;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.finalsoft.Config;
import com.finalsoft.SharedStorage;

import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.CheckBoxCell;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.SlideChooseView;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collections;

public class VPNDetector {
    private static VPNDetector _vpVpnDetector;
    private static String[] netFaces = new String[]{"tun", "ppp", "pptp", "ppp0", "ppp1"};
    boolean proxyServer = SharedStorage.proxyServer();
    private final String TAG = Config.TAG + "vcs";

    public static VPNDetector getInstance() {
        if (_vpVpnDetector == null) {
            _vpVpnDetector = new VPNDetector();

        }
        return _vpVpnDetector;
    }

    public interface IVpnCallback {
        void onResult(boolean status);
    }

    public void run(@NonNull IVpnCallback iVpnCallback) {
        if (!proxyServer) {
            iVpnCallback.onResult(false);
            Log.i(TAG, "run: the vpn status can't check , proxy server is disable in this app");
            return;
        }

        if (alertDialog != null && alertDialog.isShowing()) {
            Log.i(TAG, "run: the vpn status dialog is showing right now");
            return;
        }

        try {
            for (NetworkInterface networkInterface : Collections.list(NetworkInterface.getNetworkInterfaces())) {
                String netInterfaceItem = networkInterface.getName();
                if (networkInterface.isUp()) {
                    for (String item : netFaces) {
                        if (netInterfaceItem.toLowerCase().contains(item.trim().toLowerCase())) {
                            iVpnCallback.onResult(true);
                            return;
                        }
                    }
                }
            }

        } catch (SocketException e1) {
            e1.printStackTrace();
        }
        iVpnCallback.onResult(false);
    }

    public void showDialog(Context context, @NonNull IVpnCallback callback) {
        String title = LocaleController.getString(R.string.VpnStatus);
        String message = LocaleController.getString(R.string.VpnStatusMessage);

        showDialog(context, title, message, callback);
    }

    AlertDialog alertDialog;

    public void showDialog(Context context, String title, String text, @NonNull IVpnCallback callback) {


        try {
            AlertDialog.Builder builder = AlertsCreator.createSimpleAlert(context, title, text);

            builder.setTopImage(R.drawable.ic_baseline_vpn_key, Theme.getColor(Theme.key_dialogTopBackground));

            CheckBoxCell checkBoxCell = new CheckBoxCell(context, 0, null);
            checkBoxCell.setText(LocaleController.getString("DontAskAgain", R.string.DontAskAgain), "", false, true);
            checkBoxCell.setChecked(SharedStorage.dontShowVpnDialogAgain(), true);
            checkBoxCell.setOnClickListener(view -> {
                checkBoxCell.setChecked(!checkBoxCell.isChecked(), true);
                //save checkbox result for don't show dialog again
                SharedStorage.dontShowVpnDialogAgain(checkBoxCell.isChecked());
            });

            builder.setView(checkBoxCell);

            builder.setNegativeButton(LocaleController.getString(R.string.Cancel), null);

            boolean status = SharedStorage.proxyCustomStatus();

            builder.setPositiveButton(LocaleController.getString(status ? R.string.TurnOffProxy : R.string.TurnOnProxy), (dialog, which) -> {
                callback.onResult(!status);
                SharedStorage.proxyCustomStatus(!status);

                SharedPreferences.Editor editor = MessagesController.getGlobalMainSettings().edit();
                editor.putBoolean("proxy_enabled", !status);
                editor.apply();
                if (!SharedStorage.proxyCustomStatus()) {
                    ConnectionsManager.setProxySettings(false, "", 1080, "", "", "");
                }
                NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.proxySettingsChanged);
            });

            alertDialog = builder.create();
            alertDialog.show();


        } catch (Exception e) {
            Log.e(TAG + "grh", "CustomAlertCreator > showDialog: ", e);
        }
    }
}
