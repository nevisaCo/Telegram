/*
 * This is the source code of Telegram for Android v. 5.x.x.
 * It is licensed under GNU GPL v. 2 or later.
 * You should have received a copy of the license in this archive (see LICENSE).
 *
 * Copyright Nikolai Kudashov, 2013-2018.
 */

package com.finalsoft.ui.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.res.Configuration;
import android.os.Build;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.finalsoft.Config;
import com.finalsoft.SharedStorage;
import com.finalsoft.account.AccountSettingsActivity;
import com.finalsoft.controller.HiddenController;
import com.finalsoft.ui.font.TextNicerActivity;
import com.finalsoft.ui.tab.FolderSettingsActivity;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.R;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.TextCell;
import org.telegram.ui.Cells.TextDetailCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.voip.VoIPHelper;
import org.telegram.ui.LaunchActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Locale;

import static com.finalsoft.Config.TAG;

public class CustomSettingsActivity extends BaseFragment {

    private RecyclerListView listView;
    private ListAdapter listAdapter;
    private LinearLayoutManager layoutManager;
    ArrayList<SettingItem> settingItems = new ArrayList<>();

    private int versionRow;

    private int chatSettingRow;
    private int drawerMenuItemSetting;
    private int dialogBottomMenuItemSetting;
    private int tabMenuItemSetting;
    private int v2tLanguage;
    private int privacyItem;
    private int forwardSettingRow;
    private int ghostSettingRow;
    private int fontSettingsRow;
    private int basicFontRow;
    private int translateSettingsRow;
    private int voiceSettingsRow;
    private int calendarSettingsRow;
    private int accountSettingsRow;

    @Override
    public boolean onFragmentCreate() {
        super.onFragmentCreate();

        initItems();
        return true;
    }


