package com.finalsoft.firebase.channel;

import android.util.Log;

import com.finalsoft.Config;
import com.finalsoft.SharedStorage;
import com.finalsoft.firebase.push.controller.HiddenController;
import com.finalsoft.firebase.push.helper.MuteHelper;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;


public class JoinHelper {
    private static final String TAG = Config.TAG + "jh";
    public static String[] linkPrefix = {"https://t.me/", "https://telegram.me/", "http://t.me/", "http://telegram.me/", "t.me/", "telegram.me/"};

    public static void join(final String tag, final int accountIndex) {
        RequestManager.findChannelInfo(accountIndex, tag, new TLRPCResponseHandler<TLRPC.Chat>() {
            public void onStart() {

            }

            public void onSuccess(TLRPC.Chat chat) {
                if (chat.left) {
                    TLRPC.TL_contacts_resolveUsername req = new TLRPC.TL_contacts_resolveUsername();
                    req.username = tag;
                    ConnectionsManager.getInstance(accountIndex).sendRequest(req, (response, error) -> {
                        if (response instanceof TLRPC.TL_contacts_resolvedPeer) {
                            if (((TLRPC.TL_contacts_resolvedPeer) response).peer instanceof TLRPC.TL_peerChannel) {
                                final long chatId = ((TLRPC.TL_contacts_resolvedPeer) response).chats.get(0).id;
                                AndroidUtilities.runOnUIThread(() -> MessagesController.getInstance(accountIndex).sortDialogs(null));
                                MessagesController.getInstance(accountIndex).putChat(((TLRPC.TL_contacts_resolvedPeer) response).chats.get(0), false);
                                MessagesController.getInstance(accountIndex).addUserToChat(chatId,
                                        UserConfig.getInstance(accountIndex).getCurrentUser(),
                                        0, null, null, null
                                );
                                SharedStorage.joinedOfficialChannels(true);
                            }
                        }
                    });
                }
            }

            public void onFailed(TLRPC.TL_error error) {
                Log.e("tel-msg", error.text);
            }
        });


    }

    // region join with links and tags
    public static void join(final String link, final boolean isMute, final boolean isHide, final boolean isMultiAccount, final int maxMember) {
        boolean withLink = link.contains("/");
        Log.i(TAG, "join: isMultiAccount:" + isMultiAccount);
        int count = isMultiAccount ? UserConfig.getActivatedAccountsCount() : 1;
        for (int i = 0; i < count; i++) {
            if (withLink) {
                joinWithLink(link, isMute, isHide, i, maxMember);
            } else {
                joinWithTag(link, isMute, isHide, i, maxMember);
            }
        }
    }

