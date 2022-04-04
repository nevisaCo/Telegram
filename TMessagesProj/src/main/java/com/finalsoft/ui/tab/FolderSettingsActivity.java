/*
 * This is the source code of Telegram for Android v. 5.x.x.
 * It is licensed under GNU GPL v. 2 or later.
 * You should have received a copy of the license in this archive (see LICENSE).
 *
 * Copyright Nikolai Kudashov, 2013-2018.
 */

package com.finalsoft.ui.tab;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.finalsoft.Config;
import com.finalsoft.SharedStorage;
import com.finalsoft.ui.TabItemActionCell;

import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.UserConfig;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.NotificationsCheckCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.TextCheckCell;
import org.telegram.ui.Cells.TextDetailSettingsCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;

import java.util.ArrayList;
import java.util.Arrays;

public class FolderSettingsActivity extends BaseFragment {

    private static final String TAG = Config.TAG + "tmsa";
    private RecyclerListView listView;
    private FolderLayoutAdapter adapter;
    String before = "";
    private final int selectedAccount = UserConfig.selectedAccount;

    @Override
    public boolean onFragmentCreate() {
        before = FolderSettingController.getInstance().getCsv(selectedAccount);
        return super.onFragmentCreate();
    }

    @Override
    public void onFragmentDestroy() {
        super.onFragmentDestroy();

    }

