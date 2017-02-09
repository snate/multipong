package com.multipong.net;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Sender has the task of sending packets to a given peer
 */
public class Sender extends IntentService {
    private int id;
    private NameResolutor nameResolutor;

    private static final int SOCKET_TIMEOUT = 5000;
    public static final String ACTION_SEND_FILE = "com.multipong.net.SEND_FILE";
    public static final String EXTRAS_ADDRESS = "sender_host";
    public static final String EXTRAS_PORT = "sender_port";

    public Sender(String name) {
        super(name);
    }

    public Sender() {
        super("SenderService");
    }

    public void send(int node, Object msg){

    }

    public void sendToCoordinator(Object msg){}

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d("Sender", "Intent received");
        Context context = getApplicationContext();
        String host = "localhost";
        /*
         * TODO the next avaible free port could be find using
         * ServerSocket serverSocket = new ServerSocket(0);
         * int port = serverSocket.getLocalPort();
         */
        int port = 8888;
        if (!intent.getAction().equals(ACTION_SEND_FILE)) return;
        String data = "Hello, world";
        Socket socket = new Socket();
        try {
            socket.bind(null);
            socket.connect(new InetSocketAddress(host, port), SOCKET_TIMEOUT);
            Log.d("Sender", "Client socket - " + socket.isConnected());
            OutputStream stream = socket.getOutputStream();
            stream.write(data.getBytes(), 0,data.length());
            stream.close();
            Log.d("Sender", "Data has been sent: " + data);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(socket != null && socket.isConnected())
                try {
                    socket.close();
                    Log.d("Sender", "Socket closed.");
                } catch (IOException e) { }
        }
    }
}
