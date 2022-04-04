package com.finalsoft.controller;

import android.text.TextUtils;

import com.finalsoft.SharedStorage;

import org.telegram.messenger.UserConfig;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class FavController {
    public static void add(long id) {
        try {
            String s = SharedStorage.FavDialogs(UserConfig.selectedAccount);
            ArrayList<String> ss = new ArrayList<>();
            if (s != null && !s.isEmpty()) {
                ss.addAll(Arrays.asList(Objects.requireNonNull(s).split(",")));
            }
            ss.add(id + "");
            String sss = TextUtils.join(",", ss);
            SharedStorage.FavDialogs(sss, UserConfig.selectedAccount);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void remove(long id) {
        try {
            String items = SharedStorage.FavDialogs(UserConfig.selectedAccount);
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
                SharedStorage.FavDialogs(sss, UserConfig.selectedAccount);
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    public static boolean is(long id) {
        try {
            String s = SharedStorage.FavDialogs(UserConfig.selectedAccount);
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

    public static void clear() {
        SharedStorage.FavDialogs("", UserConfig.selectedAccount);
    }
}
