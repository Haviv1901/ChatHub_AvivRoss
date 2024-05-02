package com.example.chathub.Managers;

import static com.example.chathub.Data_Containers.Participant.membersToString;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.chathub.Data_Containers.Chat;
import com.example.chathub.Data_Containers.Message;
import com.example.chathub.Data_Containers.Participant;
import com.example.chathub.Data_Containers.TextMessage;
import com.example.chathub.Data_Containers.VoiceMessage;
import com.example.chathub.Utilities;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;

public class ChatManager
{
    private DatabaseReference chatsHandler;
    private int currentChatId;
    private String currentChatName;
    private int lastMessageId;
    private Context context;

    // managers
    private UserManager userManager;

    // consts
    private static final String TAG = "ChatManager";


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
        this.context = context;
        userManager = new UserManager(context);
        chatsHandler = FirebaseDatabase.getInstance().getReference("Chats");
    }

    public DatabaseReference getChatsHandler()
    {
        return chatsHandler;
    }

    public String getChatName()
    {
        return currentChatName;
    }




    public void sendTextMessage(String messageText, Bitmap image) {
        String imagePath = Utilities.uploadFile(Utilities.CHAT_IMAGE_PATH_STORAGE, image, Utilities.PNG_EXTENSION);
        sendTextMessage(messageText, imagePath);
    }
    public void sendTextMessage(String messageText)
    {
        sendTextMessage(messageText, "");
    }
    public void sendTextMessage(String messageText, String imagePath)
    {
        // create a new message object
        TextMessage newMessage = new TextMessage(
                userManager.getCurrentUsername(), messageText, imagePath, Utilities.getTime(), lastMessageId);
        sendMessage(newMessage);

        // update LastMessageId in database
        addToLastMessageId();
    }

    private void sendVoiceMessage(String audioPath, String audioLength)
    {
        VoiceMessage newMessage = new VoiceMessage(
                userManager.getCurrentUsername(), audioPath, Utilities.getTime(), lastMessageId, audioLength);
        sendMessage(newMessage);
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

    public void setupMessagesListener(Consumer<List<Message>> updateMessagesList)
    {
        getChatsHandler().child(getChatName()).child("Messages").addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                updateMessagesList.accept(retrieveMessagesFromSnapshot(dataSnapshot));
                //   sendNotification(chatManager.retrieveLastMessageFromSnapshot(dataSnapshot));
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {
            }
        });
    }

    public List<Message> retrieveMessagesFromSnapshot(DataSnapshot snapshot)
    {
        List<Message> messages = new ArrayList<>();
        for (DataSnapshot postSnapshot: snapshot.getChildren())
        {
            // Check if it's a text message
            if (postSnapshot.hasChild("content"))
            {
                TextMessage textMessage = postSnapshot.getValue(TextMessage.class);
                messages.add(textMessage);
            }
            // Check if it's a voice message
            else if (postSnapshot.hasChild("voice"))
            {
                VoiceMessage voiceMessage = postSnapshot.getValue(VoiceMessage.class);
                messages.add(voiceMessage);
            }
        }
        return messages;
    }

    public Message retrieveLastMessageFromSnapshot(DataSnapshot dataSnapshot)
    {
        List<Message> newMessages = retrieveMessagesFromSnapshot(dataSnapshot);
        sortMessageListById(newMessages);
        if(newMessages.size() == 0)
        {
            return null;
        }
        return newMessages.get(newMessages.size() - 1);
    }

    public void setupChatsUserParticipateInListener(Consumer<List<Chat>> callback)
    {
        getChatsHandler().addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                callback.accept(retrieveChatsLoggedUserParticipateFromSnapshot(dataSnapshot));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle possible errors.
                Log.e(TAG, "Failed to read chats.", databaseError.toException());
                Toast.makeText(context, "Failed to get chats.", Toast.LENGTH_SHORT).show();
            }
        });
    }


    public List<Chat> retrieveChatsLoggedUserParticipateFromSnapshot(DataSnapshot dataSnapshot)
    {
        List<Chat> newChats = retrieveChatsFromSnapshot(dataSnapshot);
        List<Chat> chatsUserParticipateIn = new ArrayList<>();
        for (int i = 0; i < newChats.size(); i++)
        {
            Chat chat = newChats.get(i);
            // if a chat contains the top secrect global chat code, then it will show for everyone.
            if(chat.getMembers().contains(Utilities.GLOBAL_CHAT_NAME))
            {
                chatsUserParticipateIn.add(chat);
            }
            // if the chat contain the current user, remove it from the list
            if (chat.getMembers().contains(userManager.getCurrentUid()))
            {
                chatsUserParticipateIn.add(chat);
            }
        }

        return chatsUserParticipateIn;
    }

    public List<Chat> retrieveChatsFromSnapshot(DataSnapshot dataSnapshot)
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
        String imagePath = Utilities.uploadFile(Utilities.CHAT_ICONS_STORAGE, image, Utilities.PNG_EXTENSION);

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


    public static void sortMessageListById(List<Message> newMessages) {
        Collections.sort(newMessages, new Comparator<Message>() {
            @Override
            public int compare(Message m1, Message m2) {
                return Integer.compare(m1.getMsgId(), m2.getMsgId());
            }
        });
    }


    /*
    * function will extract the file from the path and upload it to the storage
    * */
    public void uploadAudio(byte[] audioByteArr, String audioLength)
    {

        String audioPath = Utilities.uploadFile(Utilities.CHAT_AUDIO_PATH_STORAGE, audioByteArr, Utilities.THREE_GPP_EXTENSION);

        sendVoiceMessage(audioPath, audioLength);
    }


    public void downloadAudioFromStorage(String audioPath, Consumer<File> playAudio, ProgressBar pbLoading)
    {
        // Create a reference to the file to download
        StorageReference audioRef = FirebaseStorage.getInstance().getReference().child(audioPath);
    
        // Create a local file to store the downloaded file
        File localFile = null;
        try
        {
            localFile = File.createTempFile("audioFile", "3gp", context.getCacheDir());
        }
        catch (IOException e)
        {
            Log.e(TAG, "Failed to create local file", e);
        }
    
        if (localFile != null)
        {
            File finalLocalFile = localFile;
            audioRef.getFile(localFile)
                .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>()
                {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        // Local file has been created and downloaded, ready for use
                        playAudio.accept(finalLocalFile);
                        pbLoading.setVisibility(ProgressBar.GONE);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Log.e(TAG, "Failed to download file", exception);
                        pbLoading.setVisibility(ProgressBar.GONE);
                    }
                });
        }
    }
}

