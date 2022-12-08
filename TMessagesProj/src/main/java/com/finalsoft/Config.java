package com.finalsoft;

import com.finalsoft.ui.adapter.DialogBottomMenuLayoutAdapter;
import com.finalsoft.ui.tab.FolderLayoutAdapter;

import org.telegram.messenger.BuildConfig;
import org.telegram.messenger.R;
import org.telegram.ui.Adapters.DrawerLayoutAdapter;

public class Config {
    public static final String TAG =  BuildConfig.TAG;
    public static final int MAX_ACCOUNT_COUNT = BuildConfig.MAX_ACCOUNT_COUNT;

    public static long LAST_SEEN = 0;

    //region Customized:
    public static final String OFFICIAL_CHANNELS =  BuildConfig.OFFICIAL_CHANNELS; //CSV "a,b,v,.."
    public static final String SUPPORT_GROUP =  BuildConfig.SUPPORT_GROUP;
    public static final String DEFAULT_FONT =  BuildConfig.DEFAULT_FONT;
    public static final String FLURRY_APP_ID =  BuildConfig.FLURRY_APP_ID;
    public static final String PREF_NAME =  BuildConfig.PREF_NAME;
    public static final String DOMAIN_URL =  BuildConfig.DOMAIN_URL;

    public static final String PLAY_STORE_SITE_URL = "https://play.google.com/store/apps/details?id=";
    public static final String RESPONSE_STATUS = "Status";
    public static final String RESPONSE_DATA = "Data";


    //----------------------------------------------------------------
    public static final boolean INTEGRATED_SETTING = false;
    public static final boolean SMART_FORWARD_FEATURE = false;
    public static final boolean TAB_ONLINE_FEATURE = true;
    public static final boolean TAB_SCHEDULED_FEATURE = false;
    public static final boolean TRANSLATE_FEATURE = true;
    public static final boolean NEKO_FEATURE = true;
    public static final boolean DIALOG_FILTER_FEATURE = true;
    public static final boolean CHAT_PRIVACY_FEATURE = false;
    public static final boolean STICKERS_FEATURE = true;
    public static final boolean PROFILE_IMAGE_COLLECTION_FEATURE = false;
    public static final boolean V2T_FEATURE = false;
    public static final boolean MEDIA_FEATURE = true;
    public static final boolean BIG_REFRESH_LOGIN_FEATURE = true;
    public static final boolean PROXY_SPONSOR_FEATURE = true;
    public static final boolean PROXY_SMART_FEATURE = true;
    public static final boolean TOOLBAR_SHADOW_FEATURE = true;
    public static final boolean AUTO_ANSWER_FEATURE = true;
    public static final boolean SHOW_BUBBLE_FEATURE = true;
    public static final boolean UNLIMITED_PIN_FEATURE = true;
    public static final boolean PRIVATE_GHOST_FEATURE = true;
    public static final boolean SUPPORT_GROUP_FEATURE = true;
    public static final boolean PROFILE_CELL_ICONS_FEATURE = true;
    public static final boolean SHAKE_FEATURE = false;
    public static final boolean HIDE_MODE_FAKE_PASS_FEATURE = false;
    public static final boolean FAKE_PHONE_FEATURE = false;
    public static final boolean INFO_IN_CHATS_FEATURE = false;
    public static final boolean DRAWER_SETTING_FEATURE = false;
    public static final boolean GHOST_MODE_FEATURE = true;
    public static final boolean TRANSPARENT_STATUSBAR_FEATURE = true;
    public static final boolean DIALOG_BOTTOM_MENU_FEATURE = false;
    public static final boolean MUTUAL_CONTACT_FEATURE = true;

    //region ADMOB
    public static final boolean ADMOB_FEATURE = true;
    public static final boolean OFF_ADMOB_FEATURE = true;
    public static final boolean ADMOB_REWARDED_FEATURE = true;
    public static final boolean ADMOB_BANNERS_FEATURE = true;
    public static final boolean ADMOB_NATIVE_FEATURE = true;
    public static final boolean DONATE_FEATURE = true;
    //endregion

    public static final boolean DRAWER_GRID_FEATURE = false;


