/*
 * This is the source code of Telegram for Android v. 5.x.x.
 * It is licensed under GNU GPL v. 2 or later.
 * You should have received a copy of the license in this archive (see LICENSE).
 *
 * Copyright Nikolai Kudashov, 2013-2018.
 */

package com.finalsoft.ui.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Point;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.finalsoft.ui.ShareToolTextImageCell;
import com.finalsoft.ui.ShareToolTextCheckCell;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.RecyclerListView;

import java.util.ArrayList;

public class ShareToolsLayoutAdapter extends RecyclerListView.SelectionAdapter {

    private final Context mContext;
    private final boolean show_edit_text;
    private final boolean show_text_row;
    private final ArrayList<Item> items = new ArrayList<>(11);

    public static final int QUOTE = 0;
    public static final int SEND_TEXT = 1;
    public static final int EDIT_TEXT = 2;
    public static final int SMART = 3;
    public static final int SETTING = 4;

    boolean sendText;
    boolean editText;
    boolean smart;
    boolean quote;

    public ShareToolsLayoutAdapter(Context mContext, boolean sendText, boolean editText, boolean smart, boolean quote,
                                   boolean show_edit_text, boolean show_text_row) {
        this.mContext = mContext;
        this.sendText = sendText;
        this.editText = editText;
        this.smart = smart;
        this.quote = quote;
        this.show_edit_text = show_edit_text;
        this.show_text_row = show_text_row;
        Theme.createDialogsResources(mContext);
        resetItems();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void update(boolean sendText, boolean editText, boolean smart, boolean quote) {
        this.sendText = sendText;
        this.editText = editText;
        this.smart = smart;
        this.quote = quote;
        notifyDataSetChanged();
    }


    //endregion

    private void resetItems() {
        items.clear();

        items.add(new Item(QUOTE, LocaleController.getString("ShareTools_Quote", R.string.ShareTools_Quote), quote));

        if (show_text_row) {
            items.add(new Item(SEND_TEXT, LocaleController.getString("ShareTools_Text", R.string.ShareTools_Text), sendText));
        }

        if (BuildVars.SMART_FORWARD_FEATURE) {
            items.add(new Item(SMART, LocaleController.getString("ShareTools_Smart", R.string.ShareTools_Smart), smart));
        }

        if (show_edit_text) {
            items.add(new Item(EDIT_TEXT, LocaleController.getString("ShareTools_Edit", R.string.ShareTools_Edit), editText, R.drawable.msg_edit));
        }

        items.add(new Item(SETTING, LocaleController.getString("Settings", R.string.Settings), editText, R.drawable.msg_settings));

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @SuppressLint("NotifyDataSetChanged")
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
        View view = null;
        Point point = AndroidUtilities.getRealScreenSize();
        switch (viewType) {
            case 0:
                view = new ShareToolTextCheckCell(mContext, 0);
                break;
            case 1: {
                view = new ShareToolTextImageCell(mContext, 0);
                break;
            }
        }

        if (view != null) {
//            view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
            view.setLayoutParams(new RecyclerView.LayoutParams(point.x / items.size(),
                    ViewGroup.LayoutParams.MATCH_PARENT));
        }


        return new RecyclerListView.Holder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder.getItemViewType() == 0) {
            ShareToolTextCheckCell checkBoxCell = (ShareToolTextCheckCell) holder.itemView;
            items.get(position).bind(checkBoxCell);
            checkBoxCell.setPadding(0, 0, 0, 0);
        } else if (holder.getItemViewType() == 1) {
            ShareToolTextImageCell imageCell = (ShareToolTextImageCell) holder.itemView;
            items.get(position).bind(imageCell);
            imageCell.setPadding(0, 0, 0, 0);
        }
    }

    @Override
    public int getItemViewType(int i) {
        int id = getId(i);
        if ((show_edit_text && id == EDIT_TEXT) || id == SETTING) {
            return 1;
        }
        return 0;
    }

    public int getId(int position) {
        Item item = items.get(position);
        return item != null ? item.id : -1;
    }

    private static class Item {
        public String text;
        public int id;
        public int icon;
        public boolean checked;

        Item(int id, String text, boolean checked) {
            this(id, text, checked, 0);
        }

        Item(int id, String text, boolean checked, int icon) {
            this.id = id;
            this.text = text;
            this.checked = checked;
            this.icon = icon;


        }

        public void bind(ShareToolTextCheckCell textCheckCell) {
            textCheckCell.setTextAndCheck(text, checked, false);
        }

        public void bind(ShareToolTextImageCell imageCell) {
            imageCell.setTextAndIcon(text, icon, false);
        }
    }

}
