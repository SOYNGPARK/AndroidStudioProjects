package com.example.androidclient;

import android.util.Log;
import android.widget.Toast;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class TCPClient extends Thread{
    private String msg = null;
    public static final int SERVER_PORT = 4000;
    public String ipAddr = "127.0.0.1";

    public TCPClient(String msg) {
        super();
        this.msg = msg;
    }

    public void run() {
        try {
            Log.e("TCP", "TCP Client starts...");
            Socket socket = new Socket(ipAddr, SERVER_PORT);
            try {
                DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
                try {
                    dataOutputStream.writeBytes(msg);
                    dataOutputStream.flush();
                    Log.e("TCP", "TCP Client sending? " + msg );
                } catch (Exception e) {
                    Log.e("TCP", "Error");
                }
                Log.e("TCP", "TCP Client sent " + msg );
            } catch (Exception e) {

            } finally {
                socket.close();
                Log.e("TCP", "TCP Client done");
            }
            socket.close();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
