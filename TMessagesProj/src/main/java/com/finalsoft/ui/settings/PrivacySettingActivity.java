/*
 * This is the source code of Telegram for Android v. 5.x.x.
 * It is licensed under GNU GPL v. 2 or later.
 * You should have received a copy of the license in this archive (see LICENSE).
 *
 * Copyright Nikolai Kudashov, 2013-2018.
 */

package com.finalsoft.ui.settings;

import android.content.Context;
import android.text.InputType;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.finalsoft.SharedStorage;
import com.finalsoft.controller.HiddenController;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
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
import org.telegram.ui.LaunchActivity;
import org.telegram.ui.PasscodeActivity;

import java.util.ArrayList;
import java.util.Arrays;

public class PrivacySettingActivity extends BaseFragment {

    private ListAdapter listAdapter;
    private RecyclerListView listView;
    @SuppressWarnings("FieldCanBeLocal")
    private LinearLayoutManager layoutManager;


    private int preventPrivacyRow;
    private int preventPrivacyInfoRow;

    private int hideModeHeaderRow;
    private int hideModeActiveRow;
    private int hideModeNotificationTextRow;
    private int hideModePassCodeRow;
    private int hideModeFakePassCodeRow;
    private int hideModeNoPassCodeRow;
    private int hideModeInfoRow;

    private int profileHeaderRow;
    private int profileShowPhoneRow;
    private int profileFakePhoneRow;
    private int profileInfoRow;


    private int rowCount;


