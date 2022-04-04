package com.finalsoft.controller;

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

    //region Active
    private boolean isActive;

    public void init() {
        isActive = SharedStorage.hideMode();
    }

    public void setActive(boolean active) {
        SharedStorage.hideMode(active);
        isActive = active;
    }

    public boolean isActive() {
        return isActive;
    }
    //endregion

    private void toArray() {
        String s = SharedStorage.hiddenDialogs();
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
            hideList.add(id);
            String sss = TextUtils.join(",", hideList);
            SharedStorage.hiddenDialogs(sss);
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

            hideList.remove(id);

            String sss = TextUtils.join(",", hideList);
            SharedStorage.hiddenDialogs(sss);

            Log.i(TAG, "remove: " + sss);

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
            e.printStackTrace();
        }
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
            e.printStackTrace();
        }
        return false;
    }

    public boolean is(TLRPC.Dialog d) {
        if (!isActive) {
            return false;
        }

        boolean hideCurrentDialog = is(d.id) || com.finalsoft.firebase.push.controller.HiddenController.getInstance().is(d.id);
        if (ApplicationLoader.Lock_Mode) {
            if (!hideCurrentDialog) {
                return true;
            }
        } else if (hideCurrentDialog) {
            return true;
        }
        return false;
    }

    public void clear() {
        SharedStorage.hiddenDialogs("");
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
