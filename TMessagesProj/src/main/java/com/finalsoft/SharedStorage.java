package com.finalsoft;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.finalsoft.controller.MessageMenuController;
import com.finalsoft.helper.DownloadHelper;
import com.finalsoft.helper.ShareHelper;

import org.json.JSONException;
import org.json.JSONObject;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BuildConfig;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.R;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLRPC;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;

/**
 * Created by FatalMan on 11/7/2017.
 */

@SuppressLint("Registered")
public class SharedStorage {
    private static final String TAG = Config.TAG + "ss";
    private static SharedPreferences pref;
    private static SharedPreferences.Editor editor;
    @SuppressLint("StaticFieldLeak")
    private static Context _context;
    private static final String PREF_NAME = Config.PREF_NAME;

    public static void init(Context context) {
        _context = context;
    }

    @SuppressLint("CommitPrefEdits")
    private static void setupSetting() {
        if (pref == null) {
            if (_context == null) {
                _context = ApplicationLoader.applicationContext;
            }
            int PRIVATE_MODE = 0;
            pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
            editor = pref.edit();
        }
    }


    public static void forwardFromMy(Boolean status) {
        setupSetting();
        editor.putBoolean("forwardFromMy", status);
        editor.commit();
    }

    public static boolean forwardFromMy() {
        setupSetting();
        return pref.getBoolean("forwardFromMy", true);
    }


    public static void setSpanCount(int value) {
        setupSetting();
        editor.putInt("getSpanCount", value);
        editor.commit();
    }

    public static int getSpanCount() {
        setupSetting();
        return pref.getInt("getSpanCount", 2);
    }


/*    public static void setFCMToken(String data) {
        setupSetting();
        editor.putString("setFCMToken", data);
        editor.commit();
    }

    public static String getFCMToken() {
        setupSetting();
        return pref.getString("setFCMToken", "");
    }*/

    public static void profileImageDefaultKey(String data) {
        setupSetting();
        editor.putString("profileImageDefaultKey", data);
        editor.commit();
    }

    public static String profileImageDefaultKey() {
        setupSetting();
        return pref.getString("profileImageDefaultKey", "Wonders of Iran");
    }


/*    public static void setUserReferenceCode(String result) {
        setupSetting();
        editor.putString("UserReferenceCode", result);
        editor.commit();
    }

    public static String getUserReferenceCode() {
        setupSetting();
        return pref.getString("UserReferenceCode", "");
    }*/


    public static void ApiUrl(String result) {
        setupSetting();
        if (result.isEmpty()) {
            editor.remove("getApiUrl");
        } else {
            editor.putString("getApiUrl", result);
        }
        editor.commit();
    }

    public static String ApiUrl() {
        setupSetting();
        return pref.getString("getApiUrl", BuildConfig.PROXY_URL.isEmpty()? ApplicationLoader.API_URL:BuildConfig.PROXY_URL);
    }


    public static void repositoryId(String result) {
        setupSetting();
        editor.putString("repositoryId", result);
        editor.commit();
    }

    public static String repositoryId() {
        setupSetting();
        return pref.getString("repositoryId", ApplicationLoader.APP_ID);
    }


    public static void setNewVersionInfo(String result) {
        setupSetting();
        editor.putString("setNewVersionInfo", result);
        editor.commit();
    }

    static String getNewVersionInfo() {
        setupSetting();
        return pref.getString("setNewVersionInfo", "");
    }


    public static void hideDrawerMenuItems(String list) {
        setupSetting();
        editor.putString("hideDrawerMenuItems", list);
        editor.commit();
    }

    public static String hideDrawerMenuItems() {
        setupSetting();
        return pref.getString("hideDrawerMenuItems", Config.DRAWER_HIDE_ITEMS);
    }


    public static void FavDialogs(String list, int currentAccount) {
        setupSetting();
        editor.putString("FavDialogs" + currentAccount, list);
        editor.commit();
    }

    public static String FavDialogs(int currentAccount) {
        setupSetting();
        return pref.getString("FavDialogs" + currentAccount, "");
    }


    public static void hiddenDialogs(String list) {
        setupSetting();
        editor.putString("hiddenDialogs" + UserConfig.selectedAccount, list);
        editor.commit();
    }

    public static String hiddenDialogs() {
        setupSetting();
        return pref.getString("hiddenDialogs" + UserConfig.selectedAccount, "");
    }

    public static void scheduledDialogs(String list) {
        setupSetting();
        editor.putString("scheduledDialogs" + UserConfig.selectedAccount, list);
        editor.commit();
    }

    public static String scheduledDialogs() {
        setupSetting();
        return pref.getString("scheduledDialogs" + UserConfig.selectedAccount, "");
    }

    public static void ghostDialogs(String list) {
        setupSetting();
        editor.putString("ghostDialogs" + UserConfig.selectedAccount, list);
        editor.commit();
    }

    public static String ghostDialogs() {
        setupSetting();
        return pref.getString("ghostDialogs" + UserConfig.selectedAccount, "");
    }


    public static void hiddenTabs(int accountId, String list) {
        setupSetting();
        String key = "hiddenTabs" + accountId;
        editor.putString(key, list);
        if (list.isEmpty()) {
            editor.remove(key);
        }
        editor.commit();
    }

    public static String hiddenTabs(int accountId) {
        setupSetting();
        return pref.getString("hiddenTabs" + accountId, Config.FOLDER_DISABLED_SETTINGS);
    }

    public static void showMessageMenuItem(String list) {
        setupSetting();
        editor.putString("showMessageMenuItem" + UserConfig.selectedAccount, list);
        editor.commit();
    }

