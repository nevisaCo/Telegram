package com.finalsoft.ui.extend;

import org.telegram.ui.ActionBar.BaseFragment;

public class ChatActivity extends BaseFragment {
/*    //region customized: custom code
    protected ActionBarMenuSubItem hideItem;
    protected ActionBarMenuSubItem favItem;
    protected ActionBarMenuSubItem translateItem;
    protected long dialog_id;


    //region Customized: properties
    protected static final String TAG = Config.TAG + "ca";
    protected static String BANNER_UNIT_ID = "";
    protected static int ADMOB_BANNER_COUNT = SharedStorage.admobPerMessage();
    protected static boolean SHOW_ADMOB = SharedStorage.showAdmob();
    protected static boolean SHOW_BANNER_IN_CHAT = SharedStorage.showBannerInChats();
    protected static boolean SHOW_BANNER_IN_GROUP = SharedStorage.showBannerInGroups();
    protected static boolean canShowBanner = true;
    protected DialogBottomMenuLayoutAdapter dbmAdapter;
    protected boolean showMessageBubble = SharedStorage.showMessageBubble();
    protected boolean active_scheduled_tab = !TabMenuHiddenController.is(TabMenuLayoutAdapter.SCHEDULED);
    protected DownloadHelper downloadHelper = new DownloadHelper();
    protected int dmCost = SharedStorage.downloadManagerCost();
    protected boolean video_error = SharedStorage.admobVideoErrorList();
    //endregion

    protected String voice2text;
    //region Customized:
    protected Voice2TextHelper voice2TextHelper;

    protected RecyclerListView bottomMenu;
    protected boolean showBottomMenu;

    protected final int copy_link = 22;
    protected final int forward_no_quote = 40;
    protected final int forward_multiple = 41;
    protected final int add_to_download = 44;
    protected final int save_message = 200;
    protected final int repeat_message = 201;
    protected final int prpr_message = 202;
    protected final int show_message_history = 203;
    protected final int show_message_detail = 204;
    protected final int translate = 205;
    protected final int menu_settings = 206;
    protected final int delete_downloaded_files = 207;
    protected final int edit_admin_rights = 208;
    protected final int change_permission = 209;

    protected ArrayList<MessageObject> selectedObjects = new ArrayList<>();
    protected boolean proxyServer = false;
    protected boolean ghostMode = false;
    protected boolean canShowGhostMode = false;
    protected boolean canShowPersonalTranslate = false;
    protected ActionBarMenuItem ghostItem;
    protected ActionBarMenuItem proxyItem;

    protected int mc;
    //endregion
    protected final static int forwardNoQuote = 57;
    protected final static int fav = 58;
    protected final static int hide = 59;
    protected final static int firstMessage = 60;
    protected final static int edit_contact = 61;
    protected final static int block_contact = 62;
    protected final static int share_this_contact = 63;
    protected final static int bookmarked = 64;
    protected final static int refresh_proxy_item_id = 65;
    protected final static int ghost_mode_item_id = 66;
    protected final static int translate_select_language_item_id = 67;

    //Customized:
    protected int VOICE_RECOGNITION_REQUEST_CODE = 12345;//Customized:


    protected void doShowAdmob() {
        if (SHOW_ADMOB || BuildVars.DEBUG_VERSION) {
            int cpd = SharedStorage.admobPerDialog();
            if (cpd == 0) {
                return;
            }

            int count = SharedStorage.admobPerDialogCounter() + 1;
            Log.i(TAG, "doShowAdmob: cpd:" + cpd + " count:" + count);
            SharedStorage.admobPerDialogCounter(count);

            if (count >= cpd) {
                NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.showAdmobInterstitial, LaunchActivity.DIALOG_ADMOB);
                Log.i(TAG, "doShowAdmob: post notif");
            }
        } else {
            Log.i(TAG, "doShowAdmob: admob false");
        }
    }

    //region Customized: update Translate Status
    protected String translate_local = "";

    protected void updateTranslateStatus() {
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
    Proxy p;

    protected void doRefreshOfflineProxy() {
        try {
            if (ConnectionsManager.getInstance(UserConfig.selectedAccount).getConnectionState() == ConnectionsManager.ConnectionStateWaitingForNetwork) {
                Toast.makeText(getParentActivity(), "no internet connected!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (p == null)
                p = new Proxy();
            p.change(true);
            p.increaseCounter();
            proxyItem.setVisibility(View.INVISIBLE);
            Toast.makeText(getParentActivity(), "refresh proxy!", Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(() -> proxyItem.setVisibility(View.VISIBLE), 5000);
        } catch (Exception e) {
            Log.e(TAG, "doRefreshOfflineProxy: ", e);
        }
    }

    protected void goToBookmarked() {
        int MarkedMessage = SharedStorage.markMessages(dialog_id);
        scrollToMessageId(MarkedMessage, 0, true, 0, true);
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
            DatePickerDialog dialog =
                    new DatePickerDialog(getParentActivity(), (view1, year1, month, dayOfMonth1) -> {
                        Calendar calendar1 = Calendar.getInstance();
                        calendar1.clear();
                        calendar1.set(year1, month, dayOfMonth1);
                        int date = (int) (calendar1.getTime().getTime() / 1000);
                        clearChatData();
                        waitingForLoad.add(lastLoadIndex);
                        getMessagesController().loadMessages(dialog_id, 30, 0, date, true, 0, classGuid, 4, 0,
                                ChatObject.isChannel(currentChat), inScheduleMode, lastLoadIndex++);
                    }, year, monthOfYear, dayOfMonth);
            final DatePicker datePicker = dialog.getDatePicker();
            datePicker.setMinDate(1375315200000L);
            datePicker.setMaxDate(System.currentTimeMillis());
            dialog.setButton(DialogInterface.BUTTON_POSITIVE,
                    LocaleController.getString("JumpToDate", R.string.JumpToDate), dialog);
            dialog.setButton(DialogInterface.BUTTON_NEGATIVE,
                    LocaleController.getString("Cancel", R.string.Cancel), (dialog1, which) -> {

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
                MessagesController.getInstance(currentAccount).unblockUser(currentUser.id);
                AlertsCreator.showSimpleToast(org.telegram.ui.ChatActivity.this, LocaleController.getString("UserUnblocked", R.string.UserUnblocked));
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
                builder.setTitle(LocaleController.getString("BlockUser", R.string.BlockUser));
                builder.setMessage(AndroidUtilities.replaceTags(LocaleController.formatString("AreYouSureBlockContact2", R.string.AreYouSureBlockContact2, ContactsController.formatName(user.first_name, user.last_name))));
                builder.setPositiveButton(LocaleController.getString("BlockContact", R.string.BlockContact), (dialogInterface, i) -> {
                    MessagesController.getInstance(currentAccount).blockUser(currentUser.id);
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
                MessagesController.getInstance(currentAccount).blockUser(currentUser.id);
            } else {
                MessagesController.getInstance(currentAccount).unblockUser(currentUser.id);
                SendMessagesHelper.getInstance(currentAccount).sendMessage("/start", currentUser.id, null, null, false, null, null, null, true, 0);
                finishFragment();
            }
        }
    }

    protected void doDiscuss() {
        if (chatInfo == null) {
            return;
        }
        Bundle args = new Bundle();
        args.putInt("chat_id", chatInfo.linked_chat_id);
        if (!getMessagesController().checkCanOpenChat(args, org.telegram.ui.ChatActivity.this)) {
            return;
        }
        presentFragment(new org.telegram.ui.ChatActivity(args));
    }

    protected void toggleHide(View view) {
        if (HiddenController.update(dialog_id)) {
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
                favItem.setIcon(R.drawable.msg_fave);
                favItem.setText(LocaleController.getString("AddToFavorites", R.string.AddToFavorites));
            } else {
                ((DialogBottomActionCell) view).setImageResource(R.drawable.msg_fave);
            }
        } else {
            if (view == null) {
                favItem.setIcon(R.drawable.ic_ab_fave);
                favItem.setText(LocaleController.getString("DeleteFromFavorites", R.string.DeleteFromFavorites));
            } else {
                ((DialogBottomActionCell) view).setImageResource(R.drawable.ic_ab_fave);
            }
        }
        MessagesController.getInstance(UserConfig.selectedAccount).sortDialogs(null);
    }

    protected void doDeleteChat(int id) {
        if (getParentActivity() == null) {
            return;
        }
        final boolean isChat = (int) dialog_id < 0 && (int) (dialog_id >> 32) != 1;

        AlertsCreator.createClearOrDeleteDialogAlert(org.telegram.ui.ChatActivity.this, id == clear_history,
                currentChat, currentUser, currentEncryptedChat != null, (param) -> {
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
                                        getMessagesController().deleteDialog(dialog_id, 1, param);
                                        clearingHistory = false;
                                        clearHistory(false);
                                        chatAdapter.notifyDataSetChanged();
                                    }, () -> {
                                        clearingHistory = false;
                                        chatAdapter.notifyDataSetChanged();
                                    });
                            chatAdapter.notifyDataSetChanged();
                        }
                    }
                });
    }


    protected void openRightsEdit(int action, int user_id, TLRPC.ChatParticipant participant, TLRPC.TL_chatAdminRights adminRights, TLRPC.TL_chatBannedRights bannedRights, String rank) {
        ChatRightsEditActivity fragment = new ChatRightsEditActivity(user_id, currentChat.id, adminRights, currentChat.default_banned_rights, bannedRights, rank, action, true, false);
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

    protected void doAdminActions(int option){
        int action =change_permission==option? 1:0;

        for (int a = 0; a < chatInfo.participants.participants.size(); a++) {
            TLRPC.ChatParticipant participant = chatInfo.participants.participants.get(a);
            if (participant.user_id != selectedObject.messageOwner.from_id || participant.user_id == getUserConfig().getCurrentUser().id) {
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
            path = FileLoader.getPathToMessage(selectedObject.messageOwner).toString();
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
            TLRPC.User user = MessagesController.getInstance(currentAccount).getUser(selectedObject.messageOwner.from_id);
            if (user.username != null) {
                SendMessagesHelper.getInstance(currentAccount).sendMessage("/prpr@" + user.username, dialog_id, selectedObject, null, false,
                        null, null, null, true, 0);
            } else {
                SpannableString spannableString = new SpannableString("/prpr@" + user.first_name);
                spannableString.setSpan(new URLSpanUserMention(Integer.toString(user.id), 1), 6, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
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
                SendMessagesHelper.getInstance(currentAccount).sendMessage(spannableString.toString(), dialog_id, selectedObject, null, false,
                        entities, null, null, true, 0);
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
                Matcher matcher = Pattern.compile("\u200C\u200C\\n\\n--------\\n.*\u200C\u200C", Pattern.DOTALL).matcher(original);
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
                                        "\u200C\u200C\n" +
                                        "\n" +
                                        "--------" +
                                        "\n" +
                                        translation +
                                        "\u200C\u200C");
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
                    });
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "doShowTranslate: ", e);
        }
    }

    protected void doAddToDownloadManager() {
        try {
            //region Customized: show admob
            if (SHOW_ADMOB && dmCost > 0) {
                int myRewards = SharedStorage.rewardes();
                if (myRewards >= dmCost) {
                    myRewards = myRewards - dmCost;
                    SharedStorage.rewardes(myRewards);
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
                            NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.showAdmobVideo, LaunchActivity.REWARD_ADMOB);
                        } else {
                            //interstitial
                            NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.showAdmobInterstitial, LaunchActivity.REWARD_ADMOB);
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
        }
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
                args.putInt("chat_id",
                        messageObject.messageOwner.fwd_from.saved_from_peer.channel_id);
            } else if (messageObject.messageOwner.fwd_from.saved_from_peer.chat_id != 0) {
                args.putInt("chat_id", messageObject.messageOwner.fwd_from.saved_from_peer.chat_id);
            } else if (messageObject.messageOwner.fwd_from.saved_from_peer.user_id != 0) {
                args.putInt("user_id", messageObject.messageOwner.fwd_from.saved_from_peer.user_id);
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
            forwardMessages(messages, false, true, 0, UserConfig.getInstance(currentAccount).getClientUserId());
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
            textCell.setTextAndIcon(MessageMenuController.labels[tag_val],
                    MessageMenuController.icons[tag_val]);
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
    //endregion*/
}

