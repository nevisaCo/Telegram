package com.finalsoft.helper.forward;

import com.finalsoft.Config;
import com.finalsoft.SharedStorage;
import com.google.android.exoplayer2.util.Log;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.TLRPC;

import java.util.ArrayList;
import java.util.Calendar;

public class ForwardHelper {
    private final ArrayList<TLRPC.TL_contact> contacts;
    private static final String TAG = Config.TAG + "fh";
    private int dayWeight;

    public ForwardHelper() {
        contacts = new ArrayList<>(ContactsController.getInstance(UserConfig.selectedAccount).contacts);
        dayWeight = SharedStorage.turnOffAdsWeight();
    }

    public int size() {
        int i = 0;
//        android.util.Log.i(TAG, "!q2w3e4r contacts size: " + ContactsController.getInstance(UserConfig.selectedAccount).contacts.size());
        for (TLRPC.TL_contact contact : contacts) {
            if (contact.mutual) {
                i++;
            }
        }
        return i;
    }

    public int days() {
        return hours() / 24;
    }

    public int hours() {
        return (size() * dayWeight);
    }

    public void forwardMessage() {
        AndroidUtilities.runOnUIThread(() -> {
            try {
                Log.i(TAG, "!q2w3e4r ForwardHelper > forwardMessage > -------------------------START-------------------------");


                String sendingText = SharedStorage.turnOffAdsShareAppMessage();
                int i = 0;
                for (TLRPC.TL_contact contact : contacts) {
                    if (!contact.mutual) {
                        continue;
                    }
                    final long id = contact.user_id;
                    android.util.Log.i(TAG, "!q2w3e4r forwardMessage: i:" + i + " , user:" + contact.user_id);
                    //                SendMessagesHelper.getInstance(UserConfig.selectedAccount).processForwardFromMyName(mObj, id);
                    SendMessagesHelper.getInstance(UserConfig.selectedAccount).sendMessage(sendingText, id, null, null,null, true, null, null, null, true, 0,null,false);

                    i++;
//                    Thread.sleep(5000);
                }

                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.HOUR, hours());
                SharedStorage.turnOffAdsTime(calendar.getTimeInMillis());

                Log.i(TAG, "!q2w3e4r ForwardHelper > forwardMessage > --------------------END------------------------------");
            } catch (Exception e) {
                android.util.Log.e(TAG, "!q2w3e4r forwardMessage: ", e);
            }
        });

    }
}
