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



    /*
    * main activity - every activity extends this activity
    * */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


    }
}