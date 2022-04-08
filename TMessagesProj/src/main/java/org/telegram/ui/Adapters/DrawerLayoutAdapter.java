/*
 * This is the source code of Telegram for Android v. 5.x.x.
 * It is licensed under GNU GPL v. 2 or later.
 * You should have received a copy of the license in this archive (see LICENSE).
 *
 * Copyright Nikolai Kudashov, 2013-2018.
 */

package org.telegram.ui.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.content.pm.PackageManager;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.finalsoft.Config;
import com.finalsoft.SharedStorage;
import com.finalsoft.admob.AdmobController;
import com.finalsoft.controller.DrawerMenuItemsHideController;
import com.finalsoft.helper.forward.ForwardHelper;
import com.finalsoft.ui.drawer.DrawerGridActionCell;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.R;
import org.telegram.messenger.UserConfig;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.DrawerActionCell;
import org.telegram.ui.Cells.DividerCell;
import org.telegram.ui.Cells.DrawerAddCell;
import org.telegram.ui.Cells.DrawerProfileCell;
import org.telegram.ui.Cells.DrawerUserCell;
import org.telegram.ui.Cells.EmptyCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.SideMenultItemAnimator;

import java.util.ArrayList;
import java.util.Collections;

public class DrawerLayoutAdapter extends RecyclerListView.SelectionAdapter {

    private Context mContext;
    private ArrayList<Item> items = new ArrayList<>();
    private ArrayList<Integer> accountNumbers = new ArrayList<>();
    private boolean accountsShown;
    private DrawerProfileCell profileCell;
    private SideMenultItemAnimator itemAnimator;
    private boolean hasGps;

    public DrawerLayoutAdapter(Context context, SideMenultItemAnimator animator) {
        mContext = context;
        itemAnimator = animator;
        accountsShown = UserConfig.getActivatedAccountsCount() > 1 && MessagesController.getGlobalMainSettings().getBoolean("accountsShown", true);
        Theme.createCommonDialogResources(context);
        resetItems();
        try {
            hasGps = ApplicationLoader.applicationContext.getPackageManager().hasSystemFeature(PackageManager.FEATURE_LOCATION_GPS);
        } catch (Throwable e) {
            hasGps = false;
        }
    }

    private int getAccountRowsCount() {
        int count = accountNumbers.size() + 1;
        if (accountNumbers.size() < UserConfig.MAX_ACCOUNT_COUNT) {
            count++;
        }
        return count;
    }

    @Override
    public int getItemCount() {
        int count = (grid_mode ? 1 : items.size()) + 2;
        if (accountsShown) {
            count += getAccountRowsCount();
        }
        return count;
    }

    public void setAccountsShown(boolean value, boolean animated) {
        if (accountsShown == value || itemAnimator.isRunning()) {
            return;
        }
        accountsShown = value;
        if (profileCell != null) {
            profileCell.setAccountsShown(accountsShown, animated);
        }
        MessagesController.getGlobalMainSettings().edit().putBoolean("accountsShown", accountsShown).commit();
        if (animated) {
            itemAnimator.setShouldClipChildren(false);
            if (accountsShown) {
                notifyItemRangeInserted(2, getAccountRowsCount());
            } else {
                notifyItemRangeRemoved(2, getAccountRowsCount());
            }
        } else {
            notifyDataSetChanged();
        }
    }

    public boolean isAccountsShown() {
        return accountsShown;
    }

    @Override
    public void notifyDataSetChanged() {
        resetItems();
        super.notifyDataSetChanged();
    }

