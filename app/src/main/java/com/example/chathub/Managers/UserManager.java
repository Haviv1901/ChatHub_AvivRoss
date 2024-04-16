package com.example.chathub.Managers;


import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

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

    private static String currentUsername = "";
    private static String currentUserUID = "";
    private static Boolean isUserLoggedIn = false;

    // consts
    private static String TAG = "UserManager";

    // setters getter

    public static void setCurrentUsername(String username)
    {
        currentUsername = username;
    }

    public static String getCurrentUsername()
    {
        return currentUsername;
    }

    public static void setCurrentUserUID(String uid)
    {
        currentUserUID = uid;
    }

    public static String getCurrentUserUID()
    {
        return currentUserUID;
    }

    public static void setUserLoginTrue()
    {
        isUserLoggedIn = true;
    }

    public static void setUserLoginFalse()
    {
        isUserLoggedIn = false;
    }

    public static Boolean isUserLoggedIn()
    {
        return isUserLoggedIn;
    }
    public static void registerUser(String username, String password, String userUid)
    {
        currentUsername = username;
        currentUserUID = userUid;
        isUserLoggedIn = true;

        String md5Password = md5(password);

        // Create a User object
        User user = new User(username, md5Password);

        // Get a reference to the database
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();

        // Add the user to the "/Users" path in the database
        dbRef.child("Users").child(userUid).setValue(user);
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


    public static void logout()
    {
        currentUsername = "";
        currentUserUID = "";
        isUserLoggedIn = false;

        FirebaseAuth.getInstance().signOut();
    }

public static boolean tryLogIn(Context context, String username, String password, String phoneNumber)
{
    FirebaseAuth auth = FirebaseAuth.getInstance();

    PhoneAuthOptions options = PhoneAuthOptions.newBuilder(auth)
        .setPhoneNumber(phoneNumber)
        .setTimeout(60L, TimeUnit.SECONDS)
        .setActivity((Activity) context)
        .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                auth.signInWithCredential(phoneAuthCredential)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser user = task.getResult().getUser();
                            String uid = user.getUid();


                            findUser(uid, username, password);
                        }
                        else
                        {
                            // Sign in failed
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(context, "Phone number not found.", Toast.LENGTH_SHORT).show();
                        }
                    });
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                Log.w(TAG, "onVerificationFailed", e);
            }
        })
        .build();

    PhoneAuthProvider.verifyPhoneNumber(options);

    // TODO: Return the result of the username and password comparison.
    return false;
}

    private static void findUser(String uid, String username, String password)
    {
        // grab from /Users in realtime database, and key uid, compare username and password.
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
        dbRef.child("Users").child(uid).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    DataSnapshot snapshot = task.getResult();
                    if (snapshot.exists()) {
                        User user = snapshot.getValue(User.class);
                        if (user.getUsername().equals(username) && user.getPassword().equals(md5(password)))
                        {
                            registerUser(username, password, uid);
                        }
                        else
                        {
                            Log.w(TAG, "findUser: User not found.");
                        }
                    }
                    else
                    {
                        Log.w(TAG, "findUser: User not found.");
                    }
                }
                else
                {
                    Log.w(TAG, "findUser: Error getting data", task.getException());
                }
            }
        });
    }
}