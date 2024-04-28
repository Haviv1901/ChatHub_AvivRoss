package com.example.chathub.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.chathub.Managers.UserManager;
import com.example.chathub.R;
import com.hbb20.CountryCodePicker;

public class SignUpActivity extends MainActivity implements View.OnClickListener, View.OnFocusChangeListener {

    // views
    private CountryCodePicker countryCodePicker;
    private Button btSignUp;
    private EditText etUsernameSignUp, etPhoneNumberSignUp;
    private ProgressBar pbUsernameSignUp;

    // consts
    private final String TAG = "SignUpActivity";
    // managers
    private UserManager userManager;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        userManager = new UserManager(this);

        // views

        btSignUp = findViewById(R.id.btSignUp);
        etUsernameSignUp = findViewById(R.id.etUsernameSignUp);
        etPhoneNumberSignUp = findViewById(R.id.etPhoneNumberSignUp);
        countryCodePicker = findViewById(R.id.login_countrycode);
        pbUsernameSignUp = findViewById(R.id.pbUsernameSignUp);

        // on clicks
        btSignUp.setOnClickListener(this);
        etUsernameSignUp.setOnFocusChangeListener(this);

        countryCodePicker.registerCarrierNumberEditText(etPhoneNumberSignUp);
        pbUsernameSignUp.setVisibility(View.GONE);
        btSignUp.setEnabled(false);

    }

    @Override
    public void onClick(View v)
    {
        if(v == btSignUp)
        {
            // sign up
            signUpOnClick();
        }

    }

    private void signUpOnClick()
    {
        // Retrieve the text entered by the user
        String username = etUsernameSignUp.getText().toString();
        String phoneNumber = countryCodePicker.getFullNumberWithPlus();

        if (!checkUsernameValidation(username))
        {
            return;
        }

        Log.e("SignUpActivity", "Trying to sign up with username: " + username + " and phone number: " + phoneNumber);

        Intent intent = new Intent(SignUpActivity.this, OTPVerification.class);
        intent.putExtra("phoneNumber", phoneNumber);
        startActivityForResult(intent, 0);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 0)
        {
            if(resultCode == Activity.RESULT_OK)
            {
                // user signed up successfully
                String uid = data.getStringExtra("uid");
                String username = etUsernameSignUp.getText().toString();
                userManager.registerUser(username, uid);

                Intent intent = new Intent(SignUpActivity.this, ChatListActivity.class);
                startActivity(intent);
                finish();
            }
            else // otp failed
            {
                Toast.makeText(SignUpActivity.this, "Phone number verification failed", Toast.LENGTH_SHORT).show();
            }
        }
    }


    /*
    * check validation of all fields.
    * return true if all fields valid, false otherwise.
    */
    private boolean checkUsernameValidation(String username)
    {
        // Perform validation

        // check username ->  at least 2 characters, no special characters.
        if(username.length() < 2)
        {
            etUsernameSignUp.setError("Username must be at least 2 characters");
            etUsernameSignUp.requestFocus();
            return false;
        }

        // check if username exists
        pbUsernameSignUp.setVisibility(View.VISIBLE);
        userManager.isUsernameExists(username, this::changeUIOnUsernameExists);



        return true;
    }

    private void changeUIOnUsernameExists(Boolean isExists)
    {
        pbUsernameSignUp.setVisibility(View.GONE);
        if (isExists)
        {
            // The username exists, update the UI accordingly
            etUsernameSignUp.setError("Username already exists");
            etUsernameSignUp.requestFocus();
        }
        else
        {
            // The username does not exist, update the UI accordingly
            etUsernameSignUp.setBackground(getResources().getDrawable(R.drawable.edit_text_green_outline));
            btSignUp.setEnabled(true);
        }


    }



    @Override
    public void onFocusChange(View v, boolean hasFocus)
    {

        if(v == etUsernameSignUp)
        {
            // if focus is lost, check username validation
            if(!hasFocus)
            {
                String username = etUsernameSignUp.getText().toString();
                checkUsernameValidation(username);
            }
            else
            {
                // if focus is gained, remove the green background.
                etUsernameSignUp.setBackground(null);
            }

        }

    }
}