package com.multipong.net.send;

import android.util.Log;

import com.multipong.net.messages.Message;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;

public class TCPSender extends Sender {
    public TCPSender(BlockingQueue<AddressedContent> queue) {
        super(queue);
    }

    @Override
    public void send(AddressedContent content) {
        InetAddress address = content.getAddress();
        Message message = content.getMessage();
        Log.d("Sender", "Sending new message with TCP");
        String host = address.getHostAddress();
        String jsonObjectString = message.getMsg().toString();
        Socket socket = new Socket();
        try {
            socket.bind(null);
            socket.connect(new InetSocketAddress(host, getPort()), SOCKET_TIMEOUT);
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
