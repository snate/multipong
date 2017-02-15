package com.multipong.net.receive;

import android.util.Log;

import com.multipong.activity.NetworkingActivity;
import com.multipong.net.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class TCPReceiver extends Receiver {

    private ServerSocket serverSocket;
    private volatile boolean stop = false;

    public TCPReceiver(NetworkingActivity activity) {
        super(activity);
    }

    @Override
    public void run() {
        super.run();
        try {
            serverSocket = new ServerSocket(Utils.TCP_PORT);
            while(!stop) {
                final Socket client = serverSocket.accept();
                try {
                    InputStream input = client.getInputStream();
                    Scanner scanner = new Scanner(input).useDelimiter("\\A");
                    String json = scanner.hasNext() ? scanner.next() : "";
                    Log.d("TCP rec", "Message received: " + json);
                    InetAddress sender = client.getInetAddress();
                    // Send application-level ack back to sender
                    Socket ackSocket = new Socket();
                    ackSocket.connect(new InetSocketAddress(sender, Utils.TCP_ACK_PORT), 5000);
                    ackSocket.close();
                    process(json, client.getInetAddress());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            Log.d("TCPReceiver", "Socket closed");
        }
        Log.d("TCP_RECEIVER", "STOPPED");
    }

    public void stop() {
        if (stop) return;
        Log.d("STOPPING", "1");
        stop = true;
        Log.d("STOPPING", "2");
        try {
            if (serverSocket != null)
                serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d("STOPPING", "3");
    }
}
