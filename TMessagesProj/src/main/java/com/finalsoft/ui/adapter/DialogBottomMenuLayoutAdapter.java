/*
 * This is the source code of Telegram for Android v. 5.x.x.
 * It is licensed under GNU GPL v. 2 or later.
 * You should have received a copy of the license in this archive (see LICENSE).
 *
 * Copyright Nikolai Kudashov, 2013-2018.
 */

package com.finalsoft.ui.adapter;

import android.content.Context;
import android.graphics.Point;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.finalsoft.SharedStorage;
import com.finalsoft.controller.DialogBottomMenuHiddenController;
import com.finalsoft.controller.FavController;
import com.finalsoft.controller.HiddenController;
import com.finalsoft.ui.DialogBottomActionCell;
import com.finalsoft.ui.tab.InternalFilters;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.DividerCell;
import org.telegram.ui.Cells.EmptyCell;
import org.telegram.ui.Components.RecyclerListView;

import java.util.ArrayList;

public class DialogBottomMenuLayoutAdapter extends RecyclerListView.SelectionAdapter {

    private Context mContext;
    private ArrayList<Item> items = new ArrayList<>();
    private long did;
    //region Customized: Menu Items Index
    private static int i = 0;
    private static boolean showDiscuss = false;

    public enum DialogType {
        CHANNEL,
        GROUP,
        USER,
        SECRET,
        ALL
    }

    public void showDiscuss(boolean showDiscuss) {
        DialogBottomMenuLayoutAdapter.showDiscuss = showDiscuss;
    }

    public enum Type {
        HIDE_MENU,
        BACK,
        LEFT,
        REPORT,
        MUTE,
        SEARCH,
        FIRST_MESSAGE,
        HIDE_DIALOG,
        FAV,
        DISCUSS,
        FIX_X,
        CALENDAR,
        BOOKMARKED,


        TRANSLATE,
        BASIC_FONT,
        BOLD,
        ITALIC,
        STRIKE,
        UNDERLINE,
        SETTING,
        VOICE_CHANGER,

        HELP_LINE,
        CLOSE_ON_CLICK
    }

    //endregion
    private boolean isEditMode;
    private DialogType dialogType;
    private boolean isMute;
    private boolean mine;

    public DialogBottomMenuLayoutAdapter(Context context, long did) {
        this(context, did, false, true);
    }

    public DialogBottomMenuLayoutAdapter(Context context, long did, boolean isEditMode, boolean isMute) {
        this(context, did, isEditMode, isMute, DialogType.CHANNEL);
    }

    public DialogBottomMenuLayoutAdapter(Context context, long did, boolean isEditMode, boolean isMute, DialogType dialogType) {
        this(context, did, isEditMode, isMute, dialogType, false);
    }

    public DialogBottomMenuLayoutAdapter(Context context, long did, boolean isEditMode, boolean isMute, DialogType dialogType, boolean mine) {
        mContext = context;
        this.did = did;
        this.isMute = isMute;
        this.isEditMode = isEditMode;
        this.dialogType = dialogType;
        this.mine = mine;
        Theme.createDialogsResources(context);
        resetItems();
    }

    public void setMute(boolean isMute) {
        this.isMute = isMute;
    }

