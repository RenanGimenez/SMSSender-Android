package com.example.renan.smssender;

import android.Manifest;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.text.format.Formatter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.LinkedList;

import static android.Manifest.permission.SEND_SMS;

public class MainActivity extends AppCompatActivity {
    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS =0 ;
    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS=1 ;
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
            //CharSequence ip = getIpAddress();
           // code.setText(ip);
            runServer();
            checkSelfPermission(SEND_SMS);
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.READ_CONTACTS)
                    != PackageManager.PERMISSION_GRANTED) {

                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.READ_CONTACTS)) {
                    //Cela signifie que la permission à déjà était
                    //demandé et l'utilisateur l'a refusé
                    //Vous pouvez aussi expliquer à l'utilisateur pourquoi
                    //cette permission est nécessaire et la redemander
                } else {
                    //Sinon demander la permission
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.READ_CONTACTS},
                            MY_PERMISSIONS_REQUEST_READ_CONTACTS);
                }
            }
        } catch (Exception e){
            e.printStackTrace();

        }

        WifiManager wm = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        String ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());

        TextView code = (TextView)findViewById(R.id.tVCode);
        code.setText(IPtoCode(ip));
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
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.SEND_SMS)) {
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.SEND_SMS},
                        MY_PERMISSIONS_REQUEST_SEND_SMS);
            }
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_SEND_SMS: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(number, null, message, null, null);
                    Toast.makeText(getApplicationContext(), "SMS sent.",
                            Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(),
                            "SMS faild, please try again.", Toast.LENGTH_LONG).show();
                    return;
                }
            }
        }

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




}

