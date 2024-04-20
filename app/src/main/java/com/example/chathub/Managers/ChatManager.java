package com.example.chathub.Managers;

import static com.example.chathub.Data_Containers.Participant.membersToString;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.example.chathub.Data_Containers.Chat;
import com.example.chathub.Data_Containers.Message;
import com.example.chathub.Data_Containers.Participant;
import com.example.chathub.Utilities;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;

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

    // managers
    private UserManager userManager;

    // consts
    private static final String TAG = "ChatManager";
    // paths to storage
    private static final String CHAT_IMAGE_PATH_STORAGE  = "images";
    private static final String CHAT_AUDIO_PATH_STORAGE = "audio";
    private static final String CHAT_ICONS_STORAGE = "Group_Icon";
    // paths to db
    private static final String CHATS_PATH_DB = "Chats";
    // global chat
    private static final String GLOBAL_CHAT_NAME = "THIS_CHAT_IS_255.255.255.255";


    public ChatManager(Context context)
    {
        initHandlersAndManagers(context);
        currentChatId = -1;
        currentChatName = "";
    }

    public ChatManager(String currentChatName, int currentChatId, Context context)
    {
        initHandlersAndManagers(context);
        this.currentChatId = currentChatId;
        this.currentChatName = currentChatName;
        setListenerToMessageId();
    }

    private void initHandlersAndManagers(Context context)
    {
        userManager = new UserManager(context);
        chatsHandler = FirebaseDatabase.getInstance().getReference("Chats");
        sotrageHandler = FirebaseStorage.getInstance();
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

    public String uploadFile(String imageBasePath, Bitmap image)
    {
        // create a byte array of the image
        byte[] byteArray = Utilities.convertBitmapToByteArray(image);

        return uploadFile(imageBasePath, byteArray);
    }

    public String uploadFile(String imageBasePath, byte[] byteArray)
    {

        String imagePath = imageBasePath + "/" + UUID.randomUUID().toString() + ".png";

        // upload the image to the storage
        UploadTask uploadTask = sotrageHandler.getReference(imagePath).putBytes(byteArray);

        return imagePath;
    }


    public void sendMessage(String context, Bitmap image, String username) {
        String imagePath = uploadFile(CHAT_IMAGE_PATH_STORAGE, image);
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
        Log.e(TAG, "Message sent: " + message.toString());
    }


    // sets a given int as the next message id
    public void addToLastMessageId()
    {
        DatabaseReference refrenceToLastMessageId = chatsHandler.child(currentChatName).child("nextMessageId");
        refrenceToLastMessageId.setValue(lastMessageId + 1);
        lastMessageId++;
    }

    public void setListenerToMessageId()
    {
        DatabaseReference refrenceToLastMessageId = chatsHandler.child(currentChatName).child("nextMessageId");
        refrenceToLastMessageId.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                Log.e(TAG, "Retrieved last message id: " + dataSnapshot.getValue(Integer.class));
                lastMessageId = dataSnapshot.getValue(Integer.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {
                Log.e(TAG, "Failed to read value.", databaseError.toException());
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

    public List<Chat> retrieveChatsLoggedUserParticipate(DataSnapshot dataSnapshot)
    {
        List<Chat> newChats = retrieveChats(dataSnapshot);

        for (int i = 0; i < newChats.size(); i++)
        {
            Chat chat = newChats.get(i);
            // if a chat contains the top secrect global chat code, then it will show for everyone.
            if(chat.getMembers().contains(GLOBAL_CHAT_NAME))
            {
                continue;
            }
            // if the chat doesnt contain the current user, remove it from the list
            if (!chat.getMembers().contains(userManager.getCurrentUid()))
            {
                newChats.remove(i);
                i--;
            }
        }

        return newChats;
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

    public void createChat(String chatName, List<Participant> members, byte[] image)
    {
        String imagePath = uploadFile(CHAT_ICONS_STORAGE, image);

        // set next message id to 0
        // chat id to 0 temporarily
        Chat newChat = new Chat(chatName, imagePath, membersToString(members), 0, 0);

        // load next chat id, and when done, set the chat id and upload the chat to the db
        loadNextChatId(newChat);
    }


    private void loadNextChatId(Chat newChat)
    {
        DatabaseReference refrenceToNextChatId = FirebaseDatabase.getInstance()
                .getReference("Utilities")
                .child("NextChatId");
        refrenceToNextChatId.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                Log.e(TAG, "Retrieved next chat id: " + dataSnapshot.getValue(Integer.class));
                int newChatId = dataSnapshot.getValue(Integer.class);

                newChat.setChatId(newChatId);

                chatsHandler.child(newChat.getChatName()).setValue(newChat);

            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {
                Log.e(TAG, "Failed to read next chat id.", databaseError.toException());
            }
        });
    }



}

