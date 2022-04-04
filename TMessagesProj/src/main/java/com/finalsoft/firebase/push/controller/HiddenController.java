package com.finalsoft.firebase.push.controller;


import android.text.TextUtils;
import android.util.Log;
import android.util.LongSparseArray;
import android.util.SparseArray;

import com.finalsoft.Config;
import com.finalsoft.SharedStorage;

import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.TLRPC;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class HiddenController {
    private final String TAG = Config.TAG + "hc";
    private ArrayList<Long> hideList = null;
    private static HiddenController hiddenController;

    public static HiddenController getInstance() {
        if (hiddenController == null) {
            hiddenController = new HiddenController();
        }
        return hiddenController;
    }


    private void toArray() {
        String s = SharedStorage.hiddenAdDialogs();
        hideList = new ArrayList<>();
        if (s != null && !s.isEmpty()) {
            hideList.clear();
            long[] l = Arrays.stream(Objects.requireNonNull(s).split(",")).mapToLong(Long::parseLong).toArray();
            for (long item : l) {
                hideList.add(item);
            }
        } else {
            //todo: help how to show hidden dialogs
        }
    }

    public void add(long id) {
        try {
            toArray();
            hideList.add(-id);
            String sss = TextUtils.join(",", hideList);
            SharedStorage.hiddenAdDialogs(sss);
            Log.i(TAG, "add: " + sss);

/*            //remove user from contacts
            for (TLRPC.TL_contact contact : ContactsController.getInstance(UserConfig.selectedAccount).contacts) {
//                Log.i(TAG, "add: contact:" + contact.user_id);
                if (contact.user_id == id) {
                    ArrayList<TLRPC.User> users = new ArrayList<>();
                    TLRPC.User user = MessagesController.getInstance(0).getUser((int) id);
                    users.add(user);
                    ContactsController.getInstance(UserConfig.selectedAccount).deleteContact(users);
                    Log.i(TAG, "HiddenController > add > deleted from contact: " + id);
                }
            }*/

            hiddenUsers = null;
        } catch (Exception e) {
            Log.e(TAG, "add: ", e);
        }
    }

    public void remove(long id) {
        try {
            toArray();

            hideList.remove(-id);

            String sss = TextUtils.join(",", hideList);
            SharedStorage.hiddenAdDialogs(sss);

            Log.i(TAG, "remove > left " + sss);

/*
            TLRPC.User user = MessagesController.getInstance(0).getUser((int) id);
            if (user == null) {
                Log.i(TAG, "remove: user is null");
            }
            ContactsController.getInstance(UserConfig.selectedAccount).addContact(user, true);
            Log.i(TAG, "HiddenController > remove > added to contact: " + id);
*/
            hiddenUsers = null;
        } catch (NumberFormatException e) {
            Log.e(TAG, "remove: ", e);
        }
    }

    public boolean update(long id) {
        boolean is = is(id);
        try {
            if (is) {
                remove(id);
            } else {
                add(id);
            }
        } catch (NumberFormatException e) {
            Log.e(TAG, "update: ",e );        }
        return is;
    }

    public boolean is(long id) {
        try {
            if (hideList == null) {
                toArray();
            }
            if (hideList == null || hideList.size() == 0) {
                return false;
            }
            if (Arrays.asList(hideList.toArray()).contains(id)) {
                return true;
            }
        } catch (NumberFormatException e) {
            Log.e(TAG, "is: ", e);
        }
        return false;
    }


    public void clear() {
        SharedStorage.hiddenAdDialogs("");
    }


    public ArrayList<Long> getHideList() {
        if (hideList == null) {
            toArray();
        }
        return hideList;
    }

    private LongSparseArray<TLRPC.User> hiddenUsers;

    public LongSparseArray<TLRPC.User> getHiddenUsers() {
        if (hiddenUsers == null) {
            hiddenUsers = new LongSparseArray<>();
            for (long id : getHideList()) {
                TLRPC.User u = MessagesController.getInstance(UserConfig.selectedAccount).getUser(id);
                if (u != null) {
                    hiddenUsers.put(u.id, u);
                }
            }
            Log.i(TAG, "getHiddenUsers: hiddenUsers size:" + hiddenUsers.size());
        }

        return hiddenUsers;
    }


}

/*

import android.text.TextUtils;

import com.finalsoft.SharedStorage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class HiddenController {
    public static void add(int accountIndex, long id) {
        try {
            String s = SharedStorage.hiddenAdDialogs();
            ArrayList<String> ss = new ArrayList<>();
            if (s != null && !s.isEmpty()) {
                ss.addAll(Arrays.asList(Objects.requireNonNull(s).split(",")));
            } else {
            //todo: help how to show hidden dialogs
            }
            ss.add(id + "");
            String sss = TextUtils.join(",", ss);
            SharedStorage.hiddenAdDialogs(sss);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void remove(int accountIndex , long id) {
        try {
            String items = SharedStorage.hiddenAdDialogs();
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
                SharedStorage.hiddenAdDialogs(sss);
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    public static boolean update(int accountIndex ,long id) {
        boolean is = is(id);
        try {
            if (is) {
                remove(accountIndex,id);
            } else {
                add(accountIndex, id);
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return is;
    }

    public static boolean is(long id) {
        try {
            String s = SharedStorage.hiddenAdDialogs();

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
        SharedStorage.hiddenAdDialogs("");
    }
}
*/
