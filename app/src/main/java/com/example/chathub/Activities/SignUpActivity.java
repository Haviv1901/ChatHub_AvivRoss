package com.example.chathub.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.example.chathub.R;
import com.hbb20.CountryCodePicker;

public class SignUpActivity extends MainActivity implements View.OnClickListener
{

    // views
    private CountryCodePicker countryCodePicker;
    private Button btSignUp;
    private EditText etUsernameSignUp, etPhoneNumberSignUp;

    // consts
    private final String TAG = "SignUpActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

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
        String username = etUsernameSignUp.getText().toString().trim();
        String phoneNumber = countryCodePicker.getFullNumberWithPlus();

        if (!checkFieldsValidation(username))
        {
            return;
        }

        Log.e("SignUpActivity", "Trying to sign in with username: " + username + " and phone number: " + phoneNumber);

        Intent intent = new Intent(SignUpActivity.this, SignUpOTPActivity.class);
        intent.putExtra("username", username);
        intent.putExtra("phoneNumber", phoneNumber);
        startActivity(intent);

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

        // check phone number

        if(!countryCodePicker.isValidFullNumber())
        {
            etPhoneNumberSignUp.setError("Phone number not valid");
            etPhoneNumberSignUp.requestFocus();
            return false;
        }
        return true;
    }

}