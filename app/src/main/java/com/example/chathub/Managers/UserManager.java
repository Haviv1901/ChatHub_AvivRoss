package com.example.chathub.Managers;


import android.app.Activity;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class UserManager {
    private static String TAG = "UserManager";
    private FirebaseAuth mAuth;

    public static void init() {

    }

    public static void rememberMe(boolean remember) {
        // rememberMe = remember;
    }

    public static boolean isLoggedIn() {
        return false;
    }

    // get user
    public static String getUser() {
        return null;
    }

    public static void login(String email, String password) {

    }

    public static void signUp(String username, String password, String phoneNumber, Activity activity, OnCompleteListener<AuthResult> listener) {

    }
}