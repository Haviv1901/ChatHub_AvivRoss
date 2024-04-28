package com.example.chathub;

import android.Manifest;
import android.app.Activity;
import android.app.NotificationChannel;
import android.content.Context;
import android.app.NotificationManager;
import android.content.pm.PackageManager;
import android.os.Build;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import com.example.chathub.Data_Containers.Message;

public class Notifications
{
    NotificationCompat.Builder builder;
    NotificationManager notificationManager;

    public Notifications(Context context)
    {
        builder = new NotificationCompat.Builder(context, "test")
                .setSmallIcon(R.drawable.notifications_icon)
                .setContentTitle(context.getString(R.string.app_name))
                .setContentText("Example Notification")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        notificationManager = context.getSystemService(NotificationManager.class);
    }

    public void messageNotification(Message msg, Context context) {
        builder.setContentText(msg.getContent()); // Set the content of the notification to the content of the message

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) 
        {
          //  ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
        else
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            {
                CharSequence name = context.getString(R.string.app_name);
                String description = "Example Notification";
                int importance = NotificationManager.IMPORTANCE_DEFAULT;
                NotificationChannel channel = new NotificationChannel("test", name, importance);
                channel.setDescription(description);
                notificationManager.createNotificationChannel(channel);
            }

            // Post the notification
            notificationManager.notify(10, builder.build());
        }
    }
}