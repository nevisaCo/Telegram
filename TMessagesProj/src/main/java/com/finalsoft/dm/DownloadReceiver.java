package com.finalsoft.dm;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.os.SystemClock;

import androidx.legacy.content.WakefulBroadcastReceiver;
import androidx.recyclerview.widget.ItemTouchHelper;

import com.finalsoft.SharedStorage;
import com.finalsoft.helper.DownloadHelper;

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
import java.util.Calendar;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;


public class DownloadReceiver extends WakefulBroadcastReceiver
        implements DownloadController.FileDownloadProgressListener {

    private int currentAccount = UserConfig.selectedAccount;
    private AlarmManager mAlarmManager;
    private PendingIntent mPendingIntent;
    private PendingIntent mPendingIntent_end;
    private ArrayList<MessageObject> messageObjects;
    private PowerManager.WakeLock wakeLock;

    public DownloadReceiver() {
        messageObjects = DM_LoadMessages();
    }

    public void onReceive(Context context, Intent intent) {
        if (!SharedStorage.downloadModule(DownloadHelper.Modules.RUNNING)) {
            return;
        }
//        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        int intExtra = intent.getIntExtra("start_end", 0);

        if (intExtra == 1000) {
            if (SharedStorage.downloadModule(DownloadHelper.Modules.ENABLED_WIFI)) {
                SharedStorage.downloadModule(DownloadHelper.Modules.ENABLED_WIFI, true);
            }
            acquire(context);

            startDownloading(messageObjects);
        } else {
            if (SharedStorage.downloadModule(DownloadHelper.Modules.DISABLED_WIFI)) {
                SharedStorage.downloadModule(DownloadHelper.Modules.DISABLED_WIFI, false);
            }

            stopDownloading(messageObjects);

            release();
        }
    }

    public void setAlarm(Context context, Calendar calendar, Calendar calendar2) {
        Intent intent = new Intent(context, DownloadReceiver.class);
        intent.putExtra("Reminder_ID", 100);
        intent.putExtra("start_end", 1000);
        mPendingIntent = PendingIntent.getBroadcast(context, 100, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        mAlarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        assert mAlarmManager != null;
        mAlarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, (calendar.getTimeInMillis() - Calendar.getInstance().getTimeInMillis()) + SystemClock.elapsedRealtime(), mPendingIntent);

        Intent intent2 = new Intent(context, DownloadReceiver.class);
        intent.putExtra("Reminder_ID", ItemTouchHelper.Callback.DEFAULT_DRAG_ANIMATION_DURATION);
        intent.putExtra("start_end", 900);
        mPendingIntent_end = PendingIntent.getBroadcast(context, ItemTouchHelper.Callback.DEFAULT_DRAG_ANIMATION_DURATION, intent2, PendingIntent.FLAG_UPDATE_CURRENT);
        mAlarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, (calendar2.getTimeInMillis() - Calendar.getInstance().getTimeInMillis()) + SystemClock.elapsedRealtime(), mPendingIntent_end);
    }

    public void setRepeatAlarm(Context context, Calendar calendar, Calendar calendar2, int i) {
        Intent intent = new Intent(context, DownloadReceiver.class);
        intent.putExtra("start_end", 1000);
        mPendingIntent = PendingIntent.getBroadcast(context, i, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        mAlarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        assert mAlarmManager != null;
        mAlarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, (calendar.getTimeInMillis() - Calendar.getInstance().getTimeInMillis()) + SystemClock.elapsedRealtime(), 604800000, mPendingIntent);

        intent = new Intent(context, DownloadReceiver.class);
        intent.putExtra("start_end", 900);
        mPendingIntent_end = PendingIntent.getBroadcast(context, i + 10, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        mAlarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, (calendar2.getTimeInMillis() - Calendar.getInstance().getTimeInMillis()) + SystemClock.elapsedRealtime(), 604800000, mPendingIntent_end);
    }

    public void cancelAlarm(Context context) {
        mAlarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        mPendingIntent = PendingIntent.getBroadcast(context, 100, new Intent(context, DownloadReceiver.class), 0);
        mAlarmManager.cancel(mPendingIntent);
        mPendingIntent_end = PendingIntent.getBroadcast(context, ItemTouchHelper.Callback.DEFAULT_DRAG_ANIMATION_DURATION, new Intent(context, DownloadReceiver.class), 0);
        mAlarmManager.cancel(mPendingIntent_end);
        for (int i = 1; i < 8; i++) {
            mPendingIntent = PendingIntent.getBroadcast(context, i + 300, new Intent(context, DownloadReceiver.class), 0);
            mAlarmManager.cancel(mPendingIntent);
            mPendingIntent_end = PendingIntent.getBroadcast(context, (i + 300) + 10, new Intent(context, DownloadReceiver.class), 0);
            mAlarmManager.cancel(mPendingIntent_end);
        }
    }

    @SuppressLint("InvalidWakeLockTag")
    public void acquire(Context context) {
        if (wakeLock != null) {
            wakeLock.release();
        }
        wakeLock = ((PowerManager) Objects.requireNonNull(context.getSystemService(Context.POWER_SERVICE))).newWakeLock(1, "MyWakelockTag");
        wakeLock.acquire(10*60*1000L /*10 minutes*/);
    }

    public void release() {
        if (wakeLock != null) {
            wakeLock.release();
        }
        wakeLock = null;
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
            FileLoader.getInstance(currentAccount).loadFile((TLRPC.Document) attach, messageObject, 0, 0);
        } else if (attach instanceof WebFile) {
            FileLoader.getInstance(currentAccount).loadFile((WebFile) attach, 0, 0);
        }
    }

    private void startDownloading(final ArrayList<MessageObject> messageObjects) {
        MessagesStorage.getInstance(currentAccount).getStorageQueue().postRunnable(() ->
                AndroidUtilities.runOnUIThread(() -> {
                    SharedStorage.downloadModule(DownloadHelper.Modules.RUNNING, true);
                    for (MessageObject messageObject : messageObjects) {
                        TLObject attach = getDownloadObject(messageObject);
                        loadFile(attach, messageObject);
                        File pathToMessage = FileLoader.getInstance(currentAccount).getPathToMessage(messageObject.messageOwner);
                        if (pathToMessage != null && !pathToMessage.exists()) {
                            DownloadController.getInstance(currentAccount).addLoadingFileObserver(FileLoader.getAttachFileName(attach), DownloadReceiver.this);
                            return;
                        }
                    }
                }));
    }

    private void stopDownloading(ArrayList<MessageObject> messageObjects) {
        SharedStorage.downloadModule(DownloadHelper.Modules.RUNNING, false);
        for (int i = 0; i < messageObjects.size(); i++) {
            MessageObject messageObject = messageObjects.get(i);
            if (messageObject != null) {
                TLObject attach = getDownloadObject(messageObject);
                if (attach instanceof TLRPC.PhotoSize) {
                    FileLoader.getInstance(currentAccount).cancelLoadFile((TLRPC.PhotoSize) attach);
                } else if (attach instanceof TLRPC.Document) {
                    FileLoader.getInstance(currentAccount).cancelLoadFile((TLRPC.Document) attach);
                }
            }
        }


    }

    public ArrayList<MessageObject> DM_LoadMessages() {
        final ArrayList<MessageObject> objects = new ArrayList<>();
        MessagesStorage.getInstance(currentAccount).getStorageQueue().postRunnable(() -> {
            final TLRPC.TL_messages_messages res = new TLRPC.TL_messages_messages();
            SQLiteCursor cursor = null;
            try {
                cursor = MessagesStorage.getInstance(currentAccount).getDatabase().queryFinalized("SELECT * FROM my_idm ORDER BY date DESC");
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
        });
        return objects;
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
}