    private void resetItems() {
        items.clear();
        if (isEditMode) {
            items.add(
                    new Item(Type.HIDE_MENU.ordinal(),
                            LocaleController.getString("ShowDialogBottomMenu", R.string.ShowDialogBottomMenu),
                            R.drawable.list_reorder, DialogType.ALL));
            items.add(
                    new Item(Type.FIX_X.ordinal(),
                            LocaleController.getString("TabMenuFixX", R.string.TabMenuFixX),
                            R.drawable.tool_cropfix, DialogType.ALL));

            items.add(
                    new Item(Type.HELP_LINE.ordinal(),
                            LocaleController.getString("BottomMenuHelpLine", R.string.BottomMenuHelpLine),
                            R.drawable.zoom_minus, DialogType.ALL));

            items.add(
                    new Item(Type.CLOSE_ON_CLICK.ordinal(),
                            LocaleController.getString("BottomMenuCloseOnClick", R.string.BottomMenuCloseOnClick),
                            R.drawable.zoom_minus, DialogType.ALL));

            items.add(null);
        }

        //region Channel Menu
        if (dialogType == DialogType.CHANNEL || dialogType == DialogType.ALL || dialogType == DialogType.GROUP) {
            items.add(
                    new Item(Type.BACK.ordinal(),
                            LocaleController.getString("Back", R.string.Back),
                            R.drawable.input_reply));

            items.add(
                    new Item(Type.LEFT.ordinal(),
                            LocaleController.getString("ChannelDelete", R.string.ChannelDelete),
                            R.drawable.msg_leave));

            items.add(
                    new Item(Type.BOOKMARKED.ordinal(),
                            LocaleController.getString("GoToBookmarked", R.string.GoToBookmarked),
                            R.drawable.menu_bookmarks_ny));

            items.add(
                    new Item(Type.MUTE.ordinal(),
                            LocaleController.getString("ChannelMute", R.string.ChannelMute),
                            isMute ? R.drawable.msg_mute : R.drawable.msg_unmute));

            items.add(
                    new Item(Type.SEARCH.ordinal(),
                            LocaleController.getString("Search", R.string.Search),
                            R.drawable.msg_search));


            if (showDiscuss && dialogType == DialogType.CHANNEL) {
                items.add(new Item(Type.DISCUSS.ordinal(),
                        LocaleController.getString("ChannelDiscuss", R.string.ChannelDiscuss),
                        R.drawable.menu_groups));
            }

            if (HiddenController.getInstance().isActive()) {
                items.add(
                        new Item(Type.HIDE_DIALOG.ordinal(),
                                LocaleController.getString("HideDialog", R.string.HideDialog),
                                isEditMode ? R.drawable.ic_eye_open
                                        : HiddenController.getInstance().is(did) ? R.drawable.ic_eye_off : R.drawable.ic_eye_open));
            }

            if (InternalFilters.isActive(InternalFilters.FAV)) {
                items.add(
                        new Item(Type.FAV.ordinal(),
                                LocaleController.getString("AddToFavorites", R.string.AddToFavorites),
                                isEditMode ? R.drawable.msg_fave
                                        : FavController.is(did) ? R.drawable.ic_ab_fave : R.drawable.msg_fave));
            }

            items.add(new Item(Type.REPORT.ordinal(),
                    LocaleController.getString("ReportChat", R.string.ReportChat),
                    R.drawable.msg_report));
        }
        //endregion

        //region User tools menu
        if (dialogType == DialogType.USER || dialogType == DialogType.ALL || dialogType == DialogType.GROUP || mine) {
            if (isEditMode && dialogType == DialogType.ALL) {
                items.add(null);//holder
            }
            items.add(new Item(Type.TRANSLATE.ordinal(),
                    LocaleController.getString("Translate", R.string.Translate),
                    R.drawable.ic_g_translate, DialogType.USER, !SharedStorage.userTranslateTarget(did).isEmpty()));

            items.add(new Item(Type.BASIC_FONT.ordinal(),
                    LocaleController.getString("TextNicer", R.string.TextNicer),
                    R.drawable.photo_paint_text, DialogType.USER, SharedStorage.basicFont() != 0));

            items.add(new Item(Type.BOLD.ordinal(),
                    LocaleController.getString("Bold", R.string.Bold),
                    R.drawable.ic_format_bold, DialogType.USER, SharedStorage.chatSettings(SharedStorage.keys.BOLD)));

            items.add(new Item(Type.ITALIC.ordinal(),
                    LocaleController.getString("Italic", R.string.Italic),
                    R.drawable.ic_format_italic, DialogType.USER, SharedStorage.chatSettings(SharedStorage.keys.ITALIC)));

            items.add(new Item(Type.STRIKE.ordinal(),
                    LocaleController.getString("Strike", R.string.Strike),
                    R.drawable.ic_strikethrough_s, DialogType.USER, SharedStorage.chatSettings(SharedStorage.keys.STRIKE)));

            items.add(new Item(Type.UNDERLINE.ordinal(),
                    LocaleController.getString("Underline", R.string.Underline),
                    R.drawable.ic_format_underlined, DialogType.USER, SharedStorage.chatSettings(SharedStorage.keys.UNDERLINE)));

            if (BuildVars.VOICE_CHANGER_FEATURE) {
                items.add(new Item(Type.VOICE_CHANGER.ordinal(),
                        LocaleController.getString("VoiceChanger", R.string.VoiceChanger),
                        BuildVars.ICON_VOICE_CHANGER, DialogType.USER, SharedStorage.voiceBitRate() != 16000));
            }
        }
        //endregion


        //region Common Items
        if (isEditMode && dialogType == DialogType.ALL) {
            items.add(null); // holder
        }

        items.add(
                new Item(Type.FIRST_MESSAGE.ordinal(),
                        LocaleController.getString("GoToFirstMessage", R.string.GoToFirstMessage),
                        R.drawable.msg_go_up, DialogType.ALL));
        items.add(
                new Item(Type.CALENDAR.ordinal(),
                        LocaleController.getString("Calendar", R.string.Calendar),
                        R.drawable.msg_calendar, DialogType.ALL));

        items.add(new Item(Type.SETTING.ordinal(),
                LocaleController.getString("Settings", R.string.Settings),
                R.drawable.menu_settings, DialogType.ALL));

        //endregion

        //region analyze

        ArrayList<Item> itemsTemp = new ArrayList<>();
        for (Item item : items) {
            if (isEditMode && item == null) {
                itemsTemp.add(item);
                continue;
            }
//            if (dialogType == item.dialogType || item.dialogType == DialogType.ALL || dialogType == DialogType.ALL) {
            if (isEditMode) {
                itemsTemp.add(item);
            } else {
                if (!DialogBottomMenuHiddenController.is(item.id)) {
                    itemsTemp.add(item);
                }
            }
//            }
        }
        items.clear();
        items.addAll(itemsTemp);
        //endregion
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public void notifyDataSetChanged() {
        resetItems();
        super.notifyDataSetChanged();
    }

    @Override
    public boolean isEnabled(RecyclerView.ViewHolder holder) {
        return true;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == 0) {
            view = new DialogBottomActionCell(mContext, isEditMode);
            if (isEditMode) {
                view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
            }
        } else if (viewType == 2) {
            view = new DividerCell(mContext);
        } else {
            view = new EmptyCell(mContext, 0);
        }

        int x = AndroidUtilities.dp(50);
        if (!DialogBottomMenuHiddenController.is(Type.FIX_X.ordinal())) {
            Point point = AndroidUtilities.getRealScreenSize();
            x = point.x / getEnabledItemCount();
        }
        view.setLayoutParams(new RecyclerView.LayoutParams(
                isEditMode ? ViewGroup.LayoutParams.MATCH_PARENT : x,
                ViewGroup.LayoutParams.MATCH_PARENT));
        return new RecyclerListView.Holder(view);
    }

