package com.example.chathub;

import android.Manifest;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.PendingIntent;
import android.content.Context;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.chathub.Activities.MainActivity;
import com.example.chathub.Data_Containers.Message;

public class Notifications {
    NotificationCompat.Builder builder;
    NotificationManager notificationManager;

    public static final String CHANNEL_ID = "notifications_channel";
    public static final int NOTIFICATION_ID = 1;

    public Notifications(Context context)
    {
    }

    public void sendNotification(Message msg, Context context)
    {

        // Build the notification
//        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
//                .setSmallIcon(R.drawable.notifications_icon)
//                .setContentTitle(msg.getSender())
//                .setContentText(msg.getContent())
//                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
//                .setAutoCancel(true);

        // Show the notification
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }

    public void setupChannel(Context context)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            CharSequence name = "my_channel";
            String description = "This is my channel";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}