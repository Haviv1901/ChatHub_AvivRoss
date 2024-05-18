package com.example.chathub.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;

import com.example.chathub.Adapters.ChatAdapter;
import com.example.chathub.Data_Containers.Chat;
import com.example.chathub.Managers.ChatManager;
import com.example.chathub.Managers.UserManager;
import com.example.chathub.NotificationService;
import com.example.chathub.R;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Class: ChatListActivity
 * Extends: MainActivity
 * Implements: View.OnClickListener
 * Description: This class handles the chat list activity. It displays a list of chats and provides options to create a new chat, logout, and view profile.
 */
public class ChatListActivity extends MainActivity implements View.OnClickListener {

    // views
    private ListView chatsListView;
    private ImageView ibLogout, ibCreateNewChat, ivProfilePic;


    // else
    private final String TAG = "ChatListActivity";
    private List<Chat> chats;
    private ChatAdapter chatAdapter;

    // managers
    private ChatManager chatManager;
    private UserManager userManager;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);

        // set up notification service
        Intent intent = new Intent(this, NotificationService.class);
        startService(intent);

        // managers
        chatManager = new ChatManager(this);
        userManager = new UserManager();

        // views
        chatsListView = findViewById(R.id.chatList);
        ibLogout = findViewById(R.id.ibLogout);
        ibCreateNewChat = findViewById(R.id.ibCreateNewChat);
        ivProfilePic = findViewById(R.id.ivProfilePic);

        // onclicks

        ibLogout.setOnClickListener(this);
        ibCreateNewChat.setOnClickListener(this);
        ivProfilePic.setOnClickListener(this);

        getChatsFromFirebase();

        chatsListView.setOnItemClickListener((parent, view, position, id) -> {
            Chat chat = chats.get(position);
            openChatActivity(chat);
        });


    }

    /**
     * Function: getChatsFromFirebase
     * Input: None
     * Output: None
     * Description: This function fetches the chat data from Firebase.
     */
    private void getChatsFromFirebase()
    {
        chatManager.setupChatsUserParticipateInListener(this::updateMessagesList);
    }

    /**
     * Function: updateMessagesList
     * Input: List<Chat> newMessages
     * Output: None
     * Description: This function updates the list of chats. It sorts the new messages by chat ID, updates the chats list, and sets the ChatAdapter for the chatsListView.
     */
    private void updateMessagesList(List<Chat> newMessages)
    {
        Collections.sort(newMessages, new Comparator<Chat>()
        {
            @Override
            public int compare(Chat m1, Chat m2) {
                return Integer.compare(m1.getChatId(), m2.getChatId());
            }
        });

        chats = newMessages;
        chatAdapter = new ChatAdapter(ChatListActivity.this, R.layout.chat, chats);
        chatsListView.setAdapter(chatAdapter);
    }

    /**
     * Function: openChatActivity
     * Input: Chat chat
     * Output: None
     * Description: This function opens the ChatActivity for a specific chat. It creates an Intent for the ChatActivity, puts the chat name and ID as extras, and starts the activity.
     */
    public void openChatActivity(Chat chat)
    {
        // open chat activity

        Intent intent = new Intent(ChatListActivity.this, ChatActivity.class);
        intent.putExtra("chatName", chat.getChatName());
        intent.putExtra("chatId", chat.getChatId());
        startActivity(intent);
    }

    /**
     * Function: openAddChatActivity
     * Input: None
     * Output: None
     * Description: This function opens the CreateChat_NameAndProfilePic activity. It creates an Intent for the CreateChat_NameAndProfilePic activity and starts the activity.
     */
    public void openAddChatActivity()
    {
        Intent intent = new Intent(ChatListActivity.this, CreateChat_NameAndProfilePic.class);
        startActivity(intent);
    }


    @Override
    public void onClick(View v)
    {

        if(v == ibLogout)
        {
            // log out
            logOut();
        }
        else if(v == ibCreateNewChat)
        {
            // open add chat activity
            openAddChatActivity();
        }
        else if(v == ivProfilePic)
        {
            // open profile activity
            openProfileActivity();
        }
    }

    /**
     * Function: openProfileActivity
     * Input: None
     * Output: None
     * Description: This function opens the ProfileActivity. It creates an Intent for the ProfileActivity and starts the activity.
     */
    private void openProfileActivity()
    {
        Intent intent = new Intent(ChatListActivity.this, ProfileActivity.class);
        startActivity(intent);


    }

    /**
     * Function: logOut
     * Input: None
     * Output: None
     * Description: This function logs out the user and opens the LoginActivity. It calls the logout method of userManager, creates an Intent for the LoginActivity, starts the activity, and finishes the current activity.
     */
    private void logOut()
    {
        userManager.logout();
        Intent intent = new Intent(ChatListActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}