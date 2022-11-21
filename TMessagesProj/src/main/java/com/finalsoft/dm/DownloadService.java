package com.finalsoft.dm;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import org.telegram.SQLite.SQLiteCursor;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.DownloadController;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.WebFile;
import org.telegram.tgnet.NativeByteBuffer;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;

public class DownloadService extends Service implements DownloadController.FileDownloadProgressListener {

    private int currentAccount = UserConfig.selectedAccount;
    private ArrayList<MessageObject> messageObjects = new ArrayList<>();

    @Override
    public void onCreate() {
        super.onCreate();

        messageObjects.addAll(DM_LoadMessages());
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startDownloading(messageObjects);
        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        cancelDownloading();
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void onFailedDownload(String fileName, boolean canceled) {

    }

    @Override
    public void onSuccessDownload(String fileName) {
        startDownloading(messageObjects);
    }

    @Override
    public void onProgressDownload(String fileName, long downloadedSize, long totalSize) {

    }

    @Override
    public void onProgressUpload(String fileName, long uploadedSize, long totalSize, boolean isEncrypted) {

    }

    @Override
    public int getObserverTag() {
        return 0;
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    public TLObject getDownloadObject(MessageObject messageObject) {
        TLRPC.MessageMedia media = messageObject.messageOwner.media;
        if (media != null) {
            if (media.document != null) {
                return media.document;
            }
            if (media.webpage != null && media.webpage.document != null) {
                return media.webpage.document;
            }
            if (media.webpage != null && media.webpage.photo != null) {
                return FileLoader.getClosestPhotoSizeWithSize(media.webpage.photo.sizes, AndroidUtilities.getPhotoSize());
            }
            if (media.photo != null) {
                return FileLoader.getClosestPhotoSizeWithSize(media.photo.sizes, AndroidUtilities.getPhotoSize());
            }
        }
        return new TLRPC.TL_messageMediaEmpty();
    }

    private void loadFile(TLObject attach, MessageObject messageObject) {
        if (attach instanceof TLRPC.PhotoSize) {
            FileLoader.getInstance(currentAccount).loadFile(ImageLocation.getForPhoto((TLRPC.PhotoSize) attach, messageObject.messageOwner.media.photo), messageObject, null, 0, 0);
        } else if (attach instanceof TLRPC.Document) {
            FileLoader.getInstance(currentAccount).loadFile((TLRPC.Document)attach, messageObject, 0, 0);
        } else if (attach instanceof WebFile) {
            FileLoader.getInstance(currentAccount).loadFile((WebFile)attach, 0, 0);
        }
    }

    private void startDownloading(ArrayList<MessageObject> messageObjects) {
        for (MessageObject messageObject : messageObjects) {
            TLObject attach = getDownloadObject(messageObject);
            loadFile(attach, messageObject);
            File pathToMessage = FileLoader.getInstance(currentAccount).getPathToMessage(messageObject.messageOwner);
            if (pathToMessage != null && !pathToMessage.exists()) {
                DownloadController.getInstance(currentAccount).addLoadingFileObserver(FileLoader.getAttachFileName(attach), DownloadService.this);
                return;
            }
        }
    }

    private void cancelDownloading() {
        for (int i = 0; i < messageObjects.size(); i++) {
            MessageObject messageObject = messageObjects.get(i);
            if (messageObject != null) {
                TLObject attach = getDownloadObject(messageObject);
                if (attach instanceof TLRPC.PhotoSize) {
                    FileLoader.getInstance(currentAccount).cancelLoadFile((TLRPC.PhotoSize)attach);
                } else if (attach instanceof TLRPC.Document) {
                    FileLoader.getInstance(currentAccount).cancelLoadFile((TLRPC.Document)attach);
                }
            }
        };
    }

    public ArrayList<MessageObject> DM_LoadMessages() {
        final ArrayList<MessageObject> objects = new ArrayList<>();
        MessagesStorage.getInstance(currentAccount).getStorageQueue().postRunnable(new Runnable() {
            @Override
            public void run() {
                final TLRPC.TL_messages_messages res = new TLRPC.TL_messages_messages();
                SQLiteCursor cursor = null;
                try {
                    cursor = MessagesStorage.getInstance(currentAccount).getDatabase().queryFinalized(String.format(Locale.US, "SELECT * FROM my_idm ORDER BY date DESC"));
                    while (cursor.next()) {
                        NativeByteBuffer data = cursor.byteBufferValue(3);
                        if (data != null) {
                            TLRPC.Message message = TLRPC.Message.TLdeserialize(data, data.readInt32(false), false);
                            data.reuse();
                            message.id = cursor.intValue(0);
                            message.dialog_id = cursor.intValue(1);
                            message.date = cursor.intValue(2);

                            res.messages.add(message);
                        }
                    }
                } catch (Exception e) {
                    FileLog.e("tmessages", e);
                } finally {
                    if (cursor != null) {
                        cursor.dispose();
                    }
                }

                final ConcurrentHashMap<Long, TLRPC.User> usersDict = new ConcurrentHashMap<>();
                final ConcurrentHashMap<Long, TLRPC.Chat> chatsDict = new ConcurrentHashMap<>();
                for (int a = 0; a < res.users.size(); a++) {
                    TLRPC.User u = res.users.get(a);
                    usersDict.put(u.id, u);
                }
                for (int a = 0; a < res.chats.size(); a++) {
                    TLRPC.Chat c = res.chats.get(a);
                    chatsDict.put(c.id, c);
                }
                for (int a = 0; a < res.messages.size(); a++) {
                    TLRPC.Message message = res.messages.get(a);
                    MessageObject messageObject = new MessageObject(currentAccount, message, usersDict, chatsDict, true,false);
                    objects.add(messageObject);
                }
            }
        });
        return objects;
    }
}