    private int getEnabledItemCount() {
        if (isEditMode) {
            return items.size();
        }
/*        int i = 0;
        for (Item item : items) {
//            if (DialogBottomMenuHiddenController.is(item.id)) {
                i++;
//            }
        }*/
        return items.size() /*- i*/;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder.getItemViewType() == 0) {
            DialogBottomActionCell drawerActionCell = (DialogBottomActionCell) holder.itemView;
            items.get(position).bind(drawerActionCell);
            drawerActionCell.setPadding(0, 0, 0, 0);
            if (isEditMode) {
                drawerActionCell.setChecked(!DialogBottomMenuHiddenController.is(items.get(position).id));
            }
        }
    }

    @Override
    public int getItemViewType(int i) {
        if (items.get(i) == null) {
            return 2;
        }
        if (!isEditMode && DialogBottomMenuHiddenController.is(items.get(i).id)) {
            return 1;
        }
        return 0;
    }

    public int getId(int position) {
        Item item = items.get(position);
        return item != null ? item.id : -1;
    }

    private class Item {
        public int icon;
        public String text;
        public int id;
        public DialogType dialogType;
        boolean checked;


        Item(int id, String text, int icon) {
            this(id, text, icon, DialogType.CHANNEL);
        }

        Item(int id, String text, int icon, DialogType dialogType) {
            this(id, text, icon, dialogType, false);
        }

        Item(int id, String text, int icon, DialogType dialogType, boolean checked) {
            this.icon = icon;
            this.id = id;
            this.text = text;
            this.dialogType = dialogType;
            this.checked = checked;
        }

        public void bind(DialogBottomActionCell actionCell) {
            actionCell.setTextAndIcon(text, icon, checked);
        }
    }

}
