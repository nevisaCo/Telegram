/*
 * This is the source code of Telegram for Android v. 5.x.x.
 * It is licensed under GNU GPL v. 2 or later.
 * You should have received a copy of the license in this archive (see LICENSE).
 *
 * Copyright Nikolai Kudashov, 2013-2018.
 */

package com.finalsoft.ui.settings;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.finalsoft.SharedStorage;
import com.finalsoft.controller.GhostController;

import org.telegram.messenger.BuildVars;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.TextCell;
import org.telegram.ui.Cells.TextCheckCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;

import java.util.ArrayList;
import java.util.Arrays;

public class GhostSettingActivity extends BaseFragment {

    private ListAdapter listAdapter;
    private RecyclerListView listView;

    private int activeRow;
    private int activeInfoRow;

    private int showHeaderRow;
    private int showInDrawerRow;
    private int showInDialogsRow;
    private int showInChatRow;


    private int rowCount;


    @Override
    public boolean onFragmentCreate() {
        super.onFragmentCreate();

        rowCount = 0;
        activeRow = rowCount++;
        activeInfoRow = rowCount++;

        showHeaderRow = rowCount++;
        if (BuildVars.PROFILE_CELL_ICONS_FEATURE) {
            showInDrawerRow = rowCount++;
        }
        showInDialogsRow = rowCount++;
        if (BuildVars.PRIVATE_GHOST_FEATURE) {
            showInChatRow = rowCount++;
        }


        return true;
    }

    @Override
    public void onFragmentDestroy() {
        super.onFragmentDestroy();
    }


