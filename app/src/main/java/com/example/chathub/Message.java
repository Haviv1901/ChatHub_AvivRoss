package com.example.chathub;

class Message
{
    private String userId;
    private String message;
    private long timestamp;


    public Message(String userId, String message)
    {
        this.userId = userId;
        this.message = message;
    }

    public String getUserId() {
        return userId;
    }

    public String getMessage() {
        return message;
    }
}