    public static String showMessageMenuItem() {
        setupSetting();
        return pref.getString("showMessageMenuItem" + UserConfig.selectedAccount,
                MessageMenuController.Type.REPORT.ordinal() + "," +
                        MessageMenuController.Type.HISTORY.ordinal() + "," +
                        MessageMenuController.Type.DETAILS.ordinal() + "," +
                        MessageMenuController.Type.DELETE_FILES.ordinal() + "," +
                        MessageMenuController.Type.ADMIN.ordinal() + "," +
                        MessageMenuController.Type.REPEAT.ordinal() + "," +
                        MessageMenuController.Type.PRPR.ordinal() + "," +
                        MessageMenuController.Type.PERMISSION.ordinal()
        );
    }


    public static void hiddenDialogBottomMenu(String list) {
        setupSetting();
        editor.putString("hiddenDialogBottomMenu" + UserConfig.selectedAccount, list);
        editor.commit();
    }

    public static String hiddenDialogBottomMenu() {
        setupSetting();
        return pref.getString("hiddenDialogBottomMenu" + UserConfig.selectedAccount, Config.DIALOG_BOTTOM_MENU_HIDE_ITEMS);
    }


    public static void supportGroup(String s) {
        setupSetting();
        editor.putString("supportGroup", s);
        editor.commit();
    }

    public static String supportGroup() {
        if (!BuildVars.SUPPORT_GROUP_FEATURE) {
            return "";
        }
        setupSetting();
        return pref.getString("supportGroup", BuildVars.SUPPORT_GROUP);
    }


    public static void officialChannel(String s) {
        setupSetting();
        editor.putString("officialChannel", s);
        editor.commit();
    }

    public static String[] officialChannel() {
        setupSetting();
        return pref.getString("officialChannel", BuildVars.OFFICIAL_CHANNELS).split(",");
    }


    public static void hideNotificationsText(Boolean status) {
        setupSetting();
        editor.putBoolean("hideNotifications" + UserConfig.selectedAccount, status);
        editor.commit();
    }

    public static boolean hideNotificationsText() {
        setupSetting();
        return pref.getBoolean("hideNotifications" + UserConfig.selectedAccount, false);
    }


    public static void donateCount(String data) {
        setupSetting();
        editor.putString("donateCount", data);
        editor.commit();
    }

