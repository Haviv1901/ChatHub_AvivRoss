package com.example.chathub.Data_Containers;

// User class
public class User
{
    public String username;
    public String password;

    public User()
    {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String username, String password)
    {
        this.username = username;
        this.password = password;
    }
}