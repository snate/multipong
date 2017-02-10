package com.multipong.net;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.multipong.net.messages.Message;
import com.multipong.utility.PlayerNameUtility;

import org.json.JSONException;
import org.json.JSONObject;

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
    public static final String ACTION_SEND_MESSAGE = "com.multipong.net.SEND_FILE";
    public static final String EXTRAS_ADDRESS = "sender_host";
    public static final String EXTRAS_PORT = "sender_port";
    public static final String EXTRAS_CONTENT = "sender_content";

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
        /*
         * TODO the next avaible free port could be find using
         * ServerSocket serverSocket = new ServerSocket(0);
         * int port = serverSocket.getLocalPort();
         */
        if (!intent.getAction().equals(ACTION_SEND_MESSAGE)) return;
        int port = intent.getIntExtra(EXTRAS_PORT, 8888);
        String host = intent.getStringExtra(EXTRAS_ADDRESS).substring(1);
        String jsonObjectString = intent.getStringExtra(EXTRAS_CONTENT);
        Socket socket = new Socket();
        try {
            socket.bind(null);
            socket.connect(new InetSocketAddress(host, port), SOCKET_TIMEOUT);
            Log.d("Sender", "Client socket - " + socket.isConnected());
            OutputStream stream = socket.getOutputStream();
            stream.write(jsonObjectString.getBytes(), 0, jsonObjectString.length());
            stream.close();
            Log.d("Sender", "Data has been sent: " + jsonObjectString);
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