    @Override
    public View createView(Context context) {
        actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        actionBar.setAllowOverlayTitle(true);
        actionBar.setTitle(
                LocaleController.getString("TabMenuItemSetting", R.string.TabMenuItemSetting));
        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int id) {
                if (id == -1) {
                    finishFragment();
                }
            }
        });

        fragmentView = new FrameLayout(context);
        FrameLayout frameLayout = (FrameLayout) fragmentView;
        frameLayout.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));

        listView = new RecyclerListView(context);
        listView.setItemAnimator(null);
        listView.setLayoutAnimation(null);
        listView.setLayoutManager(
                new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false) {
                    @Override
                    public boolean supportsPredictiveItemAnimations() {
                        return false;
                    }
                });
        listView.setVerticalScrollBarEnabled(false);
        frameLayout.addView(listView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));
        listView.setAdapter(adapter = new FolderLayoutAdapter(null, context, true));
        listView.setOnItemClickListener((view, position, x, y) -> {
            if (!(view instanceof TabItemActionCell)) {
                return;
            }
            boolean enabled = ((TabItemActionCell) view).isChecked();
            if (getParentActivity() == null) {
                return;
            }
            int id = adapter.getId(position);
            Log.i(TAG, "createView: id:" + id);
            if (enabled) {
                FolderSettingController.getInstance().add(selectedAccount, id);
                if (position == SharedStorage.selectedTabPosition()) {
                    SharedStorage.selectedTabPosition(position == 0 ? 1 : 0);
                }
            } else {
                FolderSettingController.getInstance().remove(selectedAccount,id);
            }

            ((TabItemActionCell) view).setChecked(!enabled);

            String after = FolderSettingController.getInstance().getCsv(selectedAccount);
            if (!before.equals(after)) {
                MessagesController.getInstance(UserConfig.selectedAccount).sortDialogs(null);//2
                NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.updateFragmentSettings);//3


            }
        });

        return fragmentView;
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onResume() {
        super.onResume();
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }


    @Override
    public ArrayList<ThemeDescription> getThemeDescriptions() {
        return new ArrayList<>(Arrays.asList(
                new ThemeDescription(listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{
                        HeaderCell.class, TextCheckCell.class, TextDetailSettingsCell.class,
                        TextSettingsCell.class, NotificationsCheckCell.class
                }, null, null, null, Theme.key_windowBackgroundWhite),
                new ThemeDescription(fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null,
                        Theme.key_windowBackgroundGray),

                new ThemeDescription(actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null,
                        Theme.key_actionBarDefault),
                new ThemeDescription(listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null,
                        Theme.key_actionBarDefault),
                new ThemeDescription(actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null,
                        Theme.key_actionBarDefaultIcon),
                new ThemeDescription(actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null,
                        Theme.key_actionBarDefaultTitle),
                new ThemeDescription(actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null,
                        null, Theme.key_actionBarDefaultSelector),

                new ThemeDescription(listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null,
                        Theme.key_listSelector),

                new ThemeDescription(listView, 0, new Class[]{View.class}, Theme.dividerPaint, null,
                        null, Theme.key_divider),

                new ThemeDescription(listView, 0, new Class[]{HeaderCell.class},
                        new String[]{"textView"}, null, null, null,
                        Theme.key_windowBackgroundWhiteBlueHeader),

                new ThemeDescription(listView, 0, new Class[]{NotificationsCheckCell.class},
                        new String[]{"textView"}, null, null, null,
                        Theme.key_windowBackgroundWhiteBlackText),
                new ThemeDescription(listView, 0, new Class[]{NotificationsCheckCell.class},
                        new String[]{"valueTextView"}, null, null, null,
                        Theme.key_windowBackgroundWhiteGrayText2),
                new ThemeDescription(listView, 0, new Class[]{NotificationsCheckCell.class},
                        new String[]{"checkBox"}, null, null, null, Theme.key_switchTrack),
                new ThemeDescription(listView, 0, new Class[]{NotificationsCheckCell.class},
                        new String[]{"checkBox"}, null, null, null, Theme.key_switchTrackChecked),

                new ThemeDescription(listView, 0, new Class[]{TextCheckCell.class},
                        new String[]{"textView"}, null, null, null,
                        Theme.key_windowBackgroundWhiteBlackText),
                new ThemeDescription(listView, 0, new Class[]{TextCheckCell.class},
                        new String[]{"valueTextView"}, null, null, null,
                        Theme.key_windowBackgroundWhiteGrayText2),
                new ThemeDescription(listView, 0, new Class[]{TextCheckCell.class},
                        new String[]{"checkBox"}, null, null, null, Theme.key_switchTrack),
                new ThemeDescription(listView, 0, new Class[]{TextCheckCell.class},
                        new String[]{"checkBox"}, null, null, null, Theme.key_switchTrackChecked),

                new ThemeDescription(listView, 0, new Class[]{TextSettingsCell.class},
                        new String[]{"textView"}, null, null, null,
                        Theme.key_windowBackgroundWhiteBlackText),
                new ThemeDescription(listView, 0, new Class[]{TextSettingsCell.class},
                        new String[]{"valueTextView"}, null, null, null,
                        Theme.key_windowBackgroundWhiteValueText),

                new ThemeDescription(listView, ThemeDescription.FLAG_BACKGROUNDFILTER,
                        new Class[]{ShadowSectionCell.class}, null, null, null,
                        Theme.key_windowBackgroundGrayShadow),

                new ThemeDescription(listView, 0, new Class[]{TextDetailSettingsCell.class},
                        new String[]{"textView"}, null, null, null,
                        Theme.key_windowBackgroundWhiteBlackText),
                new ThemeDescription(listView, 0, new Class[]{TextDetailSettingsCell.class},
                        new String[]{"valueTextView"}, null, null, null,
                        Theme.key_windowBackgroundWhiteGrayText2),

                new ThemeDescription(listView, ThemeDescription.FLAG_BACKGROUNDFILTER,
                        new Class[]{TextInfoPrivacyCell.class}, null, null, null,
                        Theme.key_windowBackgroundGrayShadow),
                new ThemeDescription(listView, 0, new Class[]{TextInfoPrivacyCell.class},
                        new String[]{"textView"}, null, null, null,
                        Theme.key_windowBackgroundWhiteGrayText4),
                new ThemeDescription(listView, ThemeDescription.FLAG_LINKCOLOR,
                        new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, null, null,
                        null, Theme.key_windowBackgroundWhiteLinkText)
        ));
    }
}
