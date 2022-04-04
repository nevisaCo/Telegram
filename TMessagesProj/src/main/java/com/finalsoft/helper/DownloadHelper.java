package com.finalsoft.helper;

import android.util.Log;

import com.finalsoft.Config;

import org.telegram.SQLite.SQLitePreparedStatement;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.NativeByteBuffer;
import org.telegram.tgnet.TLRPC;

import java.io.File;
import java.util.ArrayList;

public class DownloadHelper {
    private final String TAG = Config.TAG + "dm";

    public enum TimeKeys {
        START_HOURS,
        START_MINUTES,
        END_HOURS,
        END_MINUTES,
    }

    public static boolean canAddToQueue(MessageObject messageObject) {
        if (messageObject.isSticker()) {
            return false;
        }
        if (messageObject.getDocument() != null || messageObject.messageOwner.media instanceof TLRPC.TL_messageMediaPhoto || messageObject.isMusic() || messageObject.isVideo()) {
            boolean canSave = false;
            if (messageObject.messageOwner.attachPath != null && messageObject.messageOwner.attachPath.length() != 0) {
                File f = new File(messageObject.messageOwner.attachPath);
                if (f.exists()) {
                    canSave = true;
                }
            }
            if (!canSave) {
                File f = FileLoader.getPathToMessage(messageObject.messageOwner);
                if (f.exists()) {
                    canSave = true;
                }
            }
            if (!canSave) {
                return true;
            }
        }
        return false;
    }

    public void addToQueue(final ArrayList<TLRPC.Message> messages) {
        int currentAccount = UserConfig.selectedAccount;
        MessagesStorage.getInstance(currentAccount).getStorageQueue().postRunnable(() -> {
            try {
                MessagesStorage.getInstance(currentAccount).getDatabase().beginTransaction();
                SQLitePreparedStatement state = MessagesStorage.getInstance(currentAccount).getDatabase().executeFast("REPLACE INTO my_idm VALUES(?, ?, ?, ?)");
                for (int a = 0; a < messages.size(); a++) {
                    TLRPC.Message message = messages.get(a);

                    state.requery();
                    state.bindLong(1, message.id);
                    state.bindLong(2, message.dialog_id);
                    state.bindInteger(3, ConnectionsManager.getInstance(currentAccount).getCurrentTime());
                    NativeByteBuffer data = new NativeByteBuffer(message.getObjectSize());
                    message.serializeToStream(data);
                    state.bindByteBuffer(4, data);
                    state.step();

                    data.reuse();
                }
                state.dispose();
                MessagesStorage.getInstance(currentAccount).getDatabase().commitTransaction();
            } catch (Exception e) {
                Log.e(TAG, "addToQueue: ", e);
                FileLog.e("tmessages", e);
            }
        });
    }

    public enum Modules {
        JUST_TODAY, ENABLED_WIFI, DISABLED_WIFI, MESSAGE_MENU_ICON, RUNNING, RECEIVER

    }

}

