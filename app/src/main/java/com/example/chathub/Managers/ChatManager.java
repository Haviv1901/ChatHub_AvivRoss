package com.example.chathub.Managers;

import android.graphics.Bitmap;
import android.util.Log;

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
        setListenerToMessageId();
    }

    public DatabaseReference getChatsHandler()
    {
        return chatsHandler;
    }

    public void setChatId(int chatId)
    {
        currentChatId = chatId;
    }

    public int getChatId()
    {
        return currentChatId;
    }

    public void setChatName(String chatName)
    {
        currentChatName = chatName;
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
    public void sendMessage(String context, Bitmap image)
    {
        // create a byte array of the image
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.PNG, 30, outputStream);
        byte[] byteArray = outputStream.toByteArray();

        // Check if the size exceeds 2 megabytes
        if (byteArray.length > 2 * 1024 * 1024)
        {
            // If the size exceeds 2 megabytes, compress the image further or show an error message
            // For example, compress the image further:
            int quality = 30;
            while (byteArray.length > 2 * 1024 * 1024 && quality > 0)
            {
                outputStream.reset();
                image.compress(Bitmap.CompressFormat.PNG, quality, outputStream);
                byteArray = outputStream.toByteArray();
                quality -= 5;
            }

        }

        String imagePath = "images/" + UUID.randomUUID().toString() + ".png";

        // upload the image to the storage
        UploadTask uploadTask = sotrageHandler.getReference(imagePath).putBytes(byteArray);

        sendMessage(context, imagePath);
    }
    public void sendMessage(String context)
    {
        sendMessage(context, "");
    }
    public void sendMessage(String context, String imagePath)
    {
        // create a new message object
        String time =  getTime();
        Message newMessage = new Message(UserManager.getCurrentUsername(), context, imagePath, time, lastMessageId);
        sendMessage(newMessage);

        // update LastMessageId in database
        addToLastMessageId();
    }
    // sends a message object to the db
    public void sendMessage(Message message)
    {
        chatsHandler.child(currentChatName).child("Messages").push().setValue(message);
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

