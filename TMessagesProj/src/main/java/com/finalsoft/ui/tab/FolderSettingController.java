package com.finalsoft.ui.tab;

import android.text.TextUtils;

import com.finalsoft.SharedStorage;

import org.telegram.messenger.BuildVars;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class FolderSettingController {
    private static FolderSettingController folderSettingController;

    public static FolderSettingController getInstance() {
        if (folderSettingController == null) {
            folderSettingController = new FolderSettingController();
        }
        return folderSettingController;
    }

    public void add(int accountId,int id) {
        try {
            String s = getCsv(accountId);
            ArrayList<String> ss = new ArrayList<>();
            if (s != null && !s.isEmpty()) {
                ss.addAll(Arrays.asList(Objects.requireNonNull(s).split(",")));
            }
            ss.add(id + "");
            String sss = TextUtils.join(",", ss);
            setCsv( accountId,sss);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void remove(int accountId,int id) {
        try {
            String items = getCsv(accountId);
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
                setCsv(accountId,sss);
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    public boolean update(int accountId,int id) {
        boolean is = is(accountId,id);
        try {
            if (is) {
                remove( accountId,id);
            } else {
                add(accountId,id);
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return is;
    }

    private final Map<Integer,String> csv = new HashMap<>();

    public String getCsv(int accountId) {
        if (csv.get(accountId) == null || Objects.requireNonNull(csv.get(accountId)).isEmpty()) {
            csv.put(accountId, SharedStorage.hiddenTabs(accountId));
        }
        return csv.get(accountId);
    }

    public void setCsv(int accountId, String csv) {
        this.csv.put(accountId, csv);
        SharedStorage.hiddenTabs(accountId,csv);
    }

    public boolean is(int accountId,int id) {
        try {
            String s = getCsv(accountId);
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

    public void clear(int accountId) {
        SharedStorage.hiddenTabs(accountId,"");
    }

    public boolean getFixTab(int accountId) {
        return !is(accountId, FolderLayoutAdapter.FIX_X);
    }

    public boolean getShowOnTitle(int accountId) {
        return !is(accountId,FolderLayoutAdapter.DIALOGS_FILTER_ON_TITLE);
    }

    public boolean getShowArchiveOnTabs(int accountId) {
        return !is(accountId,FolderLayoutAdapter.SHOW_ARCHIVE_ON_TABS);
    }

    public boolean getShowRemoteEmotions(int accountId) {
        return !is(accountId,FolderLayoutAdapter.SHOW_REMOTE_EMOTIONS);
    }

    public boolean getShowUnreadOnly(int accountId) {
        return !is(accountId,FolderLayoutAdapter.SHOW_UNREAD_ONLY);
    }

/*    public static boolean getRemoveActionbarShadow() {
        return !is(FolderLayoutAdapter.ACTIONBAR_SHADOW) && BuildVars.TOOLBAR_SHADOW_FEATURE;
    }*/
}
