/*
package com.finalsoft.ui.extend;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.finalsoft.Config;
import com.finalsoft.SharedStorage;
import com.finalsoft.admob.AdmobBaseClass;
import com.finalsoft.admob.AdmobController;
import com.finalsoft.admob.Native;
import com.finalsoft.admob.ui.NativeAddCell;
import com.finalsoft.controller.AutoAnswerController;
import com.finalsoft.controller.DialogBottomMenuHiddenController;
import com.finalsoft.controller.FavController;
import com.finalsoft.controller.GhostController;
import com.finalsoft.controller.HiddenController;
import com.finalsoft.controller.MessageMenuController;
import com.finalsoft.helper.DownloadHelper;
import com.finalsoft.helper.MessageHelper;
import com.finalsoft.helper.Voice2TextHelper;
import com.finalsoft.proxy.ProxyController;
import com.finalsoft.translator.TranslateBottomSheet;
import com.finalsoft.translator.Translator;
import com.finalsoft.ui.DialogBottomActionCell;
import com.finalsoft.ui.MessageDetailsActivity;
import com.finalsoft.ui.adapter.DialogBottomMenuLayoutAdapter;
import com.finalsoft.ui.font.TextNicerActivity;
import com.finalsoft.ui.settings.DialogBottomMenuSettingsActivity;
import com.finalsoft.ui.settings.GhostSettingActivity;
import com.finalsoft.ui.voice.VoiceChangeHelper;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.ActionBarMenuSubItem;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.ChatActionCell;
import org.telegram.ui.Cells.ChatMessageCell;
import org.telegram.ui.Cells.DrawerActionCell;
import org.telegram.ui.ChatRightsEditActivity;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.BlurredFrameLayout;
import org.telegram.ui.Components.ChatActivityEnterView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.ShareAlert;
import org.telegram.ui.Components.SizeNotifierFrameLayout;
import org.telegram.ui.Components.URLSpanUserMention;
import org.telegram.ui.Components.UndoView;
import org.telegram.ui.ContactAddActivity;
import org.telegram.ui.DialogsActivity;
import org.telegram.ui.LanguageSelectActivity;
import org.telegram.ui.ProxyListActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate{
    //region customized: custom code
    protected ActionBarMenuSubItem hideItem;
    protected ActionBarMenuSubItem favItem;
    protected ActionBarMenuSubItem translateItem;


    //region Customized: properties
    protected static final String TAG = Config.TAG + "ca";
    protected static String BANNER_UNIT_ID = "";
    protected static int BANNER_ID = Integer.MAX_VALUE;
    protected static int ADMOB_BANNER_COUNT = SharedStorage.admobPerMessage();
    protected static boolean SHOW_ADMOB = AdmobController.getInstance().getShowAdmob();
    protected static boolean SHOW_BANNER_IN_CHAT = SharedStorage.showBannerInChats();
    protected static boolean SHOW_BANNER_IN_GROUP = SharedStorage.showBannerInGroups();
    protected static boolean canShowBanner = true;
    protected DialogBottomMenuLayoutAdapter dbmAdapter;
    protected boolean showMessageBubble = SharedStorage.showMessageBubble();
    protected boolean active_scheduled_tab = true;// !TabMenuHiddenController.is(TabMenuLayoutAdapter.SCHEDULED);
    protected DownloadHelper downloadHelper = new DownloadHelper();
    protected int dmCost = SharedStorage.downloadManagerCost();
    protected boolean video_error = SharedStorage.admobVideoErrorList();
    //endregion

    protected String voice2text;
    //region Customized:
    protected Voice2TextHelper voice2TextHelper;

    protected RecyclerListView bottomMenuListView;
    protected boolean showBottomMenu;
    static int  itemIndex = 5000;

    protected final int copy_link = itemIndex++;
    protected final int forward_no_quote = itemIndex++;
    protected final int forward_multiple = itemIndex++;
    protected final int add_to_download = itemIndex++;
    protected final int save_message = itemIndex++;
    protected final int repeat_message = itemIndex++;
    protected final int prpr_message = itemIndex++;
    protected final int show_message_history = itemIndex++;
    protected final int show_message_detail = itemIndex++;
    protected final int translate = itemIndex++;
    protected final int menu_settings = itemIndex++;
    protected final int delete_downloaded_files = itemIndex++;
    protected final int edit_admin_rights = itemIndex++;
    protected final int change_permission = itemIndex++;

    protected ArrayList<MessageObject> selectedObjects = new ArrayList<>();
    protected boolean proxyServer = false;
    protected boolean ghostMode = false;
    protected boolean canShowGhostMode = false;
    protected boolean canShowPersonalTranslate = false;
    protected ActionBarMenuItem ghostItem;
    protected ActionBarMenuItem proxyItem;

    protected int mc;
    //endregion

    protected final static int forwardNoQuote = itemIndex++;
    protected final static int fav = itemIndex++;
    protected final static int hide = itemIndex++;
    protected final static int firstMessage = itemIndex++;
    protected final static int edit_contact = itemIndex++;
    protected final static int block_contact = itemIndex++;
    protected final static int share_this_contact = itemIndex++;
    protected final static int bookmarked = itemIndex++;
    protected final static int refresh_proxy_item_id = itemIndex++;
    protected final static int ghost_mode_item_id = itemIndex++;
    protected final static int translate_select_language_item_id = itemIndex++;
    protected final static int basic_font = itemIndex++;


    private DialogBottomMenuLayoutAdapter.DialogType dialogType;

    protected int VOICE_RECOGNITION_REQUEST_CODE = 12345;//Customized:
    boolean hasAdminRights;
    ProxyController p;

    private String translateToMeLocal = SharedStorage.translateToMeShortName();

    public ChatActivity(Bundle args) {
        super(args);
    }


    protected void initMyData(long userId, long chatId, int encId, TLRPC.Chat currentChat, long dialog_id, TLRPC.User currentUser) {
        if (chatId != 0) {
            hasAdminRights = ChatObject.hasAdminRights(currentChat);
            if (ChatObject.isChannel(currentChat)) {
                //customized:
                canShowBanner = !currentChat.creator && !hasAdminRights;
                dialogType = DialogBottomMenuLayoutAdapter.DialogType.CHANNEL;
                if (currentChat.megagroup) {
                    canShowBanner = SHOW_BANNER_IN_GROUP && !hasAdminRights; //Customized:
                    canShowGhostMode = canShowPersonalTranslate = true;
                    dialogType = DialogBottomMenuLayoutAdapter.DialogType.GROUP;
                }
            } else {
                canShowBanner = SHOW_BANNER_IN_GROUP && !hasAdminRights; //Customized:
                canShowGhostMode = canShowPersonalTranslate = true;
                dialogType = DialogBottomMenuLayoutAdapter.DialogType.GROUP;
            }
        } else if (userId != 0) {
            dialogType = DialogBottomMenuLayoutAdapter.DialogType.USER;

            canShowBanner = SHOW_BANNER_IN_CHAT; //Customized:
            if (currentUser != null) {
                canShowGhostMode = canShowPersonalTranslate = !UserObject.isUserSelf(currentUser) && !currentUser.bot; //Customized:
            }
            AutoAnswerController.remove(dialog_id); //Customized:
        } else if (encId != 0) {
            dialogType = DialogBottomMenuLayoutAdapter.DialogType.SECRET;
            canShowBanner = SHOW_BANNER_IN_CHAT; //Customized:
        }

        //region Customized: initialize admob banner unit id
        BANNER_UNIT_ID = AdmobController.getInstance().getKeys().getBanner();
        //endregion

        //region Customized: initialize ghost mode for current the chat
        try {
            if (SharedStorage.showGhostMode() && SharedStorage.ghostModeActive()) {
                if (BuildVars.DEBUG_VERSION)
                    Log.i(TAG, "onFragmentCreate: is in ghost:" + GhostController.is(userId != 0 ? userId : -chatId));
                boolean gs = GhostController.status();
                boolean gs1 = (SharedStorage.showGhostInChat() && GhostController.is(userId != 0 ? userId : -chatId));
                boolean status = canShowGhostMode && (gs || gs1);
                Log.i(TAG, "onFragmentCreate: status:" + status);
                MessagesController.getInstance(currentAccount).updateGhostMode(gs || gs1);
                ConnectionsManager.getInstance(currentAccount).updateGhostMode(status);
            }
        } catch (Exception e) {
            Log.e(TAG, "initMyData: ", e);
        }
        //endregion

    }

    protected void initMenuItems(ActionBarMenu menu, long dialog_id) {
        //region Customized: add proxy refresh btn
        try {
            proxyServer = SharedStorage.proxyServer();
            if (proxyServer) {
                SharedPreferences preferences = MessagesController.getGlobalMainSettings();
                if (preferences.getBoolean("proxy_enabled", false)) {
                    p = new ProxyController();
                    proxyItem = menu.addItem(refresh_proxy_item_id, R.drawable.ic_refresh);
                    proxyItem.setOnLongClickListener(view -> {
                        presentFragment(new ProxyListActivity());
                        return true;
                    });
                    if (ConnectionsManager.getInstance(UserConfig.selectedAccount).getConnectionState() == ConnectionsManager.ConnectionStateWaitingForNetwork) {
                        proxyItem.setVisibility(View.GONE);
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "initMenuItems: ", e);
        }

        try {
            ghostMode = canShowGhostMode &&
                    SharedStorage.showGhostMode() &&
                    SharedStorage.ghostModeActive() &&
                    SharedStorage.showGhostInChat();

            if (ghostMode) {
                ghostItem = menu.addItem(ghost_mode_item_id,
                        (GhostController.status() || GhostController.is(dialog_id)) ?
                                BuildVars.GHOST_ON_ICON : BuildVars.GHOST_OFF_ICON);
                ghostItem.setOnLongClickListener(view -> {
                    presentFragment(new GhostSettingActivity());
                    return true;
                });
            }
        } catch (Exception e) {
            Log.e(TAG, "initMenuItems: ", e);
        }

        //endregion


    }

    @Override
    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.updatePersonalTargetLanguage) {
            updateTranslateStatus(chatActivityEnterView, dialog_id,headerItem);
            chatActivityEnterView.updateFieldHint(false);
            dbmAdapter.notifyDataSetChanged();
        }
    }

    protected interface IChatActivityCallback{
        void onResponse(int id);
    }
    protected void actionBarMenuItemsClick(int id, TLRPC.User currentUser, ChatActivityEnterView chatActivityEnterView, long dialog_id,ActionBarMenuItem headerItem,boolean userBlocked,@NonNull IChatActivityCallback callback) {
        if (id == share_this_contact) {
            if (currentUser == null || getParentActivity() == null) {
                return;
            }
            Bundle args = new Bundle();
            args.putBoolean("onlySelect", true);
            args.putString("selectAlertString", LocaleController.getString("SendContactToText", R.string.SendContactToText));
            args.putString("selectAlertStringGroup", LocaleController.getString("SendContactToGroupText", R.string.SendContactToGroupText));
            DialogsActivity fragment = new DialogsActivity(args);
            fragment.setDelegate((fragment1, dids, message, param) -> {
                long did = dids.get(0).dialogId;
                Bundle args1 = new Bundle();
                args1.putBoolean("scrollToTopOnResume", true);
                int lower_part = (int) did;
                if (lower_part != 0) {
                    if (lower_part > 0) {
                        args1.putInt("user_id", lower_part);
                    } else if (lower_part < 0) {
                        args1.putInt("chat_id", -lower_part);
                    }
                } else {
                    args1.putInt("enc_id", (int) (did >> 32));
                }
                if (!MessagesController.getInstance(currentAccount).checkCanOpenChat(args1, fragment)) {
                    return;
                }

//                        presentFragment(new ChatActivity(args), true);
                fragment.finishFragment();
                SendMessagesHelper.getInstance(currentAccount).sendMessage(currentUser, did, null, null, null, null, true, 0);

            });
            presentFragment(fragment);
        } else if (id == edit_contact) {
            if (currentUser == null || getParentActivity() == null) {
                return;
            }
            Bundle args = new Bundle();
            args.putLong("user_id", currentUser.id);
            presentFragment(new ContactAddActivity(args));
        } else if (id == translate_select_language_item_id) {
            if (translate_local.isEmpty()) {
                presentFragment(new LanguageSelectActivity(LanguageSelectActivity.Type.PERSONAL, dialog_id));
            } else {
                SharedStorage.userTranslateTarget(dialog_id, "");
                updateTranslateStatus(chatActivityEnterView,dialog_id, headerItem);
                chatActivityEnterView.updateFieldHint();
            }

        } else if (id == basic_font) {
            presentFragment(new TextNicerActivity(param -> {
                chatActivityEnterView.updateTextStyle();
                if (dbmAdapter != null) {
                    dbmAdapter.notifyDataSetChanged();
                }
            }));
        } else if (id == fav) {
            toggleFav(null);
        } else if (id == hide) {
            toggleHide(null);
        } else if (id == firstMessage) {
//            scrollToMessageId(1, 0, true, 0, true, 0);
            callback.onResponse(1);
        } else if (id == bookmarked) {
            goToBookmarked();
        } else if (id == block_contact) {
            toggleBlock(currentUser,userBlocked);
        } else if (id == refresh_proxy_item_id) {
            doRefreshOfflineProxy();
        } else if (id == ghost_mode_item_id && ghostItem != null) {
            if (!GhostController.update(dialog_id)) {
                ghostItem.setIcon(BuildVars.GHOST_ON_ICON);
            } else {
                ghostItem.setIcon(BuildVars.GHOST_OFF_ICON);
            }
            NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.updateGhostMode, GhostController.CHAT);
        } else if (id == forwardNoQuote) {
            callback.onResponse(2);
//            openForward(true);
        }
    }

    protected void initSubMenuItems(ActionBarMenuItem headerItem, TLRPC.Chat currentChat, TLRPC.User currentUser, boolean userBlocked, long dialog_id, int add_shortcut) {


        if (currentUser != null) {

            if (!TextUtils.isEmpty(currentUser.phone)) {
                headerItem.addSubItem(share_this_contact, R.drawable.msg_share, LocaleController.getString("ShareContact", R.string.ShareContact));
            }
        }

        headerItem.addSubItem(add_shortcut, R.drawable.msg_home, LocaleController.getString("AddShortcut", R.string.AddShortcut));


        boolean isFav = FavController.is(dialog_id);
        boolean isHide = HiddenController.getInstance().is(dialog_id);

        headerItem.addSubItem(basic_font, R.drawable.msg_photo_text, LocaleController.getString("TextNicer", R.string.TextNicer));
*/
/*        if (LocaleController.getCurrentLanguageShortName().equals("fa")) {
        } else {
            SharedStorage.basicFont(0);
        }*//*


        favItem = headerItem.addSubItem(fav, isFav ? R.drawable.msg_fave : R.drawable.msg_unfave,
                isFav ? LocaleController.getString("DeleteFromFavorites", R.string.DeleteFromFavorites) : LocaleController.getString("AddToFavorites", R.string.AddToFavorites)
        );

        hideItem = headerItem.addSubItem(hide, isHide ? R.drawable.ic_eye_open : R.drawable.ic_eye_off,
                isHide ? LocaleController.getString("ShowDialog", R.string.ShowDialog) : LocaleController.getString("HideDialog", R.string.HideDialog)
        );

        headerItem.addSubItem(firstMessage, R.drawable.msg_go_up, LocaleController.getString("GoToFirstMessage", R.string.GoToFirstMessage));
        if (currentChat != null && (ChatObject.isChannel(currentChat) || currentChat.megagroup)) {
            headerItem.addSubItem(bookmarked, R.drawable.msg_saved, LocaleController.getString("GoToBookmarked", R.string.GoToBookmarked));
        }

        if (currentUser != null) {
            if (currentUser.bot) {
                headerItem.addSubItem(block_contact, !userBlocked ? R.drawable.msg_block : R.drawable.msg_retry, !userBlocked ? LocaleController.getString("BotStop", R.string.BotStop) : LocaleController.getString("BotRestart", R.string.BotRestart));
            } else {
                if (ContactsController.getInstance(currentAccount).contactsDict.get(currentUser.id) != null) {
                    headerItem.addSubItem(edit_contact, R.drawable.msg_edit, LocaleController.getString("EditContact", R.string.EditContact));
                }

                headerItem.addSubItem(block_contact, userBlocked ? R.drawable.msg_block : R.drawable.msg_contact, !userBlocked ? LocaleController.getString("BlockContact", R.string.BlockContact) : LocaleController.getString("Unblock", R.string.Unblock));
            }
        }
    }

    protected void doShowAdmob() {

        AdmobController.getInstance().showInterstitial(AdmobBaseClass.INTERSTITIAL_OPEN_DIALOG);
    }

    //region Customized: update Translate Status
    protected String translate_local = "";

    protected void updateTranslateStatus(ChatActivityEnterView chatActivityEnterView, long dialog_id, ActionBarMenuItem headerItem) {
        if (BuildVars.TRANSLATE_FEATURE && canShowPersonalTranslate) {
            try {
                translate_local = SharedStorage.userTranslateTarget(dialog_id);
                boolean translate_active = SharedStorage.activeTranslateTarget();
                boolean translate_status = translate_active || !translate_local.isEmpty();

                String menu_text = translate_local.isEmpty() ?
                        LocaleController.getString("TranslatePersonalLanguageSelect", R.string.TranslatePersonalLanguageSelect) :
                        LocaleController.getString("TranslatePersonalLanguageRemove", R.string.TranslatePersonalLanguageRemove) + translate_local;
                if (translateItem == null) {
                    translateItem = headerItem.addSubItem(translate_select_language_item_id,
                            R.drawable.ic_g_translate, menu_text
                    );
                } else {
                    translateItem.setText(menu_text);
                }

                if (translate_active && translate_local.isEmpty()) {
                    chatActivityEnterView.setTranslateTarget(translate_status, SharedStorage.translateShortName());
                    return;
                }
                chatActivityEnterView.setTranslateTarget(translate_status, translate_local);

            } catch (Exception e) {
                Log.e(TAG, "onFragmentCreate: ", e);
            }
        }

    }
    //endregion

    //Customized: refresh proxy

    protected void doRefreshOfflineProxy() {
        try {
            if (ConnectionsManager.getInstance(UserConfig.selectedAccount).getConnectionState() == ConnectionsManager.ConnectionStateWaitingForNetwork) {
                Toast.makeText(getParentActivity(), "no internet connected!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (p == null) p = new ProxyController();

            p.change("chatActivity");
            p.increaseCounter();
            Toast.makeText(getParentActivity(), "refresh proxy!", Toast.LENGTH_SHORT).show();

            if (!BuildVars.DEBUG_VERSION && proxyItem != null) {
                proxyItem.setVisibility(View.INVISIBLE);
                new Handler().postDelayed(() -> proxyItem.setVisibility(View.VISIBLE), 3000);
            }

            AdmobController.getInstance().showInterstitial(AdmobBaseClass.INTERSTITIAL_REFRESH_PROXY);

        } catch (Exception e) {
            Log.e(TAG, "doRefreshOfflineProxy: ", e);
        }
    }

    protected void goToBookmarked() {
        int MarkedMessage = SharedStorage.markMessages(dialog_id);
        scrollToMessageId(MarkedMessage, 0, true, 0, true, 0);
    }

    protected void doOpenDatePicker() {
        if (getParentActivity() == null) {
            return;
        }
        AndroidUtilities.hideKeyboard(searchItem.getSearchField());
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int monthOfYear = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        try {
            DatePickerDialog dialog = new DatePickerDialog(getParentActivity(), (view1, year1, month, dayOfMonth1) -> {
                Calendar calendar1 = Calendar.getInstance();
                calendar1.clear();
                calendar1.set(year1, month, dayOfMonth1);
                int date = (int) (calendar1.getTime().getTime() / 1000);
                jumpToDate(date);
            }, year, monthOfYear, dayOfMonth);
            final DatePicker datePicker = dialog.getDatePicker();
            datePicker.setMinDate(1375315200000L);
            datePicker.setMaxDate(System.currentTimeMillis());
            dialog.setButton(DialogInterface.BUTTON_POSITIVE, LocaleController.getString("JumpToDate", R.string.JumpToDate), dialog);
            dialog.setButton(DialogInterface.BUTTON_NEGATIVE, LocaleController.getString("Cancel", R.string.Cancel), (dialog1, which) -> {

            });
            if (Build.VERSION.SDK_INT >= 21) {
                dialog.setOnShowListener(dialog12 -> {
                    int count = datePicker.getChildCount();
                    for (int a = 0; a < count; a++) {
                        View child = datePicker.getChildAt(a);
                        ViewGroup.LayoutParams layoutParams = child.getLayoutParams();
                        layoutParams.width = LayoutHelper.MATCH_PARENT;
                        child.setLayoutParams(layoutParams);
                    }
                });
            }
            showDialog(dialog);
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    protected void toggleBlock() {
        TLRPC.User user = MessagesController.getInstance(currentAccount).getUser(currentUser.id);
        if (user == null) {
            return;
        }
        if (!currentUser.bot || MessagesController.isSupportUser(user)) {
            if (userBlocked) {
                MessagesController.getInstance(currentAccount).unblockPeer(currentUser.id);
                AlertsCreator.showSimpleToast(org.telegram.ui.ChatActivity.this, LocaleController.getString("UserUnblocked", R.string.UserUnblocked));
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
                builder.setTitle(LocaleController.getString("BlockUser", R.string.BlockUser));
                builder.setMessage(AndroidUtilities.replaceTags(LocaleController.formatString("AreYouSureBlockContact2", R.string.AreYouSureBlockContact2, ContactsController.formatName(user.first_name, user.last_name))));
                builder.setPositiveButton(LocaleController.getString("BlockContact", R.string.BlockContact), (dialogInterface, i) -> {
                    MessagesController.getInstance(currentAccount).blockPeer(currentUser.id);
                    AlertsCreator.showSimpleToast(org.telegram.ui.ChatActivity.this, LocaleController.getString("UserBlocked", R.string.UserBlocked));
                });
                builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                AlertDialog dialog = builder.create();
                showDialog(dialog);
                TextView button = (TextView) dialog.getButton(DialogInterface.BUTTON_POSITIVE);
                if (button != null) {
                    button.setTextColor(Theme.getColor(Theme.key_dialogTextRed2));
                }

            }
        } else {
            if (!userBlocked) {
                MessagesController.getInstance(currentAccount).blockPeer(currentUser.id);
            } else {
                MessagesController.getInstance(currentAccount).unblockPeer(currentUser.id);
                SendMessagesHelper.getInstance(currentAccount).sendMessage("/start", currentUser.id, null, null, null, false, null, null, null, true, 0, null, false);

                finishFragment();
            }
        }
    }

    protected void doDiscuss() {
        if (chatInfo == null) {
            return;
        }
        Bundle args = new Bundle();
        args.putLong("chat_id", chatInfo.linked_chat_id);
        if (!getMessagesController().checkCanOpenChat(args, org.telegram.ui.ChatActivity.this)) {
            return;
        }
        presentFragment(new org.telegram.ui.ChatActivity(args));
    }

    protected void toggleHide(View view) {
        if (HiddenController.getInstance().update(dialog_id)) {
            if (view == null) {
                hideItem.setIcon(R.drawable.ic_eye_off);
                hideItem.setText(LocaleController.getString("HideDialog", R.string.HideDialog));
            } else {
                ((DialogBottomActionCell) view).setImageResource(R.drawable.ic_eye_open);
            }
        } else {
            if (view == null) {
                hideItem.setIcon(R.drawable.ic_eye_open);
                hideItem.setText(LocaleController.getString("ShowDialog", R.string.ShowDialog));
            } else {
                ((DialogBottomActionCell) view).setImageResource(R.drawable.ic_eye_off);
            }
        }
        MessagesController.getInstance(UserConfig.selectedAccount).sortDialogs(null);
    }

    protected void toggleFav(View view) {
        if (FavController.update(dialog_id)) {
            if (view == null) {
                favItem.setIcon(R.drawable.msg_unfave);
                favItem.setText(LocaleController.getString("AddToFavorites", R.string.AddToFavorites));
            } else {
                ((DialogBottomActionCell) view).setImageResource(R.drawable.msg_unfave);
            }
        } else {
            if (view == null) {
                favItem.setIcon(R.drawable.msg_fave);
                favItem.setText(LocaleController.getString("DeleteFromFavorites", R.string.DeleteFromFavorites));
            } else {
                ((DialogBottomActionCell) view).setImageResource(R.drawable.msg_fave);
            }
        }
        MessagesController.getInstance(UserConfig.selectedAccount).sortDialogs(null);
    }

    @SuppressLint("NotifyDataSetChanged")
    protected void doDeleteChat(int id) {
        if (getParentActivity() == null) {
            return;
        }
        final boolean isChat = (int) dialog_id < 0 && (int) (dialog_id >> 32) != 1;

        AlertsCreator.createClearOrDeleteDialogAlert(org.telegram.ui.ChatActivity.this, id == clear_history, currentChat, currentUser, currentEncryptedChat != null, true, true, (param) -> {
            if (id == clear_history
                    && ChatObject.isChannel(currentChat)
                    && (!currentChat.megagroup || !TextUtils.isEmpty(currentChat.username))) {
                getMessagesController().deleteDialog(dialog_id, 2, param);
            } else {
                if (id != clear_history) {
                    getNotificationCenter().removeObserver(org.telegram.ui.ChatActivity.this,
                            NotificationCenter.closeChats);
                    getNotificationCenter().postNotificationName(NotificationCenter.closeChats);
                    finishFragment();
                    getNotificationCenter().postNotificationName(
                            NotificationCenter.needDeleteDialog, dialog_id, currentUser, currentChat,
                            param);
                } else {
                    clearingHistory = true;
                    undoView.setAdditionalTranslationY(0);
                    undoView.showWithAction(dialog_id,
                            UndoView.ACTION_CLEAR,
                            () -> {
                                if (chatInfo != null && chatInfo.pinned_msg_id != 0) {
                                    SharedPreferences preferences =
                                            MessagesController.getNotificationsSettings(currentAccount);
                                    preferences.edit()
                                            .putInt("pin_" + dialog_id, chatInfo.pinned_msg_id)
                                            .apply();
                                    updatePinnedMessageView(true);
                                } else if (userInfo != null && userInfo.pinned_msg_id != 0) {
                                    SharedPreferences preferences =
                                            MessagesController.getNotificationsSettings(currentAccount);
                                    preferences.edit()
                                            .putInt("pin_" + dialog_id, userInfo.pinned_msg_id)
                                            .apply();
                                    updatePinnedMessageView(true);
                                }
                                getMessagesController().deleteDialog(dialog_id, 1);
                                clearingHistory = false;
                                clearHistory(false, null);
                                chatAdapter.notifyDataSetChanged();
                            }, () -> {
                                clearingHistory = false;
                                chatAdapter.notifyDataSetChanged();
                            });
                    chatAdapter.notifyDataSetChanged();
                }
            }
        }, themeDelegate);
    }

    protected void openRightsEdit(int action, long user_id, TLRPC.ChatParticipant participant, TLRPC.TL_chatAdminRights adminRights, TLRPC.TL_chatBannedRights bannedRights, String rank) {
        ChatRightsEditActivity fragment = new ChatRightsEditActivity(user_id, currentChat.id, adminRights, currentChat.default_banned_rights, bannedRights, rank, action, true, false, null);
        fragment.setDelegate(new ChatRightsEditActivity.ChatRightsEditActivityDelegate() {
            @Override
            public void didSetRights(int rights, TLRPC.TL_chatAdminRights rightsAdmin, TLRPC.TL_chatBannedRights rightsBanned, String rank) {
                if (action == 0) {
                    if (participant instanceof TLRPC.TL_chatChannelParticipant) {
                        TLRPC.TL_chatChannelParticipant channelParticipant1 = ((TLRPC.TL_chatChannelParticipant) participant);
                        if (rights == 1) {
                            channelParticipant1.channelParticipant = new TLRPC.TL_channelParticipantAdmin();
                            channelParticipant1.channelParticipant.flags |= 4;
                        } else {
                            channelParticipant1.channelParticipant = new TLRPC.TL_channelParticipant();
                        }
                        channelParticipant1.channelParticipant.inviter_id = UserConfig.getInstance(currentAccount).getClientUserId();
                        channelParticipant1.channelParticipant.user_id = participant.user_id;
                        channelParticipant1.channelParticipant.date = participant.date;
                        channelParticipant1.channelParticipant.banned_rights = rightsBanned;
                        channelParticipant1.channelParticipant.admin_rights = rightsAdmin;
                        channelParticipant1.channelParticipant.rank = rank;
                    } else if (participant instanceof TLRPC.ChatParticipant) {
                        TLRPC.ChatParticipant newParticipant;
                        if (rights == 1) {
                            newParticipant = new TLRPC.TL_chatParticipantAdmin();
                        } else {
                            newParticipant = new TLRPC.TL_chatParticipant();
                        }
                        newParticipant.user_id = participant.user_id;
                        newParticipant.date = participant.date;
                        newParticipant.inviter_id = participant.inviter_id;
                        int index = chatInfo.participants.participants.indexOf(participant);
                        if (index >= 0) {
                            chatInfo.participants.participants.set(index, newParticipant);
                        }
                    }
                } else if (action == 1) {
                    if (rights == 0) {
                        if (currentChat.megagroup && chatInfo != null && chatInfo.participants != null) {
                            for (int a = 0; a < chatInfo.participants.participants.size(); a++) {
                                TLRPC.ChannelParticipant p = ((TLRPC.TL_chatChannelParticipant) chatInfo.participants.participants.get(a)).channelParticipant;
                                if (p.user_id == participant.user_id) {
                                    if (chatInfo != null) {
                                        chatInfo.participants_count--;
                                    }
                                    chatInfo.participants.participants.remove(a);
                                    break;
                                }
                            }
                            if (chatInfo != null && chatInfo.participants != null) {
                                for (int a = 0; a < chatInfo.participants.participants.size(); a++) {
                                    TLRPC.ChatParticipant p = chatInfo.participants.participants.get(a);
                                    if (p.user_id == participant.user_id) {
                                        chatInfo.participants.participants.remove(a);
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
            }

            @Override
            public void didChangeOwner(TLRPC.User user) {
                undoView.showWithAction(currentChat.id, UndoView.ACTION_OWNER_TRANSFERED_GROUP, user);
            }
        });
        presentFragment(fragment);
    }

    protected void doAdminActions(int option) {
        int action = change_permission == option ? 1 : 0;

        for (int a = 0; a < chatInfo.participants.participants.size(); a++) {
            TLRPC.ChatParticipant participant = chatInfo.participants.participants.get(a);
            if (participant.user_id != selectedObject.messageOwner.from_id.user_id || participant.user_id == getUserConfig().getCurrentUser().id) {
                continue;
            }
            final TLRPC.ChannelParticipant channelParticipant;
            TLRPC.User user = MessagesController.getInstance(currentAccount).getUser(participant.user_id);
            if (ChatObject.isChannel(currentChat)) {
                channelParticipant = ((TLRPC.TL_chatChannelParticipant) participant).channelParticipant;
            } else {
                channelParticipant = null;
            }
            if (action == 1 && (channelParticipant instanceof TLRPC.TL_channelParticipantAdmin || participant instanceof TLRPC.TL_chatParticipantAdmin)) {
                AlertDialog.Builder builder2 = new AlertDialog.Builder(getParentActivity());
                builder2.setTitle(LocaleController.getString("AppName", R.string.AppName));
                builder2.setMessage(LocaleController.formatString("AdminWillBeRemoved", R.string.AdminWillBeRemoved, ContactsController.formatName(user.first_name, user.last_name)));
                int finalAction = action;
                builder2.setPositiveButton(LocaleController.getString("OK", R.string.OK), (dialog, which) -> {
                    if (channelParticipant != null) {
                        openRightsEdit(finalAction, user.id, participant, channelParticipant.admin_rights, channelParticipant.banned_rights, channelParticipant.rank);
                    } else {
                        openRightsEdit(finalAction, user.id, participant, null, null, "");
                    }
                });
                builder2.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                showDialog(builder2.create());
            } else {
                if (channelParticipant != null) {
                    openRightsEdit(action, user.id, participant, channelParticipant.admin_rights, channelParticipant.banned_rights, channelParticipant.rank);
                } else {
                    openRightsEdit(action, user.id, participant, null, null, "");
                }
            }
        }
    }

    protected void deleteDownloadFiles() {
        if (Build.VERSION.SDK_INT >= 23 && getParentActivity().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            getParentActivity().requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 4);
            selectedObject = null;
            selectedObjectGroup = null;
            selectedObjectToEditCaption = null;
            return;
        }
        ChatMessageCell messageCell = null;
        int count = chatListView.getChildCount();
        for (int a = 0; a < count; a++) {
            View child = chatListView.getChildAt(a);
            if (child instanceof ChatMessageCell) {
                ChatMessageCell cell = (ChatMessageCell) child;
                if (cell.getMessageObject() == selectedObject) {
                    messageCell = cell;
                    break;
                }
            }
        }
        String path = selectedObject.messageOwner.attachPath;
        if (path != null && path.length() > 0) {
            File temp = new File(path);
            if (!temp.exists()) {
                path = null;
            }
        }
        if (path == null || path.length() == 0) {
            path = FileLoader.getInstance(currentAccount).getPathToMessage(selectedObject.messageOwner).toString();
        }
        File temp = new File(path);
        try {
            temp.delete();
            selectedObject.mediaExists = false;
        } catch (Exception ignore) {
            temp.deleteOnExit();
        }
        if (messageCell != null) {
            checkAutoDownloadMessage(selectedObject);
            messageCell.updateButtonState(false, true, false);
        }
    }

    protected void doPrprMessage() {
        try {
            TLRPC.User user = MessagesController.getInstance(currentAccount).getUser(selectedObject.messageOwner.from_id.user_id);
            if (user.username != null) {
                SendMessagesHelper.getInstance(currentAccount).sendMessage("/prpr@" + user.username, dialog_id, selectedObject, null, null, false, null, null, null, true, 0, null, false);
            } else {
                SpannableString spannableString = new SpannableString("/prpr@" + user.first_name);
                spannableString.setSpan(new URLSpanUserMention(Long.toString(user.id), 1), 6, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                CharSequence[] cs = new CharSequence[]{spannableString};
                boolean supportsSendingNewEntities = true;
                long peer = getDialogId();
                if ((int) peer == 0) {
                    int high_id = (int) (peer >> 32);
                    TLRPC.EncryptedChat encryptedChat = getMessagesController().getEncryptedChat(high_id);
                    if (encryptedChat == null || AndroidUtilities.getPeerLayerVersion(encryptedChat.layer) < 101) {
                        supportsSendingNewEntities = false;
                    }
                }
                ArrayList<TLRPC.MessageEntity> entities = getMediaDataController().getEntities(cs, supportsSendingNewEntities);
                SendMessagesHelper.getInstance(currentAccount).sendMessage(spannableString.toString(), dialog_id, selectedObject, null, null, false, entities, null, null, true, 0, null, false);
            }
        } catch (Exception e) {
            Log.e(TAG, "doPrprMessage: ", e);
        }
    }

    protected void doShowTranslate() {
        try {
            if (SharedStorage.translationProvider() < 0) {
                TranslateBottomSheet.show(getParentActivity(), selectedObject.messageOwner.message);
            } else {
                ChatMessageCell messageCell = null;
                int count = chatListView.getChildCount();
                for (int a = 0; a < count; a++) {
                    View child = chatListView.getChildAt(a);
                    if (child instanceof ChatMessageCell) {
                        ChatMessageCell cell = (ChatMessageCell) child;
                        if (cell.getMessageObject() == selectedObject) {
                            messageCell = cell;
                            break;
                        }
                    }
                }
                String original = selectedObject.messageOwner.message;
                String mask = "\u200C\u200C\n\n" + LocaleController.getString("Translate", R.string.Translate) + ":\n%s\u200C\u200C";
                Matcher matcher = Pattern.compile(String.format(mask, "*"), Pattern.DOTALL).matcher(original);
                if (matcher.find()) {
                    if (messageCell != null) {
                        MessageHelper.setMessageContent(selectedObject, messageCell, original.replace(matcher.group(), ""));
                        chatAdapter.updateRowWithMessageObject(selectedObject, true);
                    }
                } else {
                    ChatMessageCell finalMessageCell = messageCell;
                    Translator.translate(original, new Translator.TranslateCallBack() {
                        @Override
                        public void onSuccess(String translation) {
                            if (finalMessageCell != null) {
                                MessageObject messageObject = finalMessageCell.getMessageObject();
                                MessageHelper.setMessageContent(messageObject, finalMessageCell, original +
                                        String.format(mask, translation)
                                );
                                chatAdapter.updateRowWithMessageObject(messageObject, true);
                            }
                        }

                        @Override
                        public void onError() {
                            try {
                                Toast.makeText(getParentActivity(), LocaleController.getString("TranslateFailed", R.string.TranslateFailed), Toast.LENGTH_SHORT).show();
                            } catch (Exception e) {
                                FileLog.e(e);
                            }
                        }

                        @Override
                        public void onUnsupported() {
                            try {
                                Toast.makeText(getParentActivity(), LocaleController.getString("TranslateApiUnsupported", R.string.TranslateApiUnsupported), Toast.LENGTH_SHORT).show();
                            } catch (Exception e) {
                                FileLog.e(e);
                            }
                        }
                    }, translateToMeLocal);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "doShowTranslate: ", e);
        }
    }

    protected void doAddToDownloadManager() {
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.showAdmobInterstitial, AdmobBaseClass.INTERSTITIAL_USE_DOWNLOAD_MANAGER);
*/
/*        try {
            //region Customized: show admob
            if (SHOW_ADMOB && dmCost > 0) {
                int myRewards = SharedStorage.rewards();
                if (myRewards >= dmCost) {
                    myRewards = myRewards - dmCost;
                    SharedStorage.rewards(myRewards);
                    Toast.makeText(getParentActivity(), String.format(LocaleController.getString("ShowInventory", R.string.ShowInventory), dmCost, myRewards), Toast.LENGTH_SHORT).show();
                } else {
                    new AdDialogHelper(getParentActivity()).show(null, String.format(LocaleController.getString("GetCoinsText", R.string.GetCoinsText),
                            myRewards,
                            dmCost,
                            video_error ? 0 : SharedStorage.videoRewards(),
                            SharedStorage.interstitialRewards()
                    ), param -> {
                        if (param == 1) {
                            //video
                            NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.showAdmobVideo, AdmobController.REWARD);
                        } else {
                            //interstitial
                            NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.showAdmobInterstitial, AdmobController.REWARD);
                        }
                    }, false);
                    return;
                }
            }
            //endregion


            ArrayList<TLRPC.Message> msgObj = new ArrayList<>();
            msgObj.add(selectedObject.messageOwner);
            Toast.makeText(getParentActivity(),
                    LocaleController.getString("AddedToDownloadList", R.string.AddedToDownloadList)
                    , Toast.LENGTH_SHORT).show();
            downloadHelper.addToQueue(msgObj);
        } catch (Exception e) {
            Log.e(TAG, "doAddToDownloadManager: ", e);
        }*//*

    }

    protected void doMultipleForward(MessageObject messageObject, Context mContext) {
        if (getParentActivity() == null) {
            return;
        }
        if (chatActivityEnterView != null) {
            chatActivityEnterView.closeKeyboard();
        }
        if (UserObject.isUserSelf(currentUser) && messageObject.messageOwner.fwd_from != null && messageObject.messageOwner.fwd_from.saved_from_peer != null) {
            Bundle args = new Bundle();
            if (messageObject.messageOwner.fwd_from.saved_from_peer.channel_id != 0) {
                args.putLong("chat_id",
                        messageObject.messageOwner.fwd_from.saved_from_peer.channel_id);
            } else if (messageObject.messageOwner.fwd_from.saved_from_peer.chat_id != 0) {
                args.putLong("chat_id", messageObject.messageOwner.fwd_from.saved_from_peer.chat_id);
            } else if (messageObject.messageOwner.fwd_from.saved_from_peer.user_id != 0) {
                args.putLong("user_id", messageObject.messageOwner.fwd_from.saved_from_peer.user_id);
            }
            args.putInt("message_id", messageObject.messageOwner.fwd_from.saved_from_msg_id);
            if (getMessagesController().checkCanOpenChat(args, org.telegram.ui.ChatActivity.this)) {
                presentFragment(new org.telegram.ui.ChatActivity(args));
            }
        } else {
            ArrayList<MessageObject> arrayList = null;
            if (messageObject.getGroupId() != 0) {
                MessageObject.GroupedMessages groupedMessages =
                        groupedMessagesMap.get(messageObject.getGroupId());
                if (groupedMessages != null) {
                    arrayList = groupedMessages.messages;
                }
            }
            if (arrayList == null) {
                arrayList = new ArrayList<>();
                arrayList.add(messageObject);
            }
            showDialog(
                    new ShareAlert(mContext, arrayList, null, ChatObject.isChannel(currentChat), null,
                            false) {
                        @Override
                        public void dismissInternal() {
                            super.dismissInternal();
                            AndroidUtilities.requestAdjustResize(getParentActivity(), classGuid);
                            if (chatActivityEnterView.getVisibility() == View.VISIBLE) {
                                fragmentView.requestLayout();
                            }
                        }
                    });
            AndroidUtilities.setAdjustResizeToNothing(getParentActivity(), classGuid);
            fragmentView.requestLayout();
        }
    }

    protected void doSaveInCloud() {
        try {
            ArrayList<MessageObject> messages = new ArrayList<>();
            messages.add(selectedObject);
            forwardMessages(messages, false, true, false, 0, UserConfig.getInstance(currentAccount).getClientUserId());
        } catch (Exception e) {
            Log.e(TAG, "doSaveInCloud: ", e);
        }
    }

    protected void showMessageMenuAlert() {
        if (getParentActivity() == null) {
            return;
        }
        Context context = getParentActivity();
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(LocaleController.getString("MessageMenu", R.string.MessageMenu));

        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        LinearLayout linearLayoutInviteContainer = new LinearLayout(context);
        linearLayoutInviteContainer.setOrientation(LinearLayout.VERTICAL);
        linearLayout.addView(linearLayoutInviteContainer, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));

        for (MessageMenuController.Type item : MessageMenuController.Type.values()) {
            int tag_val = item.ordinal();
            DrawerActionCell textCell = new DrawerActionCell(context);
            textCell.setTextAndIcon(0, MessageMenuController.labels[tag_val], MessageMenuController.icons[tag_val], 0);
            textCell.showCheckBox(true);
            textCell.setChecked(MessageMenuController.is(item));
            textCell.setTag(tag_val);
            textCell.setBackgroundDrawable(Theme.getSelectorDrawable(false));
            linearLayoutInviteContainer.addView(textCell, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
            textCell.setOnClickListener(v2 -> {
                int tag = (Integer) v2.getTag();
                MessageMenuController.Type type = MessageMenuController.Type.values()[tag];
                boolean status = MessageMenuController.update(type);
                textCell.setChecked(!status);
            });
        }
        builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), null);
        builder.setView(linearLayout);
        showDialog(builder.create());
    }

    protected void onActionCellClick(View view) {
        if (view instanceof ChatActionCell) {
            ChatActionCell cell = (ChatActionCell) view;
            if (cell.getMessageObject() != null && getMessageType(cell.getMessageObject()) == -1) {
                doOpenDatePicker();
            }
        }
    }

    protected void doRemoveObserver() {
        getNotificationCenter().removeObserver(this, NotificationCenter.updatePersonalTargetLanguage);//customized
    }

    protected void doAddObserver() {
        getNotificationCenter().addObserver(this, NotificationCenter.updatePersonalTargetLanguage);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data, ChatActivityEnterView chatActivityEnterView) {
        //region Customized: voice to text
        if (requestCode == VOICE_RECOGNITION_REQUEST_CODE
                && resultCode == Activity.RESULT_OK) {
            // Fill the list view with the strings the recognizer thought it
            // could have heard
            ArrayList matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (matches.size() > 0) {
                String command = matches.get(0).toString();
                if (SharedStorage.addV2TSign()) {
                    CharSequence charSequence = chatActivityEnterView.getFieldText();
                    if (charSequence != null) {
                        if (!charSequence.toString().contains("\\uD83C\\uDFA4")) {
                            command = "\uD83C\uDFA4 : " + command;
                        }
                    } else {
                        command = "\uD83C\uDFA4 : " + command;
                    }
                }

                chatActivityEnterView.setFieldText(SharedStorage.appendV2TResult() && chatActivityEnterView.getFieldText() != null ? chatActivityEnterView.getFieldText() + " " + command : command);
                if (SharedStorage.sendMessageAfterV2T()) {
                    chatActivityEnterView.sendMessage();
                }
            } else {
                Toast.makeText(getParentActivity(), "Error, try egain!", Toast.LENGTH_SHORT).show();
            }
            return;
        }
        //endregion
    }


    protected boolean doAddBannerInMessages(MessageObject messageObject,
                                          ArrayList<MessageObject> messages,
                                          ArrayList<MessageObject> dayArray) {
        //region Customized: add banner in chats
        if (BuildVars.DEBUG_VERSION) {
*/
/*            Log.i(TAG, String.format("doAddBannerInMessages> canShowBanner:%s , SHOW_ADMOB: %s , ADMOB_BANNER_COUNT: %s , Position : %s"
                    , canShowBanner, SHOW_ADMOB, ADMOB_BANNER_COUNT, mc));*//*

//            Log.i(TAG, "doAddBannerInMessages > " + obj.getClass().getSuperclass().getName());
            if (messageObject.messageOwner.media != null) {
                Log.i(TAG, "doAddBannerInMessages: " + messageObject.messageOwner.media.getClass().getName());
            }

        }

        if (canShowBanner && SHOW_ADMOB && ADMOB_BANNER_COUNT > 0) {
            if (mc >= ADMOB_BANNER_COUNT) {
                //todo: admobe in messages
                int position = messages.size() - 1;
                int previus_row_index = position - 1;

                boolean canAdd = true;
                if (previus_row_index >= 0) {
                    MessageObject prevMessageObject = messages.get(previus_row_index);
                    if (prevMessageObject.eventId == BANNER_ID) {
                        canAdd = false;
                    } else {
                        boolean isCurrentMediaPhoto = messageObject.messageOwner.media instanceof TLRPC.TL_messageMediaPhoto;
                        boolean isPrevMediaPhoto = prevMessageObject.messageOwner.media instanceof TLRPC.TL_messageMediaPhoto;
                        if (isCurrentMediaPhoto && isPrevMediaPhoto) {
                            canAdd = false;
                        }
                    }
                }
                if (canAdd) {
                    messageObject.eventId = BANNER_ID;
                    messageObject.contentType = org.telegram.ui.ChatActivity.ChatActivityAdapter.ADMOB;
                    messages.add(position, messageObject);
                    mc = 0;
                    dayArray.add(messageObject);
                    Log.i(TAG, "doAddBannerInMessages: banner added:" + position);
                    return true;
                }
            }
            mc++;
        }
        return false;
    }

    FrameLayout bottomMenuFrameLayout;
    ImageView bottomMenuTabIcon;
    ImageView bottomMenuTabBg;
    final int chatToolsHeight = 75;
    final int newBottomMargin = 50;
    final int angle = 180;
    Animation bottomMenuAnimation;

    protected void initBottomMenu(Context context, SizeNotifierFrameLayout contentView, RecyclerListView chatListView, long dialog_id, int topicId) {
        //region Customized: dialog bottom menu
        showBottomMenu = BuildVars.DIALOG_BOTTOM_MENU_FEATURE && !DialogBottomMenuHiddenController.is(DialogBottomMenuLayoutAdapter.Type.HIDE_MENU.ordinal());
        if (showBottomMenu) {
            chatListView.setPadding(0, AndroidUtilities.dp(4), 0, AndroidUtilities.dp(13));

            int tabHeight = 25;
            int lineHeight = DialogBottomMenuHiddenController.is(DialogBottomMenuLayoutAdapter.Type.HELP_LINE.ordinal()) ? 0 : 1;

            bottomMenuFrameLayout = new FrameLayout(context);

            bottomMenuTabBg = new ImageView(context);
            bottomMenuTabBg.setImageResource(R.drawable.ic_bar);

            bottomMenuTabBg.setColorFilter(Theme.getColor(Theme.key_actionBarDefault));
            bottomMenuTabBg.setRotation(angle);
            bottomMenuTabBg.setClickable(true);
            bottomMenuFrameLayout.addView(bottomMenuTabBg, LayoutHelper.createFrame(chatToolsHeight, tabHeight, Gravity.TOP | Gravity.START));

            bottomMenuTabIcon = new ImageView(context);
            bottomMenuTabIcon.setImageResource(R.drawable.preview_arrow);
            bottomMenuTabIcon.setPadding(5, 7, 5, 5);
            bottomMenuFrameLayout.addView(bottomMenuTabIcon, LayoutHelper.createFrame(tabHeight, tabHeight, Gravity.TOP | Gravity.START,
                    24, 0, 24, 20));

            //bottomOverlayChatText.setVisibility(View.GONE);
            bottomMenuListView = new RecyclerListView(context);
            LinearLayoutManager lm =
                    new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
            bottomMenuListView.setBackgroundColor(Theme.getColor(Theme.key_actionBarDefault));
            bottomMenuListView.setLayoutManager(lm);
            dbmAdapter = new DialogBottomMenuLayoutAdapter(context, dialog_id, false,
                    getMessagesController().isDialogMuted(dialog_id, topicId), dialogType, hasAdminRights);
            Log.i(TAG, "initBottomMenu: dialogType:" + dialogType);
            bottomMenuListView.setAdapter(dbmAdapter);

            bottomMenuListView.setOnItemLongClickListener((view, position) -> {
                presentFragment(new DialogBottomMenuSettingsActivity(dialogType));
                return true;
            });

            //Toast.makeText(context, dbmAdapter.getId(position) + "", Toast.LENGTH_SHORT).show();
            bottomMenuListView.setOnItemClickListener(this::bottomMenuItemClick);

            bottomMenuFrameLayout.addView(bottomMenuListView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, (chatToolsHeight - tabHeight) + lineHeight, Gravity.TOP,
                    0, tabHeight - lineHeight, 0, 0));

            contentView.addView(bottomMenuFrameLayout, contentView.getChildCount() - 3, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, chatToolsHeight, Gravity.BOTTOM));

            bottomMenuTabBg.setOnClickListener(view -> {
                bottomMenuTabClick();
            });


//            bottomOverlayChat.addView(bottomMenu, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, 51,                    Gravity.BOTTOM));
            chatListView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);

                    if (!recyclerView.canScrollVertically(1)) {
//                    Toast.makeText(context, "Last", Toast.LENGTH_LONG).show();

                    }
                }
            });
        }
        //endregion
    }

    private void bottomMenuTabClick() {
        boolean close = bottomMenuTabIcon.getRotation() == 0;
        if (close) { //open tab
            bottomMenuListView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
            bottomMenuAnimation = new Animation() {
                @Override
                protected void applyTransformation(float interpolatedTime, Transformation t) {
                    bottomMenuFrameLayout.setLayoutParams(LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, chatToolsHeight, Gravity.BOTTOM, 0, 0, 0, (newBottomMargin * interpolatedTime)));
                    bottomMenuTabIcon.setRotation(angle * interpolatedTime);
                }
            };
        } else {//close tab
            bottomMenuAnimation = new Animation() {
                @Override
                protected void applyTransformation(float interpolatedTime, Transformation t) {
                    bottomMenuFrameLayout.setLayoutParams(LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, chatToolsHeight, Gravity.BOTTOM, 0, 0, 0, newBottomMargin - (newBottomMargin * interpolatedTime)));
                    bottomMenuTabIcon.setRotation(angle - (angle * interpolatedTime));
                }
            };
        }

        bottomMenuAnimation.setDuration(300); // in ms
        bottomMenuFrameLayout.startAnimation(bottomMenuAnimation);
        bottomMenuAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (!close) {
                    bottomMenuListView.setBackgroundColor(Theme.getColor(Theme.key_actionBarDefault));
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    private void bottomMenuItemClick(View view, int position) {
        if (bottomMenuTabIcon.getRotation() != 180) {
            return;
        }
        DialogBottomMenuLayoutAdapter.Type type =
                DialogBottomMenuLayoutAdapter.Type.values()[dbmAdapter.getId(position)];
        int duration = 300;
        switch (type) {
            case HIDE_MENU:
                break;
            case BACK:
                finishFragment();
                break;
            case LEFT:
                doDeleteChat(delete_chat);
                break;
            case REPORT:
                AlertsCreator.createReportAlert(getParentActivity(), dialog_id, 0, org.telegram.ui.ChatActivity.this, null);
                duration = 0;
                break;
            case MUTE: {
                boolean mute = getMessagesController().isDialogMuted(dialog_id, getTopicId());
                toggleMute(!mute);
                dbmAdapter.setMute(!mute);
                dbmAdapter.notifyDataSetChanged();
            }
            break;
            case SEARCH:
                openSearchWithText(null);
                break;
            case FIRST_MESSAGE:
                scrollToMessageId(1, 0, true, 0, true, 0);
                break;
            case HIDE_DIALOG:
                toggleHide(view);
                break;
            case FAV:
                toggleFav(view);
                break;
            case DISCUSS:
                doDiscuss();
                break;
            case FIX_X:
                break;
            case CALENDAR:
                doOpenDatePicker();
                duration = 0;
                break;
            case BOOKMARKED: {
                goToBookmarked();
                break;
            }
            case TRANSLATE: {
                if (translate_local.isEmpty()) {
                    presentFragment(new LanguageSelectActivity(LanguageSelectActivity.Type.PERSONAL, dialog_id));
                    duration = 0;
                } else {
                    SharedStorage.userTranslateTarget(dialog_id, "");
                    updateTranslateStatus(chatActivityEnterView, dialog_id, headerItem);
                    chatActivityEnterView.updateFieldHint();
                }
                dbmAdapter.notifyDataSetChanged();
                break;
            }
            case BASIC_FONT:
                duration = 0;
                presentFragment(new TextNicerActivity(new MessagesStorage.IntCallback() {
                    @Override
                    public void run(int param) {
                        chatActivityEnterView.updateTextStyle();
                        dbmAdapter.notifyDataSetChanged();
                    }//? //hossein's first code :)) question mark !
                }));
                break;
            case BOLD: {
                SharedStorage.chatSettings(SharedStorage.keys.BOLD, !SharedStorage.chatSettings(SharedStorage.keys.BOLD));
                dbmAdapter.notifyDataSetChanged();
                chatActivityEnterView.updateTextStyle();
                break;
            }
            case ITALIC: {
                SharedStorage.chatSettings(SharedStorage.keys.ITALIC, !SharedStorage.chatSettings(SharedStorage.keys.ITALIC));
                dbmAdapter.notifyDataSetChanged();
                chatActivityEnterView.updateTextStyle();
                break;
            }
            case STRIKE: {
                boolean status = !SharedStorage.chatSettings(SharedStorage.keys.STRIKE);
                SharedStorage.chatSettings(SharedStorage.keys.STRIKE, status);
                if (status) {
                    SharedStorage.chatSettings(SharedStorage.keys.UNDERLINE, false);
                }
                dbmAdapter.notifyDataSetChanged();
                chatActivityEnterView.updateTextStyle();
                break;
            }
            case UNDERLINE: {
                boolean status = !SharedStorage.chatSettings(SharedStorage.keys.UNDERLINE);
                SharedStorage.chatSettings(SharedStorage.keys.UNDERLINE, status);
                if (status) {
                    SharedStorage.chatSettings(SharedStorage.keys.STRIKE, false);
                }
                dbmAdapter.notifyDataSetChanged();
                chatActivityEnterView.updateTextStyle();
                break;
            }
            case SETTING: {
                presentFragment(new DialogBottomMenuSettingsActivity(dialogType));
                duration = 0;
                break;
            }
            case VOICE_CHANGER: {
                duration = -1;
                VoiceChangeHelper.show(view.getContext(), index -> {
                    if (index == 1) {
                        dbmAdapter.notifyDataSetChanged();
                    }
                    closeBottomMenu(300);
                });
                break;

            }
            case HELP_LINE:
                break;
        }
        if (duration >= 0) {
            closeBottomMenu(duration);
        }
    }

    private void closeBottomMenu(int duration) {
        if (bottomMenuTabBg.getRotation() == 180) {
            if (DialogBottomMenuHiddenController.is(DialogBottomMenuLayoutAdapter.Type.CLOSE_ON_CLICK.ordinal())) {
                return;
            }
            if (duration > 20) {
                bottomMenuAnimation = new Animation() {
                    @Override
                    protected void applyTransformation(float interpolatedTime, Transformation t) {
                        bottomMenuFrameLayout.setLayoutParams(LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, chatToolsHeight, Gravity.BOTTOM, 0, 0, 0, newBottomMargin - (newBottomMargin * interpolatedTime)));
                        bottomMenuTabIcon.setRotation(angle - (angle * interpolatedTime));
                    }
                };
                bottomMenuAnimation.setDuration(duration); // in ms
                bottomMenuFrameLayout.startAnimation(bottomMenuAnimation);
            } else {
                bottomMenuFrameLayout.setLayoutParams(LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, chatToolsHeight, Gravity.BOTTOM, 0, 0, 0, 0));
                bottomMenuTabIcon.setRotation(0);
            }
        }
    }

    protected void initSmartForward(Context context, FrameLayout bottomMessagesActionContainer, TLRPC.Chat currentChat, SparseArray<MessageObject>[] selectedMessagesIds, ChatActivityEnterView chatActivityEnterView, IChatActivityCallback callback) {
        if (!Config.SMART_FORWARD_FEATURE || !BuildVars.DEBUG_PRIVATE_VERSION) {
            return;
        }
        TextView smartForwardButton = new TextView(context);
        smartForwardButton.setText(LocaleController.getString("MultipleShare", R.string.MultipleShare));
        smartForwardButton.setGravity(Gravity.CENTER_VERTICAL);
        smartForwardButton.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
        smartForwardButton.setPadding(AndroidUtilities.dp(21), 0, AndroidUtilities.dp(21), 0);
        smartForwardButton.setCompoundDrawablePadding(AndroidUtilities.dp(6));
        smartForwardButton.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.getColor(Theme.key_actionBarActionModeDefaultSelector), 3));
        smartForwardButton.setTextColor(Theme.getColor(Theme.key_actionBarActionModeDefaultIcon));
        smartForwardButton.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));

        Drawable image = context.getResources().getDrawable(R.drawable.input_forward).mutate();
        image.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_actionBarActionModeDefaultIcon), PorterDuff.Mode.MULTIPLY));

        smartForwardButton.setCompoundDrawablesWithIntrinsicBounds(image, null, null, null);
        smartForwardButton.setOnClickListener(v -> callback.onResponse(1));
        bottomMessagesActionContainer.addView(smartForwardButton, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.MATCH_PARENT, Gravity.LEFT | Gravity.TOP));

        smartForwardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<MessageObject> arrayList = new ArrayList<>();
                for (int a = 0; a < 2; a++) {
                    for (int b = 0, N = selectedMessagesIds[a].size(); b < N; b++) {
                        MessageObject message = selectedMessagesIds[a].valueAt(b);
                        arrayList.add(message);
//                        long groupId = message.getGroupId();

                    }
                }
//                doMultipleForward(forwardingMessages,context);

                showDialog(
                        new ShareAlert(context, arrayList, null, ChatObject.isChannel(currentChat), null,
                                false) {
                            @Override
                            public void dismissInternal() {
                                super.dismissInternal();
                                AndroidUtilities.requestAdjustResize(getParentActivity(), classGuid);
                                if (chatActivityEnterView.getVisibility() == View.VISIBLE) {
                                    fragmentView.requestLayout();
                                }
                            }
                        });
            }
        });
    }

    protected void myCreateMenu(ArrayList<CharSequence> items, ArrayList<Integer> options, ArrayList<Integer> icons, MessageObject message, int type, boolean noforwards, MessageObject selectedObject, TLRPC.EncryptedChat currentEncryptedChat, TLRPC.Chat currentChat, TLRPC.User currentUser, TLRPC.ChatFull chatInfo, boolean b) {
        if (currentEncryptedChat == null) {
            if (type == 6 && !noforwards) {
                if (MessageMenuController.is(MessageMenuController.Type.DELETE_FILES)) {
                    items.add(LocaleController.getString("DeleteDownloadedFile", R.string.DeleteDownloadedFile));
                    options.add(delete_downloaded_files);
                    icons.add(R.drawable.msg_clearcache);
                }

            }

            if (!selectedObject.isSponsored() && b && !selectedObject.needDrawBluredPreview() && !selectedObject.isLiveLocation() && selectedObject.type != 16 && !noforwards) {
                items.add(LocaleController.getString("Forward", R.string.Forward));
                options.add(2);
                icons.add(R.drawable.msg_forward_quote);

                if (MessageMenuController.is(MessageMenuController.Type.FORWARD_WITHOUT_QUOTE)) {
                    items.add(LocaleController.getString("ForwardNoQuote", R.string.ForwardNoQuote));
                    options.add(forward_no_quote);
                    icons.add(R.drawable.input_forward);
                }
                if (MessageMenuController.is(MessageMenuController.Type.SMART_FORWARD)) {
                    items.add(BuildVars.SMART_FORWARD_FEATURE ? LocaleController.getString("MultipleShare", R.string.MultipleShare) : LocaleController.getString("AdvanceShare", R.string.AdvanceShare));
                    options.add(forward_multiple);
                    icons.add(R.drawable.input_forward);
                }

                if (DownloadHelper.canAddToQueue(selectedObject) && MessageMenuController.is(MessageMenuController.Type.DOWNLOAD) && Config.DOWNLOAD_MANAGER_FEATURE) {
                    items.add(LocaleController.getString("AddToDownloads", R.string.AddToDownloads));
                    options.add(add_to_download);
                    icons.add(R.drawable.msg_download);
                }
   */
