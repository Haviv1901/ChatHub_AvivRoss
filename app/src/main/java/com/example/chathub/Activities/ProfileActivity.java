package com.example.chathub.Activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.chathub.Managers.UserManager;
import com.example.chathub.R;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {


    // views
    private TextView tvUsernameProfile, btSaveUsername;
    private ImageButton ibBackToChatList;
    private ImageButton ibEditUsername;
    private EditText etNewUsername;
    private LinearLayout llSaveUsername;
    private ProgressBar pbUsernameSignUp;

    // managers
    private UserManager userManager;

    // else
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        userManager = new UserManager(this);

        // views
        tvUsernameProfile = findViewById(R.id.tvUsernameProfile);
        ibEditUsername = findViewById(R.id.ibEditUsername);
        etNewUsername = findViewById(R.id.etNewUsername);
        btSaveUsername = findViewById(R.id.btSaveUsername);
        ibBackToChatList = findViewById(R.id.ibBackToChatList);
        llSaveUsername = findViewById(R.id.llSaveUsername);
        pbUsernameSignUp = findViewById(R.id.pbUsernameSignUp);

        // on clicks
        ibEditUsername.setOnClickListener(this);
        btSaveUsername.setOnClickListener(this);
        ibBackToChatList.setOnClickListener(this);

        // extract username
        username = userManager.getCurrentUsername();

        tvUsernameProfile.setText(username);
        etNewUsername.setText(username);
        pbUsernameSignUp.setVisibility(View.GONE);

    }

    @Override
    public void onClick(View v)
    {

        if(v.getId() == R.id.ibEditUsername)
        {
            editUsername();
        }
        else if(v.getId() == R.id.btSaveUsername)
        {
            saveUsernameOnClick();
        }
        else if(v.getId() == R.id.ibBackToChatList)
        {
            finish();
        }

    }

    private void saveUsernameOnClick()
    {

        String newUsername = etNewUsername.getText().toString();

        if(newUsername.equals(username))
        {
            turnOffEditMode();
            return;
        }
        
        checkUsernameValidation(newUsername);

    }

    private void checkUsernameValidation(String newUsername)
    {

        if(newUsername.length() < 2)
        {
            etNewUsername.setError("Username can't be less than 2 characters");
            return;
        }

        pbUsernameSignUp.setVisibility(View.VISIBLE);
        userManager.isUsernameExists(newUsername, this::changeUIOnUsernameExists);


    }

    private void changeUIOnUsernameExists(Boolean isExists)
    {
        pbUsernameSignUp.setVisibility(View.GONE);
        if (isExists)
        {
            etNewUsername.setError("Username already exists");
            etNewUsername.requestFocus();
        }
        else
        {
            turnOffEditMode();
            tvUsernameProfile.setText(etNewUsername.getText().toString());
            userManager.changeUsername(etNewUsername.getText().toString());
        }

    }

    private void editUsername()
    {
        // set editText's text to username

        turnOnEditMode();
    }


    private void turnOffEditMode()
    {
        // hide save button and progres bar
        llSaveUsername.setVisibility(View.GONE);

        // hide edit text and show text view
        etNewUsername.setVisibility(View.GONE);
        tvUsernameProfile.setVisibility(View.VISIBLE);
    }


    private void turnOnEditMode()
    {
        // show progress bar and save button layout
        llSaveUsername.setVisibility(View.VISIBLE);
        btSaveUsername.setVisibility(View.VISIBLE);

        etNewUsername.setVisibility(View.VISIBLE);
        tvUsernameProfile.setVisibility(View.GONE);
    }
}