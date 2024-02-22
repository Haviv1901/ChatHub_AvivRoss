package com.example.chathub;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.chathub.MainActivity;
import com.example.chathub.R;

public class ChatActivity extends MainActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
    }
}