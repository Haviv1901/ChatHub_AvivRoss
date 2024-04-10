package com.example.chathub;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import android.graphics.Bitmap;
import android.util.Log;
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