    public static final boolean CONTACT_CHANGES_FEATURE = true;
    public static final boolean FIND_USERNAME_FEATURE = true;
    public static final boolean PROFILE_MAKER_FEATURE = true;

    public static final boolean VOICE_CHANGER_FEATURE = true;

    public static final boolean BASIC_FONTS_FEATURE = true;

    public static final boolean GOOGLE_RATE_FEATURE = true;

	public static final boolean PROMO_FEATURE = true;

    public static final boolean CONTACT_DELETE_FEATURE = true;

    public static final boolean SHARE_IN_CHAT_FEATURE = true;

    public static final boolean PEOPLE_NEARBY_FEATURE = true;

    public static final boolean LOGIN_PROXY_CHECKBOX_FEATURE = true;

    public static final boolean DOWNLOAD_MANAGER_FEATURE = false;

    public static final boolean SHOW_REFRESH_IN_DIALOGS = true;

    //endregion

    public static boolean getDialogsDefaultIcons(SharedStorage.keys key) {
        return SharedStorage.keys.OPEN_ARCHIVE != key
                && SharedStorage.keys.HIDE_KEYBOARD != key
                && SharedStorage.keys.SHOW_EDITED != key
                && SharedStorage.keys.SHOW_DELETED != key
                && SharedStorage.keys.AUTO_ANSWER != key
                && SharedStorage.keys.SHOW_GIFT != key

                && SharedStorage.keys.ITALIC != key
                && SharedStorage.keys.BOLD != key
                && SharedStorage.keys.STRIKE != key
                && SharedStorage.keys.UNDERLINE != key
                && SharedStorage.keys.SHOW_AUTO_ANSWER != key;
    }
    public static final String FOLDER_DISABLED_SETTINGS = String.format("%s,%s,%s,%s,%s",
            FolderLayoutAdapter.SHOW_ARCHIVE_ON_TABS,
            FolderLayoutAdapter.DIALOGS_FILTER_ON_TITLE,
            FolderLayoutAdapter.SHOW_REMOTE_EMOTIONS,
            FolderLayoutAdapter.SHOW_UNREAD_ONLY,
            FolderLayoutAdapter.DIALOGS_FILTER_ON_PEN);

    public static final String DRAWER_HIDE_ITEMS = String.format("%s,%s,%s,%s,%s,%s,%s",
            /*DrawerLayoutAdapter.SUPPORT_GROUP,*/
            DrawerLayoutAdapter.SHOW_GRID_MODE,
            DrawerLayoutAdapter.SAVED_MEDIA,
            DrawerLayoutAdapter.SCHEDULE_MESSAGE,
            DrawerLayoutAdapter.THEME,
            DrawerLayoutAdapter.SUPPORT_GROUP,
            DrawerLayoutAdapter.SUPPORT_GROUP,
            DrawerLayoutAdapter.BIG_AVATAR
    );


    public static final String DIALOG_BOTTOM_MENU_HIDE_ITEMS = String.format("%s,%s",
            DialogBottomMenuLayoutAdapter.Type.FIX_X.ordinal(),
            DialogBottomMenuLayoutAdapter.Type.HIDE_MENU.ordinal()
    );
    //endregion

    public static final int ICON_CUSTOM_SETTINGS = R.drawable.msg_settings; //R.drawable.ic_custom_settings
    public static final int ICON_CONTACT_CHANGES = R.drawable.msg_contacts; //R.drawable.ic_user_changes; //
    public static final int ICON_PEOPLE_NEARBY =R.drawable.msg_location; //R.drawable.ic_people_nearby;
    public static final int ICON_ADDMOB = R.drawable.ic_admob;//R.drawable.ic_admob_logo;
    public static final int ICON_GHOST_MENU =R.drawable.ic_ghost;  //R.drawable.ic_menu_ghost;
    public static final int GHOST_ON_ICON = R.drawable.ic_ghost_on; //R.drawable.ic_ghost_on;
    public static final int GHOST_OFF_ICON = R.drawable.ic_ghost_off; //R.drawable.ic_ghost_on;
    public static final int ICON_ALL_DIALOGS_TAB = R.drawable.ic_all_dialogs; // R.drawable.msg_media
    public static final int ICON_VOICE_CHANGER = R.drawable.ic_voice_changer;//R.drawable.input_mic;
}
