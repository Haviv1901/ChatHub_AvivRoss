package com.example.chathub.Managers;


import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.chathub.Activities.LoginActivity;
import com.example.chathub.Data_Containers.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeUnit;

public class UserManager
{

    private Context context;

    private String username;
    private Boolean isUsernameSaved;

    // consts
    private final String TAG = "UserManager";

    public UserManager(Context context)
    {
        this.context = context;
        username = "";
        isUsernameSaved = false;
    }


    public void registerUser(String username, String userUid)
    {
        // Create a User object
        User user = new User(username);

        // Get a reference to the database
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();

        // Add the user to the "/Users" path in the database
        dbRef.child("Users").child(userUid).setValue(user);

        isUsernameSaved = true;
        this.username = username;
    }

    public static String md5(final String s)
    {
        final String MD5 = "MD5";
        try
        {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest
                    .getInstance(MD5);
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest)
            {
                String h = Integer.toHexString(0xFF & aMessageDigest);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();

        }
        catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }
        return "";
    }


    public void logout()
    {
        username = "";
        isUsernameSaved = false;
        FirebaseAuth.getInstance().signOut();
    }

    public void tryLogIn(String phoneNumber)
    {
        FirebaseAuth auth = FirebaseAuth.getInstance();

        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity((Activity) context)
            .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks()
            {
                @Override
                public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential)
                {
                    auth.signInWithCredential(phoneAuthCredential)
                        .addOnCompleteListener(task ->
                        {
                            ((LoginActivity)context).loginSuccess();
                        });
                }
                @Override
                public void onVerificationFailed(@NonNull FirebaseException e)
                {
                    ((LoginActivity)context).loginFailed();
                }
            })
            .build();

        PhoneAuthProvider.verifyPhoneNumber(options);
    }
    public String getCurrentUsername()
    {
        return "Not Implemented";
    }

    public String getCurrentUserUID()
    {
        return "Not Implemented";
    }

    public Boolean isUserLoggedIn()
    {
        return false;
    }
}