package com.example.renan.smssender;

/**
 * Created by renan on 09/01/2018.
 */

public class Message {
    private Object content;
    private Contact sender;
    private Contact reciever;


    public Message(Object content, Contact sender, Contact reciever) {
        this.content = content;
        this.sender = sender;
        this.reciever = reciever;
    }


    public Object getContent() {
        return content;
    }


    public Contact getSender() {
        return sender;
    }


    public Contact getReciever() {
        return reciever;
    }


    public boolean hasBeenSent() {
        return sender.isUser();
    }

    public boolean hasBeenRecieved() {
        return reciever.isUser();
    }

}
