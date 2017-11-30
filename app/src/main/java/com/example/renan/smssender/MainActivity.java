package com.example.renan.smssender;

        import android.content.Intent;
        import android.support.v7.app.AppCompatActivity;
        import android.os.Bundle;
        import android.view.View;
        import android.widget.EditText;
        import android.widget.TextView;
        import android.widget.Toast;

        import java.net.*;
        import java.io.*;
        import java.util.Enumeration;

public class MainActivity extends AppCompatActivity {
    private Thread socketServerThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView t = (TextView) findViewById(R.id.ipInfo);
        try {
            CharSequence address = getIpAddress();
            t.append(address);
        } catch (Exception e){
            e.printStackTrace();
            t.append("erreur");
        }


    }

    private String getIpAddress() {
        String ip = "";
        try {
            Enumeration<NetworkInterface> enumNetworkInterfaces = NetworkInterface
                    .getNetworkInterfaces();
            while (enumNetworkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = enumNetworkInterfaces
                        .nextElement();
                Enumeration<InetAddress> enumInetAddress = networkInterface
                        .getInetAddresses();
                while (enumInetAddress.hasMoreElements()) {
                    InetAddress inetAddress = enumInetAddress.nextElement();

                    if (inetAddress.isSiteLocalAddress()) {
                        ip = inetAddress.getHostAddress() + "\n";
                    }
                }
            }
        } catch (SocketException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            ip += "Something Wrong! " + e.toString() + "\n";
        }
        return ip;
    }


    public void runServer(View view) {
        socketServerThread = new Thread(new SocketServerThread(this));
        socketServerThread.start();

    }

    public void displayMessageFromComputer(String message) {
       TextView rMTextView = findViewById(R.id.remoteMessage);
       rMTextView.setText(message);
    }

    public void sendMessage(View view) {
        EditText phoneEditText = findViewById(R.id.phoneText);
        String messageToSend = phoneEditText.getText().toString();
        SocketServerThread.sendToComputer(messageToSend);
        phoneEditText.setText("");
    }
}
