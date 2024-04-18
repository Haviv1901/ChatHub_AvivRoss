package com.example.chathub.Data_Containers;


import androidx.annotation.NonNull;

public class Message {
    private String sender;
    private String content;
    private String image;
    private String date;
    private int msgId;

    public Message()
    {
        // Default constructor required for Firebase
    }

    public Message(String sender, String content, String image, String date)
    {
        this.sender = sender;
        this.content = content;
        this.image = image;
        this.date = date;
        msgId = -1;
    }
    public Message(String sender, String content, String image, String date, int msgId)
    {
        this.sender = sender;
        this.content = content;
        this.image = image;
        this.date = date;
        this.msgId = msgId;
    }

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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
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
        return "Sender: " + sender + ", Content: " + content + ", Image: " + image + ", Date: " + date + ", MsgId: " + msgId;

    }
}