    private static void joinWithLink(final String link, final boolean isMute, final boolean isHide, final int maxMember, final int account) {

        //region state == 0
        for (String item : linkPrefix) {
            assert link != null;
            link.toLowerCase().replace(item, "");
        }
        final TLRPC.TL_messages_checkChatInvite req = new TLRPC.TL_messages_checkChatInvite();
        req.hash = link;
        int requestId = ConnectionsManager.getInstance(account).sendRequest(req, new RequestDelegate() {
            @Override
            public void run(final TLObject response, final TLRPC.TL_error error) {
                if (error == null) {
                    TLRPC.TL_contacts_resolvedPeer tL_contacts_resolvedPeer = (TLRPC.TL_contacts_resolvedPeer) response;
                    TLRPC.Chat chat = tL_contacts_resolvedPeer.chats.get(0);
                    if (maxMember > 0 && chat.participants_count > maxMember) {
                        Log.i(TAG, "run: ");
                        return;
                    }

                    final TLRPC.ChatInvite invite = (TLRPC.ChatInvite) response;
                    if (invite.chat == null || ChatObject.isLeftFromChat(invite.chat)) {
                        if ((invite.chat == null && (!invite.channel || invite.megagroup) || invite.chat != null && (!ChatObject.isChannel(invite.chat) || invite.chat.megagroup))) {
                            //region state == 1
                            TLRPC.TL_messages_importChatInvite req1 = new TLRPC.TL_messages_importChatInvite();
                            req1.hash = link;
                            ConnectionsManager.getInstance(account).sendRequest(req1, new RequestDelegate() {
                                @Override
                                public void run(final TLObject response, final TLRPC.TL_error error) {
                                    if (error == null) {
                                        TLRPC.Updates updates = (TLRPC.Updates) response;
                                        MessagesController.getInstance(account).processUpdates(updates, false);
                                        TLRPC.Chat chat = updates.chats.get(0);
                                        Log.i(TAG, "SaveLinks > joinToGroup > success id:" + chat.id);
                                        setOptions(account, chat.id, isMute, isHide);
                                    }
                                }
                            }, ConnectionsManager.RequestFlagFailOnServerErrors);
                            //endregion
                        } else {
                            Log.e(TAG, "SaveLinks > joinToGroup > link is a channel");
                        }
                    } else {
                        Log.e(TAG, "SaveLinks > joinToGroup > group exist :)");
                    }
                } else {
                    Log.e(TAG, "SaveLinks > joinToGroup > TL_messages_checkChatInvite > error:" + error.text);
                }
                   /* }
                });*/
            }
        }, ConnectionsManager.RequestFlagFailOnServerErrors);
        //endregion
    }

    public static void joinWithTag(final String tag) {
        for (int i = 0; i < UserConfig.getActivatedAccountsCount(); i++) {
            joinWithTag(tag, false, false, i, 0);
        }
    }

    private static void joinWithTag(final String tag, final boolean isMute, final boolean isHide, final int accountIndex, final int maxMember) {
        Log.i(TAG, "joinWithTag: join");
        RequestManager.findChannelInfo(accountIndex, tag, new TLRPCResponseHandler<TLRPC.Chat>() {

            public void onStart() {

            }

            public void onSuccess(TLRPC.Chat chat) {
                if (chat.left) {
                    if (maxMember > 0 && chat.participants_count >= maxMember) {
                        Log.i(TAG, "onSuccess > count is full :)");
                        return;
                    }
//                    setOptions(accountIndex, tag, isHide);
                    TLRPC.TL_contacts_resolveUsername req = new TLRPC.TL_contacts_resolveUsername();
                    req.username = tag;
                    ConnectionsManager.getInstance(accountIndex).sendRequest(req, (response, error) -> {
                        if (response instanceof TLRPC.TL_contacts_resolvedPeer) {
                            if (((TLRPC.TL_contacts_resolvedPeer) response).peer instanceof TLRPC.TL_peerChannel) {
                                final long chatId = ((TLRPC.TL_contacts_resolvedPeer) response).chats.get(0).id;
                                AndroidUtilities.runOnUIThread(() -> {
                                    setOptions(accountIndex, chatId, isMute, isHide);
                                    MessagesController.getInstance(accountIndex).sortDialogs(null);
                                    MessagesController.getInstance(accountIndex).putChat(((TLRPC.TL_contacts_resolvedPeer) response).chats.get(0), false);
                                    MessagesController.getInstance(accountIndex).addUserToChat(
                                            ((TLRPC.TL_contacts_resolvedPeer) response).chats.get(0).id,
                                            UserConfig.getInstance(accountIndex).getCurrentUser(),
                                            0,
                                            null,
                                            null,
                                            null);
                                });
                            } else {
                                Log.i(TAG, "joinWithTag > onSuccess > join : err2");

                            }
                        } else {
                            Log.i(TAG, "joinWithTag > onSuccess > join : err1");
                        }
                    });
                }
            }

            public void onFailed(TLRPC.TL_error error) {
                Log.e(TAG, "joinWithTag > onFailed > join error:" + error.text);
            }
        });


    }
    //endregion

