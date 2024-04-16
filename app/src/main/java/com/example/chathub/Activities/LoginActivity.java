package com.example.chathub.Activities;


import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.content.Intent;
import com.example.chathub.Managers.UserManager;

import com.example.chathub.R;
import com.hbb20.CountryCodePicker;

public class LoginActivity extends MainActivity implements View.OnClickListener
{

    // views
    private EditText etUsername, etPassword, etPhoneNumberLogin;
    private CheckBox cbRememberMe;
    private Button btnLogin;
    private TextView btnDontHaveAccount;
    private boolean rememberMe;
    private CountryCodePicker loginPage_countrycode;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        // basic variables
        rememberMe = false;

        // views
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        cbRememberMe = findViewById(R.id.cbRememberMe);
        btnLogin = findViewById(R.id.btLogin);
        btnDontHaveAccount = findViewById(R.id.btDontHaveAccount);
        loginPage_countrycode = findViewById(R.id.loginPage_countrycode);
        etPhoneNumberLogin = findViewById(R.id.etPhoneNumberLogin);

        etUsername.setFocusable(true);
        etUsername.setFocusableInTouchMode(true);///add this line
        etUsername.requestFocus();

        cbRememberMe.setOnClickListener(this);
        btnLogin.setOnClickListener(this);
        btnDontHaveAccount.setOnClickListener(this);

        loginPage_countrycode.registerCarrierNumberEditText(etPhoneNumberLogin);


    }

    @Override
    public void onClick(View v)
    {

        if(v == btnLogin)
        {
            //login
            loginOnClick();
        }
        else if(v == btnDontHaveAccount)
        {
            //go to register activity
            dontHaveAccountOnClick();
        }
        else if(v == cbRememberMe)
        {
            rememberMeOnClick();
        }

    }

    private void loginOnClick()
    {
        String username = etUsername.getText().toString();
        String password = etPassword.getText().toString();
        String phoneNumber = loginPage_countrycode.getFullNumberWithPlus();

        if (!checkFieldsValidation(username, password, phoneNumber))
        {
            return;
        }

        UserManager.tryLogIn(username, password, phoneNumber);

        Intent intent = new Intent(LoginActivity.this, ChatListActivity.class);
        startActivity(intent);


    }

    private boolean checkFieldsValidation(String username, String password, String phoneNumber)
    {
        // username, password and phone number not empty
        if(username.isEmpty())
        {
            etUsername.setError("Username is required");
            etUsername.requestFocus();
            return false;
        }

        if(password.isEmpty())
        {
            etPassword.setError("Password is required");
            etPassword.requestFocus();
            return false;
        }

        if(phoneNumber.isEmpty())
        {
            etPhoneNumberLogin.setError("Phone number is required");
            etPhoneNumberLogin.requestFocus();
            return false;
        }


        return true;
    }

    private void dontHaveAccountOnClick() {
        Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
        startActivity(intent);
    }

    private void rememberMeOnClick()
    {
        rememberMe = !rememberMe;
    }
}