package com.example.chathub.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.chathub.Managers.UserManager;
import com.example.chathub.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthProvider;

import androidx.annotation.NonNull;
import android.content.Intent;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class OTPVerification extends AppCompatActivity implements View.OnClickListener
{
    // views
    private EditText etOTPConfirm;
    private ProgressBar pbOTPSignUp;
    private Button btContinueOTP;
    private TextView tvOTPResend;

    // firebase
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private PhoneAuthProvider.ForceResendingToken resendingToken;
    private Long timeoutSeconds = 60L;
    private String verificationCode;
    private UserManager userManager;

    // log in info
    private String phoneNumber;
    // consts
    private final String TAG = "OTPVerification";
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_otpactivity);

         userManager = new UserManager(this);

        // views
        etOTPConfirm = findViewById(R.id.etOTPConfirm);
        pbOTPSignUp = findViewById(R.id.pbOTPSignUp);
        btContinueOTP = findViewById(R.id.btContinueOTP);
        tvOTPResend = findViewById(R.id.tvOTPResend);

        // on clicks
        btContinueOTP.setOnClickListener(this);

        // get user info
        phoneNumber = getIntent().getStringExtra("phoneNumber");


        sendOtp(phoneNumber,false);

    }

    void sendOtp(String phoneNumber,boolean isResend)
    {
        startResendTimer();
        setInProgress(true);
        PhoneAuthOptions.Builder builder =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(phoneNumber)
                        .setTimeout(timeoutSeconds, TimeUnit.SECONDS)
                        .setActivity(this)
                        .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks()
                        {
                            @Override
                            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential)
                            {
                                signIn(phoneAuthCredential);
                                setInProgress(false);
                            }

                            @Override
                            public void onVerificationFailed(@NonNull FirebaseException e)
                            {
                                Toast.makeText(OTPVerification.this, "OTP verification failed", Toast.LENGTH_SHORT).show();
                                Log.e(TAG, "onVerificationFailed: " + e.getMessage());
                                setInProgress(false);
                                finish();
                            }

                            @Override
                            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken)
                            {
                                super.onCodeSent(s, forceResendingToken);
                                verificationCode = s;
                                resendingToken = forceResendingToken;
                                Log.e(TAG, "Code sent to phone number");
                                setInProgress(false);
                            }
                        });
        if(isResend)
        {
            PhoneAuthProvider.verifyPhoneNumber(builder.setForceResendingToken(resendingToken).build());
        }
        else
        {
            PhoneAuthProvider.verifyPhoneNumber(builder.build());
        }

    }

    void setInProgress(boolean inProgress)
    {
        if(inProgress)
        {
            pbOTPSignUp.setVisibility(View.VISIBLE);
            btContinueOTP.setVisibility(View.GONE);
        }
        else
        {
            pbOTPSignUp.setVisibility(View.GONE);
            btContinueOTP.setVisibility(View.VISIBLE);
        }
    }

    void signIn(PhoneAuthCredential phoneAuthCredential)
    {
        //login and go to next activity
        setInProgress(true);
        mAuth.signInWithCredential(phoneAuthCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>()
        {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task)
            {
                setInProgress(false);

                if(task.isSuccessful())
                {
                    // finish intent and return result successfuill with user uid
                    Intent intent = getIntent();
                    intent.putExtra("uid",mAuth.getCurrentUser().getUid());
                    setResult(RESULT_OK,intent);
                    finish();
                }
                else
                {
                    Toast.makeText(OTPVerification.this, "OTP verification failed", Toast.LENGTH_SHORT).show();
                    Log.e("SignUpOTPActivity", "Sign In failed. -> " + task.getException().getMessage());
                    finish();
                }
            }
        });


    }

    void startResendTimer() {
        tvOTPResend.setEnabled(false);
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                timeoutSeconds--;
                final String text = "Resend OTP in " + timeoutSeconds + " seconds";
                runOnUiThread(() -> {
                    tvOTPResend.setText(text);
                });
                if (timeoutSeconds <= 0) {
                    timeoutSeconds = 60L;
                    timer.cancel();
                    runOnUiThread(() -> {
                        tvOTPResend.setEnabled(true);
                    });
                }
            }
        }, 0, 1000);
    }


    @Override
    public void onClick(View v) {
        if(v == btContinueOTP)
        {
            // continue
            continueOnClick();
        }
    }

    private void continueOnClick()
    {
        String enteredOtp  = etOTPConfirm.getText().toString();
        PhoneAuthCredential credential =  PhoneAuthProvider.getCredential(verificationCode,enteredOtp);
        signIn(credential);
    }
}