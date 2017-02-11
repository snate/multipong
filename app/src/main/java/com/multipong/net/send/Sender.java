package com.multipong.net.send;

import android.util.Log;

import com.multipong.net.Utils;
import com.multipong.net.messages.Message;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;

/**
 * Sender has the task of sending packets to a given peer
 */
public class Sender implements Runnable {

    private static final int SOCKET_TIMEOUT = 5000;

    private final BlockingQueue<AddressedContent> messages;
    private volatile boolean stop = false;

    public Sender(BlockingQueue<AddressedContent> queue) {
        messages = queue;
    }

    public static class AddressedContent {
        private Message message;
        private InetAddress address;

        public AddressedContent(Message message, InetAddress address) {
            this.message = message;
            this.address = address;
        }
    }

    @Override
    public void run() {
        while (!stop) {
            AddressedContent content = null;
            try {
                content = messages.take();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (content.address == null || content.message == null) continue;
            Log.d("Sender", "Sending new message");
            int port = Utils.PORT; // TODO: may it change?
            String host = content.address.getHostAddress();
            String jsonObjectString = content.message.getMsg().toString();
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
                if (socket != null && socket.isConnected())
                    try {
                        socket.close();
                        Log.d("Sender", "Socket closed.");
                    } catch (IOException e) {
                    }
            }
        }
    }

    public void stop() {
        stop = true;
    }
}
