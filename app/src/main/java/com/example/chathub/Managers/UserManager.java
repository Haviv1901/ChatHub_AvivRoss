package com.example.chathub.Managers;


import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.chathub.Data_Containers.User;
import com.example.chathub.Utilities;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.function.Consumer;

public class UserManager {

    private Context context;
    private FirebaseAuth mAuth;

    // static fields
    private static String username = "";
    private static String uid = "";
    private static Boolean isUsernameSaved = false;

    // consts
    private final String TAG = "UserManager";

    public UserManager(Context context)
    {
        mAuth = FirebaseAuth.getInstance();
        this.context = context;

        initUserDataAndWait();
    }


    /*
     * this function will init username and uid static fields of the class.
     * it is thread-safe and will not stop the thread.
     * if u want to ensure getting the username and uid, use initUsernameAndUidAndWait()
     * */
    private Boolean initUserData()
    {
        // for this function to work, user must be logged in
        if (!isUserLoggedIn()) {
            return false;
        }
        // for added efficiency, if the username is already saved, don't do anything
        if (isUsernameSaved)
        {
            return true;
        }


        FirebaseUser currentUser = mAuth.getCurrentUser();
        String currentUserUid = currentUser.getUid();
        this.uid = currentUserUid;

        // snapshot the realtime database
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
        dbRef.child("Users").child(currentUserUid).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    DataSnapshot snapshot = task.getResult();
                    if (snapshot.exists()) {
                        User user = snapshot.getValue(User.class);
                        username = user.getUsername();
                        isUsernameSaved = true;
                    }
                } else {
                    username = "";
                    uid = "";
                    isUsernameSaved = false;
                }
            }
        });

        return true;

    }

    /*
     * this function call initUsernameAndUid and wait until it finishes.
     * this function promises that at the end, the username and uid will be initialized.
     *  ** WARNING ** this function WILL stop the thread it is called on.
     * */
    private void initUserDataAndWait()
    {
        if (!isUserLoggedIn())
        {
            return;
        }

        while (!initUserData())
        {
            try
            {
                Thread.sleep(100);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }
    }


    public void registerUser(String username, String userUid) {
        // Create a User object
        User user = new User(username);

        // Get a reference to the database
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();

        // Add the user to the "/Users" path in the database
        dbRef.child("Users").child(userUid).setValue(user);

        isUsernameSaved = true;
        this.username = username;
    }

    public static String md5(final String s) {
        final String MD5 = "MD5";
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest
                    .getInstance(MD5);
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest) {
                String h = Integer.toHexString(0xFF & aMessageDigest);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }


    public void logout() {
        username = "";
        this.uid = "";
        isUsernameSaved = false;
        mAuth.signOut();
        Log.e(TAG, "User logged out");
    }

    public String getCurrentUsername()
    {
        initUserData();
        return username;
    }

    public String getCurrentUid() {
        initUserData();
        return uid;
    }

    public Boolean isUserLoggedIn()
    {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null)
        {
            return true;
        }
        return false;
    }

    public void isUsernameExists(String username, Consumer<Boolean> callback)
    {
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
        dbRef.child("Users").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>()
        {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task)
            {
                if (task.isSuccessful())
                {
                    DataSnapshot snapshot = task.getResult();
                    for (DataSnapshot userSnapshot : snapshot.getChildren())
                    {
                        User user = userSnapshot.getValue(User.class);
                        if (user.getUsername().equals(username))
                        {
                            callback.accept(true);
                            return;
                        }
                    }
                }
                else
                {
                    Log.e(TAG, "Error getting data", task.getException());
                }
                callback.accept(false);
            }
        });
    }

    public void changeUsername(String newUsername)
    {

        // acces /Users/uid and change the fields username to the new username
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
        dbRef.child(Utilities.USERS_PATH_DB).child(getCurrentUid()).child("username").setValue(newUsername);

        UserManager.username = newUsername;

    }


}