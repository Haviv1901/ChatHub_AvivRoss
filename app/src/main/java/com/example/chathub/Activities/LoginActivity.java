package com.example.chathub.Activities;


import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.chathub.Managers.UserManager;
import com.example.chathub.R;
import com.example.chathub.Utilities;
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

    private static final int REQUEST_PERMISSIONS = 1 ;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // ask for permissions
        ActivityCompat.requestPermissions(this, Utilities.PERMISSIONS, REQUEST_PERMISSIONS);

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

    /**
     * Function: onRequestPermissionsResult
     * Input: int requestCode - the request code
     *        String[] permissions - the permissions requested
     *        int[] grantResults - the results of the permissions request
     * Output: void
     * Description: This function is called when the user responds to a permission request. It checks if the user granted the permission to record audio.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean permissionToRecordAccepted = false;
        switch (requestCode)
        {
            case REQUEST_PERMISSIONS:
                permissionToRecordAccepted  = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                break;
        }
        if (!permissionToRecordAccepted ) finish();

    }

    /**
     * Function: onClick
     * Input: View v - the view that was clicked
     * Output: void
     * Description: This function is called when a view is clicked. It checks which view was clicked and calls the appropriate function.
     */
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

    /**
     * Function: loginOnClick
     * Input: None
     * Output: void
     * Description: This function is called when the user clicks the login button. It gets the phone number from the country code picker and the phone number edit text.
     *              It checks if the phone number is valid and starts the OTPVerification activity.
     */
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

    /**
     * Function: onActivityResult
     * Input: int requestCode - the request code
     *        int resultCode - the result code
     *        Intent data - the intent
     * Output: void
     * Description: This function is called when the OTPVerification activity returns a result. It checks if the result is OK and calls the loginSuccess function.
     *              If the result is not OK, it calls the loginFailed function.
     */
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

    /**
     * Function: loginFailed
     * Input: None
     * Output: void
     * Description: This function is called when the login failed. It sets the error message and logs the error.
     */
    public void loginFailed()
    {
        setLoadingOff();

        setErrorMsg("Login failed");
        Log.e("LoginActivity", "Login failed");
    }

    /**
     * Function: loginSuccess
     * Input: None
     * Output: void
     * Description: This function is called when the login is successful. It starts the ChatListActivity and finishes the current activity.
     */
    public void loginSuccess()
    {
        setLoadingOff();

        Intent intent = new Intent(LoginActivity.this, ChatListActivity.class);
        startActivity(intent);
        finish();

        Log.e("LoginActivity", "Login success");
    }

    /**
     * Function: setLoadingOn
     * Input: None
     * Output: void
     * Description: This function is called when the loading is started. It sets the progress bar to visible.
     */
    private void setLoadingOn()
    {
        pbOTPLogin.setVisibility(View.VISIBLE);
    }

    /**
     * Function: setLoadingOff
     * Input: None
     * Output: void
     * Description: This function is called when the loading is finished. It sets the progress bar to invisible.
     */
    private void setLoadingOff()
    {
        pbOTPLogin.setVisibility(View.GONE);
    }

    /**
     * Function: checkFieldsValidation
     * Input: String phoneNumber - the phone number
     * Output: boolean - true if the phone number is valid, false otherwise
     * Description: This function checks if the phone number is valid. If the phone number is empty, it sets the error message and returns false.
     *              Otherwise, it returns true.
     */
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

    /**
     * Function: dontHaveAccountOnClick
     * Input: None
     * Output: void
     * Description: This function is called when the user clicks the don't have account button. It starts the SignUpActivity.
     */
    private void dontHaveAccountOnClick()
    {
        Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
        startActivity(intent);
    }

    /**
     * Function: setErrorMsg
     * Input: String msg - the error message
     * Output: void
     * Description: This function is called when the error message is set. It sets the error message text view to visible and sets the error message.
     */
    private void setErrorMsg(String msg)
    {
        tvErrorMsg.setVisibility(View.VISIBLE);
        tvErrorMsg.setText(msg);
    }

    /**
     * Function: hideErrorMsg
     * Input: None
     * Output: void
     * Description: This function is called when the error message is hidden. It sets the error message text view to invisible.
     */
    private void hideErrorMsg()
    {
        tvErrorMsg.setVisibility(View.GONE);
    }
}