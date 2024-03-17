package com.example.chathub;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

public class ChatManager
{
    private DatabaseReference mDatabase;

    public ChatManager()
    {
        mDatabase = FirebaseDatabase.getInstance().getReference("messages");
    }

    public void sendMessage(String userId, String message)
    {

    }

    public void receiveMessages()
    {

    }

    public void parseMessages(DataSnapshot dataSnapshot)
    {

    }
}

