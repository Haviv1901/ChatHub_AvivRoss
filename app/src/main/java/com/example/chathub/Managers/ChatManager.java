package com.example.chathub.Managers;

import android.graphics.Bitmap;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.chathub.Data_Containers.Chat;
import com.example.chathub.Data_Containers.Message;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ChatManager
{
    private DatabaseReference chatsHandler;
    private int currentChatId;
    private String currentChatName;
    private int lastMessageId;
    private FirebaseStorage sotrageHandler;

    public ChatManager()
    {
        chatsHandler = FirebaseDatabase.getInstance().getReference("Chats");
        sotrageHandler = FirebaseStorage.getInstance();
        currentChatId = -1;
        currentChatName = "";
    }

    public ChatManager(String currentChatName, int currentChatId)
    {
        chatsHandler = FirebaseDatabase.getInstance().getReference("Chats");
        sotrageHandler = FirebaseStorage.getInstance();
        this.currentChatId = currentChatId;
        this.currentChatName = currentChatName;
        setListenerToMessageId();
    }

    public DatabaseReference getChatsHandler()
    {
        return chatsHandler;
    }

    public String getChatName()
    {
        return currentChatName;
    }

    private String getTime()
    {
        LocalTime now = LocalTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        return now.format(formatter);
    }
    public void sendMessage(String context, Bitmap image, String username)
    {
        // create a byte array of the image
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.PNG, 0, outputStream);
        byte[] byteArray = outputStream.toByteArray();


        String imagePath = "images/" + UUID.randomUUID().toString() + ".png";

        // upload the image to the storage
        UploadTask uploadTask = sotrageHandler.getReference(imagePath).putBytes(byteArray);

        sendMessage(context, imagePath, username);
    }
    public void sendMessage(String context, String username)
    {
        sendMessage(context, "", username);
    }
    public void sendMessage(String context, String imagePath, String username)
    {
        // create a new message object
        String time =  getTime();
        Message newMessage = new Message(username, context, imagePath, time, lastMessageId);
        sendMessage(newMessage);

        // update LastMessageId in database
        addToLastMessageId();
    }
    // sends a message object to the db
    public void sendMessage(Message message)
    {
        msgSentLog(message);
        chatsHandler.child(currentChatName).child("Messages").push().setValue(message);
    }

    private void msgSentLog(Message message)
    {
        Log.e("ChatManager", "Message sent: " + message.toString());
    }


    // sets a given int as the next message id
    public void addToLastMessageId()
    {
        DatabaseReference refrenceToLastMessageId = chatsHandler.child(currentChatName).child("NextMessageId");
        refrenceToLastMessageId.setValue(lastMessageId + 1);
        lastMessageId++;
    }

    public void setListenerToMessageId()
    {
        DatabaseReference refrenceToLastMessageId = chatsHandler.child(currentChatName).child("NextMessageId");
        refrenceToLastMessageId.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                Log.e("ChatManager", "Retrieved last message id: " + dataSnapshot.getValue(Integer.class));
                lastMessageId = dataSnapshot.getValue(Integer.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {
                Log.e("ChatManager", "Failed to read value.", databaseError.toException());
            }
        });
    }


    public List<Message> retrieveMessages(DataSnapshot dataSnapshot)
    {
        List<Message> newMessages = new ArrayList<>();
        for (DataSnapshot messageSnapshot : dataSnapshot.getChildren())
        {
            Message message = messageSnapshot.getValue(Message.class);
            newMessages.add(message);
        }

        return newMessages;
    }

    public List<Chat> retrieveChats(DataSnapshot dataSnapshot)
    {
        List<Chat> newChats = new ArrayList<>();
        for (DataSnapshot chatSnapshot : dataSnapshot.getChildren())
        {
            Chat chat = chatSnapshot.getValue(Chat.class);
            newChats.add(chat);
        }

        return newChats;
    }
}

