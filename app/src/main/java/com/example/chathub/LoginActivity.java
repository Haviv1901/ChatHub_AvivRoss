package com.example.chathub;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

public class LoginActivity extends MainActivity implements View.OnClickListener {

    private EditText etUsername, etPassword;
    private CheckBox cbRememberMe;
    private Button btnLogin;
    private TextView btnDontHaveAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


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

    }
}