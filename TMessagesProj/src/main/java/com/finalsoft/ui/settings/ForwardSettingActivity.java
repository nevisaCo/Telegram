/*
 * This is the source code of Telegram for Android v. 5.x.x.
 * It is licensed under GNU GPL v. 2 or later.
 * You should have received a copy of the license in this archive (see LICENSE).
 *
 * Copyright Nikolai Kudashov, 2013-2018.
 */

package com.finalsoft.ui.settings;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.InputType;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.finalsoft.Config;
import com.finalsoft.SharedStorage;
import com.finalsoft.helper.ShareHelper;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.NotificationsCheckCell;
import org.telegram.ui.Cells.TextCell;
import org.telegram.ui.Cells.TextCheckCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.EditTextCaption;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;

import java.util.ArrayList;
import java.util.Arrays;

public class ForwardSettingActivity extends BaseFragment {

    private ListAdapter listAdapter;
    private RecyclerListView listView;

    private int activeRow;
    private int activeInfoRow;

    private int smartHeaderRow;
    private int signRow;
    private int targetChannelsRow;
    private int keepOriginalSignRow;
    private int boldSignRow;
    private int smartInfoRow;

    private int settingHeaderRow;
    private int chatToolbarActionRow;


    private int rowCount;


    @Override
    public boolean onFragmentCreate() {
        super.onFragmentCreate();

        rowCount = 0;
        activeRow = rowCount++;
        activeInfoRow = rowCount++;

        smartHeaderRow = rowCount++;
        targetChannelsRow = rowCount++;
        signRow = rowCount++;
        keepOriginalSignRow = rowCount++;
        boldSignRow = rowCount++;
        smartInfoRow = rowCount++;

        settingHeaderRow = rowCount++;
        chatToolbarActionRow = rowCount++;

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
        actionBar.setTitle(LocaleController.getString("ForwardSettings", R.string.ForwardSettings));
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
                    boolean xx = SharedStorage.forwardSetting(ShareHelper.ACTIVE);
                    ((TextCheckCell) view).setChecked(!xx);
                    SharedStorage.forwardSetting(ShareHelper.ACTIVE, !xx);
                } else if (position == targetChannelsRow) {
                    boolean xx = SharedStorage.forwardSetting(ShareHelper.TARGET_PUBLIC);
                    ((TextCheckCell) view).setChecked(!xx);
                    SharedStorage.forwardSetting(ShareHelper.TARGET_PUBLIC, !xx);
                } else if (position == keepOriginalSignRow) {
                    boolean xx = SharedStorage.forwardSetting(ShareHelper.KEEP_ORIGINAL_FOR_PRIVATE);
                    ((TextCheckCell) view).setChecked(!xx);
                    SharedStorage.forwardSetting(ShareHelper.KEEP_ORIGINAL_FOR_PRIVATE, !xx);
                } else if (position == boldSignRow) {
                    boolean xx = SharedStorage.forwardSetting(ShareHelper.BOLD_SIGN);
                    ((TextCheckCell) view).setChecked(!xx);
                    SharedStorage.forwardSetting(ShareHelper.BOLD_SIGN, !xx);
                } else if (position == chatToolbarActionRow) {
                    boolean xx = SharedStorage.forwardSetting(ShareHelper.TOOLBAR_ACTION);
                    ((TextCheckCell) view).setChecked(!xx);
                    SharedStorage.forwardSetting(ShareHelper.TOOLBAR_ACTION, !xx);
                }
                //endregion

            } else if (view instanceof NotificationsCheckCell) {
                //region NotificationsCheckCell
                NotificationsCheckCell checkCell = ((NotificationsCheckCell) view);
                boolean area = LocaleController.isRTL && x <= AndroidUtilities.dp(76) || !LocaleController.isRTL && x >= view.getMeasuredWidth() - AndroidUtilities.dp(76);
                if (!area) {
                    //text clicked
                    if (position == signRow) {
                        toggleSignId(checkCell, context);
                    }
                } else {
                    //checkbox clicked
                    if (position == signRow) {
                        boolean xx = SharedStorage.forwardSetting(ShareHelper.MY_SIGN_FOR_PRIVATE);
                        checkCell.setChecked(!xx);
                        SharedStorage.forwardSetting(ShareHelper.MY_SIGN_FOR_PRIVATE, !xx);
                    }
                }
                //endregion
            }


        });


        return fragmentView;
    }

    @SuppressLint("NotifyDataSetChanged")
    private void toggleSignId(NotificationsCheckCell checkCell, Context context) {
        String mySign = SharedStorage.smartForwardSign();
        AlertDialog.Builder builder = AlertsCreator.createSimpleAlert(context,
                LocaleController.getString("SmartShareSign", R.string.SmartShareSign),
                LocaleController.getString("SmartShareSignText", R.string.SmartShareSignText));

        EditTextCaption textCaption = new EditTextCaption(context, null);
        textCaption.setHint(Config.OFFICIAL_CHANNELS);
        textCaption.setText(mySign);
        textCaption.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(textCaption);
        builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
        builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), (dialog, which) -> {
            SharedStorage.smartForwardSign(textCaption.getText().toString());
            boolean status = textCaption.getText().toString().length() > 0;
            checkCell.setChecked(status);
            SharedStorage.forwardSetting(ShareHelper.MY_SIGN_FOR_PRIVATE, status);
            listAdapter.notifyDataSetChanged();
        });
        builder.create().show();
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onResume() {
        super.onResume();
        if (listAdapter != null) {
            listAdapter.notifyDataSetChanged();
        }
    }

    private class ListAdapter extends RecyclerListView.SelectionAdapter {

        private final Context mContext;

        public ListAdapter(Context context) {
            mContext = context;
        }

        @Override
        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            int position = holder.getAdapterPosition();
            return position == keepOriginalSignRow
                    || position == boldSignRow
                    || position == signRow
                    || position == targetChannelsRow
                    || position == smartInfoRow
                    || position == chatToolbarActionRow
                    || position == activeRow
                    ;
        }

        @Override
        public int getItemCount() {
            return rowCount;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
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
                    view.setBackground(Theme.getThemedDrawable(mContext, R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
                    break;
                case 3:
                    view = new NotificationsCheckCell(mContext);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
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
                    if (position == targetChannelsRow) {
                        checkBoxCell.setTextAndCheck(LocaleController.getString("SmartShareTargetChannels", R.string.SmartShareTargetChannels),
                                SharedStorage.forwardSetting(ShareHelper.TARGET_PUBLIC), true);
                    } else if (position == keepOriginalSignRow) {
                        checkBoxCell.setTextAndCheck(LocaleController.getString("SmartShareKeepOriginalId4Other", R.string.SmartShareKeepOriginalId4Other),
                                SharedStorage.forwardSetting(ShareHelper.KEEP_ORIGINAL_FOR_PRIVATE), true);
                    } else if (position == boldSignRow) {
                        checkBoxCell.setTextAndCheck(LocaleController.getString("SmartShareBoldSign", R.string.SmartShareBoldSign),
                                SharedStorage.forwardSetting(ShareHelper.BOLD_SIGN), true);
                    } else if (position == chatToolbarActionRow) {
                        checkBoxCell.setTextAndCheck(LocaleController.getString("ShareActionBarAction", R.string.ShareActionBarAction),
                                SharedStorage.forwardSetting(ShareHelper.TOOLBAR_ACTION), true);
                    } else if (position == activeRow) {
                        checkBoxCell.setTextAndCheck(LocaleController.getString("ProForwardActive", R.string.ProForwardActive),
                                SharedStorage.forwardSetting(ShareHelper.ACTIVE), true);
                    }
                    break;

                case 1:
                    HeaderCell headerCell = (HeaderCell) holder.itemView;
                    if (position == settingHeaderRow) {
                        headerCell.setText(LocaleController.getString("OtherSetting", R.string.OtherSetting));
                    } else if (position == smartHeaderRow) {
                        headerCell.setText(LocaleController.getString("SmartShare", R.string.SmartShare));
                    }
                    break;
                case 2:
                    TextInfoPrivacyCell textInfoPrivacyCell = (TextInfoPrivacyCell) holder.itemView;
                    if (position == smartInfoRow) {
                        textInfoPrivacyCell.setText(LocaleController.getString("SmartShareInfo", R.string.SmartShareInfo));
                    } else if (position == activeInfoRow) {
                        textInfoPrivacyCell.setText(LocaleController.getString("ShareActiveInfo", R.string.ShareActiveInfo));
                    }
                    break;
                case 3: {
                    NotificationsCheckCell ncc = (NotificationsCheckCell) holder.itemView;
                    String text = "";
                    String value = "";
                    boolean check = false;
                    if (position == signRow) {
                        text = LocaleController.getString("SmartShareSign", R.string.SmartShareSign);
                        check = SharedStorage.smartForwardSign().length() > 0;
                        value = check ? SharedStorage.smartForwardSign() : LocaleController.getString("NotSet", R.string.NotSet);
                    }
                    ncc.setTextAndValueAndCheck(text, value, check, true);
                    break;
                }

            }
        }

        @Override
        public int getItemViewType(int i) {
            if (i == smartHeaderRow || i == settingHeaderRow) {
                return 1;
            }
            if (i == smartInfoRow || i == activeInfoRow) {
                return 2;
            }
            if (i == signRow) {
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
