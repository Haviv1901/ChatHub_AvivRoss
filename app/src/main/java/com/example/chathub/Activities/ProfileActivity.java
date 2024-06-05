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

        userManager = new UserManager();

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

    /* Function: onClick
     * Input: View v - the view that was clicked
     * Output: void
     * Description: This function is called when a view is clicked. It checks which view was clicked and calls the appropriate function.
     */
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

    /* Function: saveUsernameOnClick
     * Input: void
     * Output: void
     * Description: This function is called when the save button is clicked. It checks if the new username is valid and saves it to the database.
     */
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

    /* Function: checkUsernameValidation
     * Input: String newUsername - the new username
     * Output: void
     * Description: This function checks if the new username is valid.
     */
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

    /* Function: changeUIOnUsernameExists
     * Input: Boolean isExists - whether the username exists
     * Output: void
     * Description: This function changes the UI based on whether the username exists or not.
     */
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
            username = etNewUsername.getText().toString();
        }

    }

    /* Function: editUsername
     * Input: void
     * Output: void
     * Description: This function sets the edit text to the current username and turns on the edit mode.
     */
    private void editUsername()
    {
        // set editText's text to username

        turnOnEditMode();
    }

    /* Function: turnOffEditMode
     * Input: void
     * Output: void
     * Description: This function turns off the edit mode.
     */
    private void turnOffEditMode()
    {
        // hide save button and progres bar
        llSaveUsername.setVisibility(View.GONE);

        // hide edit text and show text view
        etNewUsername.setVisibility(View.GONE);
        tvUsernameProfile.setVisibility(View.VISIBLE);
    }

    /* Function: turnOnEditMode
     * Input: void
     * Output: void
     * Description: This function turns on the edit mode.
     */
    private void turnOnEditMode()
    {
        // show progress bar and save button layout
        llSaveUsername.setVisibility(View.VISIBLE);
        btSaveUsername.setVisibility(View.VISIBLE);

        etNewUsername.setVisibility(View.VISIBLE);
        tvUsernameProfile.setVisibility(View.GONE);
    }
}