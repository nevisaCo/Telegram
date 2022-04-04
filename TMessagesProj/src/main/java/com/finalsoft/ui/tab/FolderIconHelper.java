package com.finalsoft.ui.tab;

import org.telegram.messenger.UserConfig;

public class FolderIconHelper {

    private static FolderIconHelper folderIconHelper;

    public static FolderIconHelper getInstance() {
        if (folderIconHelper == null) {
            folderIconHelper = new FolderIconHelper();
        }
        return folderIconHelper;
    }

    public enum TabStatus {
        TextOnly,
        IconOnly,
        TextAndIcon
    }

    public TabStatus getStatus(int accountId) {
        boolean showIcon = !FolderSettingController.getInstance().is(accountId,FolderLayoutAdapter.SHOW_ICONS);
        boolean showName = !FolderSettingController.getInstance().is(accountId,FolderLayoutAdapter.SHOW_NAMES);
        if (showIcon && showName) {
            return TabStatus.TextAndIcon;
        }
        return showIcon ? TabStatus.IconOnly : TabStatus.TextOnly;
    }

    public int getIcons(String emotion) {
        try {
            return Integer.parseInt(emotion);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

   /* public int getIcons(int flag) {
        int icon = 0;
        if (flag == Integer.MAX_VALUE) {
            return R.drawable.msg_media;
        }

        for (TLRPC.TL_dialogFilterSuggested suggested : InternalFilters.internalFilters) {
            Log.i("q2w3e4r", "getIcons: suggested:" + suggested.filter.flags + "-" + flag + " , " + suggested.filter.title);
            if (suggested.filter.flags == flag) {
                icon =  suggested.filter.emoticon;
                break;
            }
        }
        return icon;
    }*/
}