    @Override
    public boolean isEnabled(RecyclerView.ViewHolder holder) {
        int itemType = holder.getItemViewType();
        return itemType == 3 || itemType == 4 || itemType == 5 || itemType == 6;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case PROFILE_CELL: {
                profileCell = new DrawerProfileCell(mContext);
                profileCell.setOnArrowClickListener(v -> {
                    DrawerProfileCell drawerProfileCell = (DrawerProfileCell) v;
                    setAccountsShown(drawerProfileCell.isAccountsShown(), true);
                });
                view = profileCell;
                break;
            }
            case EMPTY_CELL: {
                view = new EmptyCell(mContext, AndroidUtilities.dp(8));
                break;
            }
            case DIVIDER: {
                view = new DividerCell(mContext);
                break;
            }
            case ACTION_CELL: {
                view = new DrawerActionCell(mContext);
                if (isSettingActivity) {
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                }
                break;
            }
            case USER_CELL: {
                view = new DrawerUserCell(mContext);
                break;
            }
            case ADD_CELL: {
                view = new DrawerAddCell(mContext);
                break;
            }
            case TEXT_INFO_CELL: {
                view = new TextInfoPrivacyCell(mContext, 10);
                break;
            }
            case GRID_MODE_CELL: {
                RecyclerListView recyclerListView = new RecyclerListView(mContext);
//                recyclerListView.setBackgroundColor(Color.GREEN);
                recyclerListView.setLayoutManager(new GridLayoutManager(mContext, 4));
                view = recyclerListView;
                break;
            }
            default: {
                view = new EmptyCell(mContext, AndroidUtilities.dp(0));
                break;
            }
        }
        view.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        return new RecyclerListView.Holder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case PROFILE_CELL: {
                DrawerProfileCell profileCell = (DrawerProfileCell) holder.itemView;
                profileCell.toggleGhostMode();
                profileCell.setUser(MessagesController.getInstance(UserConfig.selectedAccount).getUser(UserConfig.getInstance(UserConfig.selectedAccount).getClientUserId()), accountsShown);
                break;
            }
            case ACTION_CELL: {
                position -= 2;
                if (accountsShown) {
                    position -= getAccountRowsCount();
                }
                DrawerActionCell drawerActionCell = (DrawerActionCell) holder.itemView;
                items.get(position).bind(drawerActionCell);
                drawerActionCell.setPadding(0, 0, 0, 0);
                drawerActionCell.showCheckBox(isSettingActivity);
                if (isSettingActivity) {
                    drawerActionCell.setChecked(!DrawerMenuItemsHideController.getInstance().is(items.get(position).id));
                }
                if (items.get(position).id == TURN_OFF_ADMOB) {
                    Log.i(TAG, "onBindViewHolder: RED");
                    drawerActionCell.setBackgroundColor(Theme.getColor(Theme.key_chat_selectedBackground));
                } else {
                    drawerActionCell.setBackgroundColor(Color.TRANSPARENT);
                }
                break;
            }
            case USER_CELL: {
                DrawerUserCell drawerUserCell = (DrawerUserCell) holder.itemView;
                drawerUserCell.setAccount(accountNumbers.get(position - 2));
                break;
            }
            case TEXT_INFO_CELL: {
                TextInfoPrivacyCell cell = (TextInfoPrivacyCell) holder.itemView;
                cell.getTextView().setGravity(Gravity.CENTER_HORIZONTAL);
                cell.getTextView().setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText3));
                cell.getTextView().setMovementMethod(null);
                position -= 2;
                if (accountsShown) {
                    position -= getAccountRowsCount();
                }
                cell.setText(items.get(position).text);
                break;
            }
            case GRID_MODE_CELL: {
                RecyclerListView recyclerListView = (RecyclerListView) holder.itemView;
                recyclerListView.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                if (gridAdapter == null) {
                    initGridAdapter();
                }
                recyclerListView.setAdapter(gridAdapter);
                break;
            }

        }
    }

    @Override
    public int getItemViewType(int i) {
//        Log.i(TAG, "getItemViewType: i:" + i);
        if (i < 2) {
            return isSettingActivity ? DEFAULT_CELL : i;
        }
        i -= 2;
        if (accountsShown) {
            if (i < accountNumbers.size()) {
                return USER_CELL;
            } else {
                if (accountNumbers.size() < UserConfig.MAX_ACCOUNT_COUNT) {
                    if (i == accountNumbers.size()) {
                        return ADD_CELL;
                    } else if (i == accountNumbers.size() + 1) {
                        return DIVIDER;
                    }
                } else {
                    if (i == accountNumbers.size()) {
                        return DIVIDER;
                    }
                }
            }
            i -= getAccountRowsCount();
        }
        if (grid_mode) {
            return GRID_MODE_CELL;
        }
        if (items.get(i) == null) {
            return DIVIDER;
        }
        if (!isSettingActivity && DrawerMenuItemsHideController.getInstance().is(items.get(i).id)) {
            return DEFAULT_CELL;
        }

        if (!isSettingActivity && items.get(i).id == DRAWER_MENU_SETTINGS) {
            return TEXT_INFO_CELL;
        }
        if (isSettingActivity && !items.get(i).editable) {
            return DEFAULT_CELL;
        }
        return ACTION_CELL;
    }

    public void swapElements(int fromIndex, int toIndex) {
        int idx1 = fromIndex - 2;
        int idx2 = toIndex - 2;
        if (idx1 < 0 || idx2 < 0 || idx1 >= accountNumbers.size() || idx2 >= accountNumbers.size()) {
            return;
        }
        final UserConfig userConfig1 = UserConfig.getInstance(accountNumbers.get(idx1));
        final UserConfig userConfig2 = UserConfig.getInstance(accountNumbers.get(idx2));
        final int tempLoginTime = userConfig1.loginTime;
        userConfig1.loginTime = userConfig2.loginTime;
        userConfig2.loginTime = tempLoginTime;
        userConfig1.saveConfig(false);
        userConfig2.saveConfig(false);
        Collections.swap(accountNumbers, idx1, idx2);
        notifyItemMoved(fromIndex, toIndex);
    }

    private void resetItems() {
        accountNumbers.clear();
        for (int a = 0; a < UserConfig.MAX_ACCOUNT_COUNT; a++) {
            if (UserConfig.getInstance(a).isClientActivated()) {
                accountNumbers.add(a);
            }
        }
        Collections.sort(accountNumbers, (o1, o2) -> {
            long l1 = UserConfig.getInstance(o1).loginTime;
            long l2 = UserConfig.getInstance(o2).loginTime;
            if (l1 > l2) {
                return 1;
            } else if (l1 < l2) {
                return -1;
            }
            return 0;
        });

        items.clear();
        if (!UserConfig.getInstance(UserConfig.selectedAccount).isClientActivated()) {
            return;
        }
        int eventType = Theme.getEventType();
        int newGroupIcon;
        int newSecretIcon;
        int newChannelIcon;
        int contactsIcon;
        int callsIcon;
        int savedIcon;
        int settingsIcon;
        int inviteIcon;
        int helpIcon;
        int peopleNearbyIcon;
        if (eventType == 0) {
            newGroupIcon = R.drawable.menu_groups_ny;
            newSecretIcon = R.drawable.menu_secret_ny;
            newChannelIcon = R.drawable.menu_channel_ny;
            contactsIcon = R.drawable.menu_contacts_ny;
            callsIcon = R.drawable.menu_calls_ny;
            savedIcon = R.drawable.menu_bookmarks_ny;
            settingsIcon = R.drawable.menu_settings_ny;
            inviteIcon = R.drawable.menu_invite_ny;
            helpIcon = R.drawable.menu_help_ny;
            peopleNearbyIcon = R.drawable.menu_nearby_ny;
        } else if (eventType == 1) {
            newGroupIcon = R.drawable.menu_groups_14;
            newSecretIcon = R.drawable.menu_secret_14;
            newChannelIcon = R.drawable.menu_broadcast_14;
            contactsIcon = R.drawable.menu_contacts_14;
            callsIcon = R.drawable.menu_calls_14;
            savedIcon = R.drawable.menu_bookmarks_14;
            settingsIcon = R.drawable.menu_settings_14;
            inviteIcon = R.drawable.menu_secret_ny;
            helpIcon = R.drawable.menu_help;
            peopleNearbyIcon = R.drawable.menu_secret_14;
        } else if (eventType == 2) {
            newGroupIcon = R.drawable.menu_groups_hw;
            newSecretIcon = R.drawable.menu_secret_hw;
            newChannelIcon = R.drawable.menu_broadcast_hw;
            contactsIcon = R.drawable.menu_contacts_hw;
            callsIcon = R.drawable.menu_calls_hw;
            savedIcon = R.drawable.menu_bookmarks_hw;
            settingsIcon = R.drawable.menu_settings_hw;
            inviteIcon = R.drawable.menu_invite_hw;
            helpIcon = R.drawable.menu_help_hw;
            peopleNearbyIcon = R.drawable.menu_secret_hw;
        } else {
            newGroupIcon = R.drawable.menu_groups;
            newSecretIcon = R.drawable.menu_secret;
            newChannelIcon = R.drawable.menu_broadcast;
            contactsIcon = R.drawable.menu_contacts;
            callsIcon = R.drawable.menu_calls;
            savedIcon = R.drawable.menu_saved;
            settingsIcon = R.drawable.menu_settings;
            inviteIcon = R.drawable.menu_invite;
            helpIcon = R.drawable.menu_help;
            peopleNearbyIcon = R.drawable.menu_nearby;
        }


        //region Customized: Menu Items


//        boolean hasWallet = Build.VERSION.SDK_INT >= 18 && !TextUtils.isEmpty(UserConfig.getInstance(UserConfig.selectedAccount).walletConfig) && !TextUtils.isEmpty(UserConfig.getInstance(UserConfig.selectedAccount).walletBlockchainName);

        if (isSettingActivity) {
            if (Config.DRAWER_GRID_FEATURE) {
                items.add(new Item(SHOW_GRID_MODE,
                        LocaleController.getString("DrawerGridMode", R.string.DrawerGridMode),
                        R.drawable.ic_grid_on));
            }

            items.add(new Item(BIG_AVATAR, LocaleController.getString("ShowBigAvatar", R.string.ShowBigAvatar),
                    R.drawable.profile_photos));
            items.add(null); // divider
        }

        if (SharedStorage.proxyServer()) {
            if (!DrawerMenuItemsHideController.getInstance().is(PROXY_ITEM) || isSettingActivity) {
                items.add(new Item(PROXY_ITEM,
                        LocaleController.getString("ProxySettings", R.string.ProxySettings),
                        SharedStorage.proxyCustomStatus() ? R.drawable.proxy_off : R.drawable.proxy_on, true));
                items.add(null); // divider
            }
        }

        items.add(new Item(NEW_GROUP, LocaleController.getString("NewGroup", R.string.NewGroup), newGroupIcon, true));

        items.add(new Item(SECRET_CHAT, LocaleController.getString("NewSecretChat", R.string.NewSecretChat), newSecretIcon, true));

        items.add(new Item(NEW_CHANNEL, LocaleController.getString("NewChannel", R.string.NewChannel), newChannelIcon, true));

        items.add(null); // divider

        if (BuildVars.OFF_ADMOB_FEATURE && AdmobController.getInstance().getShowAdmob() /*&& forwardHelper.days() > 0*/) {
            items.add(new Item(TURN_OFF_ADMOB, LocaleController.getString("TurnOffAdmob", R.string.TurnOffAdmob),
                    Config.ICON_ADDMOB, true));
        }


        if (BuildVars.PEOPLE_NEARBY_FEATURE) {
            items.add(new Item(PEOPLE_NEARBY,
                    LocaleController.getString("PeopleNearby", R.string.PeopleNearby),
                    Config.ICON_PEOPLE_NEARBY));
        }


        items.add(new Item(SCHEDULE_MESSAGE,
                LocaleController.getString("ScheduledMessages", R.string.ScheduledMessages),
                R.drawable.msg_schedule, false));

        if (BuildVars.V2T_FEATURE && SharedStorage.showV2T()) {
            items.add(new Item(VOICE_2_TEXT,
                    LocaleController.getString("Voice2Text", R.string.Voice2Text),
                    R.drawable.ic_google_voice, false));
        }

        if (BuildVars.PROFILE_IMAGE_COLLECTION_FEATURE) {
            items.add(new Item(PROFILE_IMAGES,
                    LocaleController.getString("ProfileImageCollection", R.string.ProfileImageCollection),
                    R.drawable.menu_camera, true));
        }


        items.add(new Item(SAVED_MESSAGE, LocaleController.getString("SavedMessages", R.string.SavedMessages), savedIcon, true));

        if (BuildVars.MEDIA_FEATURE) {
            items.add(
                    new Item(SAVED_MEDIA, LocaleController.getString("SavedMedia", R.string.SavedMedia),
                            R.drawable.msg_media, true));
        }

        items.add(
                new Item(ARCHIVED_DIALOGS,
                        LocaleController.getString("ArchiveChats", R.string.ArchiveChats),
                        R.drawable.msg_archive, true));

        items.add(new Item(CONTACTS, LocaleController.getString("Contacts", R.string.Contacts), contactsIcon, true));

/*        items.add(
                new Item(BOOKMARKED_DIALOG, LocaleController.getString("Favorites", R.string.Favorites),
                        R.drawable.msg_fave));*/

        items.add(new Item(CALLS, LocaleController.getString("Calls", R.string.Calls), callsIcon, true));

/*        items.add(new Item(IMAGE_EDITOR, LocaleController.getString("AccDescrPhotoEditor", R.string.AccDescrPhotoEditor),
                R.drawable.profile_photos, false));*/

        items.add(null); // divider

        if (Config.DOWNLOAD_MANAGER_FEATURE) {
            items.add(new Item(DOWNLOAD_MANAGER, LocaleController.getString("DownloadManager", R.string.DownloadManager),
                    R.drawable.msg_download, false));
        }

        if (Config.CONTACT_CHANGES_FEATURE) {
            String label = String.format(LocaleController.getString("ContactsChanges", R.string.ContactsChanges), SharedStorage.contactChangeCount());
            items.add(new Item(CONTACT_CHANGES, label,
                    Config.ICON_CONTACT_CHANGES, true));
        }

        if (Config.FIND_USERNAME_FEATURE) {
            items.add(new Item(FIND_USERNAME, LocaleController.getString("UsernameFinder", R.string.UsernameFinder),
                    R.drawable.msg_usersearch, true));
        }

        if (Config.PROFILE_MAKER_FEATURE) {
            items.add(new Item(PROFILE_MAKER, LocaleController.getString("ProfileMaker", R.string.ProfileMaker),
                    R.drawable.ic_profile_maker, true));
        }

        items.add(new Item(THEME, LocaleController.getString("Theme", R.string.Theme),
                R.drawable.msg_theme, false));


        items.add(new Item(CLEAR_CACHE, LocaleController.getString("ClearCache", R.string.ClearCache), R.drawable.msg_clear));
        items.add(new Item(DATA_USAGE, LocaleController.getString("NetworkUsage", R.string.NetworkUsage),
                R.drawable.menu_data, true));

        items.add(new Item(SETTINGS, LocaleController.getString("Settings", R.string.Settings), settingsIcon, false));

        if (!BuildVars.INTEGRATED_SETTING) {
            String s = String.format((LocaleController.isRTL ? "%2$s %1$s" : "%1$s %2$s"),
                    LocaleController.getString("AppName", R.string.AppName),
                    LocaleController.getString("Settings", R.string.Settings));

            items.add(new Item(CUSTOM_SETTINGS, s, Config.ICON_CUSTOM_SETTINGS));
        }

//        Log.i(TAG, "!q2w3e4r resetItems:  SharedStorage.showAdmob():"+  SharedStorage.showAdmob());
//        Log.i(TAG, "!q2w3e4r resetItems: forwardHelper.days():" + forwardHelper.days());

        items.add(new Item(SHARE, LocaleController.getString("ShareApp", R.string.ShareApp), inviteIcon, true));

        if (BuildVars.SUPPORT_GROUP_FEATURE) {
            items.add(new Item(SUPPORT_GROUP,
                    LocaleController.getString("SupportGroup", R.string.SupportGroup),
                    R.drawable.menu_groups, true));
        }

        items.add(new Item(OFFICIAL_CHANNEL,
                LocaleController.getString("OfficialChannel", R.string.OfficialChannel),
                R.drawable.menu_broadcast, true));

/*    items.add(new Item(INVITE, LocaleController.getString("InviteFriends", R.string.InviteFriends),
        eventType ? R.drawable.menu_invite_ny : R.drawable.menu_invite));*/

        //items.add(new Item(FAQ, LocaleController.getString("TelegramFAQ", R.string.TelegramFAQ),
        //    eventType ? R.drawable.menu_help_ny : R.drawable.menu_help));


  /*
            items.add(null); // divider
            items.add(new Item(VIDEO, LocaleController.getString("Cinema", R.string.Cinema),
                    R.drawable.menu_channel_ny));*/

        items.add(null);
        if (Config.ADMOB_FEATURE) {
            if (SharedStorage.showDonate()) {
                items.add(new Item(DONATE,
                        LocaleController.getString("Donate", R.string.Donate),
                        R.drawable.menu_secret_ny, false));
            }
        }

        items.add(new Item(ON_OFF, SharedStorage.turnOff() ?
                LocaleController.getString("TurnOn", R.string.TurnOn) :
                LocaleController.getString("TurnOff", R.string.TurnOff),
                R.drawable.ic_off));


        if (BuildVars.DRAWER_SETTING_FEATURE) {
            items.add(new Item(DRAWER_MENU_SETTINGS,
                    LocaleController.getString("DrawerMenuItemSetting", R.string.DrawerMenuItemSetting),
                    R.drawable.photo_tools, false));
        }
        //endregion

//        items.add(new Item(7, LocaleController.getString("InviteFriends", R.string.InviteFriends), inviteIcon));
//        items.add(new Item(13, LocaleController.getString("TelegramFeatures", R.string.TelegramFeatures), helpIcon));
    }

    public int getId(int position) {
        position -= 2;
        if (accountsShown) {
            position -= getAccountRowsCount();
        }
        if (position < 0 || position >= items.size()) {
            return -1;
        }
        Item item = items.get(position);
        return item != null ? item.id : -1;
    }

    public int getFirstAccountPosition() {
        if (!accountsShown) {
            return RecyclerView.NO_POSITION;
        }
        return 2;
    }

    public int getLastAccountPosition() {
        if (!accountsShown) {
            return RecyclerView.NO_POSITION;
        }
        return 1 + accountNumbers.size();
    }

    public boolean getEditable(int position) {
        position -= 2;
        if (accountsShown) {
            position -= getAccountRowsCount();
        }
        if (position < 0 || position >= items.size()) {
            return false;
        }
        Item item = items.get(position);
        return item != null && item.editable;
    }

    private static class Item {
        public int icon;
        public String text;
        public int id;
        public boolean editable;

        Item(int id, String text, int icon) {
            this(id, text, icon, true);

        }

        Item(int id, String text, int icon, boolean editable) {
            this.icon = icon;
            this.id = id;
            this.text = text;
            this.editable = editable;
        }

        public void bind(DrawerActionCell actionCell) {
            actionCell.setTextAndIcon(id, text, icon);
        }
    }

    public void toggleGhostMode() {
        profileCell = new DrawerProfileCell(mContext);
        profileCell.toggleGhostMode();
        notifyDataSetChanged();
    }

    public void toggleGridMode() {
        try {
            if (Config.DRAWER_GRID_FEATURE) {
                DrawerMenuItemsHideController.getInstance().toggle(SHOW_GRID_MODE);
                grid_mode = !isSettingActivity && !DrawerMenuItemsHideController.getInstance().is(SHOW_GRID_MODE);
                notifyDataSetChanged();
                profileCell.toggleGridMode();
            }
        } catch (Exception e) {
            Log.i(TAG, "toggleGridMode > Exception: ", e);
        }
    }

    private void initGridAdapter() {
        gridAdapter = new RecyclerListView.SelectionAdapter() {
            ArrayList<Item> copyItems = new ArrayList<>();

            @Override
            public boolean isEnabled(RecyclerView.ViewHolder holder) {
                return true;
            }

            @NonNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = new DrawerGridActionCell(mContext);
//                        view.setBackgroundResource(R.drawable.bluecounter);
                view.setLayoutParams(LayoutHelper.createLinear(
                        LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT,
                        2, 10, 2, 10));
                return new RecyclerListView.Holder(view);
            }

            @Override
            public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
                Item item = copyItems.get(position);
                if (item != null) {
                    DrawerGridActionCell actionCell = (DrawerGridActionCell) holder.itemView;
                    actionCell.setTextAndIcon(item.text, item.icon);
                    actionCell.setOnClickListener(view -> intCallback.onItemClick(item.id));
                } else {
                    holder.itemView.setVisibility(View.GONE);
                }

            }

            @Override
            public int getItemCount() {
                copyItems.clear();
                for (Item item : items) {
                    if (item != null && !DrawerMenuItemsHideController.getInstance().is(item.id)) {
                        copyItems.add(item);
                    }
                }
                //remove holder if proxy setting is disabled
/*
                if (copyItems.size() > 0 && copyItems.get(0) == null) {
                    copyItems.remove(0);
                }
*/

                return copyItems.size();
            }
        };

    }


    //region Customized: Menu Items Index

    private static final int PROFILE_CELL = 0;
    private static final int EMPTY_CELL = 1;
    private static final int DIVIDER = 2;
    private static final int ACTION_CELL = 3;
    private static final int USER_CELL = 4;
    private static final int ADD_CELL = 5;
    private static final int TEXT_INFO_CELL = 6;
    public static final int GRID_MODE_CELL = 7;
    private static final int DEFAULT_CELL = 8;
    private static final String TAG = Config.TAG + "dla";

    private static int i = 0;
    public static int PROFILE = i++; //1
    public static int CONTACTS = i++;
    public static int BOOKMARKED_DIALOG = i++;
    public static int PEOPLE_NEARBY = i++;
    public static int SAVED_MESSAGE = i++;
    public static int ARCHIVED_DIALOGS = i++;
    public static int VOICE_2_TEXT = i++;
    public static int PROFILE_IMAGES = i++;
    public static int SCHEDULE_MESSAGE = i++;
    public static int CALLS = i++;
    public static int SHARE = i++;
    public static int INVITE = i++;
    public static int SETTINGS = i++;
    public static int CUSTOM_SETTINGS = i++;
    public static int DOWNLOAD_MANAGER = i++;
    public static int THEME = i++;
    public static int CATEGORIES = i++;
    public static int FAQ = i++;
    public static int NEW_GROUP = i++;
    public static int SECRET_CHAT = i++;
    public static int NEW_CHANNEL = i++;
    public static int VIDEO = i++;
    public static int CLEAR_CACHE = i++;
    public static int OFFICIAL_CHANNEL = i++;
    public static int SUPPORT_GROUP = i++;
    public static int DONATE = i++;
    public static int DRAWER_MENU_SETTINGS = i++;
    public static int IMAGE_EDITOR = i++;
    public static int SAVED_MEDIA = i++;
    public static int DATA_USAGE = i++;
    //    public static int WALLET = i++;
    public static int ON_OFF = i++;
    public static int BIG_AVATAR = i++;
    public static int SHOW_GRID_MODE = i++;
    public static int TURN_OFF_ADMOB = i++;
    public static int CONTACT_CHANGES = i++;
    public static int FIND_USERNAME = i++;
    public static int PROFILE_MAKER = i++;
    public static int PROXY_ITEM = i++;

    private boolean isSettingActivity;
    private boolean grid_mode = false;
    private IDrawerCallback intCallback;
    private ForwardHelper forwardHelper = new ForwardHelper();

    private RecyclerListView.SelectionAdapter gridAdapter;

    public DrawerLayoutAdapter(Context context, SideMenultItemAnimator animator, boolean isSettingActivity, IDrawerCallback intCallback) {
        this(context,animator);
        this.intCallback = intCallback;
        this.isSettingActivity = isSettingActivity;
        if (Config.DRAWER_GRID_FEATURE) {
            grid_mode = !isSettingActivity && !DrawerMenuItemsHideController.getInstance().is(SHOW_GRID_MODE);
        }
    }

    public interface IDrawerCallback{
        void onItemClick(int id);
    }
    //endregion

}
