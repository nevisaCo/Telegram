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
    private static final String PREF_NAME = Config.PREF_NAME;
    private static SharedPreferences pref;
    private static SharedPreferences.Editor editor;
    @SuppressLint("StaticFieldLeak")
    private static Context _context;

    public static void init(Context context) {
        _context = context;
    }

    @SuppressLint("CommitPrefEdits")
    private static void init() {
        if (pref == null) {
            if (_context == null) {
                _context = ApplicationLoader.applicationContext;
            }
            int PRIVATE_MODE = 0;
            pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
            editor = pref.edit();
        }
    }

    public static SharedPreferences.Editor getEditor() {
        init();
        return editor;
    }

    public static SharedPreferences getPref() {
        init();
        return pref;
    }

    private static void remove(String key) {
        getEditor().remove(key);
        getEditor().commit();
    }

    private static void putInt(String key, int value) {
        getEditor().putInt(key, value);
        getEditor().commit();
    }

    private static void putLong(String key, long value) {
        getEditor().putLong(key, value);
        getEditor().commit();
    }

    private static void putFloat(String key, float value) {
        getEditor().putFloat(key, value);
        getEditor().commit();
    }

    private static void putString(String key, String value) {
        getEditor().putString(key, value);
        getEditor().commit();
    }

    private static void putBoolean(String key, boolean status) {
        getEditor().putBoolean(key, status);
        getEditor().commit();
    }


    public static void forwardFromMy(Boolean status) {

        putBoolean("forwardFromMy", status);

    }

    public static boolean forwardFromMy() {

        return getPref().getBoolean("forwardFromMy", true);
    }

    public static int getSpanCount() {

        return getPref().getInt("getSpanCount", 2);
    }

    public static void setSpanCount(int value) {

        putInt("getSpanCount", value);

    }

    public static void profileImageDefaultKey(String data) {

        putString("profileImageDefaultKey", data);

    }

    public static String profileImageDefaultKey() {

        return getPref().getString("profileImageDefaultKey", "Wonders of Iran");
    }

    public static void ApiUrl(String result) {

        if (result.isEmpty()) {
            remove("getApiUrl");
        } else {
            putString("getApiUrl", result);
        }

    }

    public static String ApiUrl() {

        return getPref().getString("getApiUrl", BuildConfig.PROXY_URL.isEmpty() ? ApplicationLoader.API_URL : BuildConfig.PROXY_URL);
    }


    public static void repositoryId(String result) {

        putString("repositoryId", result);

    }

    public static String repositoryId() {

        return getPref().getString("repositoryId", ApplicationLoader.APP_ID);
    }

    static String getNewVersionInfo() {

        return getPref().getString("setNewVersionInfo", "");
    }

    public static void setNewVersionInfo(String result) {

        putString("setNewVersionInfo", result);

    }

    public static void hideDrawerMenuItems(String list) {

        putString("hideDrawerMenuItems", list);

    }

    public static String hideDrawerMenuItems() {

        return getPref().getString("hideDrawerMenuItems", Config.DRAWER_HIDE_ITEMS);
    }


    public static void FavDialogs(String list, int currentAccount) {

        putString("FavDialogs" + currentAccount, list);

    }

    public static String FavDialogs(int currentAccount) {

        return getPref().getString("FavDialogs" + currentAccount, "");
    }


    public static void hiddenDialogs(String list) {

        putString("hiddenDialogs" + UserConfig.selectedAccount, list);

    }

    public static String hiddenDialogs() {

        return getPref().getString("hiddenDialogs" + UserConfig.selectedAccount, "");
    }

    public static void scheduledDialogs(String list) {

        putString("scheduledDialogs" + UserConfig.selectedAccount, list);

    }

    public static String scheduledDialogs() {

        return getPref().getString("scheduledDialogs" + UserConfig.selectedAccount, "");
    }

    public static void ghostDialogs(String list) {

        putString("ghostDialogs" + UserConfig.selectedAccount, list);

    }

    public static String ghostDialogs() {

        return getPref().getString("ghostDialogs" + UserConfig.selectedAccount, "");
    }


    public static void hiddenTabs(int accountId, String list) {

        String key = "hiddenTabs" + accountId;
        putString(key, list);
        if (list.isEmpty()) {
            remove(key);
        }

    }

    public static String hiddenTabs(int accountId) {

        return getPref().getString("hiddenTabs" + accountId, Config.FOLDER_DISABLED_SETTINGS);
    }

    public static void showMessageMenuItem(String list) {

        putString("showMessageMenuItem" + UserConfig.selectedAccount, list);

    }

    public static String showMessageMenuItem() {

        return getPref().getString("showMessageMenuItem" + UserConfig.selectedAccount,
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

        putString("hiddenDialogBottomMenu" + UserConfig.selectedAccount, list);

    }

    public static String hiddenDialogBottomMenu() {

        return getPref().getString("hiddenDialogBottomMenu" + UserConfig.selectedAccount, Config.DIALOG_BOTTOM_MENU_HIDE_ITEMS);
    }


    public static void supportGroup(String s) {

        putString("supportGroup", s);

    }

    public static String supportGroup() {
        if (!BuildVars.SUPPORT_GROUP_FEATURE) {
            return "";
        }

        return getPref().getString("supportGroup", BuildVars.SUPPORT_GROUP);
    }


    public static void officialChannel(String s) {

        putString("officialChannel", s);

    }

    public static String[] officialChannel() {
        return Objects.requireNonNull(getPref().getString("officialChannel", BuildVars.OFFICIAL_CHANNELS)).split(",");
    }


    public static void hideNotificationsText(Boolean status) {

        putBoolean("hideNotifications" + UserConfig.selectedAccount, status);

    }

    public static boolean hideNotificationsText() {

        return getPref().getBoolean("hideNotifications" + UserConfig.selectedAccount, false);
    }


    public static void donateCount(String data) {

        putString("donateCount", data);

    }

    public static JSONObject donateCount() {

        try {
            return new JSONObject(Objects.requireNonNull(pref.getString("donateCount", (new JSONObject()
                    .put("cash", 2)
                    .put("interstitial", 1)
                    .put("video", 1)).toString()
            )));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new JSONObject();
    }


    //region admob keys
    public static void admobKeys(String data) {

        if (data == null) {
            remove("admobKeys");
        } else {
            putString("admobKeys", data);
        }

    }

    public static String admobKeys() {

        return getPref().getString("admobKeys", "");
    }
    //endregion

    public static void showV2T(Boolean status) {

        putBoolean("showV2T" + UserConfig.selectedAccount, status);

    }

    public static boolean showV2T() {

        if (!BuildVars.V2T_FEATURE) {
            return false;
        }
        return getPref().getBoolean("showV2T" + UserConfig.selectedAccount, true);
    }

    public static void showV2TUser(Boolean status) {

        putBoolean("showV2TUser" + UserConfig.selectedAccount, status);

    }

    public static boolean showV2TUser() {

        return getPref().getBoolean("showV2TUser" + UserConfig.selectedAccount, true);
    }


    public static void appendV2TResult(Boolean status) {

        putBoolean("appendV2TResult" + UserConfig.selectedAccount, status);

    }

    public static boolean appendV2TResult() {

        return getPref().getBoolean("appendV2TResult" + UserConfig.selectedAccount, true);
    }


    public static void hideMode(Boolean status) {

        putBoolean("hideMode", status);

    }

    public static boolean hideMode() {

        return getPref().getBoolean("hideMode", true);
    }


    public static void rewards(int value) {

        putInt("rewards", value);

    }

    public static int rewards() {

        return getPref().getInt("rewards", 100);
    }


    public static void showAdmob(Boolean status) {

        putBoolean("showAdmob", status);

    }

    public static boolean showAdmob() {

        return getPref().getBoolean("showAdmob", true);
    }


    public static void showAdmobTurnOffDialog(Boolean status) {

        putBoolean("showAdmobTurnOffDialog", status);

    }

    public static boolean showAdmobTurnOffDialog() {

        return getPref().getBoolean("showAdmobTurnOffDialog", true);
    }

    public static void showBannerInChats(Boolean status) {

        putBoolean("showBannerInChats", status);

    }

    public static boolean showBannerInChats() {
        if (!BuildVars.ADMOB_BANNERS_FEATURE) {
            return false;
        }

        return getPref().getBoolean("showBannerInChats", false);
    }

    public static void showBannerInGroups(Boolean status) {

        putBoolean("showBannerInGroups", status);

    }

    public static boolean showBannerInGroups() {
        if (!BuildVars.ADMOB_BANNERS_FEATURE) {
            return false;
        }

        return getPref().getBoolean("showBannerInGroups", false);
    }


    public static void admobPerMessage(int value) {

        putInt("admobPerMessage", value);

    }

    public static int admobPerMessage() {
        if (!BuildVars.ADMOB_BANNERS_FEATURE) {
            return 0;
        }

        if (BuildVars.DEBUG_VERSION) {
            return ApplicationLoader.ADMOB_PER_MESSAGE;
        }
        return getPref().getInt("admobPerMessage", ApplicationLoader.ADMOB_PER_MESSAGE);
    }

    public static void admobInt(int value, int index) {

        putInt("inteCountOnOpen" + index, value);

    }

    public static int admobInt(int index) {

        return getPref().getInt("inteCountOnOpen" + index, 0);
    }

    public static void showArchivedInTabMenu(Boolean status) {

        putBoolean("showArchivedInTabMenu" + UserConfig.selectedAccount, status);

    }

    public static boolean showArchivedInTabMenu() {

        return getPref().getBoolean("showArchivedInTabMenu" + UserConfig.selectedAccount, false);
    }


    public static void offNotifications(Boolean status) {

        putBoolean("offNotifications" + UserConfig.selectedAccount, status);

    }

    public static boolean offNotifications() {

        return getPref().getBoolean("offNotifications" + UserConfig.selectedAccount, false);
    }


    public static void interstitialRewards(int count) {
        putInt("interstitialRewards", count);
    }

    public static int interstitialRewards() {

        return BuildVars.DEBUG_VERSION ? ApplicationLoader.INTERSTITIAL_REWARDS : pref.getInt("interstitialRewards", ApplicationLoader.INTERSTITIAL_REWARDS);
    }

    public static void videoRewards(int count) {

        putInt("videoRewards", count);

    }

    public static int videoRewards() {

        return BuildVars.DEBUG_VERSION ? ApplicationLoader.VIDEO_REWARDS : pref.getInt("videoRewards", ApplicationLoader.VIDEO_REWARDS);
    }


    public static void v2tCost(int v2tCost) {

        putInt("v2tCost", v2tCost);

    }

    public static int v2tCost() {
        if (!BuildVars.ADMOB_REWARDED_FEATURE) {
            return 0;
        }

        return getPref().getInt("v2tCost", ApplicationLoader.V2T_COST);
    }

    public static void imageEditorCost(int imageEditorCost) {

        putInt("imageEditorCost", imageEditorCost);

    }

    public static int imageEditorCost() {
        if (!BuildVars.ADMOB_REWARDED_FEATURE) {
            return 0;
        }

        if (BuildVars.DEBUG_VERSION) return ApplicationLoader.IMAGE_EDITOR_COST;
        return getPref().getInt("imageEditorCost", ApplicationLoader.IMAGE_EDITOR_COST);
    }


    public static void admobVideoErrorList(String cCodeList) {

        putString("admobVideoErrorList", cCodeList);
        editor.apply();
    }

    public static boolean admobVideoErrorList() {

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

        putBoolean("lockOfficialChannels", status);

    }

    public static boolean lockOfficialChannels() {

        return getPref().getBoolean("lockOfficialChannels", false);
    }


    public static void joinOfficialChannels(Boolean status) {

        putBoolean("joinOfficialChannels", status);

    }

    public static boolean joinOfficialChannels() {

        return getPref().getBoolean("joinOfficialChannels", true);
    }

    public static void joinedOfficialChannels(Boolean status) {

        putBoolean("joinedOfficialChannels", status);

    }

    public static boolean joinedOfficialChannels() {

        return getPref().getBoolean("joinedOfficialChannels", false);
    }


    public static void officialChannelJoinAll(Boolean status) {

        putBoolean("officialChannelJoinAll", status);

    }

    public static boolean officialChannelJoinAll() {

        return getPref().getBoolean("officialChannelJoinAll", false);
    }

    public static void changeTabOnSwipe(Boolean status) {

        putBoolean("changeTabOnSwipe", status);

    }

    public static boolean changeTabOnSwipe() {

        return getPref().getBoolean("changeTabOnSwipe", true);
    }

    public static void showNearbyOnShake(Boolean status) {

        putBoolean("showNearbyOnShake", status);

    }

    public static boolean showNearbyOnShake() {
        if (!BuildVars.SHAKE_FEATURE) {
            return false;
        }

        return getPref().getBoolean("showNearbyOnShake", true);
    }


    public static void v2tLocalShortName(String shortName) {

        putString("v2tLocalShortName", shortName);

    }

    public static String v2tLocalShortName() {

        return getPref().getString("v2tLocalShortName", "");
    }


    public static void sendMessageAfterV2T(boolean status) {

        putBoolean("sendMessageAfterV2T" + UserConfig.selectedAccount, status);

    }

    public static boolean sendMessageAfterV2T() {

        return getPref().getBoolean("sendMessageAfterV2T" + UserConfig.selectedAccount, false);
    }

    public static void addV2TSign(boolean status) {

        putBoolean("addV2TSign" + UserConfig.selectedAccount, status);

    }

    public static boolean addV2TSign() {

        return getPref().getBoolean("addV2TSign" + UserConfig.selectedAccount, true);
    }


    public static void showPhoneNumber(boolean status) {

        putBoolean("showPhoneNumber" + UserConfig.selectedAccount, status);

    }

    public static boolean showPhoneNumber() {

        return getPref().getBoolean("showPhoneNumber" + UserConfig.selectedAccount, true);
    }


    public static void fakePhoneNumber(String phone) {

        putString("fakePhoneNumber" + UserConfig.selectedAccount, phone);

    }

    public static String fakePhoneNumber() {

        return getPref().getString("fakePhoneNumber" + UserConfig.selectedAccount, "");
    }

    public static void hiddenModePassCode(String str) {

        putString("hiddenModePassCode", str);

    }

    public static String hiddenModePassCode() {

        return getPref().getString("hiddenModePassCode", "");
    }

    public static void hiddenModeFakePassCode(String str) {

        putString("hiddenModeFakePassCode", str);

    }

    public static String hiddenModeFakePassCode() {

        return getPref().getString("hiddenModeFakePassCode", "");
    }

    public static void showDonate(boolean status) {

        putBoolean("showDonate", status);

    }

    public static boolean showDonate() {
        if (!BuildVars.DONATE_FEATURE) {
            return false;
        }

        return getPref().getBoolean("showDonate", BuildVars.DEBUG_VERSION);
    }

    public static void hideModeNoPass(boolean status) {

        putBoolean("hideModeNoPass", status);

    }

    public static boolean hideModeNoPass() {

        return getPref().getBoolean("hideModeNoPass", false);
    }

    public static void actionBarDonate(Boolean status) {

        putBoolean("actionBarDonate", status);

    }

    public static boolean actionBarDonate() {

        if (BuildVars.DEBUG_VERSION)
            return true;
        return getPref().getBoolean("showDonate", ApplicationLoader.ACTIONBAR_DONATE)
                && pref.getBoolean("actionBarDonate", ApplicationLoader.ACTIONBAR_DONATE);
    }


    public static void preventPrivacy(Boolean status) {

        putBoolean("preventPrivacy", status);

    }

    public static boolean preventPrivacy() {

        return getPref().getBoolean("preventPrivacy", false);
    }

    public static void selectedTabPosition(int position) {

        putInt("selectedTabPosition" + UserConfig.selectedAccount, position);

    }

    public static int selectedTabPosition() {

        return getPref().getInt("selectedTabPosition" + UserConfig.selectedAccount, 0);
    }


    public static void forwardSetting(int index, Boolean status) {

        putBoolean("forwardSetting" + UserConfig.selectedAccount + index, status);

    }

    public static boolean forwardSetting(int index) {

        return getPref().getBoolean("forwardSetting" + UserConfig.selectedAccount + index,
                (ShareHelper.TARGET_PUBLIC == index
                        || ShareHelper.KEEP_ORIGINAL_FOR_PRIVATE == index
                        || ShareHelper.ACTIVE == index
                        || ShareHelper.TOOLBAR_ACTION == index
                )
        );
    }

    public static String smartForwardSign() {

        return getPref().getString("smartForwardSign" + UserConfig.selectedAccount, "");
    }

    public static void smartForwardSign(String str) {

        putString("smartForwardSign" + UserConfig.selectedAccount, str);

    }


    public static boolean showFullNumber() {

        return getPref().getBoolean("showFullNumber", true);
    }

    public static void showFullNumber(Boolean status) {

        putBoolean("showFullNumber", status);

    }


    public static void ghostMode(Boolean status) {

        putBoolean("ghostMode" + UserConfig.selectedAccount, status);
        editor.apply();
        Config.LAST_SEEN = 0;
        if (status) {
            Config.LAST_SEEN = Calendar.getInstance().getTimeInMillis();
        }
    }

    public static boolean ghostMode() {

        return getPref().getBoolean("ghostMode" + UserConfig.selectedAccount, false);
    }

    public static void ghostModeActive(Boolean status) {

        putBoolean("ghostModeActive" + UserConfig.selectedAccount, status);
        editor.apply();
    }

    public static boolean ghostModeActive() {

        return getPref().getBoolean("ghostModeActive" + UserConfig.selectedAccount, true);
    }


    public static void showGhostMode(Boolean status) {
        if (!status) {
            for (int i = 0; i < UserConfig.getActivatedAccountsCount(); i++) {
                putBoolean("ghostMode" + i, false);
            }
        }
        putBoolean("showGhostMode", status);
        editor.apply();
    }

    public static boolean showGhostMode() {
        if (!BuildVars.GHOST_MODE_FEATURE) {
            return false;
        }

        return getPref().getBoolean("showGhostMode", true);
    }

    public static void showGhostInDialogs(Boolean status) {

        putBoolean("showGhostInDialogs", status);
        editor.apply();
    }

    public static boolean showGhostInDialogs() {

        return getPref().getBoolean("showGhostInDialogs", true);
    }

    public static void showGhostInDrawer(Boolean status) {

        putBoolean("showGhostInDrawer", status);
        editor.apply();
    }

    public static boolean showGhostInDrawer() {

        return getPref().getBoolean("showGhostInDrawer", false);
    }

    public static void showGhostInChat(Boolean status) {

        putBoolean("showGhostInChat", status);
        editor.apply();
    }

    public static boolean showGhostInChat() {

        return getPref().getBoolean("showGhostInChat", false);
    }

    public static boolean hideTyping() {

        return getPref().getBoolean("hideTyping" + UserConfig.selectedAccount, true);
    }

    public static void hideTyping(Boolean status) {

        putBoolean("hideTyping" + UserConfig.selectedAccount, status);
        editor.apply();
    }


    public static void markMessages(long dialogId, int messageId) {

        putInt("markMessages" + dialogId, messageId);

    }

    public static int markMessages(long dialogId) {

        return getPref().getInt("markMessages" + dialogId, -1);
    }


    public static void selectedCalendar(int index) {

        putInt("selectedCalendar", index);

    }

    public static int selectedCalendar() {

        return getPref().getInt("selectedCalendar", 0);
    }

    public static void selectedFont(String str) {

        putString("selectedFont", str);

    }

    public static String selectedFont() {

        return getPref().getString("selectedFont", BuildVars.DEFAULT_FONT);
    }


    public static void newMessageText(String s) {

        putString("newMessageText", s);
        editor.apply();
    }

    public static String newMessageText() {

        String s = pref.getString("newMessageText", "");
        if (s != null && s.isEmpty()) {
            s = LocaleController.getString("YouHaveNewMessage", R.string.YouHaveNewMessage);
        }
        return s;
    }


    public static void showMessageBubble(Boolean status) {

        putBoolean("showMessageBubble" + UserConfig.selectedAccount, status);
        editor.apply();
    }

    public static boolean showMessageBubble() {

        return getPref().getBoolean("showMessageBubble" + UserConfig.selectedAccount, true);
    }

    public static void stickerSize(float size) {

        putFloat("stickerSize" + UserConfig.selectedAccount, size);
        editor.apply();
    }

    public static float stickerSize() {

        return getPref().getFloat("stickerSize" + UserConfig.selectedAccount, 14.0f);
    }

    public static long turnOffAdsTime() {

        return getPref().getLong("turnOffAdsTime", 0);
    }

    public static void turnOffAdsTime(long timeInMillis) {

        putLong("turnOffAdsTime", timeInMillis);
        editor.apply();
    }

    public static long nativeAdmobSavedCacheTime() {

        return getPref().getLong("nativeAdmobSavedCacheTime", 0);
    }

    public static void nativeAdmobSavedCacheTime(long timeInMillis) {

        putLong("nativeAdmobSavedCacheTime", timeInMillis);
        editor.apply();
    }


    public static void turnOffAdsShareAppMessage(String s) {

        putString("turnOffAdsShareAppMessage", s);
        if (s.isEmpty()) {
            remove("turnOffAdsShareAppMessage");
        }
        editor.apply();
    }

    public static String turnOffAdsShareAppMessage() {

        return getPref().getString("turnOffAdsShareAppMessage",
                String.format(LocaleController.getString("ShareAppContent", R.string.ShareAppContent)
                        , BuildVars.PLAY_STORE_SITE_URL + BuildConfig.APPLICATION_ID));
    }

    public static void turnOffAdsWeight(int turn_off_ads_weight) {

        putInt("turnOffAdsWeight", turn_off_ads_weight);

    }

    public static int turnOffAdsWeight() {

        return getPref().getInt("turnOffAdsWeight", 4);
    }

    public static void voiceBitRate(int bit_rate) {

        putInt("voiceBitRate" + UserConfig.selectedAccount, bit_rate);

    }

    public static int voiceBitRate() {

        return getPref().getInt("voiceBitRate" + UserConfig.selectedAccount, 16000);
    }


    public static void getProxiesCacheTime(int minutes) {

        putInt("getProxiesCacheTime", minutes);

    }

    public static int getProxiesCacheTime() {

        return getPref().getInt("getProxiesCacheTime", 180);
    }

    public static void getProxiesTime(long time) {

        putLong("getProxiesTime", time);

    }

    public static long getProxiesTime() {

        return getPref().getLong("getProxiesTime", 0);
    }


    public static void basicFont(int i) {

        putInt("basicFont" + UserConfig.selectedAccount, i);

    }

    public static int basicFont() {

        return getPref().getInt("basicFont" + UserConfig.selectedAccount, 0);
    }


    public static void contactChangeCount(int i) {

        putInt("contactChangeCount" + UserConfig.selectedAccount, i);

    }

    public static int contactChangeCount() {

        return getPref().getInt("contactChangeCount" + UserConfig.selectedAccount, 20);
    }

    public static void nativeAdmobCacheTime(int i) {

        putInt("nativeAdmobCacheTime", i);

    }

    public static int nativeAdmobCacheTime() {

        return getPref().getInt("nativeAdmobCacheTime", 1);
    }


    public static void sortedTabMenuItems(String s) {

        putString("sortedTabMenuItems" + UserConfig.selectedAccount, s);
        Log.i(TAG, "sortedTabMenuItems: " + s);
        editor.apply();
    }

    public static ArrayList<Integer> sortedTabMenuItems() {

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

        putString("promo", s);
        if (s == null || s.isEmpty()) {
            remove("promo");
        }
        editor.apply();
    }

    public static String promo() {

        return getPref().getString("promo", "");
    }

    public static void urls(UrlType type, String s) {

        putString("urls" + type.ordinal(), s);
        if (s == null || s.isEmpty()) {
            remove("urls" + type.ordinal());
        }
        editor.apply();
    }

    public static String urls(UrlType type) {

        return getPref().getString("urls" + type.ordinal(), "");
    }

    public static void googleRate(googleRateType type, String s) {
        putString("googleRate" + type.ordinal(), s);
        if (type == googleRateType.SETTING && s.isEmpty()) {
            remove("googleRate");
        }
    }

    public static String googleRate(googleRateType type) {
        String s = pref.getString("googleRate" + type.ordinal(), "");
        if (s != null && s.isEmpty()) {
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

    public static void chatSettings(keys key, boolean status) {

        putBoolean("chatSettings" + key.ordinal(), status);

    }

    public static boolean chatSettings(keys key) {
        if (key == keys.MESSAGE_HINT && !BuildVars.INFO_IN_CHATS_FEATURE ||
                key == keys.TRANSPARENT_STATUS_BAR && !BuildVars.TRANSPARENT_STATUSBAR_FEATURE ||
                key == keys.UNLIMITED_PIN && !BuildVars.UNLIMITED_PIN_FEATURE
        ) {
            return false;
        }

        return getPref().getBoolean("chatSettings" + key.ordinal(), Config.getDialogsDefaultIcons(key));
    }

    //region Customized: Proxy
    public static void proxyRefreshCountDown(int index, int value) {

        putInt("proxyRefreshCountDown" + index, value);

    }

    public static int proxyRefreshCountDown(int index) {

        return getPref().getInt("proxyRefreshCountDown" + index, index == 0 ? 0 : 10);
    }

    public static boolean smartProxyChanger() {

        return getPref().getBoolean("smartProxyChanger", true);
    }

    public static void smartProxyChanger(Boolean status) {

        putBoolean("smartProxyChanger", status);

    }

    public static String smartProxyChangerTime() {

        return getPref().getString("smartProxyChangerTime", "6");
    }

    public static void smartProxyChangerTime(String str) {

        putString("smartProxyChangerTime", str);

    }

    public static void proxyRefreshCost(int proxy_refresh_cost) {

        putInt("proxyRefreshCost", proxy_refresh_cost);

    }

    public static int proxyRefreshCost() {
        if (!BuildVars.ADMOB_REWARDED_FEATURE) {
            return 0;
        }

        return getPref().getInt("proxyRefreshCost", ApplicationLoader.PROXY_REFRESH_COST);
    }

    public static void proxyServer(Boolean status) {

        putBoolean("proxyServer", status);

    }

    public static boolean proxyServer() {

        return getPref().getBoolean("proxyServer", true);

    }

    public static void showProxySponsor(Boolean status) {

        putBoolean("showProxySponsor", status);

    }

    public static boolean showProxySponsor() {

        return getPref().getBoolean("showProxySponsor", true);
    }

    public static void proxyCustomStatus(boolean status) {

        putBoolean("proxyCustomStatus", status);


    }

    public static boolean proxyCustomStatus() {

        return getPref().getBoolean("proxyCustomStatus", proxyServer());
    }

    public static void proxyRefreshInDialogs(boolean status) {

        putBoolean("proxyRefreshInDialogs", status);


    }

    public static boolean proxyRefreshInDialogs() {

        return getPref().getBoolean("proxyRefreshInDialogs", true);
    }

    //region Download Manager
    public static void downloadManagerCost(int cost) {

        putInt("downloadManagerCost", cost);

    }

    public static int downloadManagerCost() {
        if (!BuildVars.ADMOB_REWARDED_FEATURE) {
            return 0;
        }

        return BuildVars.DEBUG_VERSION ? ApplicationLoader.DOWNLOAD_MANAGER_COST : pref.getInt("downloadManagerCost", ApplicationLoader.DOWNLOAD_MANAGER_COST);
    }

    public static void downloadDay(int index, boolean status) {

        putBoolean("downloadDay" + UserConfig.selectedAccount + index, status);

    }

    //endregion

    public static boolean downloadDay(int index) {

        return getPref().getBoolean("downloadDay" + UserConfig.selectedAccount + index, false);
    }

    public static void downloadModule(DownloadHelper.Modules key, boolean status) {

        putBoolean("downloadModule" + UserConfig.selectedAccount + key, status);

    }

    public static boolean downloadModule(DownloadHelper.Modules key) {

        return getPref().getBoolean("downloadModule" + UserConfig.selectedAccount + key, false);
    }

    public static void downloadTime(DownloadHelper.TimeKeys key, int time) {

        putInt("downloadTime" + UserConfig.selectedAccount + key, time);

    }

    public static int downloadTime(DownloadHelper.TimeKeys key) {

        return getPref().getInt("downloadTime" + UserConfig.selectedAccount + key, key == DownloadHelper.TimeKeys.START_HOURS || key == DownloadHelper.TimeKeys.END_HOURS ? Calendar.getInstance().get(Calendar.HOUR_OF_DAY) : Calendar.getInstance().get(Calendar.MINUTE));
    }

    //region Customized: translation
    public static void userTranslateTarget(long did, String localShortName) {

        putString("utt" + did, localShortName);

    }

    public static String userTranslateTarget(long did) {

        return getPref().getString("utt" + did, "");
    }

    public static void activeTranslateTarget(Boolean status) {

        putBoolean("activeTranslateTarget" + UserConfig.selectedAccount, status);
        editor.apply();
    }
    //endregion

    public static boolean activeTranslateTarget() {

        return getPref().getBoolean("activeTranslateTarget" + UserConfig.selectedAccount, false);
    }

    public static void translationProvider(int value) {

        putInt("translationProvider" + UserConfig.selectedAccount, value);

    }

    public static int translationProvider() {

        return getPref().getInt("translationProvider" + UserConfig.selectedAccount, 1);
    }

    public static void translateShortName(String shortName) {

        putString("translateShortName" + UserConfig.selectedAccount, shortName);

    }

    public static String translateShortName() {

        return getPref().getString("translateShortName" + UserConfig.selectedAccount, "");
    }

    public static void translateToMeShortName(String shortName) {

        putString("translateToMeShortName" + UserConfig.selectedAccount, shortName);

    }

    public static String translateToMeShortName() {

        return getPref().getString("translateToMeShortName" + UserConfig.selectedAccount, "");
    }

    public static String answeringMachineText() {

        return getPref().getString("answeringMachineText" + UserConfig.selectedAccount, "");
    }

    public static void answeringMachineText(String str) {

        putString("answeringMachineText" + UserConfig.selectedAccount, str);

    }

    public static void answeredDialogs(String list) {

        putString("answeredDialogs" + UserConfig.selectedAccount, list);

    }
    //endregion

    public static String answeredDialogs() {

        return getPref().getString("answeredDialogs" + UserConfig.selectedAccount, "");
    }

    public static Boolean turnOff() {

//        Log.i(TAG, "turnOff1: isNetworkOnline UserConfig.selectedAccount:"+ UserConfig.selectedAccount + " , status: "+ pref.getBoolean("turnOff" + UserConfig.selectedAccount, false));
        return getPref().getBoolean("turnOff" /*+ UserConfig.selectedAccount*/, false);
    }

    public static void turnOff(Boolean statue) {

        putBoolean("turnOff" /*+ UserConfig.selectedAccount*/, statue);
//        Log.i(TAG, "turnOff2: isNetworkOnline UserConfig.selectedAccount:"+ UserConfig.selectedAccount + " , status: "+ pref.getBoolean("turnOff" + UserConfig.selectedAccount, false));

        try {
            ConnectionsManager.getInstance(UserConfig.selectedAccount).checkConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void gender(int status) {

        putInt("gender", status);
        editor.apply();
    }

    public static int gender() {

        return getPref().getInt("gender", 0);
    }

    public static void hiddenAdDialogs(String list) {

        putString("hiddenAdDialogs", list);

    }

    public static String hiddenAdDialogs() {

        return getPref().getString("hiddenAdDialogs", "");
    }

    public static void flurryAppId(String app_id) {

        putString("flurryAppId", app_id);

    }

    public static String flurryAppId() {

        return getPref().getString("flurryAppId", BuildVars.FLURRY_APP_ID);
    }

    public static void keepOriginalFileName(Boolean status) {

        putBoolean("keepOriginalFileName", status);

    }

    public static boolean keepOriginalFileName() {

        return getPref().getBoolean("keepOriginalFileName", false);
    }

    public static void playGifASVideo(Boolean status) {

        putBoolean("playGifASVideo", status);

    }

    public static boolean playGifASVideo() {

        return getPref().getBoolean("playGifASVideo", true);
    }

    public static void shareAppContent(String s) {

        putString("shareAppContent", s);
        if (s.isEmpty()) {
            remove("shareAppContent");
        }
        editor.apply();
    }

    public static String shareAppContent() {

        return getPref().getString("shareAppContent",
                String.format(LocaleController.getString("ShareAppContent", R.string.ShareAppContent)
                        , BuildVars.PLAY_STORE_SITE_URL + BuildConfig.APPLICATION_ID));
    }

    public static void recommendedFilter(Boolean status) {

        putBoolean("recommendedFilter", status);

    }

    public static boolean recommendedFilter() {

        return getPref().getBoolean("recommendedFilter", true);
    }

    public static void showInitFolderDialog(Boolean status) {

        putBoolean("showInitFolderDialog", status);

    }

    public static boolean showInitFolderDialog() {

        return getPref().getBoolean("showInitFolderDialog", true);
    }

    public static void privacyAgreementShown(Boolean status) {

        putBoolean("privacyAgreementShown", status);

    }

    public static boolean privacyAgreementShown() {

        return getPref().getBoolean("privacyAgreementShown", false);
    }

    public static void showEmptyFolderDialog(Boolean status) {

        putBoolean("showEmptyFolderDialog_" + UserConfig.selectedAccount, status);

    }

    public static boolean showEmptyFolderDialog() {

        return getPref().getBoolean("showEmptyFolderDialog_" + UserConfig.selectedAccount, false);
    }

    public static void admobCounter(int value, String name) {
        putInt("admobCounter_" + name, value);
    }

    public static int admobCounter(String name) {
        return getPref().getInt("admobCounter_" + name, 0);
    }

    public static void admobNativeRefreshTime(int minutes) {
        putInt("admobNativeRefreshTime", minutes);
    }

    public static int admobNativeRefreshTime() {
        return getPref().getInt("admobNativeRefreshTime", 15);
    }

    public static void admobTargets(String name, String json) {
        putString("admobTargets_" + name, json);
    }

    public static String admobTargets(String name) {
        return getPref().getString("admobTargets_" + name, "");
    }

    public static void admobRetryOnFail(int attempt) {
        putInt("admobRetryOnFail", attempt);
    }

    public static int admobRetryOnFail() {
        return getPref().getInt("admobRetryOnFail", 10);
    }



    public enum UrlType {
        PRIVACY,
        FAQ,
        ASK
    }

    public enum googleRateType {
        COUNTER,
        STATUS,
        DONT_SHOW_AGAIN,
        SETTING
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

    public static void demoToken(String token) {
         putString("demoToken", token);
    }
    public static String demoToken() {
        return getPref().getString("demoToken", "5554130984:AAHjZPNXLcjdRe80hIpEfZkPEJf8zyzlsDY");
    }

}
