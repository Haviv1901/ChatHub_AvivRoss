package com.example.chathub;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.chathub.Activities.ChatActivity;
import com.example.chathub.Data_Containers.Chat;
import com.example.chathub.Data_Containers.Message;
import com.example.chathub.Data_Containers.TextMessage;
import com.example.chathub.Managers.ChatManager;
import com.example.chathub.Managers.UserManager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class NotificationService  extends Service
{

    // managers
    private ChatManager chatManager;
    private UserManager userManager;

    // consts
    public static final String CHANNEL_ID = "notifications_channel";
    public static final int NOTIFICATION_ID = 1;
    private static final String TAG = "NotificationService";

    @Override
    public void onCreate() {
        super.onCreate();
        // This is the place where you can setup listeners for your chats
        chatManager = new ChatManager(this);
        userManager = new UserManager(this);
        setupChannel();
        getChatsFromFirebase();
        Log.e(TAG, "Notification listener started.");
    }

    private void getChatsFromFirebase()
    {
        chatManager.setupChatsUserParticipateInListener(this::setupChatListeners);

    }

    private void setupChatListeners(List<Chat> chats)
    {

        for (Chat chat : chats)
        {
            listenToChat(chat.getChatName());
        }

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // The service is starting, due to a call to startService()
        // Here you can extract any data from the Intent, if needed
        return START_STICKY; // Service will be restarted if it gets terminated
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // The service is no longer used and is being destroyed
        // Here you can cleanup any resources like listeners, threads etc.
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        // Return the communication channel to the service.
        // If you don't want to allow binding, return null
        return null;
    }


    private void listenToChat(String chatName)
    {
        /// this method will set up a listener for the messages in the chat
        FirebaseDatabase.getInstance().getReference("Chats").child(chatName).child("Messages").addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                sendNotification(chatManager.retrieveLastMessageFromSnapshot(dataSnapshot), chatName);
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {
            }
        });

    }

    private void setupChannel()
    {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is not in the Support Library.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            CharSequence name = "Channel Name";
            String description = "Channel Description";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this.
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void sendNotification(Message message, String chatName)
    {
        if(!checkConditionsToSendNotification(message, chatName))
        {
            return;
        }
        Log.e(TAG, "Sending notification for chat: " + chatName);

        // set title
        String title = "New message in " + chatName;
        // set data
        String messageData = "";
        if(message instanceof TextMessage)
        {
            TextMessage textMessage = (TextMessage) message;
            // take first 10 charts of textMessage.getContent(), add ...
            if(textMessage.getContent().length() > 10)
                messageData = textMessage.getSender() + " Sent: " + textMessage.getContent().substring(0, 10) + "...";
            else
                messageData =  textMessage.getSender() + " Sent: " + textMessage.getContent();
        }
        else
        {
            messageData = message.getSender() + " Sent a voice message";
        }


        // send notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.addchaticon)
                .setContentTitle(title)
                .setContentText(messageData)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED)
        {
            return;
        }
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }

    private boolean checkConditionsToSendNotification(Message message, String chatName)
    {
        // if user is not init yet, dont send any notifications
        if(userManager.getCurrentUsername() == null || userManager.getCurrentUsername().isEmpty())
        {
            return false;
        }
        // if message is null do not send notification
        if(message == null)
        {
            return false;
        }
        // if the last message is the logged user, do not send notification.
        if(message.getSender().equals(userManager.getCurrentUsername()))
        {
            return false;
        }
        // if the message is from the chat the user currently on, do not send notification
        if(ChatActivity.chatNameForNotifications.equals(chatName))
        {
            return false;
        }

        return true;


    }
}