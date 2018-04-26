package com.example.renan.smssender;

import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;

/**
 * Created by Renan on 21/11/2017.
 */

class SocketServerThread implements Runnable {
    private static final int PORT = 10000;
    private static PrintWriter out;
    private static BufferedReader in;
    private static MainActivity mainActivity;

    public SocketServerThread(){
        mainActivity = MainActivity.getActivity();
    }

    @Override
    public void run() {

        try {
            ServerSocket socketserver = new ServerSocket(PORT);
            raiseToast("I am ready to accept my favorite client!");
            Socket socket = socketserver.accept();
            mainActivity.runOnUiThread(new Runnable() {
                public void run() {
                    mainActivity.setConnectionState();
                }
            });
            raiseToast("My favourite client just connected to my server! I'm proud :D");

            out = new PrintWriter(socket.getOutputStream());

            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            new Thread(new Reader(in)).start();

            sendContacts();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendContacts() {
        LinkedList<Contact> contactList  = mainActivity.getContactList();
        for (int i=0; i<contactList.size();++i){
            if(contactList.get(i).getNumTel().equals("")){
                contactList.remove(i);
                i--;
            }
        }
        out.println("CONTACT_LIST");
        out.println(contactList);
        out.flush();
    }


    private void displayMessageFromComputer(String remoteMessage) {
        final String REMOTEMESSAGE = remoteMessage;
        mainActivity.runOnUiThread(new Runnable() {
            public void run() {
            //    mainActivity.display(REMOTEMESSAGE);
            }
        });

    }
    private void raiseToast(String message) {
        final String MESSAGE = message;
        mainActivity.runOnUiThread(new Runnable() {
            public void run() {
                Toast toast = Toast.makeText(mainActivity.getApplicationContext(), MESSAGE, Toast.LENGTH_SHORT);
                toast.show();
            }
        });
    }

    public static void sendToComputer(String message){
        new Thread(new SendToComputerThread(out, "TEST CELLPHONE", message)).start();
    }
}
