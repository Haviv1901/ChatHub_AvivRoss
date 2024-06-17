package com.example.chathub.Data_Containers;


import androidx.annotation.NonNull;

public class Message {
    /*
    * this class is a base class to the two types of messages available in chat hub.
    * */
    private String sender;
    private String date;
    private int msgId;

    // ctor
    public Message()
    {
        // Default constructor required for Firebase
    }
    public Message(String sender, String date, int msgId)
    {
        this.sender = sender;
        this.date = date;
        this.msgId = msgId;
    }

    // getters setters
    public int getMsgId()
    {
        return msgId;
    }

    public void setMsgId(int msgId)
    {
        this.msgId = msgId;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }



    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @NonNull
    @Override
    public String toString()
    {
        return "Sender: " + sender + ", Date: " + date + ", MsgId: " + msgId;
    }
}