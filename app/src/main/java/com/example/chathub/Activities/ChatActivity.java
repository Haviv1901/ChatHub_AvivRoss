package com.example.chathub.Activities;

import androidx.annotation.Nullable;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.example.chathub.Data_Containers.Message;
import com.example.chathub.Adapters.MessageAdapter;
import com.example.chathub.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;



public class ChatActivity extends MainActivity implements View.OnClickListener {

    // views
    private ListView messegesListView;
    private ImageView ibBack, ibCameraButton, ibMicrophoneButton, ibSendButton, ivImage, ivXIcon;
    private EditText etMessage;
    private LinearLayout imageBar;

    // else
    private MessageAdapter messageAdapter;
    private List<Message> messages;
    private Boolean isImage;
    private Bitmap image;


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
        ibSendButton = findViewById(R.id.ibSendButton);
        ivImage = findViewById(R.id.ivImage);
        imageBar = findViewById(R.id.imageBar);
        ivXIcon = findViewById(R.id.ivXIcon);

        // onclicks

        ibBack.setOnClickListener(this);
        ibCameraButton.setOnClickListener(this);
        ibMicrophoneButton.setOnClickListener(this);
        ibSendButton.setOnClickListener(this);
        ivXIcon.setOnClickListener(this);

        // set listener
        getMessagesFromFirebase();

        // set image bar to invisible
        hideImageBar();


    }

    /// this method will set up a listener for the messages in the chat
    private void getMessagesFromFirebase()
    {
        chatManager.getChatsHandler().child(chatManager.getChatName()).child("Messages").addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                updateMessagesList(chatManager.retrieveMessages(dataSnapshot));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle possible errors.
                Toast.makeText(ChatActivity.this, "Fail to get data.", Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void updateMessagesList(List<Message> newMessages) {
        Collections.sort(newMessages, new Comparator<Message>() {
            @Override
            public int compare(Message m1, Message m2) {
                return Integer.compare(m1.getMsgId(), m2.getMsgId());
            }
        });
    
        messages = newMessages;
        messageAdapter = new MessageAdapter(ChatActivity.this, R.layout.message, messages);
        messegesListView.setAdapter(messageAdapter);
        messegesListView.setSelection(messages.size() - 1);
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
                pickFromGallery();
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
            else if(v == ivXIcon)
            {
                //remove image
                hideImageBar();
            }
            else
            {
                //do nothing
            }


    }

    private void hideImageBar()
    {
        ivImage.setVisibility(View.GONE);
        imageBar.setVisibility(View.GONE);
        imageBar.setVisibility(View.GONE);
        isImage = false;
    }

    private void showImageBar()
    {
        ivImage.setVisibility(View.VISIBLE);
        imageBar.setVisibility(View.VISIBLE);
        imageBar.setVisibility(View.VISIBLE);
        isImage = true;
    }

    private void pickFromGallery()
    {
        //Create an Intent with action as ACTION_PICK
        Intent intent=new Intent(Intent.ACTION_OPEN_DOCUMENT);
        // Sets the type as image/*. This ensures only components of type image are selected
        intent.setType("image/*");
        //We pass an extra array with the accepted mime types. This will ensure only components with these MIME types as targeted.
        String[] mimeTypes = {"image/jpeg", "image/png"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES,mimeTypes);
        // Launching the Intent
        startActivityForResult(intent,0);
    }

@Override
protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
{
    super.onActivityResult(requestCode, resultCode, data);
    if (resultCode == Activity.RESULT_OK) {
        Uri selectedImage = data.getData();
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR2)
        {
            getContentResolver().takePersistableUriPermission(selectedImage, Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }



        //put image in ImageView
        ivImage.setImageURI(selectedImage);

        // Get Bitmap from Uri
        try
        {
            image = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
            showImageBar();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}

    private void sendMessage()
    {

        String message = etMessage.getText().toString();
        // check that the message is not empty
        if(message.isEmpty() && isImage == false)
        {
            return;
        }

        // send message to data base
        if(isImage)
        {
            chatManager.sendMessage(message, image);
        }
        else
        {
            chatManager.sendMessage(message);
        }


        // reset the message box
        etMessage.setText("");
        hideImageBar();
    }



    private void backOnClick()
    {
        finish();
    }
}