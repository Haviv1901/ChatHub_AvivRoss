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

import com.example.chathub.Notifications;
import com.example.chathub.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.hbb20.CountryCodePicker;

public class LoginActivity extends MainActivity implements View.OnClickListener
{

    // views
    private EditText etPhoneNumberLogin;
    private ProgressBar pbOTPLogin;
    private Button btnLogin;
    private TextView btnDontHaveAccount, tvErrorMsg;
    private CountryCodePicker loginPage_countrycode;

    // manager
    private UserManager userManager;
    // firebase
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // init notifications
        Notifications notifications = new Notifications(this);
        notifications.setupChannel(this);

        // init manager
        userManager = new UserManager(this);

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(userManager.isUserLoggedIn())
        {
            Intent intent = new Intent(LoginActivity.this, ChatListActivity.class);
            startActivity(intent);
            finish();
        }





        // views
        pbOTPLogin = findViewById(R.id.pbOTPLogin);
        btnLogin = findViewById(R.id.btLogin);
        btnDontHaveAccount = findViewById(R.id.btDontHaveAccount);
        loginPage_countrycode = findViewById(R.id.loginPage_countrycode);
        etPhoneNumberLogin = findViewById(R.id.etPhoneNumberLogin);
        tvErrorMsg = findViewById(R.id.tvErrorMsg);

        etPhoneNumberLogin.setFocusable(true);
        etPhoneNumberLogin.setFocusableInTouchMode(true);
        etPhoneNumberLogin.requestFocus();

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

    }

    private void loginOnClick()
    {
        setLoadingOn();
        String phoneNumber = loginPage_countrycode.getFullNumberWithPlus();

        if (!checkFieldsValidation(phoneNumber))
        {
            setLoadingOff();
            return;
        }

        Intent intent = new Intent(LoginActivity.this, OTPVerification.class);
        intent.putExtra("phoneNumber", phoneNumber);
        startActivityForResult(intent, 0);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK)
        {
            loginSuccess();
        }
        else
        {
            loginFailed();
        }
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


        return true;
    }

    private void dontHaveAccountOnClick()
    {
        Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
        startActivity(intent);
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