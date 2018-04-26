package com.example.renan.smssender;

import java.io.BufferedReader;

/**
 * Created by Renan on 03/12/2017.
 */

public class Reader implements Runnable {
    private BufferedReader in;
    private MainActivity mainActivity;

    public Reader(BufferedReader in) {
        this.mainActivity = MainActivity.getActivity();
        this.in = in;
    }
    @Override
    public void run() {
        try {
             while(true) {
                 String command = in.readLine();
                 System.out.println(command);
                 switch (command) {
                     case "SEND":
                         send();
                         break;
                     default:
                         System.out.println("error");
                 }
             }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void send() throws Exception {
        final String NUMBER = in.readLine();
        final String MESSAGE = in.readLine();
        mainActivity.runOnUiThread(new Runnable() {
            public void run() {
                mainActivity.resetDisplay();
               /* mainActivity.display("Send to: " + NUMBER);
                mainActivity.display("Message: " + MESSAGE);*/
                mainActivity.send(NUMBER,MESSAGE);
            }
        });

    }
}
