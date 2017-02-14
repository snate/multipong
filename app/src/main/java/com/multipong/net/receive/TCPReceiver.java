package com.multipong.net.receive;

import android.util.Log;

import com.multipong.activity.MultiplayerGameFormationActivity;
import com.multipong.activity.NetworkingActivity;
import com.multipong.net.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class TCPReceiver extends Receiver {

    private ServerSocket serverSocket;

    public TCPReceiver(NetworkingActivity activity) {
        super(activity);
    }

    @Override
    public void run() {
        super.run();
        try {
            serverSocket = new ServerSocket(Utils.PORT);
            while(true) {
                final Socket client = serverSocket.accept();
                try {
                    InputStream input = client.getInputStream();
                    Scanner scanner = new Scanner(input).useDelimiter("\\A");
                    String json = scanner.hasNext() ? scanner.next() : "";
                    Log.d("TCP rec", "Message received: " + json);
                    process(json, client.getInetAddress());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            Log.d("TCPReceiver", "Socket closed");
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
