package com.finalsoft.controller;

import android.text.TextUtils;

import com.finalsoft.SharedStorage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class DrawerMenuItemsHideController {
    private static DrawerMenuItemsHideController drawerMenuItemsHideController;
    public static DrawerMenuItemsHideController getInstance(){
        if (drawerMenuItemsHideController==null){
            drawerMenuItemsHideController = new DrawerMenuItemsHideController();
        }
        return drawerMenuItemsHideController;
    }

    public void add(int id) {
        try {
            String s = getCsv();
            ArrayList<String> ss = new ArrayList<>();
            if (s != null && !s.isEmpty()) {
                ss.addAll(Arrays.asList(Objects.requireNonNull(s).split(",")));
            }
            ss.add(id + "");
            String sss = TextUtils.join(",", ss);
            setCsv(sss);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void remove(int id) {
        try {
            String items = getCsv();
            if (items != null && !items.isEmpty()) {
                ArrayList<String> menuItems = new ArrayList<>(Arrays.asList(Objects.requireNonNull(items).split(",")));
                for (String item : Objects.requireNonNull(menuItems)) {
                    if (id == Integer.parseInt(item)) {
                        menuItems.remove(item);
                        break;
                    }
                }
                String sss = TextUtils.join(",", menuItems);
                setCsv(sss);
            }

        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    private String csv;

    private String getCsv() {
        if (csv == null || csv.isEmpty()) {
            csv = SharedStorage.hideDrawerMenuItems();
        }
        return csv;
    }

    private void setCsv(String s) {
        csv = s;
        SharedStorage.hideDrawerMenuItems(s);
    }

    public boolean is(int id) {
        try {
            String s = getCsv();
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

    public void clear() {
        setCsv("");
    }

    public void toggle(int id) {
        if (is(id)) {
            remove(id);
        } else {
            add(id);
        }
    }
}
