package com.example.user.rideshareapp1;

/**
 * Created by User on 6/14/2016.
 */
public class ChatItem {

    String name;
    String content;

    public ChatItem(String name, String content){

        this.name = name;

        this.content = content;

    }

    public String getName() { return name;}

    public String getContent() { return content;}

    public void setName(String name){ this.name = name;}

    public void setContent(String content) { this.content = content;}
}
