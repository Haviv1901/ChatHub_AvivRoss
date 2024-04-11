package com.example.chathub.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.chathub.Managers.UserManager;

import com.example.chathub.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthMissingActivityForRecaptchaException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class SignUpActivity extends MainActivity implements View.OnClickListener {

    // views
    private Button btSignUp, btGoogle;
    private EditText etUsernameSignUp, etPasswordSignUp, etConfirmPasswordSignUp, etPhoneNumberSignUp;
    private CheckBox cbRememberMeSignUp;
    private ProgressBar pbSignUp;

    // firebase:
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private String verificationCode;
    private PhoneAuthProvider.ForceResendingToken resendingToken;

    // consts
    private final String TAG = "SignUpActivity";
    private final Long TIMEOUT = 60L; // 60 seconds

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
        cbRememberMeSignUp = findViewById(R.id.cbRememberMeSignUp);
        pbSignUp = findViewById(R.id.pbSignUp);

        // on clicks
        btSignUp.setOnClickListener(this);
        btGoogle.setOnClickListener(this);


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
        String phoneNumber = etPhoneNumberSignUp.getText().toString();

        if (!checkFieldsValidation(username, password, confirmPassword))
        {
            return;
        }

        Log.e("SignUpActivity", "Trying to sign in with username: " + username + " and password: " + password + " and phone number: " + phoneNumber);

    }





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
        return true;
    }

}