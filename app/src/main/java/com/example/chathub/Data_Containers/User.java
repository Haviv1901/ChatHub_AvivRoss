package com.example.chathub.Data_Containers;

// User class
public class User
{
    public String username;

    public User()
    {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String username)
    {
        this.username = username;
    }

    public String getUsername()
    {
        return username;
    }


    public void setUsername(String username)
    {
        this.username = username;
    }

}