package com.finalsoft.controller;

import android.text.TextUtils;

import com.finalsoft.SharedStorage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class DialogBottomMenuHiddenController {
    public static void add(int id) {
        try {
            String s = SharedStorage.hiddenDialogBottomMenu();
            ArrayList<String> ss = new ArrayList<>();
            if (s != null && !s.isEmpty()) {
                ss.addAll(Arrays.asList(Objects.requireNonNull(s).split(",")));
            }
            ss.add(id + "");
            String sss = TextUtils.join(",", ss);
            SharedStorage.hiddenDialogBottomMenu(sss);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void remove(int id) {
        try {
            String items = SharedStorage.hiddenDialogBottomMenu();
            if (items != null && !items.isEmpty()) {
                ArrayList<String> menuItems =
                        new ArrayList<>(Arrays.asList(Objects.requireNonNull(items).split(",")));
                for (String item : Objects.requireNonNull(menuItems)) {
                    if (id == Integer.parseInt(item)) {
                        menuItems.remove(item);
                        break;
                    }
                }
                String sss = TextUtils.join(",", menuItems);
                SharedStorage.hiddenDialogBottomMenu(sss);
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    public static boolean update(int id) {
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

    public static boolean is(int id) {
        try {
            String s = SharedStorage.hiddenDialogBottomMenu();

            if (s != null && !s.isEmpty()) {
                for (String ss : Objects.requireNonNull(s).split(",")) {
                    if (id == Integer.parseInt(ss)) {
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
        SharedStorage.hiddenDialogBottomMenu("");
    }

    public static String get() {
        return SharedStorage.hiddenDialogBottomMenu();
    }
}
