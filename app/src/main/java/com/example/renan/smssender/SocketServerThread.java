package com.example.renan.smssender;

import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;

/**
 * Created by Renan on 21/11/2017.
 */

class SocketServerThread implements Runnable {
    private static final int PORT = 10001;
    private static PrintWriter out;
    private static BufferedReader in;
    private static ObjectOutputStream objectOut;
    private static MainActivity mainActivity;

    public SocketServerThread(){
        mainActivity = MainActivity.getActivity();
    }

    @Override
    public void run() {

        try {
            ServerSocket socketserver = new ServerSocket(PORT);
            raiseToast("I am ready to accept my favorite client!");
            while(true) {
                Socket socket = socketserver.accept();
                mainActivity.runOnUiThread(new Runnable() {
                    public void run() {
                        mainActivity.setConnectionState();
                    }
                });
                raiseToast("My favourite client just connected to my server! I'm proud :D");

                out = new PrintWriter(socket.getOutputStream(), false);
                objectOut = new ObjectOutputStream(socket.getOutputStream());
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                new Thread(new Reader(in)).start();
                sendMessages();
                sendContacts();

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendContacts() {
        LinkedList<Contact> contactList  = mainActivity.getContactList();
        for (int i=0; i<contactList.size();++i){
            if(contactList.get(i).getNumTel().equals("") || contactList.get(i).getName().charAt(0) == '.'){
                contactList.remove(i);
                i--;
            }
        }
        out.println("CONTACT_LIST");

        out.println(contactList.size());
        for (int i=0; i<contactList.size();++i){
            out.println(contactList.get(i).getName());
            out.println(contactList.get(i).getNumTel());
        }
            /*objectOut.writeObject(contactList);
            objectOut.flush();*/
        out.flush();
    }

    private void sendMessages() {
        LinkedList<Message> messageList  = mainActivity.getMessageList();
        for (int i=0; i<messageList.size();++i){
            Log.d("Message", messageList.get(i).toString());
        }
        out.println("MESSAGE_LIST");
        out.println(messageList.size());
        out.flush();
        for (int i=0; i<messageList.size();++i){
            out.println(messageList.get(i).getSender());
            out.println(messageList.get(i).getReceiver());
            out.println(messageList.get(i).getName());
            out.println(messageList.get(i).getDate());
            out.println(messageList.get(i).getContent());
            out.println("END_OF_CONTENT");
            out.println(messageList.get(i).getType());
            out.flush();
        }
            /*objectOut.writeObject(contactList);
            objectOut.flush();*/
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
        Log.d("toast",message);

        final String MESSAGE = message;
        mainActivity.runOnUiThread(new Runnable() {
            public void run() {
                Toast toast = Toast.makeText(mainActivity.getApplicationContext(), MESSAGE, Toast.LENGTH_SHORT);
                toast.show();
            }
        });
    }

    public static void sendToComputer(String messageFrom, String messageContent){
       new Thread(new SendToComputerThread(out, messageFrom, messageContent)).start();
    }
}