/*                         int MarkedMessage = SharedStorage.markedMessages(dialog_id, selectedObject.getId());

                            if (MarkedMessage != selectedObject.getId()) {
                                items.add(LocaleController.getString("SetChatMarker", R.string.SetChatMarker));
                                options.add(add_message_to_archive);
                            } else {
                                items.add(LocaleController.getString("DeleteChatMarker", R.string.DeleteChatMarker));
                                options.add(delete_message_from_archive);
                            }*//*


                if (!UserObject.isUserSelf(currentUser) && MessageMenuController.is(MessageMenuController.Type.SAVE_MESSAGE)) {
                    items.add(LocaleController.getString("AddToSavedMessages", R.string.AddToSavedMessages));
                    options.add(save_message);
                    icons.add(R.drawable.msg_saved);
                }
                if (BuildVars.NEKO_FEATURE) {
                    boolean allowRepeat = currentUser != null
                            || (currentChat != null && ChatObject.canSendMessages(currentChat));
                    if (allowRepeat && MessageMenuController.is(MessageMenuController.Type.REPEAT)) {
                        items.add(LocaleController.getString("Repeat", R.string.Repeat));
                        options.add(repeat_message);
                        icons.add(R.drawable.msg_repeat);
                    }
                }
            }

            //region Customized:
            if (b && BuildVars.NEKO_FEATURE) {
                if (MessageMenuController.is(MessageMenuController.Type.PRPR)) {
                    boolean allowPrpr = currentUser != null
                            || (currentChat != null && ChatObject.canSendMessages(currentChat) && !currentChat.broadcast &&
                            message.isFromUser());
                    if (allowPrpr) {
                        items.add(LocaleController.getString("Prpr", R.string.Prpr));
                        options.add(prpr_message);
                        icons.add(R.drawable.msg_prpr);
                    }
                }
                if (MessageMenuController.is(MessageMenuController.Type.HISTORY)) {
                    boolean allowViewHistory = currentUser == null
                            && (currentChat != null && !currentChat.broadcast && message.isFromUser());

                    if (allowViewHistory) {
                        items.add(LocaleController.getString("ViewUserHistory", R.string.ViewHistory));
                        options.add(show_message_history);
                        icons.add(R.drawable.msg_recent);
                    }
                }-
                if (MessageMenuController.is(MessageMenuController.Type.DETAILS)) {
                    items.add(LocaleController.getString("MessageDetails", R.string.MessageDetails));
                    options.add(show_message_detail);
                    icons.add(R.drawable.msg_info);
                }
                if (BuildVars.TRANSLATE_FEATURE && !TextUtils.isEmpty(selectedObject.messageOwner.message) && MessageMenuController.is(MessageMenuController.Type.TRANSLATE)) {
                    Matcher matcher = Pattern.compile("\u200C\u200C\\n\\n--------\\n.*\u200C\u200C", Pattern.DOTALL).matcher(selectedObject.messageOwner.message);
                    items.add(matcher.find() ? LocaleController.getString("UndoTranslate", R.string.UndoTranslate) : LocaleController.getString("Translate", R.string.Translate));
                    options.add(translate);
                    icons.add(R.drawable.ic_g_translate);
                }
            }
            //endregion
        }


        //region Customized:
        if (chatInfo != null && chatInfo.participants != null && chatInfo.participants.participants != null) {
            for (int a = 0; a < chatInfo.participants.participants.size(); a++) {
                TLRPC.ChatParticipant participant = chatInfo.participants.participants.get(a);
                if (participant.user_id != selectedObject.messageOwner.from_id.user_id || participant.user_id == getUserConfig().getCurrentUser().id) {
                    continue;
                }

                boolean canEditAdmin;
                boolean canRestrict;
                boolean editingAdmin;
                final TLRPC.ChannelParticipant channelParticipant;

                if (ChatObject.isChannel(currentChat)) {
                    channelParticipant = ((TLRPC.TL_chatChannelParticipant) participant).channelParticipant;
                    canEditAdmin = ChatObject.canAddAdmins(currentChat);
                    if (canEditAdmin && (channelParticipant instanceof TLRPC.TL_channelParticipantCreator || channelParticipant instanceof TLRPC.TL_channelParticipantAdmin && !channelParticipant.can_edit)) {
                        canEditAdmin = false;
                    }
                    canRestrict = ChatObject.canBlockUsers(currentChat) && (!(channelParticipant instanceof TLRPC.TL_channelParticipantAdmin || channelParticipant instanceof TLRPC.TL_channelParticipantCreator) || channelParticipant.can_edit);
                    editingAdmin = channelParticipant instanceof TLRPC.TL_channelParticipantAdmin;
                } else {
                    canEditAdmin = currentChat.creator;
                    canRestrict = currentChat.creator;
                    editingAdmin = participant instanceof TLRPC.TL_chatParticipantAdmin;
                }

                if (canEditAdmin && MessageMenuController.is(MessageMenuController.Type.ADMIN)) {
                    items.add(editingAdmin ? LocaleController.getString("EditAdminRights", R.string.EditAdminRights) : LocaleController.getString("SetAsAdmin", R.string.SetAsAdmin));
                    icons.add(R.drawable.msg_admins);
                    options.add(edit_admin_rights);
                }
                if (canRestrict && MessageMenuController.is(MessageMenuController.Type.PERMISSION)) {
                    items.add(LocaleController.getString("ChangePermissions", R.string.ChangePermissions));
                    icons.add(R.drawable.msg_permissions);
                    options.add(change_permission);
                }
            }
        }

        if (MessageMenuController.is(MessageMenuController.Type.SETTINGS)) {
            items.add(LocaleController.getString("ChatMenusSettings", R.string.ChatMenusSettings));
            options.add(menu_settings);
            icons.add(R.drawable.msg_settings);
        }
        //endregion

    }

   protected boolean forwardFromMyName = false;

    protected void processSelectedMyOption(int option, int canForwardMessagesCount, MessageObject.GroupedMessages selectedObjectGroup, MessageObject.GroupedMessages forwardingMessageGroup, MessageObject selectedObject, MessageObject forwardingMessage, long dialog_id, long mergeDialogId) {
        switch (option) {
            case forward_no_quote: {
                forwardingMessage = selectedObject;
                forwardingMessageGroup = selectedObjectGroup;
                canForwardMessagesCount =
                        forwardingMessageGroup == null ? 1 : forwardingMessageGroup.messages.size();

                forwardFromMyName = true;
                openForward(true);
                break;
            }
            case forward_multiple:
                doMultipleForward(selectedObject, getParentActivity());
                break;

            case add_to_download:
                doAddToDownloadManager();
                break;

            case translate: {
                doShowTranslate();
                break;
            }
            case show_message_detail: {
                presentFragment(new MessageDetailsActivity(selectedObject));
                break;
            }
            case delete_downloaded_files: {
                deleteDownloadFiles();
                break;
            }
            case edit_admin_rights: {
                doAdminActions(edit_admin_rights);
                break;
            }
            case change_permission: {
                doAdminActions(change_permission);
                break;
            }
            case menu_settings: {
                showMessageMenuAlert();
                break;
            }
            case show_message_history: {
                TLRPC.User user = MessagesController.getInstance(currentAccount).getUser(selectedObject.messageOwner.from_id.user_id);
                getMediaDataController().searchMessagesInChat("", dialog_id, mergeDialogId, classGuid, 0, 0, user, searchingChatMessages);
                showMessagesSearchListView(true);
                break;
            }
            case prpr_message: {
                doPrprMessage();
                break;
            }
            case save_message: {
                doSaveInCloud();
                break;
            }
            case repeat_message: {
                ArrayList<MessageObject> messages = new ArrayList<>();
                messages.add(selectedObject);
                forwardMessages(messages, false, true, true, 0);
                break;
            }
            default:
                throw new IllegalStateException("Unexpected value: " + option);
        }
    }

     boolean nativeShown = false;

    protected boolean showNativeResult = false;

    protected void  showTopPanelForShare(TLRPC.User user, long did, boolean show, boolean userBlocked, TextView addToContactsButton, TextView reportSpamButton, ImageView closeReportSpam, BlurredFrameLayout topChatPanelView, IChatActivityCallback callback) {
        if (show) {
            Log.d(TAG, "showTopPanelForShare: show original top panel , returned!");
            showNativeResult = true; //show report as spam
        }


        if (userBlocked) {
            Log.d(TAG, "showTopPanelForShare:  userBlocked , returned!");
            showNativeResult = show;
        }

        if (user != null && (user.self || user.bot)) {
            Log.d(TAG, "showTopPanelForShare: user itself or is the bot, returned!");
            showNativeResult = show;
        }

        if (dialogType != DialogBottomMenuLayoutAdapter.DialogType.GROUP && dialogType != DialogBottomMenuLayoutAdapter.DialogType.USER && dialogType != DialogBottomMenuLayoutAdapter.DialogType.SECRET) {
            Log.d(TAG, "showTopPanelForShare: dialogType not match:" + dialogType);
            showNativeResult = show;
        }

        try {
            SharedPreferences preferences = MessagesController.getNotificationsSettings(currentAccount);
            int status = preferences.getInt("top_panel_status_" + did, 0);
            if (!Config.SHARE_IN_CHAT_FEATURE) {
                status = 2;
                Log.i(TAG, "showTopPanelForShare: SHARE_IN_CHAT_FEATURE false");
            }
            addToContactsButton.setVisibility(View.GONE);
            reportSpamButton.setVisibility(View.GONE);

            if (status == 0) {
                addToContactsButton.setTag(1);
                addToContactsButton.setVisibility(View.VISIBLE);
                addToContactsButton.setText(LocaleController.getString("ShareApp", R.string.ShareApp).toUpperCase());
                addToContactsButton.setOnClickListener(view -> {

                    String str = SharedStorage.shareAppContent();
                    SendMessagesHelper.getInstance(UserConfig.selectedAccount).sendMessage(str, did, null, null, null, true, null, null, null, true, 0, null, false);

                    //if user share app , set 1 for never show the ad for this user ,
                    preferences.edit().putInt("top_panel_status_" + did, 1).apply();
                    callback.onResponse(1);
                });

                showNativeResult = true;
                closeReportSpam.setOnClickListener(view -> {
                    //if user ignore the share app , set 2 for show the ad this user ,
                    preferences.edit().putInt("top_panel_status_" + did, 2).apply();
                    callback.onResponse(1);
                });

            } else if (status == 2) { //can show native admob

                if (!nativeShown) {
                    NativeAddCell nativeAddCell = null;
                    AdmobController.getInstance().getUINativeItem("top_chat", new Native.IGetNativeItem() {
                        @Override
                        public void onServe(NativeAddCell nativeAddCell) {
                            showNativeResult = nativeAddCell != null;
                            if (nativeAddCell != null) {
                                topChatPanelView.setTag("native");
                                topChatPanelView.setClickable(true);
                                topChatPanelView.addView(nativeAddCell, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, Gravity.TOP | Gravity.LEFT, 0, 0, 35, 0));
                                topChatPanelView.getLayoutParams().height = AndroidUtilities.dp(80);
                                nativeShown = true;

                                closeReportSpam.setOnClickListener(null);
                                closeReportSpam.setClickable(true);
                                closeReportSpam.setOnClickListener(view -> {
                                    topChatPanelView.setVisibility(View.GONE);
                                });

                                callback.onResponse(1);
                            }
                        }
                    });


                } else {
                    //if false : ad hide smoothlly
                    showNativeResult = true;
                }

                //customized

            }


        } catch (Exception e) {
            Log.e(TAG, "showTopPanelForShare: ", e);
        }
    }


    //endregion


}

*/
