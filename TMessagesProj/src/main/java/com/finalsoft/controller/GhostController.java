package com.finalsoft.controller;

import android.text.TextUtils;

import com.finalsoft.SharedStorage;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.ConnectionsManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

import co.nevisa.commonlib.admob.AdLocation;
import co.nevisa.commonlib.admob.AdmobBaseClass;
import co.nevisa.commonlib.admob.AdmobController;


public class GhostController {
    public static final int DIALOGS = 0;
    public static final int DRAWER = 1;
    public static final int CHAT = 2;
    public static final int ACTIVE_CHANGE = 3;
    public static final int DIALOGS_ACTIVE_CHANGE = 4;
    public static final int DRAWER_ACTIVE_CHANGE = 5;
    public static final int CHAT_ACTIVE_CHANGE = 6;

    public static boolean toggle() {
        boolean m = !status();
        setOn(m);
        return m;
    }

    public static void update() {
        boolean m = status();
        setOn(m);
    }

    public static boolean status() {
        return SharedStorage.ghostMode();
    }

    public static void setOn(boolean status) {
        SharedStorage.ghostMode(status);
        SharedStorage.hideTyping(status);

        MessagesController.getInstance(UserConfig.selectedAccount).updateGhostMode(status);
        ConnectionsManager.getInstance(UserConfig.selectedAccount).updateGhostMode(status);

        AdmobController.getInstance().showInterstitial(AdLocation.INTERSTITIAL_TOGGLE_GHOST);
        
    }

    public static void add(long id) {
        try {
            String s = SharedStorage.ghostDialogs();
            ArrayList<String> ss = new ArrayList<>();
            if (s != null && !s.isEmpty()) {
                ss.addAll(Arrays.asList(Objects.requireNonNull(s).split(",")));
            } else {
                //todo: help how to show hidden dialogs
            }
            ss.add(id + "");
            String sss = TextUtils.join(",", ss);
            SharedStorage.ghostDialogs(sss);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void remove(long id) {
        try {
            String items = SharedStorage.ghostDialogs();
            if (items != null && !items.isEmpty()) {
                ArrayList<String> menuItems =
                        new ArrayList<>(Arrays.asList(Objects.requireNonNull(items).split(",")));
                for (String item : Objects.requireNonNull(menuItems)) {
                    if (id == Long.parseLong(item)) {
                        menuItems.remove(item);
                        break;
                    }
                }
                String sss = TextUtils.join(",", menuItems);
                SharedStorage.ghostDialogs(sss);
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    public static boolean update(long id) {
        boolean is = is(id);
        try {
            if (is) {
                remove(id);
            } else {
                add(id);
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return is;
    }

    public static boolean is(long id) {
        try {
            String s = SharedStorage.ghostDialogs();

            if (s != null && !s.isEmpty()) {
                for (String ss : Objects.requireNonNull(s).split(",")) {
                    if (id == Long.parseLong(ss)) {
                        return true;
                    }
                }
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void clear() {
        SharedStorage.ghostDialogs("");
    }
}