    private void initItems() {
        int rowCount = 0;

        //region Customized : add custom setting
        int header = rowCount++;
        drawerMenuItemSetting = rowCount++;

        dialogBottomMenuItemSetting = BuildVars.DIALOG_BOTTOM_MENU_FEATURE ? rowCount++ : -1;

        tabMenuItemSetting = rowCount++;

        v2tLanguage = BuildVars.V2T_FEATURE ? rowCount++ : -1;

        privacyItem = rowCount++;

        forwardSettingRow = BuildVars.SMART_FORWARD_FEATURE ? rowCount++ : -1;

        chatSettingRow = rowCount++;

        fontSettingsRow = rowCount++;

        ghostSettingRow = SharedStorage.showGhostMode() ? rowCount++ : -1;

        basicFontRow = LocaleController.getCurrentLanguageShortName().equals("fa") && Config.BASIC_FONTS_FEATURE ? rowCount++ : -1;

        translateSettingsRow = rowCount++;

        calendarSettingsRow = rowCount++;

        voiceSettingsRow = rowCount++;

        accountSettingsRow = rowCount++;

        versionRow = rowCount++;

        //endregion

        settingItems.add(new SettingItem(header, String.format((LocaleController.isRTL ? "%2$s %1$s" : "%1$s %2$s"),
                LocaleController.getString("AppName", R.string.AppName),
                LocaleController.getString("Settings", R.string.Settings)), 0, SettingItem.Type.HEADER_CELL));

        if (BuildVars.V2T_FEATURE && SharedStorage.showV2T()) {
            settingItems.add(new SettingItem(v2tLanguage,
                    LocaleController.getString("Voice2TextSettings", R.string.Voice2TextSettings),
                    R.drawable.ic_google_voice, true));
        }

        settingItems.add(new SettingItem(privacyItem, LocaleController.getString("PrivacySettings",
                R.string.PrivacySettings),
                R.drawable.menu_secret, true));
        /*    if (HiddenController.getInstance().isActive() && SharedStorage.preventPrivacy() && SharedStorage.hiddenModePassCode().length() > 0) {
                textCell.setColorFilter(Color.GREEN);
            }*/
        if (BuildVars.SMART_FORWARD_FEATURE) {
            settingItems.add(new SettingItem(forwardSettingRow, LocaleController.getString("ForwardSettings", R.string.ForwardSettings),
                    R.drawable.msg_forward, true));
        }

        settingItems.add(new SettingItem(chatSettingRow, LocaleController.getString("ChatSettings", R.string.ChatSettings),
                R.drawable.menu_chats, true));

        if (SharedStorage.showGhostMode()) {
            settingItems.add(new SettingItem(ghostSettingRow, LocaleController.getString("GhostModeSettings", R.string.GhostModeSettings),
                    Config.ICON_GHOST_MENU, true));
        }

        if (LocaleController.getCurrentLanguageShortName().equals("fa") && Config.BASIC_FONTS_FEATURE) {
            settingItems.add(new SettingItem(basicFontRow,
                    LocaleController.getString("TextNicer", R.string.TextNicer),
                    R.drawable.photo_paint_text, true));
        }

        settingItems.add(new SettingItem(drawerMenuItemSetting,
                LocaleController.getString("DrawerMenuItemSetting",
                        R.string.DrawerMenuItemSetting),
                R.drawable.msg_list, true));

        if (BuildVars.DIALOG_BOTTOM_MENU_FEATURE) {
            settingItems.add(new SettingItem(dialogBottomMenuItemSetting,
                    LocaleController.getString("DialogBottomMenuSetting",
                            R.string.DialogBottomMenuSetting),
                    R.drawable.list_reorder, true));
        }

        settingItems.add(new SettingItem(tabMenuItemSetting,
                LocaleController.getString("TabMenuItemSetting",
                        R.string.TabMenuItemSetting),
                R.drawable.menu_folders, true));

        settingItems.add(new SettingItem(fontSettingsRow,
                LocaleController.getString("SelectFont",
                        R.string.SelectFont),
                R.drawable.msg_text_outlined, true));

        if (BuildVars.TRANSLATE_FEATURE) {
            settingItems.add(new SettingItem(translateSettingsRow,
                    LocaleController.getString("Translate",
                            R.string.Translate),
                    R.drawable.ic_g_translate, true));
        }

        if (BuildVars.VOICE_CHANGER_FEATURE) {
            settingItems.add(new SettingItem(voiceSettingsRow,
                    LocaleController.getString("VoiceChanger",
                            R.string.VoiceChanger),
                    BuildVars.ICON_VOICE_CHANGER, true));
        }

        settingItems.add(new SettingItem(calendarSettingsRow,
                LocaleController.getString("Calendar",
                        R.string.Calendar),
                R.drawable.msg_calendar, true));


        settingItems.add(new SettingItem(accountSettingsRow,
                LocaleController.getString("AccountSettings",
                        R.string.AccountSettings),
                R.drawable.actions_permissions, true));


        try {
            PackageInfo pInfo = ApplicationLoader.applicationContext.getPackageManager().getPackageInfo(ApplicationLoader.applicationContext.getPackageName(), 0);
            int code = pInfo.versionCode / 10;
            String abi = "";
            switch (pInfo.versionCode % 10) {
                case 1:
                case 3:
                    abi = "arm-v7a";
                    break;
                case 2:
                case 4:
                    abi = "x86";
                    break;
                case 5:
                case 7:
                    abi = "arm64-v8a";
                    break;
                case 6:
                case 8:
                    abi = "x86_64";
                    break;
                case 0:
                case 9:
                    abi = "universal " + Build.CPU_ABI + " " + Build.CPU_ABI2;
                    break;
            }
            String s = LocaleController.formatString("TelegramVersion", R.string.TelegramVersion, String.format(Locale.US, "v%s (%d) %s", pInfo.versionName, code, abi));
            settingItems.add(new SettingItem(versionRow, s, 0, "", false, SettingItem.Type.TEXT_INFO_PRIVACY_CELL, null));
        } catch (Exception e) {
            Log.e(TAG, "initItems: ", e);
        }


        Collections.sort(settingItems, (settingItem, t1) -> settingItem.getId() - t1.getId());
    }


