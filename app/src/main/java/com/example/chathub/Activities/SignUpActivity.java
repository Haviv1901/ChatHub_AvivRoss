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
    private Button btSignUp, btGoogle;
    private EditText etUsernameSignUp, etPasswordSignUp, etConfirmPasswordSignUp, etPhoneNumberSignUp;

    // consts
    private final String TAG = "SignUpActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // views

        btSignUp = findViewById(R.id.btSignUp);
        btGoogle = findViewById(R.id.btGoogle);
        etUsernameSignUp = findViewById(R.id.etUsernameSignUp);
        etPasswordSignUp = findViewById(R.id.etPasswordSignUp);
        etConfirmPasswordSignUp = findViewById(R.id.etConfirmPasswordSignUp);
        etPhoneNumberSignUp = findViewById(R.id.etPhoneNumberSignUp);
        countryCodePicker = findViewById(R.id.login_countrycode);

        // on clicks
        btSignUp.setOnClickListener(this);
        btGoogle.setOnClickListener(this);


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
        else if(v == btGoogle)
        {
            // sign up with google
            signUpWithGoogleOnClick();
        }

    }

    private void signUpWithGoogleOnClick()
    {

    }

    private void signUpOnClick()
    {
        // Retrieve the text entered by the user
        String username = etUsernameSignUp.getText().toString().trim();
        String password = etPasswordSignUp.getText().toString();
        String confirmPassword = etConfirmPasswordSignUp.getText().toString();
        String phoneNumber = countryCodePicker.getFullNumberWithPlus();

        if (!checkFieldsValidation(username, password, confirmPassword))
        {
            //return;
        }

        Log.e("SignUpActivity", "Trying to sign in with username: " + username + " and password: " + password + " and phone number: " + phoneNumber);

        Intent intent = new Intent(SignUpActivity.this, SignUpOTPActivity.class);
        intent.putExtra("username", username);
        intent.putExtra("password", password);
        intent.putExtra("phoneNumber", phoneNumber);
        startActivity(intent);

    }




    /*
    * check validation of all fields.
    * return true if all fields valid, false otherwise.
    */
    private boolean checkFieldsValidation(String username, String password, String confirmPassword) {
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

        // check password -> at least 6 characters, one number, no special characters.

        if (password.length() < 6)
        {
            etPasswordSignUp.setError("Password must be at least 6 characters");
            etPasswordSignUp.requestFocus();
            return false;
        }

        if (!password.matches(".*\\d.*"))
        {
            etPasswordSignUp.setError("Password must contain at least one number");
            etPasswordSignUp.requestFocus();
            return false;
        }

        if (!password.matches("[a-zA-Z0-9]+"))
        {
            etPasswordSignUp.setError("Password must contain only letters and numbers");
            etPasswordSignUp.requestFocus();
            return false;
        }

        // check confirm password

        if (!password.equals(confirmPassword))
        {
            etConfirmPasswordSignUp.setError("Passwords do not match");
            etConfirmPasswordSignUp.requestFocus();
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