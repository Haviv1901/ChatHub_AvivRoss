package com.example.chathub.Managers;


import android.app.Activity;
import android.util.Log;

import com.example.chathub.Data_Containers.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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
    public static void signInUser(String username, String password, String userUid)
    {
        currentUsername = username;
        currentUserUID = userUid;
        isUserLoggedIn = true;

        // Create a User object
        User user = new User(username, password);

        // Get a reference to the database
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();

        // Add the user to the "/Users" path in the database
        dbRef.child("Users").child(userUid).setValue(user);
    }


}