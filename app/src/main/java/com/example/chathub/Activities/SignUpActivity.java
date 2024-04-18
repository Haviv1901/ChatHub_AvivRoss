package com.example.chathub.Activities;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.chathub.Managers.UserManager;
import com.example.chathub.R;
import com.hbb20.CountryCodePicker;

import java.io.IOException;

public class SignUpActivity extends MainActivity implements View.OnClickListener
{

    // views
    private CountryCodePicker countryCodePicker;
    private Button btSignUp;
    private EditText etUsernameSignUp, etPhoneNumberSignUp;

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

        // on clicks
        btSignUp.setOnClickListener(this);


        countryCodePicker.registerCarrierNumberEditText(etPhoneNumberSignUp);


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

        if (!checkFieldsValidation(username))
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

                userManager.registerUser(etUsernameSignUp.getText().toString(), uid);

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
    private boolean checkFieldsValidation(String username)
    {
        // Perform validation

        // check username ->  at least 6 characters, no special characters.
        if(username.length() < 6)
        {
            etUsernameSignUp.setError("Username must be at least 6 characters");
            etUsernameSignUp.requestFocus();
            return false;
        }

        if (!username.matches("[a-zA-Z0-9]+"))
        {
            etUsernameSignUp.setError("Username must contain only letters and numbers");
            etUsernameSignUp.requestFocus();
            return false;
        }
        return true;
    }

}