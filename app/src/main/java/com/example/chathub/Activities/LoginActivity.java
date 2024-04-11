package com.example.chathub.Activities;


import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.content.Intent;

import com.example.chathub.R;

public class LoginActivity extends MainActivity implements View.OnClickListener
{

    // views
    private EditText etUsername, etPassword;
    private CheckBox cbRememberMe;
    private Button btnLogin;
    private TextView btnDontHaveAccount;
    private boolean rememberMe;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        // basic variables
        rememberMe = false;

        // views1
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        cbRememberMe = findViewById(R.id.cbRememberMe);
        btnLogin = findViewById(R.id.btLogin);
        btnDontHaveAccount = findViewById(R.id.btDontHaveAccount);

        etUsername.setFocusable(true);
        etUsername.setFocusableInTouchMode(true);///add this line
        etUsername.requestFocus();

        cbRememberMe.setOnClickListener(this);
        btnLogin.setOnClickListener(this);
        btnDontHaveAccount.setOnClickListener(this);


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


        Intent intent = new Intent(LoginActivity.this, ChatListActivity.class);
        startActivity(intent);
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