    @Override
    public View createView(Context context) {
        actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        actionBar.setAllowOverlayTitle(true);
        actionBar.setTitle(LocaleController.getString("Settings", R.string.Settings));
        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int id) {
                if (id == -1) {
                    finishFragment();
                }
            }
        });

        int scrollTo;
        if (listView != null) {
            scrollTo = layoutManager.findFirstVisibleItemPosition();
            View topView = layoutManager.findViewByPosition(scrollTo);
            if (topView != null) {
            }
        }

        listAdapter = new ListAdapter(context);

        fragmentView = new FrameLayout(context);
        FrameLayout frameLayout = (FrameLayout) fragmentView;
        frameLayout.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));

        listView = new RecyclerListView(context) {
            @Override
            public boolean hasOverlappingRendering() {
                return false;
            }
        };
        listView.setHideIfEmpty(false);
        listView.setVerticalScrollBarEnabled(false);
        listView.setLayoutManager(layoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false) {
            @Override
            public boolean supportsPredictiveItemAnimations() {
                return false;
            }
        });
        listView.setGlowColor(Theme.getColor(Theme.key_avatar_backgroundActionBarBlue));
        listView.setPadding(0, 0, 0, 0);
        frameLayout.addView(listView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT, Gravity.TOP | Gravity.LEFT));
        listView.setAdapter(listAdapter);
        listView.setItemAnimator(null);
        listView.setLayoutAnimation(null);
        listView.setClipToPadding(false);
        listView.setOnItemClickListener((view, position) -> {
            int id = settingItems.get(position).getId();
            if (id == v2tLanguage) {
                presentFragment(new Voice2TextSettingActivity());
            } else if (id == privacyItem) {
                if (HiddenController.getInstance().isActive() && SharedStorage.preventPrivacy() && SharedStorage.hiddenModePassCode().length() > 0) {
                    ((LaunchActivity) getParentActivity()).showPasscodeActivity(true,true,0,0, () ->
                            presentFragment(new PrivacySettingActivity()),null
                    );
                } else {
                    presentFragment(new PrivacySettingActivity());
                }
            } else if (id == forwardSettingRow) {
                presentFragment(new ForwardSettingActivity());
            } else if (id == chatSettingRow) {
                presentFragment(new ChatSettingActivity());
            } else if (id == ghostSettingRow) {
                presentFragment(new GhostSettingActivity());
            } else if (id == basicFontRow) {
                presentFragment(new TextNicerActivity(param -> {

                }));
            } else if (id == drawerMenuItemSetting) {
                presentFragment(new DrawerMenuSettingsActivity());
            } else if (id == dialogBottomMenuItemSetting) {
                presentFragment(new DialogBottomMenuSettingsActivity());
            } else if (id == tabMenuItemSetting) {
                presentFragment(new FolderSettingsActivity());
            } else if (id == fontSettingsRow) {
                presentFragment(new FontSettingActivity());
            } else if (id == calendarSettingsRow) {
                presentFragment(new CalendarSettingActivity());
            }else if (id == accountSettingsRow) {
                presentFragment(new AccountSettingsActivity());
            } else if (id == translateSettingsRow) {
                presentFragment(new TranslateSettingActivity());
            } else if (id == voiceSettingsRow) {
                presentFragment(new VoiceSettingActivity());
            }
        });

        listView.setOnItemLongClickListener(new RecyclerListView.OnItemLongClickListener() {

            private int pressCount = 0;

            @Override
            public boolean onItemClick(View view, int position) {
                if (position == versionRow) {
                    pressCount++;
                    if (pressCount >= 2 || BuildVars.DEBUG_PRIVATE_VERSION) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
                        builder.setTitle(LocaleController.getString("DebugMenu", R.string.DebugMenu));
                        CharSequence[] items;
                        items = new CharSequence[]{
                                LocaleController.getString("DebugMenuImportContacts", R.string.DebugMenuImportContacts),
                                LocaleController.getString("DebugMenuReloadContacts", R.string.DebugMenuReloadContacts),
                                LocaleController.getString("DebugMenuResetContacts", R.string.DebugMenuResetContacts),
                                LocaleController.getString("DebugMenuResetDialogs", R.string.DebugMenuResetDialogs),
                                BuildVars.LOGS_ENABLED ? LocaleController.getString("DebugMenuDisableLogs", R.string.DebugMenuDisableLogs) : LocaleController.getString("DebugMenuEnableLogs", R.string.DebugMenuEnableLogs),
                                SharedConfig.inappCamera ? LocaleController.getString("DebugMenuDisableCamera", R.string.DebugMenuDisableCamera) : LocaleController.getString("DebugMenuEnableCamera", R.string.DebugMenuEnableCamera),
                                LocaleController.getString("DebugMenuClearMediaCache", R.string.DebugMenuClearMediaCache),
                                LocaleController.getString("DebugMenuCallSettings", R.string.DebugMenuCallSettings),
                                null,
                                BuildVars.DEBUG_PRIVATE_VERSION ? "Check for app updates" : null,
                                LocaleController.getString("DebugMenuReadAllDialogs", R.string.DebugMenuReadAllDialogs),
                                SharedConfig.pauseMusicOnRecord ? LocaleController.getString("DebugMenuDisablePauseMusic", R.string.DebugMenuDisablePauseMusic) : LocaleController.getString("DebugMenuEnablePauseMusic", R.string.DebugMenuEnablePauseMusic),
                                BuildVars.DEBUG_VERSION && !AndroidUtilities.isTablet() ? (SharedConfig.smoothKeyboard ? LocaleController.getString("DebugMenuDisableSmoothKeyboard", R.string.DebugMenuDisableSmoothKeyboard) : LocaleController.getString("DebugMenuEnableSmoothKeyboard", R.string.DebugMenuEnableSmoothKeyboard)) : null
                        };
                        builder.setItems(items, (dialog, which) -> {
                            if (which == 0) {
                                UserConfig.getInstance(currentAccount).syncContacts = true;
                                UserConfig.getInstance(currentAccount).saveConfig(false);
                                ContactsController.getInstance(currentAccount).forceImportContacts();
                            } else if (which == 1) {
                                ContactsController.getInstance(currentAccount).loadContacts(false, 0);
                            } else if (which == 2) {
                                ContactsController.getInstance(currentAccount).resetImportedContacts();
                            } else if (which == 3) {
                                MessagesController.getInstance(currentAccount).forceResetDialogs();
                            } else if (which == 4) {
                                BuildVars.LOGS_ENABLED = !BuildVars.LOGS_ENABLED;
                                SharedPreferences sharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("systemConfig", Context.MODE_PRIVATE);
                                sharedPreferences.edit().putBoolean("logsEnabled", BuildVars.LOGS_ENABLED).commit();
                            } else if (which == 5) {
                                SharedConfig.toggleInappCamera();
                            } else if (which == 6) {
                                MessagesStorage.getInstance(currentAccount).clearSentMedia();
                                SharedConfig.setNoSoundHintShowed(false);
                                SharedPreferences.Editor editor = MessagesController.getGlobalMainSettings().edit();
                                editor.remove("archivehint").remove("archivehint_l").remove("gifhint").remove("soundHint").remove("themehint").commit();
                                SharedConfig.textSelectionHintShows = 0;
                            } else if (which == 7) {
                                VoIPHelper.showCallDebugSettings(getParentActivity());
                            } else if (which == 8) {
                                SharedConfig.toggleRoundCamera16to9();
                            } else if (which == 9) {
                                ((LaunchActivity) getParentActivity()).checkAppUpdate(true);
                            } else if (which == 10) {
                                MessagesStorage.getInstance(currentAccount).readAllDialogs(-1);
                            } else if (which == 11) {
                                SharedConfig.togglePauseMusicOnRecord();
                            } else if (which == 12) {
                                SharedConfig.toggleSmoothKeyboard();
                                if (SharedConfig.smoothKeyboard && getParentActivity() != null) {
                                    getParentActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
                                }
                            }
                        });
                        builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                        showDialog(builder.create());
                    } else {
                        try {
                            Toast.makeText(getParentActivity(), "¯\\_(ツ)_/¯", Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            FileLog.e(e);
                        }
                    }
                    return true;
                }
                return false;
            }
        });


