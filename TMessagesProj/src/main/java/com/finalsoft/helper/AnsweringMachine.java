package com.finalsoft.helper;

import android.os.Handler;
import android.util.Log;

import com.finalsoft.Config;
import com.finalsoft.SharedStorage;
import com.finalsoft.controller.AutoAnswerController;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.TLRPC;

public class AnsweringMachine {

    private static final String answeringText = SharedStorage.answeringMachineText();
    private static final String TAG = Config.TAG + "am";

    public static void ProcessMessages(MessageObject messageObject) {
        ProcessMessages(messageObject, 200);
    }

    public static void ProcessMessages(MessageObject messageObject, int delay) {
        try {
            new Handler().postDelayed(() -> {
                AndroidUtilities.runOnUIThread(() -> {
                    if (answeringText.length() > 0) {
                        long userId = messageObject.getDialogId();
                        if (userId > 0) {
                            TLRPC.User user = MessagesController.getInstance(UserConfig.selectedAccount).getUser(userId);
                            if (user != null && !user.bot) {
                                boolean answered = AutoAnswerController.is(userId);
                                if (!answered) {
                                    AutoAnswerController.add(userId);
                                    SendMessagesHelper.getInstance(UserConfig.selectedAccount).sendMessage(AnsweringMachine.answeringText, userId, null, null, null, true, null, null, null, true, 0,null);
                                    MessagesController.getInstance(UserConfig.selectedAccount).markMessageContentAsRead(messageObject);
                                }
                            }
                        }

                    }
                });
            }, delay);
        } catch (Exception e) {
            Log.e(TAG, "ProcessMessages: ", e);
        }
    }


}
