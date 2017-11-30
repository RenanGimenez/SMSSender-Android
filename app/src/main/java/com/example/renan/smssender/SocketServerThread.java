package com.example.renan.smssender;

import android.app.Activity;
import android.os.Looper;
import android.widget.Toast;

import java.io.*;
import java.net.*;

/**
 * Created by Renan on 21/11/2017.
 */

class SocketServerThread implements Runnable {
    private static final int PORT = 2009;
    private MainActivity activity;
    private static PrintWriter out;
    private static BufferedReader in;

    public SocketServerThread(MainActivity activity){
        this.activity = activity;
    }
    @Override
    public void run() {
        try {
            ServerSocket socketserver = new ServerSocket(PORT);
            raiseToast("I am ready to accept my favorite client!");
            Socket socket = socketserver.accept();
            raiseToast("My favourite client just connected to my server! I'm proud :D");

            out = new PrintWriter(socket.getOutputStream());

            sendToComputer("What message want you to display on the phone?");

            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
           while(true) {
                String remoteMessage = in.readLine();
                displayMessageFromComputer(remoteMessage);
           }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void displayMessageFromComputer(String remoteMessage) {
        final String REMOTEMESSAGE = remoteMessage;
        activity.runOnUiThread(new Runnable() {
            public void run() {
                activity.displayMessageFromComputer(REMOTEMESSAGE);
            }
        });

    }
    private void raiseToast(String message) {
        final String MESSAGE = message;
        activity.runOnUiThread(new Runnable() {
            public void run() {
                Toast toast = Toast.makeText(activity.getApplicationContext(), MESSAGE, Toast.LENGTH_SHORT);
                toast.show();
            }
        });
    }

    public static void sendToComputer(String message){
        new Thread(new SendToComputerThread(out, message)).start();
    }
}
