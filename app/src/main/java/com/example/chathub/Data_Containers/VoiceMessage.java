package com.example.chathub.Data_Containers;

import androidx.annotation.NonNull;

public class VoiceMessage extends Message
{

    // VoiceMessage is a subclass of Message
    private String filePathInStorage;
    private String audioLength;

    public VoiceMessage()
    {
        super();
        // Default constructor required for Firebase
    }

    public VoiceMessage(String sender, String filePathInStorage, String date, int msgId, String audioLength)
    {
        super(sender, date, msgId);
        this.filePathInStorage = filePathInStorage;
        this.audioLength = audioLength;
    }

    public String getVoice()
    {
        return filePathInStorage;
    }

    public void setVoice(String voice) {
        this.filePathInStorage = voice;
    }

    public String getAudioLength()
    {
        return audioLength;
    }

    public void setAudioLength(String audioLength)
    {
        this.audioLength = audioLength;
    }

    @NonNull
    @Override
    public String toString() {
        return super.toString() + ", Voice: " + filePathInStorage;
    }


}
