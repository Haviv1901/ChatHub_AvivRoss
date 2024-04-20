package com.example.chathub.Data_Containers;

import java.util.List;

public class Participant
{
    private String username;
    private String uid;
    private boolean selected;

    public Participant()
    {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public Participant(String username, String uid)
    {
        this.uid = uid;
        this.username = username;
        this.selected = false;
    }
    public String getUsername()
    {
        return username;
    }

    public void setUsername(String username)
    {
        this.username = username;
    }

    public String getUid()
    {
        return uid;
    }

    public void setUid(String uid)
    {
        this.uid = uid;
    }

    public static String membersToString(List<Participant> memebers)
    {
        StringBuilder sb = new StringBuilder();
        for (Participant p : memebers)
        {
            sb.append(p.getUid());
            sb.append(",");
        }
        return sb.toString();
    }

}