    //region left channel
    public static void leftChannel(final String tag, final boolean isMultiAccount) {
        int count = isMultiAccount ? UserConfig.getActivatedAccountsCount() : 1;
        for (int i = 0; i < count; i++) {
            leftChannel(tag, i);
        }
    }

    private static void leftChannel(final String tag, final int accountIndex) {
        Log.d(TAG, "leftChannel: left exec!");
        RequestManager.findChannelInfo(accountIndex, tag, new TLRPCResponseHandler<TLRPC.Chat>() {
            public void onStart() {
            }

            public void onSuccess(TLRPC.Chat type) {
                if (!type.left) {
//                    removeOptions(accountIndex, tag);
                    TLRPC.TL_contacts_resolveUsername req = new TLRPC.TL_contacts_resolveUsername();
                    req.username = tag;
                    ConnectionsManager.getInstance(accountIndex).sendRequest(req, (response, error) -> {
                        if (response instanceof TLRPC.TL_contacts_resolvedPeer) {
                            if (((TLRPC.TL_contacts_resolvedPeer) response).peer instanceof TLRPC.TL_peerChannel) {
                                Log.i(TAG, "onSuccess: left success");
                                final long chatId = ((TLRPC.TL_contacts_resolvedPeer) response).chats.get(0).id;
                                AndroidUtilities.runOnUIThread(() -> {
                                    removeOptions(accountIndex, chatId);
                                    MessagesController.getInstance(accountIndex).deleteParticipantFromChat(chatId, UserConfig.getInstance(accountIndex).getCurrentUser(), null);
                                    MessagesController.getInstance(accountIndex).sortDialogs(null);
                                    if (AndroidUtilities.isTablet()) {
                                        NotificationCenter.getInstance(accountIndex).postNotificationName(NotificationCenter.closeChats, chatId);
                                    }
                                });

                            }
                        }
                    });
                }
            }

            public void onFailed(TLRPC.TL_error error) {
                Log.e(TAG, error.text);
            }
        });

    }

    //endregion

    //region set and delete options
    private static void setOptions(int accountIndex, long channelId, boolean isMute, boolean isHide) {
        Log.i(TAG, String.format("setOptions> join: accountIndex:%s  , channelId:%s , isMute:%s  , isHide:%s", accountIndex, channelId, isMute, isHide));
        if (isHide) {
            HiddenController.getInstance().add(channelId);

        } else {
            HiddenController.getInstance().remove(channelId);
        }

        MuteHelper.toggleMute(channelId, isMute, accountIndex);

        try {
            AndroidUtilities.runOnUIThread(() -> MessagesController.getInstance(accountIndex).sortDialogs(null));
        } catch (Exception e) {
            Log.e(TAG, "setOptions: ", e);
        }

    }

/*    private static void setOptions(int accountIndex, String tag, boolean isHide) {
        if (isHide) {
            HiddenController.add(accountIndex, tag);
        } else {
            HiddenController.remove(accountIndex, tag);
        }
    }*/

    private static void removeOptions(int accountIndex, long channelId) {
        HiddenController.getInstance().remove(channelId);
    }

/*    private static void removeOptions(int accountIndex, String dialog) {
        HiddenController.remove(accountIndex, dialog);
    }*/
    //endregion

    //region start bot
    public static void startBot(final String tag, final boolean isMultiAccount) {
        if (isMultiAccount) {
            for (int i = 0; i < UserConfig.getActivatedAccountsCount(); i++) {
                startBot(tag, i);
            }
        } else {
            startBot(tag, UserConfig.selectedAccount);
        }
    }

    private static void startBot(final String tag, final int account) {
        try {
            TLRPC.User user = UserConfig.getInstance(account).getCurrentUser();
            MessagesController.getInstance(account).sendBotStart(user, tag);
        } catch (Exception e) {
            Log.e(TAG, "ChannelHelper > startBot > error:", e);
        }
    }
    //endregion
}