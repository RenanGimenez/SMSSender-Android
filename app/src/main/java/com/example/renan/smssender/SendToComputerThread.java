package com.example.renan.smssender;

import java.io.PrintWriter;

/**
 * Created by Renan on 23/11/2017.
 */

public class SendToComputerThread implements Runnable {
    private PrintWriter out;
    private String messageFrom;
    private String messageContent;

    public SendToComputerThread(PrintWriter out, String messageFrom, String messageContent){
        this.out = out;
        this.messageFrom = messageFrom;
        this.messageContent = messageContent;
    }

    @Override
    public void run() {
        out.println("DISPLAY");
        out.println(messageFrom);
        out.println(messageContent);
        out.println("END_OF_DISPLAY");
        out.flush();
    }
}
