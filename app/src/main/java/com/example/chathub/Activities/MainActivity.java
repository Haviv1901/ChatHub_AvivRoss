package com.example.chathub.Activities;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import com.example.chathub.Managers.ChatManager;
import com.example.chathub.R;
import com.example.chathub.Managers.UserManager;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity
{

    protected FirebaseDatabase firebaseDatabase;
    protected DatabaseReference mDatabase;
    protected ChatManager chatManager;
    protected UserManager loginManager;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // init firebase handlers
        firebaseDatabase = FirebaseDatabase.getInstance();
        mDatabase = firebaseDatabase.getReference();

        // init chat and login managers

        chatManager = new ChatManager();

        loginManager = new UserManager();
    }
}