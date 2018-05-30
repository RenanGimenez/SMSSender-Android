package com.example.renan.smssender;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.Telephony;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.text.format.Formatter;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Arrays;
import java.util.LinkedList;

import static android.Manifest.permission.ACCESS_WIFI_STATE;
import static android.Manifest.permission.READ_CONTACTS;
import static android.Manifest.permission.READ_SMS;
import static android.Manifest.permission.RECEIVE_SMS;
import static android.Manifest.permission.SEND_SMS;

public class MainActivity extends AppCompatActivity {
    private static final Integer MY_PERMISSIONS_REQUEST_OK = 0;
    private String number;
    private String message;
    private Thread socketServerThread;
    private static MainActivity activity;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        activity = this;


        try {
            checkPermissions();
            runServer();
        } catch (Exception e){
            e.printStackTrace();

        }

        WifiManager wm = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);

        WifiManager wifiMgr = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
        int ip = wifiInfo.getIpAddress();
        String ipAddress = Formatter.formatIpAddress(ip);

        TextView code = (TextView)findViewById(R.id.tVCode);
        code.setText(IPtoCode(ipAddress));

    }

    private void checkPermissions() {
        if ((ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) ||
                (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) ||
                (ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED) ||
                (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED))

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_CONTACTS, Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_SMS, Manifest.permission.SEND_SMS},
                        MY_PERMISSIONS_REQUEST_OK);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
        } else {
            Toast.makeText(this, "You must give all pemissions asked before using the app", Toast.LENGTH_LONG).show();
        }

    }
    private String IPtoCode(String ip) {
        String[] arrayBytes = ip.split("\\.");
        String code = new String();
        for (int i=0; i < arrayBytes.length; ++i){
            String hexbyte = Integer.toHexString( new Integer(arrayBytes[i]));
            switch (hexbyte.length()){
                case 1:
                    code += "00"+hexbyte;
                    break;
                case 2:
                    code += "0"+hexbyte;
                    break;
                case 3:
                    code += hexbyte;
                    break;
                default:
                    break;
            }

        }

        return code;

    }


    public void runServer() {
        socketServerThread = new Thread(new SocketServerThread());
        socketServerThread.start();

    }

   /* public void display(String messageToDisplay) {
       TextView rMTextView = findViewById(R.id.remoteMessage);
       String actualMessage = (String) rMTextView.getText();
       rMTextView.setText(actualMessage+"\n"+messageToDisplay);
    }*/

  /*  public void sendMessage(View view) {
        EditText phoneEditText = findViewById(R.id.phoneText);
        String messageToSend = phoneEditText.getText().toString();
        SocketServerThread.sendToComputer(messageToSend);
        phoneEditText.setText("");
    }
*/
    public static MainActivity getActivity(){
        return activity;
    }

    public void resetDisplay() {
      /*  TextView rMTextView = findViewById(R.id.remoteMessage);
        rMTextView.setText("");*/
    }

    public void send(String number, String message) {
        this.number = number;
        this.message = message;

        SmsManager smsManager = SmsManager.getDefault();
        ArrayList<String> parts = smsManager.divideMessage(message);
        int numParts = parts.size();

        if (numParts > 1)
            smsManager.sendMultipartTextMessage(number, null, parts, null, null);
        else
            smsManager.sendTextMessage(number, null, message, null, null);

        Toast.makeText(getApplicationContext(), "SMS sent.",
                Toast.LENGTH_LONG).show();
    }


    public void setConnectionState() {
        TextView state = (TextView)findViewById(R.id.tVState);
        state.setText("You are connected to the computer");
    }

    public LinkedList<Contact> getContactList() {
        LinkedList<Contact> contacts = new LinkedList<>();

        ContentResolver cr = getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);

        if ((cur != null ? cur.getCount() : 0) > 0) {
            while (cur != null && cur.moveToNext()) {
                String name = new String();
                String phoneNo = new String();

                String id = cur.getString(
                        cur.getColumnIndex(ContactsContract.Contacts._ID));
                name = cur.getString(cur.getColumnIndex(
                        ContactsContract.Contacts.DISPLAY_NAME));

                if (cur.getInt(cur.getColumnIndex(
                        ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                    Cursor pCur = cr.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{id}, null);
                    while (pCur.moveToNext()) {
                        phoneNo = pCur.getString(pCur.getColumnIndex(
                                ContactsContract.CommonDataKinds.Phone.NUMBER));

                        if(!phoneNo.startsWith("+33"))
                            phoneNo = "+33"+phoneNo.substring(1);

                    }
                    pCur.close();
                }
                contacts.add(new Contact(name,phoneNo));
            }
        }
        if(cur!=null){
            cur.close();
        }


        return contacts;
    }

    public LinkedList<Message> getMessageList(){
        LinkedList<Message> messages = new LinkedList<>();
        Uri mSmsinboxQueryUri = Telephony.Sms.Inbox.CONTENT_URI;

        Cursor cursor1 = getContentResolver().query(mSmsinboxQueryUri,new String[] { "_id", "thread_id", "address", "person", "date","body", "type" }, null, null, null);
        Log.d("COLUMNS", Arrays.toString(cursor1.getColumnNames()));
        startManagingCursor(cursor1);
        String[] columns = new String[] { "address", "person", "date", "body","type" };
        if (cursor1.getCount() > 0) {
            String count = Integer.toString(cursor1.getCount());
            while (cursor1.moveToNext()){

                String address = cursor1.getString(cursor1.getColumnIndex(columns[0]));
                String name = cursor1.getString(cursor1.getColumnIndex(columns[1]));
                Long date = cursor1.getLong(cursor1.getColumnIndex(columns[2]));
                String msg = cursor1.getString(cursor1.getColumnIndex(columns[3]));
                String type = cursor1.getString(cursor1.getColumnIndex(columns[4]));
                messages.add(new Message(address,"USER",name,date,msg,type));
            }
        }

        Uri mSmssentQueryUri = Telephony.Sms.Sent.CONTENT_URI;
        Cursor cursor2 = getContentResolver().query(mSmssentQueryUri,new String[] { "_id", "thread_id", "address", "person", "date","body", "type" }, null, null, null);
        Log.d("COLUMNS", Arrays.toString(cursor2.getColumnNames()));
        startManagingCursor(cursor2);
        columns = new String[] { "address", "person", "date", "body","type" };
        if (cursor2.getCount() > 0) {
            String count2 = Integer.toString(cursor2.getCount());
            while (cursor2.moveToNext()){
                String address = cursor2.getString(cursor2.getColumnIndex(columns[0]));
                String name = cursor2.getString(cursor2.getColumnIndex(columns[1]));
                Long date = cursor2.getLong(cursor2.getColumnIndex(columns[2]));
                String msg = cursor2.getString(cursor2.getColumnIndex(columns[3]));
                String type = cursor2.getString(cursor2.getColumnIndex(columns[4]));
                messages.add(new Message("USER",address,name,date,msg,type));

            }
        }
        
       
        return messages;
    }

}

