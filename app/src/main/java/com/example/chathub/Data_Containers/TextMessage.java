package com.example.chathub.Data_Containers;

import androidx.annotation.NonNull;

public class TextMessage extends Message
{

    private String content;
    private String image;

    public TextMessage()
    {
        super();
        // Default constructor required for Firebase
    }

    public TextMessage(String sender, String content, String image, String date, int msgId)
    {
        super(sender, date, msgId);
        this.content = content;
        this.image = image;
    }

    public String getContent()
    {
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

    @NonNull
    @Override
    public String toString() {
        return super.toString() + ", Content: " + content + ", Image: " + image;
    }
}