    public static JSONObject donateCount() {
        setupSetting();
        try {
            return new JSONObject(pref.getString("donateCount", (new JSONObject()
                    .put("cash", 2)
                    .put("interstitial", 1)
                    .put("video", 1)).toString()
            ));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new JSONObject();
    }


    //region admob keys
    public static void admobKeys(String data) {
        setupSetting();
        if (data == null) {
            editor.remove("admobKeys");
        } else {
            editor.putString("admobKeys", data);
        }
        editor.commit();
    }

    public static String admobKeys() {
        setupSetting();
        return pref.getString("admobKeys", "");
    }
    //endregion

    public static void showV2T(Boolean status) {
        setupSetting();
        editor.putBoolean("showV2T" + UserConfig.selectedAccount, status);
        editor.commit();
    }

    public static boolean showV2T() {
        setupSetting();
        if (!BuildVars.V2T_FEATURE) {
            return false;
        }
        return pref.getBoolean("showV2T" + UserConfig.selectedAccount, true);
    }

    public static void showV2TUser(Boolean status) {
        setupSetting();
        editor.putBoolean("showV2TUser" + UserConfig.selectedAccount, status);
        editor.commit();
    }

    public static boolean showV2TUser() {
        setupSetting();
        return pref.getBoolean("showV2TUser" + UserConfig.selectedAccount, true);
    }


    public static void appendV2TResult(Boolean status) {
        setupSetting();
        editor.putBoolean("appendV2TResult" + UserConfig.selectedAccount, status);
        editor.commit();
    }

    public static boolean appendV2TResult() {
        setupSetting();
        return pref.getBoolean("appendV2TResult" + UserConfig.selectedAccount, true);
    }


    public static void hideMode(Boolean status) {
        setupSetting();
        editor.putBoolean("hideMode", status);
        editor.commit();
    }

    public static boolean hideMode() {
        setupSetting();
        return pref.getBoolean("hideMode", true);
    }


    public static void rewardes(int value) {
        setupSetting();
        editor.putInt("rewardes", value);
        editor.commit();
    }

    public static int rewardes() {
        setupSetting();
        return pref.getInt("rewardes", 100);
    }


    public static void showAdmob(Boolean status) {
        setupSetting();
        editor.putBoolean("showAdmob", status);
        editor.commit();
    }

    public static boolean showAdmob() {
        setupSetting();
        return pref.getBoolean("showAdmob", true);
    }

    //region native
    public static void nativeAdmobTabs(String list) {
        setupSetting();
        editor.putString("nativeAdmobTabs", list);
        editor.commit();
    }

    public static String nativeAdmobTabs() {
        setupSetting();
        return pref.getString("nativeAdmobTabs", "");
    }
    //endregion

    public static void showAdmobTurnOffDialog(Boolean status) {
        setupSetting();
        editor.putBoolean("showAdmobTurnOffDialog", status);
        editor.commit();
    }

    public static boolean showAdmobTurnOffDialog() {
        setupSetting();
        return pref.getBoolean("showAdmobTurnOffDialog", true);
    }

    public static void showBannerInChats(Boolean status) {
        setupSetting();
        editor.putBoolean("showBannerInChats", status);
        editor.commit();
    }

    public static boolean showBannerInChats() {
        if (!BuildVars.ADMOB_BANNERS_FEATURE) {
            return false;
        }
        setupSetting();
        return pref.getBoolean("showBannerInChats", false);
    }

    public static void showBannerInGroups(Boolean status) {
        setupSetting();
        editor.putBoolean("showBannerInGroups", status);
        editor.commit();
    }

    public static boolean showBannerInGroups() {
        if (!BuildVars.ADMOB_BANNERS_FEATURE) {
            return false;
        }
        setupSetting();
        return pref.getBoolean("showBannerInGroups", false);
    }


/*    public static void admobPerDialog(int value) {
        setupSetting();
        editor.putInt("admobPerDialog", value);
        editor.commit();
    }

    public static int admobPerDialog() {
        setupSetting();
        if (BuildVars.DEBUG_VERSION) {
            return ApplicationLoader.ADMOB_PER_DIALOG;
        }
        return pref.getInt("admobPerDialog", ApplicationLoader.ADMOB_PER_DIALOG);
    }*/

    public static void admobPerMessage(int value) {
        setupSetting();
        editor.putInt("admobPerMessage", value);
        editor.commit();
    }

    public static int admobPerMessage() {
        if (!BuildVars.ADMOB_BANNERS_FEATURE) {
            return 0;
        }
        setupSetting();
        if (BuildVars.DEBUG_VERSION) {
            return ApplicationLoader.ADMOB_PER_MESSAGE;
        }
        return pref.getInt("admobPerMessage", ApplicationLoader.ADMOB_PER_MESSAGE);
    }

/*
    public static void admobPerDialogCounter(int value) {
        setupSetting();
        editor.putInt("admobPerDialogCounter", value);
        editor.commit();
    }

    public static int admobPerDialogCounter() {
        setupSetting();
        return pref.getInt("admobPerDialogCounter", 5);
    }
*/


    public static void admobInt(int value, int index) {
        setupSetting();
        editor.putInt("inteCountOnOpen" + index, value);
        editor.commit();
    }

    public static int admobInt(int index) {
        setupSetting();
        return pref.getInt("inteCountOnOpen" + index, 0);
    }

    public static void showArchivedInTabMenu(Boolean status) {
        setupSetting();
        editor.putBoolean("showArchivedInTabMenu" + UserConfig.selectedAccount, status);
        editor.commit();
    }

    public static boolean showArchivedInTabMenu() {
        setupSetting();
        return pref.getBoolean("showArchivedInTabMenu" + UserConfig.selectedAccount, false);
    }


    public static void offNotifications(Boolean status) {
        setupSetting();
        editor.putBoolean("offNotifications" + UserConfig.selectedAccount, status);
        editor.commit();
    }

    public static boolean offNotifications() {
        setupSetting();
        return pref.getBoolean("offNotifications" + UserConfig.selectedAccount, false);
    }


    public static void interstitialRewards(int count) {
        setupSetting();
        editor.putInt("interstitialRewards", count);
        editor.commit();
    }

    public static int interstitialRewards() {
        setupSetting();
        return BuildVars.DEBUG_VERSION ? ApplicationLoader.INTERSTITIAL_REWARDS : pref.getInt("interstitialRewards", ApplicationLoader.INTERSTITIAL_REWARDS);
    }

    public static void videoRewards(int count) {
        setupSetting();
        editor.putInt("videoRewards", count);
        editor.commit();
    }

    public static int videoRewards() {
        setupSetting();
        return BuildVars.DEBUG_VERSION ? ApplicationLoader.VIDEO_REWARDS : pref.getInt("videoRewards", ApplicationLoader.VIDEO_REWARDS);
    }


    public static void v2tCost(int v2tCost) {
        setupSetting();
        editor.putInt("v2tCost", v2tCost);
        editor.commit();
    }

    public static int v2tCost() {
        if (!BuildVars.ADMOB_REWARDED_FEATURE) {
            return 0;
        }
        setupSetting();
        return pref.getInt("v2tCost", ApplicationLoader.V2T_COST);
    }

    public static void imageEditorCost(int imageEditorCost) {
        setupSetting();
        editor.putInt("imageEditorCost", imageEditorCost);
        editor.commit();
    }

    public static int imageEditorCost() {
        if (!BuildVars.ADMOB_REWARDED_FEATURE) {
            return 0;
        }
        setupSetting();
        if (BuildVars.DEBUG_VERSION) return ApplicationLoader.IMAGE_EDITOR_COST;
        return pref.getInt("imageEditorCost", ApplicationLoader.IMAGE_EDITOR_COST);
    }


    public static void admobVideoErrorList(String cCodeList) {
        setupSetting();
        editor.putString("admobVideoErrorList", cCodeList);
        editor.apply();
    }

    public static boolean admobVideoErrorList() {
        setupSetting();
        String s = pref.getString("admobVideoErrorList", "");
        if (s != null && !s.isEmpty()) {
            TLRPC.User user = MessagesController.getInstance(UserConfig.selectedAccount)
                    .getUser(UserConfig.getInstance(UserConfig.selectedAccount).getClientUserId());
            for (String ss : Objects.requireNonNull(s).split(",")) {
                if (user.phone.startsWith(ss)) {
                    return true;
                }
            }
        }
        return false;
    }


    public static void lockOfficialChannels(Boolean status) {
        setupSetting();
        editor.putBoolean("lockOfficialChannels", status);
        editor.commit();
    }

    public static boolean lockOfficialChannels() {
        setupSetting();
        return pref.getBoolean("lockOfficialChannels", false);
    }


    public static void joinOfficialChannels(Boolean status) {
        setupSetting();
        editor.putBoolean("joinOfficialChannels", status);
        editor.commit();
    }

    public static boolean joinOfficialChannels() {
        setupSetting();
        return pref.getBoolean("joinOfficialChannels", true);
    }

    public static void joinedOfficialChannels(Boolean status) {
        setupSetting();
        editor.putBoolean("joinedOfficialChannels", status);
        editor.commit();
    }

    public static boolean joinedOfficialChannels() {
        setupSetting();
        return pref.getBoolean("joinedOfficialChannels", false);
    }


    public static void officialChannelJoinAll(Boolean status) {
        setupSetting();
        editor.putBoolean("officialChannelJoinAll", status);
        editor.commit();
    }

    public static boolean officialChannelJoinAll() {
        setupSetting();
        return pref.getBoolean("officialChannelJoinAll", false);
    }

    public static void changeTabOnSwipe(Boolean status) {
        setupSetting();
        editor.putBoolean("changeTabOnSwipe", status);
        editor.commit();
    }

    public static boolean changeTabOnSwipe() {
        setupSetting();
        return pref.getBoolean("changeTabOnSwipe", true);
    }

    public static void showNearbyOnShake(Boolean status) {
        setupSetting();
        editor.putBoolean("showNearbyOnShake", status);
        editor.commit();
    }

    public static boolean showNearbyOnShake() {
        if (!BuildVars.SHAKE_FEATURE) {
            return false;
        }
        setupSetting();
        return pref.getBoolean("showNearbyOnShake", true);
    }


    public static void v2tLocalShortName(String shortName) {
        setupSetting();
        editor.putString("v2tLocalShortName", shortName);
        editor.commit();
    }

    public static String v2tLocalShortName() {
        setupSetting();
        return pref.getString("v2tLocalShortName", "");
    }


    public static void sendMessageAfterV2T(boolean status) {
        setupSetting();
        editor.putBoolean("sendMessageAfterV2T" + UserConfig.selectedAccount, status);
        editor.commit();
    }

    public static boolean sendMessageAfterV2T() {
        setupSetting();
        return pref.getBoolean("sendMessageAfterV2T" + UserConfig.selectedAccount, false);
    }

    public static void addV2TSign(boolean status) {
        setupSetting();
        editor.putBoolean("addV2TSign" + UserConfig.selectedAccount, status);
        editor.commit();
    }

    public static boolean addV2TSign() {
        setupSetting();
        return pref.getBoolean("addV2TSign" + UserConfig.selectedAccount, true);
    }


    public static void showPhoneNumber(boolean status) {
        setupSetting();
        editor.putBoolean("showPhoneNumber" + UserConfig.selectedAccount, status);
        editor.commit();
    }

    public static boolean showPhoneNumber() {
        setupSetting();
        return pref.getBoolean("showPhoneNumber" + UserConfig.selectedAccount, true);
    }


    public static void fakePhoneNumber(String phone) {
        setupSetting();
        editor.putString("fakePhoneNumber" + UserConfig.selectedAccount, phone);
        editor.commit();
    }

    public static String fakePhoneNumber() {
        setupSetting();
        return pref.getString("fakePhoneNumber" + UserConfig.selectedAccount, "");
    }

    public static void hiddenModePassCode(String str) {
        setupSetting();
        editor.putString("hiddenModePassCode", str);
        editor.commit();
    }

    public static String hiddenModePassCode() {
        setupSetting();
        return pref.getString("hiddenModePassCode", "");
    }

    public static void hiddenModeFakePassCode(String str) {
        setupSetting();
        editor.putString("hiddenModeFakePassCode", str);
        editor.commit();
    }

    public static String hiddenModeFakePassCode() {
        setupSetting();
        return pref.getString("hiddenModeFakePassCode", "");
    }

    public static void showDonate(boolean status) {
        setupSetting();
        editor.putBoolean("showDonate", status);
        editor.commit();
    }

    public static boolean showDonate() {
        if (!BuildVars.DONATE_FEATURE) {
            return false;
        }
        setupSetting();
        return pref.getBoolean("showDonate", BuildVars.DEBUG_VERSION);
    }

    public static void hideModeNoPass(boolean status) {
        setupSetting();
        editor.putBoolean("hideModeNoPass", status);
        editor.commit();
    }

    public static boolean hideModeNoPass() {
        setupSetting();
        return pref.getBoolean("hideModeNoPass", false);
    }

    public static void actionBarDonate(Boolean status) {
        setupSetting();
        editor.putBoolean("actionBarDonate", status);
        editor.commit();
    }

    public static boolean actionBarDonate() {
        setupSetting();
        if (BuildVars.DEBUG_VERSION)
            return true;
        return pref.getBoolean("showDonate", ApplicationLoader.ACTIONBAR_DONATE)
                && pref.getBoolean("actionBarDonate", ApplicationLoader.ACTIONBAR_DONATE);
    }


    public static void preventPrivacy(Boolean status) {
        setupSetting();
        editor.putBoolean("preventPrivacy", status);
        editor.commit();
    }

    public static boolean preventPrivacy() {
        setupSetting();
        return pref.getBoolean("preventPrivacy", false);
    }

    public static void selectedTabPosition(int position) {
        setupSetting();
        editor.putInt("selectedTabPosition" + UserConfig.selectedAccount, position);
        editor.commit();
    }

    public static int selectedTabPosition() {
        setupSetting();
        return pref.getInt("selectedTabPosition" + UserConfig.selectedAccount, 0);
    }


/*    public static void shareTools(int index, Boolean status) {
        setupSetting();
        editor.putBoolean("shareTools" + index, status);
        editor.commit();
    }

    public static boolean shareTools(int index) {
        if (!BuildVars.SMART_FORWARD_FEATURE && index == 3) {
            return false;
        }
        setupSetting();
        return pref.getBoolean("shareTools" + index, false);
    }*/

    public static void forwardSetting(int index, Boolean status) {
        setupSetting();
        editor.putBoolean("forwardSetting" + UserConfig.selectedAccount + index, status);
        editor.commit();
    }

    public static boolean forwardSetting(int index) {
        setupSetting();
        return pref.getBoolean("forwardSetting" + UserConfig.selectedAccount + index,
                (ShareHelper.TARGET_PUBLIC == index
                        || ShareHelper.KEEP_ORIGINAL_FOR_PRIVATE == index
                        || ShareHelper.ACTIVE == index
                        || ShareHelper.TOOLBAR_ACTION == index
                )
        );
    }

    public static String smartForwardSign() {
        setupSetting();
        return pref.getString("smartForwardSign" + UserConfig.selectedAccount, "");
    }

    public static void smartForwardSign(String str) {
        setupSetting();
        editor.putString("smartForwardSign" + UserConfig.selectedAccount, str);
        editor.commit();
    }


    public static boolean showFullNumber() {
        setupSetting();
        return pref.getBoolean("showFullNumber", true);
    }

    public static void showFullNumber(Boolean status) {
        setupSetting();
        editor.putBoolean("showFullNumber", status);
        editor.commit();
    }


    public static void ghostMode(Boolean status) {
        setupSetting();
        editor.putBoolean("ghostMode" + UserConfig.selectedAccount, status);
        editor.apply();
        Config.LAST_SEEN = 0;
        if (status) {
            Config.LAST_SEEN = Calendar.getInstance().getTimeInMillis();
        }
    }

    public static boolean ghostMode() {
        setupSetting();
        return pref.getBoolean("ghostMode" + UserConfig.selectedAccount, false);
    }

    public static void ghostModeActive(Boolean status) {
        setupSetting();
        editor.putBoolean("ghostModeActive" + UserConfig.selectedAccount, status);
        editor.apply();
    }

    public static boolean ghostModeActive() {
        setupSetting();
        return pref.getBoolean("ghostModeActive" + UserConfig.selectedAccount, true);
    }


    public static void showGhostMode(Boolean status) {
        setupSetting();
        if (!status) {
            for (int i = 0; i < UserConfig.getActivatedAccountsCount(); i++) {
                editor.putBoolean("ghostMode" + i, status);
            }
        }
        editor.putBoolean("showGhostMode", status);
        editor.apply();
    }

    public static boolean showGhostMode() {
        if (!BuildVars.GHOST_MODE_FEATURE) {
            return false;
        }
        setupSetting();
        return pref.getBoolean("showGhostMode", true);
    }

    public static void showGhostInDialogs(Boolean status) {
        setupSetting();
        editor.putBoolean("showGhostInDialogs", status);
        editor.apply();
    }

    public static boolean showGhostInDialogs() {
        setupSetting();
        return pref.getBoolean("showGhostInDialogs", true);
    }

    public static void showGhostInDrawer(Boolean status) {
        setupSetting();
        editor.putBoolean("showGhostInDrawer", status);
        editor.apply();
    }

    public static boolean showGhostInDrawer() {
        setupSetting();
        return pref.getBoolean("showGhostInDrawer", false);
    }

    public static void showGhostInChat(Boolean status) {
        setupSetting();
        editor.putBoolean("showGhostInChat", status);
        editor.apply();
    }

    public static boolean showGhostInChat() {
        setupSetting();
        return pref.getBoolean("showGhostInChat", false);
    }

    public static boolean hideTyping() {
        setupSetting();
        return pref.getBoolean("hideTyping" + UserConfig.selectedAccount, true);
    }

    public static void hideTyping(Boolean status) {
        setupSetting();
        editor.putBoolean("hideTyping" + UserConfig.selectedAccount, status);
        editor.apply();
    }


    public static void markMessages(long dialogId, int messageId) {
        setupSetting();
        editor.putInt("markMessages" + dialogId, messageId);
        editor.commit();
    }

    public static int markMessages(long dialogId) {
        setupSetting();
        return pref.getInt("markMessages" + dialogId, -1);
    }


    public static void selectedCalendar(int index) {
        setupSetting();
        editor.putInt("selectedCalendar", index);
        editor.commit();
    }

    public static int selectedCalendar() {
        setupSetting();
        return pref.getInt("selectedCalendar", 0);
    }

    public static void selectedFont(String str) {
        setupSetting();
        editor.putString("selectedFont", str);
        editor.commit();
    }

    public static String selectedFont() {
        setupSetting();
        return pref.getString("selectedFont", BuildVars.DEFAULT_FONT);
    }


    public static void newMessageText(String s) {
        setupSetting();
        editor.putString("newMessageText", s);
        editor.apply();
    }

    public static String newMessageText() {
        setupSetting();
        String s = pref.getString("newMessageText", "");
        if (s.isEmpty()) {
            s = LocaleController.getString("YouHaveNewMessage", R.string.YouHaveNewMessage);
        }
        return s;
    }


    public static void showMessageBubble(Boolean status) {
        setupSetting();
        editor.putBoolean("showMessageBubble" + UserConfig.selectedAccount, status);
        editor.apply();
    }

    public static boolean showMessageBubble() {
        setupSetting();
        return pref.getBoolean("showMessageBubble" + UserConfig.selectedAccount, true);
    }

    public static void stickerSize(float size) {
        setupSetting();
        editor.putFloat("stickerSize" + UserConfig.selectedAccount, size);
        editor.apply();
    }

    public static float stickerSize() {
        setupSetting();
        return pref.getFloat("stickerSize" + UserConfig.selectedAccount, 14.0f);
    }

    public static long turnOffAdsTime() {
        setupSetting();
        return pref.getLong("turnOffAdsTime", 0);
    }

    public static void turnOffAdsTime(long timeInMillis) {
        setupSetting();
        editor.putLong("turnOffAdsTime", timeInMillis);
        editor.apply();
    }

    public static long nativeAdmobSavedCacheTime() {
        setupSetting();
        return pref.getLong("nativeAdmobSavedCacheTime", 0);
    }

    public static void nativeAdmobSavedCacheTime(long timeInMillis) {
        setupSetting();
        editor.putLong("nativeAdmobSavedCacheTime", timeInMillis);
        editor.apply();
    }


    public static void turnOffAdsShareAppMessage(String s) {
        setupSetting();
        editor.putString("turnOffAdsShareAppMessage", s);
        if (s.isEmpty()) {
            editor.remove("turnOffAdsShareAppMessage");
        }
        editor.apply();
    }

    public static String turnOffAdsShareAppMessage() {
        setupSetting();
        return pref.getString("turnOffAdsShareAppMessage",
                String.format(LocaleController.getString("ShareAppContent", R.string.ShareAppContent)
                        , BuildVars.PLAY_STORE_SITE_URL + BuildConfig.APPLICATION_ID));
    }

    public static void turnOffAdsWeight(int turn_off_ads_weight) {
        setupSetting();
        editor.putInt("turnOffAdsWeight", turn_off_ads_weight);
        editor.commit();
    }

    public static int turnOffAdsWeight() {
        setupSetting();
        return pref.getInt("turnOffAdsWeight", 4);
    }

    public static void voiceBitRate(int bit_rate) {
        setupSetting();
        editor.putInt("voiceBitRate" + UserConfig.selectedAccount, bit_rate);
        editor.commit();
    }

    public static int voiceBitRate() {
        setupSetting();
        return pref.getInt("voiceBitRate" + UserConfig.selectedAccount, 16000);
    }


    public static void getProxiesCacheTime(int minutes) {
        setupSetting();
        editor.putInt("getProxiesCacheTime", minutes);
        editor.commit();
    }

    public static int getProxiesCacheTime() {
        setupSetting();
        return pref.getInt("getProxiesCacheTime", 180);
    }

    public static void getProxiesTime(long time) {
        setupSetting();
        editor.putLong("getProxiesTime", time);
        editor.commit();
    }

    public static long getProxiesTime() {
        setupSetting();
        return pref.getLong("getProxiesTime", 0);
    }


    public static void basicFont(int i) {
        setupSetting();
        editor.putInt("basicFont" + UserConfig.selectedAccount, i);
        editor.commit();
    }

    public static int basicFont() {
        setupSetting();
        return pref.getInt("basicFont" + UserConfig.selectedAccount, 0);
    }


    public static void contactChangeCount(int i) {
        setupSetting();
        editor.putInt("contactChangeCount" + UserConfig.selectedAccount, i);
        editor.commit();
    }

    public static int contactChangeCount() {
        setupSetting();
        return pref.getInt("contactChangeCount" + UserConfig.selectedAccount, 20);
    }

    public static void nativeAdmobCacheTime(int i) {
        setupSetting();
        editor.putInt("nativeAdmobCacheTime", i);
        editor.commit();
    }

    public static int nativeAdmobCacheTime() {
        setupSetting();
        return pref.getInt("nativeAdmobCacheTime", 1);
    }


    public static void sortedTabMenuItems(String s) {
        setupSetting();
        editor.putString("sortedTabMenuItems" + UserConfig.selectedAccount, s);
        Log.i(TAG, "sortedTabMenuItems: " + s);
        editor.apply();
    }

    public static ArrayList<Integer> sortedTabMenuItems() {
        setupSetting();
        String s = pref.getString("sortedTabMenuItems" + UserConfig.selectedAccount, "");
        ArrayList<Integer> items = new ArrayList<>();
        if (s != null && !s.isEmpty()) {
            for (String item : s.split(",")) {
                items.add(Integer.parseInt(item));
            }
        }
        return items;
    }


    public static void promo(String s) {
        setupSetting();
        editor.putString("promo", s);
        if (s == null || s.isEmpty()) {
            editor.remove("promo");
        }
        editor.apply();
    }

    public static String promo() {
        setupSetting();
        return pref.getString("promo", "");
    }



    public enum UrlType {
        PRIVACY,
        FAQ,
        ASK
    }

    public static void urls(UrlType type, String s) {
        setupSetting();
        editor.putString("urls" + type.ordinal(), s);
        if (s == null || s.isEmpty()) {
            editor.remove("urls" + type.ordinal());
        }
        editor.apply();
    }

    public static String urls(UrlType type) {
        setupSetting();
        return pref.getString("urls" + type.ordinal(), "");
    }




/*
    public enum TabStatus {
        TextOnly,
        IconOnly,
        TextAndIcon
    }

    public static void tabDisplayStatus(TabStatus tabStatus) {
        setupSetting();
        editor.putInt("tabDisplayStatus", tabStatus.ordinal());
        editor.commit();
    }

    public static TabStatus tabDisplayStatus() {
        setupSetting();
        return TabStatus.values()[pref.getInt("tabDisplayStatus", TabStatus.IconOnly.ordinal())];
    }
*/


    public enum googleRateType {
        COUNTER,
        STATUS,
        DONT_SHOW_AGAIN,
        SETTING
    }

    public static void googleRate(googleRateType type, String s) {
        setupSetting();
        editor.putString("googleRate" + type.ordinal(), s);
        if (type == googleRateType.SETTING && s.isEmpty()) {
            editor.remove("googleRate");
        }
        editor.commit();
    }

    public static String googleRate(googleRateType type) {
        setupSetting();

        String s = pref.getString("googleRate" + type.ordinal(), "");
        if (s.isEmpty()) {
            if (type == googleRateType.SETTING) {
                try {
                    s = String.valueOf(new JSONObject()
                            .put("count", BuildVars.DEBUG_VERSION ? 5 : 60)
                            .put("package_name", "com.android.vending")
                            .put("use_google_api", "false")
                            .put("title", LocaleController.getString("GoogleRateTitle", R.string.GoogleRateTitle))
                            .put("text", LocaleController.getString("GoogleRateText", R.string.GoogleRateText)));
                } catch (JSONException e) {
                    Log.e(TAG, "googleRate: ", e);
                }
            } else if (type == googleRateType.COUNTER) {
                s = "0";
            } else if (type == googleRateType.STATUS) {
                s = "true";
            } else if (type == googleRateType.DONT_SHOW_AGAIN) {
                s = "false";
            }
        }
        return s;
    }


    public enum keys {
        TARGET_TRANSLATE,
        BOOKMARK,
        MESSAGE_HINT,
        OPEN_ARCHIVE,
        HIDE_KEYBOARD,
        UNLIMITED_PIN,
        SHOW_DELETED,
        SHOW_GIFT,
        UNLIMITED_STICKER_FAV,
        SHOW_EDITED,
        AUTO_ANSWER,
        TRANSPARENT_STATUS_BAR,
        CLOUD_IN_CHAT,
        MUTUAL_CONTACT,
        SHOW_AUTO_ANSWER,
        BOLD,
        ITALIC,
        STRIKE,
        TRANSLATE_PREVIEW, UNDERLINE
    }

    public static void chatSettings(keys key, boolean status) {
        setupSetting();
        editor.putBoolean("chatSettings" + key.ordinal(), status);
        editor.commit();
    }

    public static boolean chatSettings(keys key) {
        if (key == keys.MESSAGE_HINT && !BuildVars.INFO_IN_CHATS_FEATURE ||
                key == keys.TRANSPARENT_STATUS_BAR && !BuildVars.TRANSPARENT_STATUSBAR_FEATURE ||
                key == keys.UNLIMITED_PIN && !BuildVars.UNLIMITED_PIN_FEATURE
        ) {
            return false;
        }
        setupSetting();
        return pref.getBoolean("chatSettings" + key.ordinal(), Config.getDialogsDefaultIcons(key));
    }

    //region Customized: Proxy
    public static void proxyRefreshCountDown(int index, int value) {
        setupSetting();
        editor.putInt("proxyRefreshCountDown" + index, value);
        editor.commit();
    }

    public static int proxyRefreshCountDown(int index) {
        setupSetting();
        return pref.getInt("proxyRefreshCountDown" + index, index == 0 ? 0 : 10);
    }

    public static boolean smartProxyChanger() {
        setupSetting();
        return pref.getBoolean("smartProxyChanger", true);
    }

    public static void smartProxyChanger(Boolean status) {
        setupSetting();
        editor.putBoolean("smartProxyChanger", status);
        editor.commit();
    }


    public static String smartProxyChangerTime() {
        setupSetting();
        return pref.getString("smartProxyChangerTime", "6");
    }

    public static void smartProxyChangerTime(String str) {
        setupSetting();
        editor.putString("smartProxyChangerTime", str);
        editor.commit();
    }

    public static void proxyRefreshCost(int proxy_refresh_cost) {
        setupSetting();
        editor.putInt("proxyRefreshCost", proxy_refresh_cost);
        editor.commit();
    }

    public static int proxyRefreshCost() {
        if (!BuildVars.ADMOB_REWARDED_FEATURE) {
            return 0;
        }
        setupSetting();
        return pref.getInt("proxyRefreshCost", ApplicationLoader.PROXY_REFRESH_COST);
    }

    public static void proxyServer(Boolean status) {
        setupSetting();
        editor.putBoolean("proxyServer", status);
        editor.commit();
    }

    public static boolean proxyServer() {
        setupSetting();
        return pref.getBoolean("proxyServer", true);

    }

    public static void showProxySponsor(Boolean status) {
        setupSetting();
        editor.putBoolean("showProxySponsor", status);
        editor.commit();
    }

    public static boolean showProxySponsor() {
        setupSetting();
        return pref.getBoolean("showProxySponsor", true);
    }

    public static void proxyCustomStatus(boolean status) {
        setupSetting();
        editor.putBoolean("proxyCustomStatus", status);
        editor.commit();

    }

    public static boolean proxyCustomStatus() {
        setupSetting();
        return pref.getBoolean("proxyCustomStatus", proxyServer());
    }


    public static void proxyRefreshInDialogs(boolean status) {
        setupSetting();
        editor.putBoolean("proxyRefreshInDialogs", status);
        editor.commit();

    }

    public static boolean proxyRefreshInDialogs() {
        setupSetting();
        return pref.getBoolean("proxyRefreshInDialogs", true);
    }

    //endregion

    //region Download Manager
    public static void downloadManagerCost(int cost) {
        setupSetting();
        editor.putInt("downloadManagerCost", cost);
        editor.commit();
    }

    public static int downloadManagerCost() {
        if (!BuildVars.ADMOB_REWARDED_FEATURE) {
            return 0;
        }
        setupSetting();
        return BuildVars.DEBUG_VERSION ? ApplicationLoader.DOWNLOAD_MANAGER_COST : pref.getInt("downloadManagerCost", ApplicationLoader.DOWNLOAD_MANAGER_COST);
    }

    public static void downloadDay(int index, boolean status) {
        setupSetting();
        editor.putBoolean("downloadDay" + UserConfig.selectedAccount + index, status);
        editor.commit();
    }

    public static boolean downloadDay(int index) {
        setupSetting();
        return pref.getBoolean("downloadDay" + UserConfig.selectedAccount + index, false);
    }

    public static void downloadModule(DownloadHelper.Modules key, boolean status) {
        setupSetting();
        editor.putBoolean("downloadModule" + UserConfig.selectedAccount + key, status);
        editor.commit();
    }

    public static boolean downloadModule(DownloadHelper.Modules key) {
        setupSetting();
        return pref.getBoolean("downloadModule" + UserConfig.selectedAccount + key, false);
    }

    public static void downloadTime(DownloadHelper.TimeKeys key, int time) {
        setupSetting();
        editor.putInt("downloadTime" + UserConfig.selectedAccount + key, time);
        editor.commit();
    }

    public static int downloadTime(DownloadHelper.TimeKeys key) {
        setupSetting();
        return pref.getInt("downloadTime" + UserConfig.selectedAccount + key, key == DownloadHelper.TimeKeys.START_HOURS || key == DownloadHelper.TimeKeys.END_HOURS ? Calendar.getInstance().get(Calendar.HOUR_OF_DAY) : Calendar.getInstance().get(Calendar.MINUTE));
    }
    //endregion

    //region Customized: translation
    public static void userTranslateTarget(long did, String localShortName) {
        setupSetting();
        editor.putString("utt" + did, localShortName);
        editor.commit();
    }

    public static String userTranslateTarget(long did) {
        setupSetting();
        return pref.getString("utt" + did, "");
    }

    public static void activeTranslateTarget(Boolean status) {
        setupSetting();
        editor.putBoolean("activeTranslateTarget" + UserConfig.selectedAccount, status);
        editor.apply();
    }

    public static boolean activeTranslateTarget() {
        setupSetting();
        return pref.getBoolean("activeTranslateTarget" + UserConfig.selectedAccount, false);
    }

    public static void translationProvider(int value) {
        setupSetting();
        editor.putInt("translationProvider" + UserConfig.selectedAccount, value);
        editor.commit();
    }

    public static int translationProvider() {
        setupSetting();
        return pref.getInt("translationProvider" + UserConfig.selectedAccount, 1);
    }

    public static void translateShortName(String shortName) {
        setupSetting();
        editor.putString("translateShortName" + UserConfig.selectedAccount, shortName);
        editor.commit();
    }

    public static String translateShortName() {
        setupSetting();
        return pref.getString("translateShortName" + UserConfig.selectedAccount, "");
    }

    public static void translateToMeShortName(String shortName) {
        setupSetting();
        editor.putString("translateToMeShortName" + UserConfig.selectedAccount, shortName);
        editor.commit();
    }

    public static String translateToMeShortName() {
        setupSetting();
        return pref.getString("translateToMeShortName" + UserConfig.selectedAccount, "");
    }
    //endregion

    public static String answeringMachineText() {
        setupSetting();
        return pref.getString("answeringMachineText" + UserConfig.selectedAccount, "");
    }

    public static void answeringMachineText(String str) {
        setupSetting();
        editor.putString("answeringMachineText" + UserConfig.selectedAccount, str);
        editor.commit();
    }


    public static void answeredDialogs(String list) {
        setupSetting();
        editor.putString("answeredDialogs" + UserConfig.selectedAccount, list);
        editor.commit();
    }

    public static String answeredDialogs() {
        setupSetting();
        return pref.getString("answeredDialogs" + UserConfig.selectedAccount, "");
    }


    public static Boolean turnOff() {
        setupSetting();
//        Log.i(TAG, "turnOff1: isNetworkOnline UserConfig.selectedAccount:"+ UserConfig.selectedAccount + " , status: "+ pref.getBoolean("turnOff" + UserConfig.selectedAccount, false));
        return pref.getBoolean("turnOff" /*+ UserConfig.selectedAccount*/, false);
    }

    public static void turnOff(Boolean statue) {
        setupSetting();
        editor.putBoolean("turnOff" /*+ UserConfig.selectedAccount*/, statue);
//        Log.i(TAG, "turnOff2: isNetworkOnline UserConfig.selectedAccount:"+ UserConfig.selectedAccount + " , status: "+ pref.getBoolean("turnOff" + UserConfig.selectedAccount, false));
        editor.commit();
        try {
            ConnectionsManager.getInstance(UserConfig.selectedAccount).checkConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void gender(int status) {
        setupSetting();
        editor.putInt("gender", status);
        editor.apply();
    }

    public static int gender() {
        setupSetting();
        return pref.getInt("gender", 0);
    }

    public static void hiddenAdDialogs(String list) {
        setupSetting();
        editor.putString("hiddenAdDialogs", list);
        editor.commit();
    }

    public static String hiddenAdDialogs() {
        setupSetting();
        return pref.getString("hiddenAdDialogs", "");
    }


    public static void flurryAppId(String app_id) {
        setupSetting();
        editor.putString("flurryAppId", app_id);
        editor.commit();
    }

    public static String flurryAppId() {
        setupSetting();
        return pref.getString("flurryAppId", BuildVars.FLURRY_APP_ID);
    }


    public static void keepOriginalFileName(Boolean status) {
        setupSetting();
        editor.putBoolean("keepOriginalFileName", status);
        editor.commit();
    }

    public static boolean keepOriginalFileName() {
        setupSetting();
        return pref.getBoolean("keepOriginalFileName", false);
    }

    public static void playGifASVideo(Boolean status) {
        setupSetting();
        editor.putBoolean("playGifASVideo", status);
        editor.commit();
    }

    public static boolean playGifASVideo() {
        setupSetting();
        return pref.getBoolean("playGifASVideo", true);
    }


    public static void shareAppContent(String s) {
        setupSetting();
        editor.putString("shareAppContent", s);
        if (s.isEmpty()) {
            editor.remove("shareAppContent");
        }
        editor.apply();
    }

    public static String shareAppContent() {
        setupSetting();
        return pref.getString("shareAppContent",
                String.format(LocaleController.getString("ShareAppContent", R.string.ShareAppContent)
                        , BuildVars.PLAY_STORE_SITE_URL + BuildConfig.APPLICATION_ID));
    }

    public static void recommendedFilter(Boolean status) {
        setupSetting();
        editor.putBoolean("recommendedFilter", status);
        editor.commit();
    }

    public static boolean recommendedFilter() {
        setupSetting();
        return pref.getBoolean("recommendedFilter", true);
    }

    public static void showInitFolderDialog(Boolean status) {
        setupSetting();
        editor.putBoolean("showInitFolderDialog", status);
        editor.commit();
    }

    public static boolean showInitFolderDialog() {
        setupSetting();
        return pref.getBoolean("showInitFolderDialog", true);
    }

    public static void privacyAgreementShown(Boolean status) {
        setupSetting();
        editor.putBoolean("privacyAgreementShown", status);
        editor.commit();
    }

    public static boolean privacyAgreementShown() {
        setupSetting();
        return pref.getBoolean("privacyAgreementShown", false);
    }
}
