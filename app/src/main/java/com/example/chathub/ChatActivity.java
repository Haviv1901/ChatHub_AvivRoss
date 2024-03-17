package com.example.chathub;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import com.example.chathub.MainActivity;
import com.example.chathub.R;

public class ChatActivity extends MainActivity implements View.OnClickListener {

    private ListView messegesListView;
    private ImageView ibBack, ibCameraButton, ibMicrophoneButton;
    private EditText etMessage;



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

    }

    private void backOnClick()
    {
        finish();
    }
}