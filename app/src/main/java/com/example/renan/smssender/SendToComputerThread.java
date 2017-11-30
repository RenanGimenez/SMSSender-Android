package com.example.renan.smssender;

import java.io.PrintWriter;

/**
 * Created by Renan on 23/11/2017.
 */

public class SendToComputerThread implements Runnable {
    private PrintWriter out;
    private String message;

    public SendToComputerThread(PrintWriter out, String message){
        this.out = out;
        this.message = message;
    }
    @Override
    public void run() {
        out.println(message);
        out.flush();
    }
}
