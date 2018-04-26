package com.example.renan.smssender;

/**
 * Created by renan on 09/01/2018.
 */

import java.util.LinkedList;

public class Contact {
    private static final String NAME_USER = "USER";
    private String name;
    private String numTel;
    private LinkedList<Message> messages;

    public Contact(String name, String numTel, LinkedList<Message> messages) {
        this.name = name;
        this.numTel = numTel;
        this.messages = messages;
    }
    public Contact(String name, String numTel) {
        this.name = name;
        this.numTel = numTel;
        this.messages = new LinkedList<>();
    }
    public String getName() {
        return name;
    }

    public String getNumTel() {
        return numTel;
    }

    public LinkedList<Message> getMessages() {
        return messages;
    }

    public void addMessage(Message message) {
        messages.add(message);
    }

    public boolean isUser() {
        return name.equals(NAME_USER);
    }

}
