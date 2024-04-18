package com.example.chathub.Activities;


import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.content.Intent;
import android.widget.Toast;

import com.example.chathub.Managers.UserManager;

import com.example.chathub.R;
import com.hbb20.CountryCodePicker;

public class LoginActivity extends MainActivity implements View.OnClickListener
{

    // views
    private EditText etPhoneNumberLogin;
    private ProgressBar pbOTPLogin;
    private CheckBox cbRememberMe;
    private Button btnLogin;
    private TextView btnDontHaveAccount, tvErrorMsg;
    private boolean rememberMe;
    private CountryCodePicker loginPage_countrycode;

    // manager
    private UserManager userManager;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // init manager
        userManager = new UserManager(this);

        // basic variables
        rememberMe = false;

        // views
        pbOTPLogin = findViewById(R.id.pbOTPLogin);
        cbRememberMe = findViewById(R.id.cbRememberMe);
        btnLogin = findViewById(R.id.btLogin);
        btnDontHaveAccount = findViewById(R.id.btDontHaveAccount);
        loginPage_countrycode = findViewById(R.id.loginPage_countrycode);
        etPhoneNumberLogin = findViewById(R.id.etPhoneNumberLogin);
        tvErrorMsg = findViewById(R.id.tvErrorMsg);

        etPhoneNumberLogin.setFocusable(true);
        etPhoneNumberLogin.setFocusableInTouchMode(true);
        etPhoneNumberLogin.requestFocus();

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
        setLoadingOn();
        String phoneNumber = loginPage_countrycode.getFullNumberWithPlus();

        if (!checkFieldsValidation(phoneNumber))
        {
            return;
        }

        userManager.tryLogIn(phoneNumber);

    }
    
    public void loginFailed()
    {
        setLoadingOff();

        setErrorMsg("Login failed");
        Log.e("LoginActivity", "Login failed");
    }

    public void loginSuccess()
    {
        setLoadingOff();

        Intent intent = new Intent(LoginActivity.this, ChatListActivity.class);
        startActivity(intent);
        finish();

        Log.e("LoginActivity", "Login success");
    }

    private void setLoadingOn()
    {
        pbOTPLogin.setVisibility(View.VISIBLE);
    }

    private void setLoadingOff()
    {
        pbOTPLogin.setVisibility(View.GONE);
    }

    private boolean checkFieldsValidation(String phoneNumber)
    {

        if(phoneNumber.isEmpty())
        {
            setErrorMsg("Phone number is required");
            etPhoneNumberLogin.requestFocus();
            return false;
        }

        // check for phone number length 8-12
        if(phoneNumber.length() < 8 || phoneNumber.length() > 12)
        {
            setErrorMsg("Phone number must be between 8-12 digits");
            etPhoneNumberLogin.requestFocus();
            return false;
        }


        return true;
    }

    private void dontHaveAccountOnClick()
    {
        Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
        startActivity(intent);
    }

    private void rememberMeOnClick()
    {
        rememberMe = !rememberMe;
    }

    private void setErrorMsg(String msg)
    {
        tvErrorMsg.setVisibility(View.VISIBLE);
        tvErrorMsg.setText(msg);
    }

    private void hideErrorMsg()
    {
        tvErrorMsg.setVisibility(View.GONE);
    }
}