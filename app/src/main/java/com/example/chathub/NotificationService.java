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

    /*
    * service to handle notifications.
    * */

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

    /**
     * Function: getChatsFromFirebase
     * Input: None
     * Output: None
     * Description: This function will get all the chats the user is participating in from firebase
     */
    private void getChatsFromFirebase()
    {
        chatManager.setupChatsUserParticipateInListener(this::setupChatListeners);

    }

    /**
     * Function: setupChatListeners
     * Input: List<Chat> chats - the chats the user is participating in
     * Output: None
     * Description: This function will set up listeners for the chats the user is participating in
     */
    private void setupChatListeners(List<Chat> chats)
    {

        for (Chat chat : chats)
        {
            listenToChat(chat.getChatName());
        }

    }

    /**
     * Function: onStartCommand
     * Input: Intent intent - the intent that started the service, int flags - flags for the service, int startId - the id of the service
     * Output: int - the return value for the service
     * Description: This function is called when the service is started. It sets up the service to be restarted if it gets terminated.
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // The service is starting, due to a call to startService()
        // Here you can extract any data from the Intent, if needed
        return START_STICKY; // Service will be restarted if it gets terminated
    }

    /**
     * Function: onDestroy
     * Input: None
     * Output: None
     * Description: This function is called when the service is destroyed. It is used to clean up any resources used by the service.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        // The service is no longer used and is being destroyed
        // Here you can cleanup any resources like listeners, threads etc.
    }

    /*
    * Function: onBind
    * Input: Intent intent - the intent that started the service
    * Output: IBinder - the communication channel to the service\
    * Description: This function is called when a client wants to bind to the service. It returns the communication channel to the service.
    * */
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        // Return the communication channel to the service.
        // If you don't want to allow binding, return null
        return null;
    }

    /*
    * Function: listenToChat
    * Input: String chatName - the name of the chat
    * Output: void
    * Description: This function will set up a listener for the messages in the chat
    * */
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

    /*
    * Function: setupChannel
    * Input: None
    * Output: void
    * Description: This function sets up the notification channel for the service
    * */
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

    /*
    * Function: sendNotification
    * Input: Message message - the message to send, String chatName - the name of the chat
    * Output: void
    * Description: This function sends a notification to the user
     */
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

    /*
    * Function: checkConditionsToSendNotification
    * Input: Message message - the message to check, String chatName - the name of the chat
    * Output: boolean - true if the conditions are met, false otherwise
    * Description: This function checks if the conditions are met
     */
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