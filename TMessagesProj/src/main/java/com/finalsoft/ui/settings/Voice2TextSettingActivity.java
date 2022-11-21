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

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.finalsoft.SharedStorage;
import com.finalsoft.admob.AdmobController;
import com.finalsoft.helper.AdDialogHelper;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.TextCell;
import org.telegram.ui.Cells.TextCheckCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.LanguageSelectActivity;

import java.util.ArrayList;
import java.util.Arrays;

public class Voice2TextSettingActivity extends BaseFragment {

    private ListAdapter listAdapter;
    private RecyclerListView listView;
    @SuppressWarnings("FieldCanBeLocal")
    private LinearLayoutManager layoutManager;


    private int showRow;
    private int signRow;
    private int signInfoRow;
    private int sendRow;
    private int sendInfoRow;
    private int rowCount;
    private int costRow;
    private int costInfoRow;
    private int languageRow;

    @Override
    public boolean onFragmentCreate() {
        super.onFragmentCreate();

        rowCount = 0;
        languageRow = rowCount++;
        showRow = rowCount++;
        sendRow = rowCount++;
        sendInfoRow = rowCount++;
        signRow = rowCount++;
        signInfoRow = rowCount++;
        costRow = rowCount++;
        costInfoRow = rowCount++;

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
        actionBar.setTitle(LocaleController.getString("Voice2TextSettings", R.string.Voice2TextSettings));
        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int id) {
                if (id == -1) {
                    finishFragment();
                }
            }
        });

        listAdapter = new ListAdapter(context);

        fragmentView = new FrameLayout(context);
        FrameLayout frameLayout = (FrameLayout) fragmentView;
        frameLayout.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));

        listView = new RecyclerListView(context);
        listView.setVerticalScrollBarEnabled(false);
        listView.setLayoutManager(layoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        frameLayout.addView(listView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));
        listView.setAdapter(listAdapter);
        listView.setOnItemClickListener((view, position) -> {
            if (getParentActivity() == null) {
                return;
            }
            if (position == signRow) {
                boolean x = SharedStorage.addV2TSign();
                ((TextCheckCell) view).setChecked(!x);
                SharedStorage.addV2TSign(!x);
            } else if (position == showRow) {
                boolean x = SharedStorage.showV2TUser();
                ((TextCheckCell) view).setChecked(!x);
                SharedStorage.showV2TUser(!x);
            } else if (position == sendRow) {
                boolean x = SharedStorage.sendMessageAfterV2T();
                ((TextCheckCell) view).setChecked(!x);
                SharedStorage.sendMessageAfterV2T(!x);
            } else if (position == languageRow) {
                presentFragment(new LanguageSelectActivity(LanguageSelectActivity.Type.V2T));
            } else if (position == costRow) {
                int v2tCost = BuildVars.DEBUG_VERSION ? 1 : SharedStorage.v2tCost();
                int reward = SharedStorage.rewards();
                boolean video_error = SharedStorage.admobVideoErrorList();
                new AdDialogHelper(getParentActivity()).show(
                        null, String.format(LocaleController.getString("GetCoinsText", R.string.GetCoinsText),
                                reward,
                                v2tCost,
                                video_error ? 0 : SharedStorage.videoRewards(),
                                SharedStorage.interstitialRewards()
                        ), param -> {
                            if (param == 1) {
                                //video
                                NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.showAdmobRewarded, AdmobController.VIDEO_USE_V2T, true);
                            } else {
                                //interstitial
                                NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.showAdmobInterstitial, AdmobController.INTERSTITIAL_USE_V2T, true);
                            }
                        }, false);
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
            return position == costRow
                    || position == sendRow
                    || position == signRow
                    || position == showRow
                    || position == languageRow;
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
                    view = new TextInfoPrivacyCell(mContext);
                    view.setBackgroundDrawable(Theme.getThemedDrawable(mContext, R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
                    break;
                case 2:
                    view = new TextCell(mContext);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                case 3:
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
                    if (position == sendRow) {
                        checkBoxCell.setTextAndCheck(LocaleController.getString("SendVoiceOnCompleted", R.string.SendVoiceOnCompleted),
                                SharedStorage.sendMessageAfterV2T(), true);
                    } else if (position == signRow) {
                        checkBoxCell.setTextAndCheck(LocaleController.getString("SignVoice2Text", R.string.SignVoice2Text),
                                SharedStorage.addV2TSign(), true);
                    } else if (position == showRow) {
                        checkBoxCell.setTextAndCheck(LocaleController.getString("ShowVoice2Text", R.string.ShowVoice2Text),
                                SharedStorage.showV2TUser(), true);
                    }
                    break;
                case 1:
                    TextInfoPrivacyCell privacyCell = (TextInfoPrivacyCell) holder.itemView;
                    if (position == costInfoRow) {
                        privacyCell.setText(LocaleController.getString("Voice2TextCostInfo", R.string.Voice2TextCostInfo));
                    } else if (position == sendInfoRow) {
                        privacyCell.setText(LocaleController.getString("Voice2TextSendInfo", R.string.Voice2TextSendInfo));
                    } else if (position == signInfoRow) {
                        privacyCell.setText(AndroidUtilities.replaceTags(LocaleController.getString("Voice2TextSignInfo", R.string.Voice2TextSignInfo)));
                    }
                    break;
                case 2: {
                    if (position == languageRow) {
                        TextCell textCell = (TextCell) holder.itemView;
                        textCell.setTextAndIcon(LocaleController.getString("Voice2TextLanguage", R.string.Voice2TextLanguage),
                                R.drawable.msg_language, true);

                    }
                    break;
                }
                case 3: {
                    if (position == costRow) {
                        TextSettingsCell textSettingsCell = (TextSettingsCell) holder.itemView;
                        textSettingsCell.setTextAndValue(LocaleController.getString("Voice2TextCost", R.string.Voice2TextCost),
                                String.format(LocaleController.getString("Voice2TextCostFormat", R.string.Voice2TextCostFormat), SharedStorage.v2tCost()), false);
                    }
                    break;
                }
            }
        }

        @Override
        public int getItemViewType(int i) {
            if (i == costInfoRow || i == sendInfoRow || i == signInfoRow) {
                return 1;
            }
            if (i == languageRow) {
                return 2;
            }
            if (i == costRow) {
                return 3;
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