    @Override
    public View createView(Context context) {
        actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        actionBar.setAllowOverlayTitle(true);
        actionBar.setTitle(LocaleController.getString("GhostModeSettings", R.string.GhostModeSettings));
        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int id) {
                if (id == -1) {
                    finishFragment();
                }
            }
        });
        ActionBarMenu menu = actionBar.createMenu();
        ActionBarMenuItem clearAll = menu.addItem(0, R.drawable.msg_clear);
        clearAll.setOnClickListener(view -> {
            GhostController.clear();
            Toast.makeText(context, "Clear All!", Toast.LENGTH_SHORT).show();
        });


        listAdapter = new ListAdapter(context);

        fragmentView = new FrameLayout(context);
        FrameLayout frameLayout = (FrameLayout) fragmentView;
        frameLayout.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));

        listView = new RecyclerListView(context);
        listView.setVerticalScrollBarEnabled(false);
        listView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        frameLayout.addView(listView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));
        listView.setAdapter(listAdapter);
        listView.setOnItemClickListener((view, position, x, y) -> {
            if (getParentActivity() == null) {
                return;
            }
            if (view instanceof TextCheckCell) {
                //region TextCheckCell
                if (position == activeRow) {
                    boolean xx = SharedStorage.ghostModeActive();
                    ((TextCheckCell) view).setChecked(!xx);
                    SharedStorage.ghostModeActive(!xx);
                    if (xx) {
                        SharedStorage.ghostMode(false);
                    }
                    NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.updateGhostMode, GhostController.ACTIVE_CHANGE);
                } else if (position == showInChatRow) {
                    boolean xx = SharedStorage.showGhostInChat();
                    ((TextCheckCell) view).setChecked(!xx);
                    SharedStorage.showGhostInChat(!xx);
                    NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.updateGhostMode, GhostController.CHAT_ACTIVE_CHANGE);
                } else if (position == showInDialogsRow) {
                    boolean xx = SharedStorage.showGhostInDialogs();
                    ((TextCheckCell) view).setChecked(!xx);
                    SharedStorage.showGhostInDialogs(!xx);
                    NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.updateGhostMode, GhostController.DIALOGS_ACTIVE_CHANGE);
                } else if (position == showInDrawerRow) {
                    boolean xx = SharedStorage.showGhostInDrawer();
                    ((TextCheckCell) view).setChecked(!xx);
                    SharedStorage.showGhostInDrawer(!xx);
                    NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.updateGhostMode, GhostController.DRAWER_ACTIVE_CHANGE);
                }

                NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.updateFragmentSettings);

                //endregion
            }
        });


        return fragmentView;
    }


    @Override
    public void onResume() {
        super.onResume();
        if (listAdapter != null) {
            listAdapter.notifyDataSetChanged();
        }
    }

    private class ListAdapter extends RecyclerListView.SelectionAdapter {

        private Context mContext;

        public ListAdapter(Context context) {
            mContext = context;
        }

        @Override
        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            int position = holder.getAdapterPosition();
            return position == showInChatRow
                    || position == showInDialogsRow
                    || position == showInDrawerRow
                    || position == activeRow
                    ;
        }

        @Override
        public int getItemCount() {
            return rowCount;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            switch (viewType) {
                case 0:
                    view = new TextCheckCell(mContext);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                case 1:
                    view = new HeaderCell(mContext, 16);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                case 2:
                    view = new TextInfoPrivacyCell(mContext);
                    view.setBackgroundDrawable(Theme.getThemedDrawable(mContext, R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
                    break;
                case 4:
                    view = new TextCell(mContext);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                case 5:
                default:
                    view = new TextSettingsCell(mContext);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
            }
            return new RecyclerListView.Holder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            switch (holder.getItemViewType()) {
                case 0:
                    TextCheckCell checkBoxCell = (TextCheckCell) holder.itemView;
                    if (position == showInChatRow) {
                        checkBoxCell.setTextAndCheck(LocaleController.getString("GhostModeShowInChat", R.string.GhostModeShowInChat),
                                SharedStorage.showGhostInChat(), true);
                    } else if (position == showInDialogsRow) {
                        checkBoxCell.setTextAndCheck(LocaleController.getString("GhostModeShowInDialogs", R.string.GhostModeShowInDialogs),
                                SharedStorage.showGhostInDialogs(), true);
                    } else if (position == showInDrawerRow) {
                        checkBoxCell.setTextAndCheck(LocaleController.getString("GhostModeShowInDrawer", R.string.GhostModeShowInDrawer),
                                SharedStorage.showGhostInDrawer(), true);
                    } else if (position == activeRow) {
                        checkBoxCell.setTextAndCheck(LocaleController.getString("GhostModeActive", R.string.GhostModeActive),
                                SharedStorage.ghostModeActive(), true);
                    }
                    break;

                case 1:
                    HeaderCell headerCell = (HeaderCell) holder.itemView;
                    if (position == showHeaderRow) {
                        headerCell.setText(LocaleController.getString("GhostModeLocations", R.string.GhostModeLocations));
                    }
                    break;
                case 2:
                    TextInfoPrivacyCell textInfoPrivacyCell = (TextInfoPrivacyCell) holder.itemView;
                    if (position == activeInfoRow) {
                        textInfoPrivacyCell.setText(LocaleController.getString("GhostModeActiveInfo", R.string.GhostModeActiveInfo));
                    }
                    break;


            }
        }

        @Override
        public int getItemViewType(int i) {
            if (i == showHeaderRow) {
                return 1;
            }
            if (i == activeInfoRow) {
                return 2;
            }
            return 0;
        }

    }

    @Override
    public ArrayList<ThemeDescription> getThemeDescriptions() {
        return new ArrayList<>(Arrays.asList(
                new ThemeDescription(listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{TextSettingsCell.class}, null, null, null, Theme.key_windowBackgroundWhite),
                new ThemeDescription(fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundGray),

                new ThemeDescription(actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_actionBarDefault),
                new ThemeDescription(listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, Theme.key_actionBarDefault),
                new ThemeDescription(actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, Theme.key_actionBarDefaultIcon),
                new ThemeDescription(actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, Theme.key_actionBarDefaultTitle),
                new ThemeDescription(actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_actionBarDefaultSelector),

                new ThemeDescription(listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, Theme.key_listSelector),

                new ThemeDescription(listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText),
                new ThemeDescription(listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"valueTextView"}, null, null, null, Theme.key_windowBackgroundWhiteValueText),

                new ThemeDescription(listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, null, null, null, Theme.key_windowBackgroundGrayShadow),
                new ThemeDescription(listView, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText4)
        ));
    }
}
