package com.finalsoft.controller;

import android.text.TextUtils;
import android.util.Log;

import com.finalsoft.Config;
import com.finalsoft.SharedStorage;

import org.telegram.messenger.BuildVars;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class MessageMenuController {
    private static final String TAG = Config.TAG + "mmc";

    public static void add(Type id) {
        try {
            String s = SharedStorage.showMessageMenuItem();
            ArrayList<String> ss = new ArrayList<>();
            if (s != null && !s.isEmpty()) {
                ss.addAll(Arrays.asList(Objects.requireNonNull(s).split(",")));
            }
            ss.add(id.ordinal() + "");
            String sss = TextUtils.join(",", ss);
            SharedStorage.showMessageMenuItem(sss);
        } catch (Exception e) {
            Log.e(TAG, "add: ", e);
        }
    }

    public static void remove(Type id) {
        try {
            String items = SharedStorage.showMessageMenuItem();
            if (items != null && !items.isEmpty()) {
                ArrayList<String> menuItems =
                        new ArrayList<>(Arrays.asList(Objects.requireNonNull(items).split(",")));
                for (String item : Objects.requireNonNull(menuItems)) {
                    if (id.ordinal() == Integer.parseInt(item)) {
                        menuItems.remove(item);
                        break;
                    }
                }
                String sss = TextUtils.join(",", menuItems);
                SharedStorage.showMessageMenuItem(sss);
            }
        } catch (NumberFormatException e) {
            Log.e(TAG, "remove: ", e);
        }
    }

    public static boolean update(Type id) {
        boolean is = is(id);
        try {
            if (is) {
                add(id);
            } else {
                remove(id);
            }
        } catch (NumberFormatException e) {
            Log.e(TAG, "update: ", e);
        }
        return is;
    }

    public static boolean is(Type id) {
        if (!BuildVars.NEKO_FEATURE) {
            return false;
        }
        try {
            String s = SharedStorage.showMessageMenuItem();

            if (s != null && !s.isEmpty()) {
                for (String ss : Objects.requireNonNull(s).split(",")) {
                    if (id.ordinal() == Integer.parseInt(ss)) {
                        return false;
                    }
                }
            }
        } catch (NumberFormatException e) {
            Log.e(TAG, "is: ", e);
        }
        return true;
    }

    public static void clear() {
        SharedStorage.showMessageMenuItem("");
    }

    public enum Type {
        SMART_FORWARD,
        FORWARD_WITHOUT_QUOTE,
        COPY_LINK,
        DOWNLOAD,

        TRANSLATE,
        HISTORY,
        PRPR,
        REPEAT,
        SAVE_MESSAGE,
        DELETE_FILES,
        REPORT,
        ADMIN,
        PERMISSION,
        DETAILS,
        SETTINGS
    }

    public static String[] labels = {
            LocaleController.getString("MultipleShare", R.string.MultipleShare),
            LocaleController.getString("NoQuoteForward", R.string.NoQuoteForward),
            LocaleController.getString("CopyLink", R.string.CopyLink),
            LocaleController.getString("AddToDownloads", R.string.AddToDownloads),

            LocaleController.getString("Translate", R.string.Translate),
            LocaleController.getString("ViewHistory", R.string.ViewHistory),
            LocaleController.getString("Prpr", R.string.Prpr),
            LocaleController.getString("Repeat", R.string.Repeat),
            LocaleController.getString("AddToSavedMessages", R.string.AddToSavedMessages),
            LocaleController.getString("DeleteDownloadedFile", R.string.DeleteDownloadedFile),
            LocaleController.getString("ReportChat", R.string.ReportChat),
            LocaleController.getString("EditAdminRights", R.string.EditAdminRights),
            LocaleController.getString("ChangePermissions", R.string.ChangePermissions),
            LocaleController.getString("MessageDetails", R.string.MessageDetails),
            LocaleController.getString("ChatMenusSettings", R.string.ChatMenusSettings),
    };
    public static int[] icons = {
            R.drawable.input_forward,
            R.drawable.input_forward,
            R.drawable.msg_copy,
            R.drawable.msg_download,

            R.drawable.ic_g_translate,
            R.drawable.msg_recent,
            R.drawable.msg_prpr,
            R.drawable.msg_repeat,
            R.drawable.msg_saved,
            R.drawable.msg_delete,
            R.drawable.msg_report,
            R.drawable.msg_admins,
            R.drawable.msg_permissions,
            R.drawable.msg_info,
            R.drawable.msg_settings,
    };
}
