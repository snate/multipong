package com.multipong.net;

import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Receiver implements Runnable {

    private Context context;
    private ServerSocket serverSocket;

    public Receiver(Context context) {
        this.context = context;
    }

    @Override
    public void run() {
        Log.d("RECEIVER", "Started");
        serverSocket = null;
        try {
            serverSocket = new ServerSocket(Utils.PORT);
            Socket client = serverSocket.accept();

            try {
                InputStream input = client.getInputStream();
                Log.d("input", input.toString());
            }
            catch (IOException e) {
                System.out.println(e);
            }

            Log.d("Receiver", "Request accepted");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        try {
            if (serverSocket != null)
                serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
