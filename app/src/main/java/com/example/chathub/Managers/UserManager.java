package com.example.chathub.Managers;


import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.chathub.Data_Containers.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class UserManager
{

    private Context context;

    private String username;
    private String uid;
    private Boolean isUsernameSaved;
    private FirebaseAuth mAuth;

    // consts
    private final String TAG = "UserManager";

    public UserManager(Context context)
    {
        mAuth = FirebaseAuth.getInstance();

        this.context = context;
        isUsernameSaved = false;
        initUsernameAndUid();
    }

    private void initUsernameAndUid()
    {
        if(isUsernameSaved)
        {
            return;
        }


        if(isUserLoggedIn())
        {
            FirebaseUser currentUser = mAuth.getCurrentUser();
            String currentUserUid = currentUser.getUid();
            this.uid = currentUserUid;

            // snapshot the realtime database
            DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
            dbRef.child("Users").child(currentUserUid).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>()
            {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task)
                {
                    if(task.isSuccessful())
                    {
                        DataSnapshot snapshot = task.getResult();
                        if(snapshot.exists())
                        {
                            User user = snapshot.getValue(User.class);
                            username = user.getUsername();
                            isUsernameSaved = true;
                        }
                    }
                    else
                    {
                        username = "";
                        uid = "";
                        isUsernameSaved = false;
                    }
                }
            });
            return;
        }
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
        this.uid = "";
        isUsernameSaved = false;
        mAuth.signOut();
        Log.e(TAG, "User logged out");
    }
    public String getCurrentUsername()
    {
        initUsernameAndUid();
        return username;
    }

    public String getCurrentUid()
    {
        initUsernameAndUid();
        return uid;
    }

    public Boolean isUserLoggedIn()
    {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null)
        {
            return true;
        }
        return false;
    }
}