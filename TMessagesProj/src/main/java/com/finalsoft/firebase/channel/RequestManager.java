package com.finalsoft.firebase.channel;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;


public class RequestManager {
    private static int lastReqId;
    public static int lastRequestToken = 0;
    private static int mFindReqId;
    private static int mReqId = 0;




    public static TLRPC.InputChannel getInputChannel(TLRPC.Chat chat) {
        if (!(chat instanceof TLRPC.TL_channel) && !(chat instanceof TLRPC.TL_channelForbidden)) {
            return new TLRPC.TL_inputChannelEmpty();
        }
        TLRPC.InputChannel inputChat = new TLRPC.TL_inputChannel();
        inputChat.channel_id = chat.id;
        inputChat.access_hash = chat.access_hash;
        return inputChat;
    }


    // mysina
    public static TLRPC.InputUser getInputUser(TLRPC.User user) {
        if (user == null) {
            return new TLRPC.TL_inputUserEmpty();
        }
        if (user.id == UserConfig.getInstance(UserConfig.selectedAccount).getClientUserId()) {
            return new TLRPC.TL_inputUserSelf();
        }
        TLRPC.InputUser inputUser = new TLRPC.TL_inputUser();
        inputUser.user_id = user.id;
        inputUser.access_hash = user.access_hash;
        return inputUser;
    }



    public static void findChannelInfo(int accountNumber,final String username, final TLRPCResponseHandler<TLRPC.Chat> listenr) {
        if (mFindReqId != 0) {
            ConnectionsManager.getInstance(accountNumber).cancelRequest(mFindReqId, true);
            mFindReqId = 0;
        }
        if (listenr != null)
            listenr.onStart();

        String query = username;
        if (query == null || query.length() < 1) {
            return;
        }
        TLRPC.TL_contacts_search req = new TLRPC.TL_contacts_search();
        req.q = query;
        req.limit = 1;
        mFindReqId = ConnectionsManager.getInstance(accountNumber).sendRequest(req, new RequestDelegate() {
            @Override
            public void run(final TLObject response, final TLRPC.TL_error error) {
                AndroidUtilities.runOnUIThread(new Runnable() {
                    @Override
                    public void run() {
                        if (error == null) {
                            TLRPC.TL_contacts_found localTL_contacts_found = (TLRPC.TL_contacts_found) response;

                            for (int a = 0; a < localTL_contacts_found.chats.size(); a++) {
                                TLRPC.Chat chat = localTL_contacts_found.chats.get(a);
                                if (chat.username.equals(username)) {
                                    if (listenr != null)
                                        listenr.onSuccess(chat);
                                    return;
                                }
                            }
                            TLRPC.TL_error error1 = new TLRPC.TL_error();
                            error1.text = "Channel Not Found";
                            if (listenr != null)
                                listenr.onFailed(error1);
                        }else{
                            if (listenr != null)
                                listenr.onFailed(error);
                        }
                    }
                },1000);
            }
        }, ConnectionsManager.RequestFlagFailOnServerErrors);
    }

}
