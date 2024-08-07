package com.example.chathub.Data_Containers;

public class Chat
{
    /*
    * this class is a data container for inputing and retrieving data
    * from firebase realtime database for chats
    * */
    private String Members;
    private String chatImagePath;
    private String chatName;
    private int NextMessageId;
    private int chatId;



    public Chat()
    {

    }

    // ctor
    public Chat(String chatName, String chatImagePath, String members, int nextMessageId, int chatId)
    {
        this.chatName = chatName;
        this.chatImagePath = chatImagePath;
        Members = members;
        NextMessageId = nextMessageId;
        this.chatId = chatId;
    }


    // getters setters
    public String getChatName()
    {
        return chatName;
    }

    public void setChatName(String chatName)
    {
        this.chatName = chatName;
    }

    public String getChatImagePath()
    {
        return chatImagePath;
    }

    public void setChatImagePath(String chatImagePath)
    {
        this.chatImagePath = chatImagePath;
    }

    public String getMembers()
    {
        return Members;
    }

    public void setMembers(String members)
    {
        Members = members;
    }

    public int getNextMessageId()
    {
        return NextMessageId;
    }

    public void setNextMessageId(int nextMessageId)
    {
        NextMessageId = nextMessageId;
    }

    public int getChatId()
    {
        return chatId;
    }

    public void setChatId(int chatId)
    {
        this.chatId = chatId;
    }

}
