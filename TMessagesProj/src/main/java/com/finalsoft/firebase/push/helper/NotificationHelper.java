package com.finalsoft.firebase.push.helper;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.provider.Settings;
import androidx.core.app.NotificationCompat;
import androidx.core.app.TaskStackBuilder;

import org.json.JSONObject;
import org.telegram.messenger.R;
import org.telegram.ui.DialogsActivity;
import org.telegram.ui.LaunchActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static android.content.Context.NOTIFICATION_SERVICE;

public class NotificationHelper {

    private Context mContext;
    private NotificationManager mNotificationManager;
    private NotificationCompat.Builder mBuilder;
    public static final String NOTIFICATION_CHANNEL_ID = "10001";

    public NotificationHelper(Context context) {
        mContext = context;
    }

    /**
     * Create and push the notification 
     */
/*
    public void createNotification(String title, String message)
    {    
        */
/**Creates an explicit intent for an Activity in your app**//*

        Intent resultIntent = new Intent(mContext , LaunchActivity.class);
        resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        
        PendingIntent resultPendingIntent = PendingIntent.getActivity(mContext,
                0 */
/* Request code *//*
, resultIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        mBuilder = new NotificationCompat.Builder(mContext);
        mBuilder.setSmallIcon(R.drawable.ic_launcher);
        mBuilder.setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                .setContentIntent(resultPendingIntent);

        mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O)
        {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "NOTIFICATION_CHANNEL_NAME", importance);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            assert mNotificationManager != null;
           // mBuilder.setChannelId(NOTIFICATION_CHANNEL_ID);
            mNotificationManager.createNotificationChannel(notificationChannel);
        }
        assert mNotificationManager != null;
        mNotificationManager.notify(0 */
/* Request Code *//*
, mBuilder.build());
    }
*/
public void show(JSONObject o) {
        try {
            String title = o.getString("title");
            String message = o.getString("content");

            NotificationManager notificationManager =
                    (NotificationManager) mContext.getSystemService(NOTIFICATION_SERVICE);

            int notificationId = createID();
            String channelId = "channel-id";
            String channelName = "Channel Name";

            int importance = 0;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                importance = NotificationManager.IMPORTANCE_HIGH;
            }

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                NotificationChannel mChannel = new NotificationChannel(
                        channelId, channelName, importance);
                notificationManager.createNotificationChannel(mChannel);
            }

            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(mContext, channelId)
                            .setSmallIcon(R.mipmap.ic_launcher)//R.mipmap.ic_launcher
                            .setContentTitle(title)
                            .setContentText(message)
                            .setVibrate(new long[]{100, 250})
                            .setLights(Color.GREEN, 500, 5000)
                            .setAutoCancel(true);
            //.setColor(getApplicationContext().getColor(getApplication(), R.color.colorPrimary));

            TaskStackBuilder stackBuilder = TaskStackBuilder.create(mContext);
            stackBuilder.addNextIntent(new Intent(mContext, DialogsActivity.class));
            PendingIntent resultPendingIntent =
                    stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
            mBuilder.setContentIntent(resultPendingIntent);

            notificationManager.notify(notificationId, mBuilder.build());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int createID() {
        Date now = new Date();
        return Integer.parseInt(new SimpleDateFormat("ddHHmmss", Locale.FRENCH).format(now));
    }
}