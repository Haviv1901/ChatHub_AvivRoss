package com.example.chathub;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ListView;

public class ChatListActivity extends MainActivity
{


    private ListView chatsListView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);
    }



    // function to update the chat list
    public void updateChatList()
    {
        // update the chat list
    }

    // function to open chat activity
    public void openChatActivity()
    {
        // open chat activity
    }

    // function to open add chat activity
    public void openAddChatActivity()
    {
        // open add chat activity
    }

}