/*        emptyView = new EmptyTextProgressView(context);
        emptyView.showTextView();
        emptyView.setTextSize(18);
        emptyView.setVisibility(View.GONE);
        emptyView.setShowAtCenter(true);
        emptyView.setPadding(0, AndroidUtilities.dp(50), 0, 0);
        frameLayout.addView(emptyView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));
        */
        return fragmentView;
    }


    @Override
    public void onResume() {
        super.onResume();
        if (listAdapter != null) {
            listAdapter.notifyDataSetChanged();
        }
        setParentActivityTitle(LocaleController.getString("Settings", R.string.Settings));
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }


    private class ListAdapter extends RecyclerListView.SelectionAdapter {

        private Context mContext;

        public ListAdapter(Context context) {
            mContext = context;
        }

        @Override
        public int getItemCount() {
            return settingItems.size();
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            SettingItem menuItem = settingItems.get(position);
            SettingItem.Type type = SettingItem.Type.values()[holder.getItemViewType()];

            switch (type) {
                case TEXT_CELL: {
                    TextCell textCell = (TextCell) holder.itemView;
                    textCell.setTextAndIcon(menuItem.getName(), menuItem.getIcon(), menuItem.isDivider());

                    break;
                }
                case HEADER_CELL: {
                    ((HeaderCell) holder.itemView).setText(menuItem.getName());
                    break;
                }
                case TEXT_INFO_PRIVACY_CELL: {
                    TextInfoPrivacyCell textCell = (TextInfoPrivacyCell) holder.itemView;
                    textCell.setText(menuItem.getName());
                    break;
                }
            }
        }

        @Override
        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            int position = holder.getAdapterPosition();
            return settingItems.get(position).type == SettingItem.Type.TEXT_CELL;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view;
            SettingItem.Type type = SettingItem.Type.values()[viewType];
            view = SettingItem.getView(parent.getContext(), type);
            view.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT));
            return new RecyclerListView.Holder(view);
        }

        @Override
        public int getItemViewType(int position) {
            return settingItems.get(position).getType();
        }
    }

    @Override
    public ArrayList<ThemeDescription> getThemeDescriptions() {
        return new ArrayList<>(Arrays.asList(
                new ThemeDescription(fragmentView, 0, null, null, null, null, Theme.key_windowBackgroundWhite),
                new ThemeDescription(fragmentView, 0, null, null, null, null, Theme.key_windowBackgroundGray),

                new ThemeDescription(actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_actionBarDefault),
                new ThemeDescription(listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, Theme.key_avatar_backgroundActionBarBlue),
                new ThemeDescription(actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, Theme.key_actionBarDefaultIcon),
                new ThemeDescription(actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, Theme.key_actionBarDefaultTitle),
                new ThemeDescription(actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_actionBarDefaultSelector),

                new ThemeDescription(listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, Theme.key_listSelector),

                new ThemeDescription(listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, Theme.key_divider),

                new ThemeDescription(listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, null, null, null, Theme.key_windowBackgroundGrayShadow),
                new ThemeDescription(listView, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{ShadowSectionCell.class}, null, null, null, Theme.key_windowBackgroundGray),

                new ThemeDescription(listView, 0, new Class[]{TextCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText),
                new ThemeDescription(listView, 0, new Class[]{TextCell.class}, new String[]{"valueTextView"}, null, null, null, Theme.key_windowBackgroundWhiteValueText),
                new ThemeDescription(listView, 0, new Class[]{TextCell.class}, new String[]{"imageView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayIcon),

                new ThemeDescription(listView, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlueHeader),

                new ThemeDescription(listView, 0, new Class[]{TextDetailCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText),
                new ThemeDescription(listView, 0, new Class[]{TextDetailCell.class}, new String[]{"valueTextView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText2),

                new ThemeDescription(listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, null, null, null, Theme.key_windowBackgroundGrayShadow),
                new ThemeDescription(listView, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{TextInfoPrivacyCell.class}, null, null, null, Theme.key_windowBackgroundGray),
                new ThemeDescription(listView, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText4)
        ));
    }
}
