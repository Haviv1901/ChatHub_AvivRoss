package com.example.chathub;

import android.graphics.Bitmap;

class Message {
    private String sender;
    private String content;
    private String image;
    private String date;

    public Message() {
        // Default constructor required for Firebase
    }

    public Message(String sender, String content, String image, String date) {
        this.sender = sender;
        this.content = content;
        this.image = image;
        this.date = date;
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
}