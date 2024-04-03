package com.example.chathub;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.chathub.MainActivity;
import com.example.chathub.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends MainActivity implements View.OnClickListener {

    private ListView messegesListView;
    private ImageView ibBack, ibCameraButton, ibMicrophoneButton;
    private EditText etMessage;
    private MessageAdapter messageAdapter;
    private List<Message> messages;
    private Button ibSendButton;



    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);



        // views

        messegesListView = findViewById(R.id.messegesListView);
        ibBack = findViewById(R.id.ibBack);
        ibCameraButton = findViewById(R.id.ibCameraButton);
        ibMicrophoneButton = findViewById(R.id.ibMicrophoneButton);
        etMessage = findViewById(R.id.etMessage);

        // onclicks

        ibBack.setOnClickListener(this);
        ibCameraButton.setOnClickListener(this);
        ibMicrophoneButton.setOnClickListener(this);

        // set listener
        getMessagesFromFirebase();



    }

    private void getMessagesFromFirebase()
    {
        mDatabase.child("Chats").child("chat1").child("Messages").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                retrieveMessages(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle possible errors.
                Toast.makeText(ChatActivity.this, "Fail to get data.", Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void retrieveMessages(DataSnapshot dataSnapshot)
    {
        List<Message> newMessages = new ArrayList<>();
        for (DataSnapshot messageSnapshot : dataSnapshot.getChildren())
        {
            Message message = messageSnapshot.getValue(Message.class);
            newMessages.add(message);
        }

        updateMessagesList(newMessages);
    }

    private void updateMessagesList(List<Message> newMessages) {
        messages = newMessages;
        messageAdapter = new MessageAdapter(ChatActivity.this, R.layout.message, R.id.tvMessageContent, messages);
        messegesListView.setAdapter(messageAdapter);
    }


    @Override
    public void onClick(View v)
    {

            if(v == ibBack)
            {
                //go to chat list activity
                backOnClick();
            }
            else if(v == ibCameraButton)
            {
                //open camera

            }
            else if(v == ibMicrophoneButton)
            {
                //open microphone
            }
            else if(v == ibSendButton)
            {
                //send message
                sendMessage();
            }


    }

    private void sendMessage()
    {
        String message = etMessage.getText().toString();
        if(message.isEmpty())
        {
            Toast.makeText(this, "Please enter a message", Toast.LENGTH_SHORT).show();
            return;
        }

        // send message to data base

        etMessage.setText("");
    }

    private void backOnClick()
    {
        finish();
    }
}