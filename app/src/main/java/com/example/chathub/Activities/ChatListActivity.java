package com.example.chathub.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.chathub.Adapters.ChatAdapter;
import com.example.chathub.Adapters.MessageAdapter;
import com.example.chathub.Data_Containers.Chat;
import com.example.chathub.Data_Containers.Message;
import com.example.chathub.Managers.ChatManager;
import com.example.chathub.Managers.UserManager;
import com.example.chathub.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ChatListActivity extends MainActivity implements View.OnClickListener {

    // views
    private ListView chatsListView;
    private ImageView ibLogout, ibCreateNewChat;
    private UserManager userManager;

    // else

    private List<Chat> chats;
    private ChatAdapter chatAdapter;

    // managers
    private ChatManager chatManager;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);

        // managers
        chatManager = new ChatManager();
        userManager = new UserManager(this);

        // views
        chatsListView = findViewById(R.id.chatList);
        ibLogout = findViewById(R.id.ibLogout);
        ibCreateNewChat = findViewById(R.id.ibCreateNewChat);

        // onclicks

        ibLogout.setOnClickListener(this);
       // ibCreateNewChat.setOnClickListener(this);

        getMessagesFromFirebase();

        chatsListView.setOnItemClickListener((parent, view, position, id) -> {
            Chat chat = chats.get(position);
            openChatActivity(chat);
        });

    }


    /// this method will set up a listener for the messages in the chat
    private void getMessagesFromFirebase()
    {
        chatManager.getChatsHandler().addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                updateMessagesList(chatManager.retrieveChats(dataSnapshot));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle possible errors.
                Toast.makeText(ChatListActivity.this, "Fail to get data.", Toast.LENGTH_SHORT).show();

            }
        });
    }

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

    // function to open chat activity
    public void openChatActivity(Chat chat)
    {
        // open chat activity

        Intent intent = new Intent(ChatListActivity.this, ChatActivity.class);
        intent.putExtra("chatName", chat.getChatName());
        intent.putExtra("chatId", chat.getChatId());
        startActivity(intent);
    }

    // function to open add chat activity
    public void openAddChatActivity()
    {
        // open add chat activity
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

    }

    private void logOut()
    {
        userManager.logout();
        Intent intent = new Intent(ChatListActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}