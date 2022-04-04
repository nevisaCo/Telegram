/*
 * This is the source code of Telegram for Android v. 5.x.x.
 * It is licensed under GNU GPL v. 2 or later.
 * You should have received a copy of the license in this archive (see LICENSE).
 *
 * Copyright Nikolai Kudashov, 2013-2018.
 */

package com.finalsoft.ui.tab;

import android.content.Context;
import android.graphics.Point;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.finalsoft.Config;
import com.finalsoft.SharedStorage;
import com.finalsoft.ui.TabItemActionCell;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.DividerCell;
import org.telegram.ui.Cells.EmptyCell;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.DialogsActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Objects;

public class FolderLayoutAdapter extends RecyclerListView.SelectionAdapter
        implements NotificationCenter.NotificationCenterDelegate {


    private static final String TAG = Config.TAG + "tla";
    private Context mContext;
    public ArrayList<Item> items = new ArrayList<>();

    //region Customized: Menu Items Index
    private static int i = 20;
/*    public static int ALL = i++;
    public static int SERVER_ONLY = i++;
    public static int CAN_ADD_USER = i++;
    public static int FORWARD = i++;//3
    public static int USERS = i++;
    public static int CHANNELS = i++;
    public static int GROUPS = i++;
    public static int SEVEN = i++;//7

    public static int SCHEDULED = i=14;
    public static int BOTS = i++;
    public static int FAVORITES = i++;
    public static int UNREAD = i++;
    public static int MINE = i++;
    public static int ONLINE = i++;
    public static int SUPER_GROUPS = i++;*/

    public static int SHOW_TABS = i++;
    public static int SHOW_TOP = i++;
    public static int FIX_X = i++;
    public static int ACTIONBAR_SHADOW = i++;
    public static int DIALOGS_FILTER_ON_TITLE = i++;
    public static int DIALOGS_FILTER_ON_PEN = i++;
    public static int SHOW_UNREAD_ONLY = i++;

    public static int SHOW_ARCHIVE_ON_TABS = i++;
    public static int SHOW_REMOTE_EMOTIONS = i++;
    public static int SHOW_NAMES = i++;
    public static int SHOW_ICONS = i++;


    //endregion
    private boolean isEditMode;
    private DialogsActivity dialogsActivity;

    public FolderLayoutAdapter(DialogsActivity dialogsActivity, Context context) {
        this(dialogsActivity, context, false);
    }

    public FolderLayoutAdapter(DialogsActivity dialogsActivity, Context context, boolean isEditMode) {
        this.dialogsActivity = dialogsActivity;
        mContext = context;
        this.isEditMode = isEditMode;
        Theme.createDialogsResources(context);
        resetItems();
        addObserver();
    }

    private static int selectedTab = 0;

    public static int getSelectedTab() {
        return selectedTab;
    }

    public static void setSelectedTab(int id) {
        selectedTab = id;
    }

    public int size() {
        return getEnabledItemCount();
    }

    public String getTitle(int index) {
        return items.get(index).text;
    }

    public int getIcon(int index) {
        return items.get(index).icon;
    }

    private void resetItems() {
        items.clear();
        //region Customized: Menu Items
        if (isEditMode) {
/*            items.add(
                    new Item(SHOW_TABS,
                            LocaleController.getString("ShowTabMenu", R.string.ShowTabMenu),
                            R.drawable.outline_pack));*/
            items.add(
                    new Item(SHOW_NAMES,
                            LocaleController.getString("ShowFolderName", R.string.ShowFolderName),
                            R.drawable.photo_paint_text));

            items.add(
                    new Item(SHOW_ICONS, LocaleController.getString("ShowFolderIcon", R.string.ShowFolderIcon),
                            R.drawable.ic_smiles2_sad));


            if (BuildVars.DIALOG_FILTER_FEATURE) {
                items.add(null);
                //header
                items.add(
                        new Item(-1,
                                LocaleController.getString("DialogsFilterSetting", R.string.DialogsFilterSetting),
                                0));

                items.add(
                        new Item(DIALOGS_FILTER_ON_TITLE,
                                LocaleController.getString("ShowDialogsOnTitle", R.string.ShowDialogsOnTitle),
                                R.drawable.photo_tools));

                items.add(
                        new Item(DIALOGS_FILTER_ON_PEN,
                                LocaleController.getString("ShowDialogsOnPen", R.string.ShowDialogsOnPen),
                                R.drawable.photo_tools));

                items.add(
                        new Item(SHOW_UNREAD_ONLY,
                                LocaleController.getString("ShowDialogsUnreadOnly", R.string.ShowDialogsUnreadOnly),
                                R.drawable.msg_markunread));

            }
            items.add(null);

            //region Visual features
            //header
            items.add(
                    new Item(-1,
                            LocaleController.getString("TabVisualSettings", R.string.TabVisualSettings),
                            0));

            if (BuildVars.TOOLBAR_SHADOW_FEATURE) {
                items.add(
                        new Item(ACTIONBAR_SHADOW,
                                LocaleController.getString("ShowActionBarShadow", R.string.ShowActionBarShadow),
                                R.drawable.photo_tools));
            }

/*            items.add(
                    new Item(SHOW_TOP,
                            LocaleController.getString("ShowTabMenuOnTop", R.string.ShowTabMenuOnTop),
                            R.drawable.msg_go_up));*/

            items.add(
                    new Item(FIX_X,
                            LocaleController.getString("TabMenuFixX", R.string.TabMenuFixX),
                            R.drawable.tool_cropfix));

            items.add(
                    new Item(SHOW_ARCHIVE_ON_TABS,
                            LocaleController.getString("ShowArchiveOnAllTabs", R.string.ShowArchiveOnAllTabs),
                            R.drawable.msg_archive));

            items.add(
                    new Item(SHOW_REMOTE_EMOTIONS,
                            LocaleController.getString("UseFolderRemoteEmotion", R.string.UseFolderRemoteEmotion),


                            R.drawable.smiles_tab_smiles));
            items.add(null);


            //endregion

            //header
/*            items.add(
                    new Item(-1,
                            LocaleController.getString("TabSelectItems", R.string.TabSelectItems),
                            0));*/
        }


        if (!isEditMode) {
            for (MessagesController.DialogFilter dialogFilter : MessagesController.getInstance(UserConfig.selectedAccount).dialogFilters) {
                Item item = new Item(dialogFilter.id, dialogFilter.name, FolderIconHelper.getInstance().getIcons(dialogFilter.emoticon));
                items.add(item);
            }

            ArrayList<Item> enabled_items = new ArrayList<>();
            enabled_items.clear();
            for (Item item : items) {
                if (item != null) {
                    if (!FolderSettingController.getInstance().is(UserConfig.selectedAccount, item.id)) {
                        enabled_items.add(item);
                    }
                }
            }

            items.clear();
            items.addAll(enabled_items);

            ArrayList<Integer> sorted_items = SharedStorage.sortedTabMenuItems();
            if (sorted_items.size() > 0) {
                Collections.sort(items,
                        Comparator.comparing(item -> sorted_items.indexOf(item.id)));
            }
        }


        //endregion
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    private int getEnabledItemCount() {
        return items.size();
    }

    @Override
    public void notifyDataSetChanged() {
        resetItems();
        Log.i(TAG, "tab view notifyDataSetChanged");
        super.notifyDataSetChanged();
    }

    @Override
    public boolean isEnabled(RecyclerView.ViewHolder holder) {
        return items.get(holder.getAdapterPosition()) != null;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == 0) {
            view = new TabItemActionCell(mContext, isEditMode);
            if (isEditMode) {
                view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
            }
        } else if (viewType == 2) {
            view = new DividerCell(mContext);
        } else if (viewType == 3) {
            view = new HeaderCell(mContext);
            view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
        } else {
            view = new EmptyCell(mContext, 0);
        }

        int x = AndroidUtilities.dp(50);
        if (!FolderSettingController.getInstance().is(UserConfig.selectedAccount, FIX_X)) {
            Point point = AndroidUtilities.getRealScreenSize();
            x = point.x / getEnabledItemCount();
        }

        view.setLayoutParams(new RecyclerView.LayoutParams(
                isEditMode ? ViewGroup.LayoutParams.MATCH_PARENT : viewType == 0 ? x : 0,
                ViewGroup.LayoutParams.MATCH_PARENT));

        return new RecyclerListView.Holder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int viewType = holder.getItemViewType();
        if (viewType == 0) {
            TabItemActionCell drawerActionCell = (TabItemActionCell) holder.itemView;
            items.get(position).bind(drawerActionCell, getSelectedTab() == items.get(position).id);
            if (isEditMode) {
                drawerActionCell.setChecked(!FolderSettingController.getInstance().is(UserConfig.selectedAccount, items.get(position).id));
            } else {
                AndroidUtilities.runOnUIThread(() -> {
                    int[] total = procUnreadCount(position);
                    drawerActionCell.setBadge(total[0], total[1] == 1);
                });
            }
        } else if (viewType == 3) {
            HeaderCell headerCell = (HeaderCell) holder.itemView;
            headerCell.setText(items.get(position).text);
        }
    }

    public int[] procUnreadCount(int position) {
        int id = getId(position);
        int total = 0;
        boolean unmute = false;
        boolean showArchivedInTabMenu = SharedStorage.showArchivedInTabMenu();
        for (TLRPC.Dialog d : Objects.requireNonNull(dialogsActivity.getDialogsArray(UserConfig.selectedAccount, id, 0, false))
        ) {
            if (!showArchivedInTabMenu && d.folder_id != 0) {
                continue;
            }
            if (d.unread_count > 0
                    && !unmute
                    && !MessagesController.getInstance(UserConfig.selectedAccount).isDialogMuted(d.id)
            ) {
                unmute = true;
            }
            total += d.unread_count;

        }

        return new int[]{total, unmute ? 1 : 0};
    }

    @Override
    public int getItemViewType(int i) {
        if (items.get(i) == null) {
            return 2;
        }
        if (!isEditMode && FolderSettingController.getInstance().is(UserConfig.selectedAccount, items.get(i).id)) {
            return 1;
        }
        if (isEditMode && items.get(i).id < 0) {
            return 3;
        }
        return 0;
    }

    public int getId(int position) {
        Log.i(TAG, "getId: position:" + position);
        if (position == Integer.MAX_VALUE) {
            return 0;
        }
        Item item = items.get(position);
        return item != null ? item.id : -1;
    }

    @Override
    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.updateTabCounter
                || id == NotificationCenter.notificationsSettingsUpdated
                || id == NotificationCenter.removeAllMessagesFromDialog
                || id == NotificationCenter.historyCleared
        ) {
            Log.i(TAG, "didReceivedNotification: " + id);
            AndroidUtilities.runOnUIThread(() -> notifyDataSetChanged());

        }
    }

    public class Item {
        public int icon;
        public String text;
        public int id;

        Item(int id, String text, int icon) {
            this.icon = icon;
            this.id = id;
            this.text = text;
        }

        public void bind(TabItemActionCell actionCell, boolean selected) {
            actionCell.setTextAndIcon(text, icon, selected);
        }
    }

    public void removeObserver() {
        //        NotificationCenter.getInstance(UserConfig.selectedAccount).removeObserver(this, NotificationCenter.didReceiveNewMessages);
        //        NotificationCenter.getInstance(UserConfig.selectedAccount).removeObserver(this, NotificationCenter.messagesDeleted);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.updateTabCounter);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.notificationsSettingsUpdated);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.removeAllMessagesFromDialog);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.historyCleared);
//        Log.i(TAG, "removeObserver: ");
    }

    public void addObserver() {
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.updateTabCounter);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.notificationsSettingsUpdated);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.removeAllMessagesFromDialog);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.historyCleared);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.historyCleared);
//        Log.i(TAG, "addObserver: ");
    }

    public ArrayList<Item> getItems() {
        return items;
    }
}