    @Override
    public boolean onFragmentCreate() {
        super.onFragmentCreate();

        rowCount = 0;
        preventPrivacyRow = rowCount++;
        preventPrivacyInfoRow = rowCount++;

        hideModeHeaderRow = rowCount++;
        hideModeActiveRow = rowCount++;
        hideModeNotificationTextRow = rowCount++;
        hideModePassCodeRow = rowCount++;

        hideModeFakePassCodeRow = BuildVars.HIDE_MODE_FAKE_PASS_FEATURE ? rowCount++ : -1;
        hideModeNoPassCodeRow = rowCount++;
        hideModeInfoRow = rowCount++;

        profileHeaderRow = rowCount++;
        profileShowPhoneRow = rowCount++;
        profileFakePhoneRow = BuildVars.FAKE_PHONE_FEATURE ? rowCount++ : -1;

        profileInfoRow = rowCount++;

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
        actionBar.setTitle(LocaleController.getString("PrivacySettings", R.string.PrivacySettings));
        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int id) {
                if (id == -1) {
                    finishFragment();
                }
            }
        });

        ActionBarMenu manu = actionBar.createMenu();
        ActionBarMenuItem clearAll = manu.addItem(0, R.drawable.msg_clear);
        clearAll.setOnClickListener(view -> ((LaunchActivity) getParentActivity()).showPasscodeActivity(true, true, 0, 0, () ->
                {
                    HiddenController.getInstance().clear();
                    Toast.makeText(context, "All Hidden Chats cleared!", Toast.LENGTH_SHORT).show();
                }, null
        ));

        listAdapter = new ListAdapter(context);

        fragmentView = new FrameLayout(context);
        FrameLayout frameLayout = (FrameLayout) fragmentView;
        frameLayout.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));

        listView = new RecyclerListView(context);
        listView.setVerticalScrollBarEnabled(false);
        listView.setLayoutManager(layoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        frameLayout.addView(listView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));
        listView.setAdapter(listAdapter);
        listView.setOnItemClickListener((view, position, x, y) -> {
            if (getParentActivity() == null) {
                return;
            }

            if (position == preventPrivacyRow) {
                boolean xx = SharedStorage.preventPrivacy();
                if (!xx) {
                    if (!HiddenController.getInstance().isActive()) {
                        Toast.makeText(context, LocaleController.getString("ToggleHideModeOn", R.string.ToggleHideModeOn), Toast.LENGTH_LONG).show();
                        return;
                    }
                    if (SharedStorage.hiddenModePassCode().length() == 0) {
                        Toast.makeText(context, LocaleController.getString("HideModeMustSetPass", R.string.HideModeMustSetPass), Toast.LENGTH_LONG).show();
                        return;
                    }
                }
                ((TextCheckCell) view).setChecked(!xx);
                SharedStorage.preventPrivacy(!xx);

            } else if (position == hideModeActiveRow) {
                boolean xx = HiddenController.getInstance().isActive();
                if (xx) {
                    AlertDialog.Builder builder = AlertsCreator.createSimpleAlert(context,
                            "Danger!",
                            LocaleController.getString("HiddenModeClearData", R.string.HiddenModeClearData));
                    builder.setNegativeButton("No", null);
                    builder.setPositiveButton("Yes", (dialog, which) -> {
                        if (SharedStorage.hiddenModePassCode().length() > 0) {
                            ((LaunchActivity) getParentActivity()).showPasscodeActivity(false, true, 0, 0, () ->
                                    {
                                        ((TextCheckCell) view).setChecked(false);
                                        HiddenController.getInstance().setActive(false);
                                        HiddenController.getInstance().clear();
                                        ApplicationLoader.Lock_Mode = false;
                                        getMessagesController().sortDialogs(null);
                                        SharedStorage.hiddenModePassCode("");
                                        SharedStorage.hiddenModeFakePassCode("");
                                    }, null
                            );
                        } else {
                            ((TextCheckCell) view).setChecked(false);
                            HiddenController.getInstance().setActive(false);
                            HiddenController.getInstance().clear();
                            ApplicationLoader.Lock_Mode = false;
                            getMessagesController().sortDialogs(null);
                            SharedStorage.hiddenModePassCode("");
                            SharedStorage.hiddenModeFakePassCode("");
                        }
                    }).create();
                    builder.show();
                } else {
                    HiddenController.getInstance().setActive(true);
                    ((TextCheckCell) view).setChecked(true);
                    presentFragment(new PasscodeActivity(PasscodeActivity.TYPE_HIDE));
                }

            } else if (position == hideModeNoPassCodeRow) {

                boolean xx = !SharedStorage.hideModeNoPass();
                if (xx) {
                    if (SharedStorage.hiddenModePassCode().length() == 0) {
                        return;
                    }
                    AlertDialog.Builder builder = AlertsCreator.createSimpleAlert(context,
                            "Danger!",
                            LocaleController.getString("HiddenModeClearPass", R.string.HiddenModeClearPass));
                    builder.setNegativeButton("No", null);
                    builder.setPositiveButton("Yes", (dialog, which) -> {
                        ((LaunchActivity) getParentActivity()).showPasscodeActivity(true,true,0,0, () ->
                                {
                                    SharedStorage.hideModeNoPass(true);
                                    ((TextCheckCell) view).setChecked(true);
                                    SharedStorage.hiddenModePassCode("");
                                    SharedStorage.hiddenModeFakePassCode("");
                                    SharedStorage.preventPrivacy(false);
                                },null
                        );
                    }).create();
                    builder.show();

                } else {
                    SharedStorage.hideModeNoPass(false);
                    ((TextCheckCell) view).setChecked(false);

                    if (SharedStorage.hiddenModePassCode().length() == 0)
                        presentFragment(new PasscodeActivity(PasscodeActivity.TYPE_HIDE));
                }


            } else if (position == hideModeFakePassCodeRow) {

            } else if (position == profileShowPhoneRow) {
                boolean check = SharedStorage.showPhoneNumber();
                ((TextCheckCell) view).setChecked(!check);
                SharedStorage.showPhoneNumber(!check);
            }

            if (view instanceof NotificationsCheckCell) {
                NotificationsCheckCell checkCell = ((NotificationsCheckCell) view);
                boolean area = LocaleController.isRTL && x <= AndroidUtilities.dp(76) || !LocaleController.isRTL && x >= view.getMeasuredWidth() - AndroidUtilities.dp(76);
                if (!area) {
                    //text clicked
                    if (position == profileFakePhoneRow) {
                        toggleFakePhone(checkCell, context);
                    } else if (position == hideModePassCodeRow) {
                        if (SharedStorage.hiddenModePassCode().length() == 0) {
                            presentFragment(new PasscodeActivity(PasscodeActivity.TYPE_HIDE));
                        }
                    } else if (position == hideModeNotificationTextRow) {
                        getNewMessageText(checkCell, context);
                    }
                } else {
                    //checkbox clicked
                    if (position == profileFakePhoneRow) {
                        if (checkCell.isChecked()) {
                            SharedStorage.fakePhoneNumber("");
                            listAdapter.notifyDataSetChanged();
                        } else {
                            toggleFakePhone(checkCell, context);
                        }
                    } else if (position == hideModePassCodeRow) {
                        if (!checkCell.isChecked()) {
                            presentFragment(new PasscodeActivity(PasscodeActivity.TYPE_HIDE));
                        } else {
                            ((LaunchActivity) getParentActivity()).showPasscodeActivity(true,false,0,0, () ->
                                    {
                                        checkCell.setChecked(false);
                                        SharedStorage.hiddenModePassCode("");
                                        SharedStorage.hiddenModeFakePassCode("");
                                    },null
                            );
                        }
                    } else if (position == hideModeNotificationTextRow) {
                        if (checkCell.isChecked()) {
                            SharedStorage.newMessageText("");
                            listAdapter.notifyDataSetChanged();
                        } else {
                            getNewMessageText(checkCell, context);
                        }
                    }
                }
            }


        });


        return fragmentView;
    }

    private void toggleFakePhone(NotificationsCheckCell checkCell, Context context) {
        String fakePhone = SharedStorage.fakePhoneNumber();
        AlertDialog.Builder builder = AlertsCreator.createSimpleAlert(context,
                LocaleController.getString("ProfileFakePhone", R.string.ProfileFakePhone),
                "");

        EditTextCaption textCaption = new EditTextCaption(context,null);
        textCaption.setHint("15554626904");
        textCaption.setText(fakePhone);
        textCaption.setInputType(InputType.TYPE_CLASS_PHONE);
        builder.setView(textCaption);
        builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
        builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), (dialog, which) -> {
            SharedStorage.fakePhoneNumber(textCaption.getText().toString());
            checkCell.setChecked(textCaption.getText().toString().length() > 0);
            listAdapter.notifyDataSetChanged();
            SharedStorage.showPhoneNumber(textCaption.getText().toString().length() > 0);
//            getNotificationCenter().postNotificationName();
        });
        builder.create().show();
    }

    private void getNewMessageText(NotificationsCheckCell checkCell, Context context) {
        String msg = SharedStorage.newMessageText();
        AlertDialog.Builder builder = AlertsCreator.createSimpleAlert(context,
                LocaleController.getString("HiddenModeNewMessage", R.string.HiddenModeNewMessage),
                "");

        EditTextCaption textCaption = new EditTextCaption(context,null);
        textCaption.setText(msg);
        textCaption.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(textCaption);
        builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
        builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), (dialog, which) -> {
            SharedStorage.newMessageText(textCaption.getText().toString());
            checkCell.setChecked(textCaption.getText().toString().length() > 0);
            listAdapter.notifyDataSetChanged();
        });
        builder.create().show();
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
            return position == hideModeNoPassCodeRow
                    || position == hideModeNotificationTextRow
                    || position == hideModePassCodeRow
                    || position == hideModeActiveRow
                    || position == hideModeInfoRow
                    || position == hideModeFakePassCodeRow
                    || position == profileShowPhoneRow
                    || position == preventPrivacyRow
                    || position == profileFakePhoneRow;
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
                    if (position == hideModeActiveRow) {
                        checkBoxCell.setTextAndCheck(LocaleController.getString("HiddenMode", R.string.HiddenMode),
                                HiddenController.getInstance().isActive(), true);
                    } else if (position == hideModeNoPassCodeRow) {
                        checkBoxCell.setTextAndCheck(LocaleController.getString("HiddenModeNoPass", R.string.HiddenModeNoPass),
                                SharedStorage.hideModeNoPass(), true);
                    } else if (position == profileShowPhoneRow) {
                        checkBoxCell.setTextAndCheck(LocaleController.getString("ProfileShowPhone", R.string.ProfileShowPhone),
                                SharedStorage.showPhoneNumber(), true);
                    } else if (position == preventPrivacyRow) {
                        checkBoxCell.setTextAndCheck(LocaleController.getString("preventPrivacy", R.string.PreventPrivacy),
                                SharedStorage.preventPrivacy(), true);
                    }
                    break;

                case 1:
                    HeaderCell headerCell = (HeaderCell) holder.itemView;
                    if (position == profileHeaderRow) {
                        headerCell.setText(LocaleController.getString("ProfileSettings", R.string.ProfileSettings));
                    } else if (position == hideModeHeaderRow) {
                        headerCell.setText(LocaleController.getString("HiddenMode", R.string.HiddenMode));
                    }
                    break;
                case 2:
                    TextInfoPrivacyCell textInfoPrivacyCell = (TextInfoPrivacyCell) holder.itemView;
                    if (position == hideModeInfoRow) {
                        textInfoPrivacyCell.setText(LocaleController.getString("HideModeInfo", R.string.HideModeInfo));
                    } else if (position == profileInfoRow) {
                        textInfoPrivacyCell.setText(LocaleController.getString("ProfileSettingInfo", R.string.ProfileSettingInfo));
                    } else if (position == preventPrivacyInfoRow) {
                        textInfoPrivacyCell.setText(LocaleController.getString("PreventPrivacyInfo", R.string.PreventPrivacyInfo));
                    }
                    break;
                case 3: {
                    NotificationsCheckCell ncc = (NotificationsCheckCell) holder.itemView;
                    String text = "";
                    String value = "";
                    boolean check = false;
                    if (position == hideModeFakePassCodeRow) {
                        text = LocaleController.getString("HiddenModeFakePass", R.string.HiddenModeFakePass);
                        check = SharedStorage.hiddenModeFakePassCode().length() > 0;
                        value = check ? LocaleController.getString("HideModePassCodeSet", R.string.HideModePassCodeSet) : LocaleController.getString("NotSet", R.string.NotSet);
                    } else if (position == hideModePassCodeRow) {
                        text = LocaleController.getString("HiddenModePass", R.string.HiddenModePass);
                        check = SharedStorage.hiddenModePassCode().length() > 0;
                        value = check ? LocaleController.getString("HideModePassCodeSet", R.string.HideModePassCodeSet) : LocaleController.getString("NotSet", R.string.NotSet);
                    } else if (position == hideModeNotificationTextRow) {
                        text = LocaleController.getString("HiddenModeNewMessage", R.string.HiddenModeNewMessage);
                        check = SharedStorage.newMessageText().length() > 0;
                        value = SharedStorage.newMessageText();
                    } else if (position == profileFakePhoneRow) {
                        text = LocaleController.getString("ProfileFakePhone", R.string.ProfileFakePhone);
                        check = SharedStorage.fakePhoneNumber().length() > 0;
                        value = check ? SharedStorage.fakePhoneNumber() : LocaleController.getString("NotSet", R.string.NotSet);
                    }
                    ncc.setTextAndValueAndCheck(text, value, check, true);
                    break;
                }
                /*case 3: {
                    if (position == costRow) {
                        TextSettingsCell textSettingsCell = (TextSettingsCell) holder.itemView;
                        textSettingsCell.setTextAndValue(LocaleController.getString("Voice2TextCost", R.string.Voice2TextCost),
                                String.format(LocaleController.getString("Voice2TextCostFormat", R.string.Voice2TextCostFormat), SharedStorage.v2tCost()), false);
                    }
                    break;
                }*/
            }
        }

        @Override
        public int getItemViewType(int i) {
            if (i == hideModeHeaderRow || i == profileHeaderRow) {
                return 1;
            }
            if (i == hideModeInfoRow || i == profileInfoRow || i == preventPrivacyInfoRow) {
                return 2;
            }
            if (i == hideModeFakePassCodeRow
                    || i == hideModeNotificationTextRow
                    || i == hideModePassCodeRow
                    || i == profileFakePhoneRow) {
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
