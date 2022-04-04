package com.finalsoft.controller;

import android.text.TextUtils;

import com.finalsoft.SharedStorage;

import org.telegram.messenger.MessagesController;
import org.telegram.messenger.UserConfig;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class ScheduledController {
    public static void add(long id) {
        try {
            String s = SharedStorage.scheduledDialogs();
            ArrayList<String> ss = new ArrayList<>();
            if (s != null && !s.isEmpty()) {
                ss.addAll(Arrays.asList(Objects.requireNonNull(s).split(",")));
            } else {
            //todo: help how to show hidden dialogs
            }
            ss.add(id + "");
            String sss = TextUtils.join(",", ss);
            SharedStorage.scheduledDialogs(sss);
            MessagesController.getInstance(UserConfig.selectedAccount).sortDialogs(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void remove(long id) {
        try {
            String items = SharedStorage.scheduledDialogs();
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
                SharedStorage.scheduledDialogs(sss);
                MessagesController.getInstance(UserConfig.selectedAccount).sortDialogs(null);
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
            String s = SharedStorage.scheduledDialogs();

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
        SharedStorage.scheduledDialogs("");
    }
}
