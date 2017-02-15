package com.multipong.net.send;

import android.util.Log;

import com.multipong.net.Utils;
import com.multipong.net.messages.Message;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPSender extends Sender {

    protected ServerSocket ackSocket;

    @Override
    public void send(AddressedContent content) {
        InetAddress address = content.getAddress();
        Message message = content.getMessage();
        Log.d("Sender", "Sending new message with TCP");
        String host = address.getHostAddress();
        Socket socket = new Socket();
        try {
            socket.bind(null);
            socket.connect(new InetSocketAddress(host, Utils.TCP_PORT), SOCKET_TIMEOUT);
            Log.d("TCP Sender", "To " + host);
            OutputStream stream = socket.getOutputStream();
            String jsonObjectString = message.getMsg().toString();
            Log.d("TCP Sender", "Sending " + jsonObjectString);
            Long startTime = System.currentTimeMillis();
            stream.write(jsonObjectString.getBytes(), 0, jsonObjectString.length());
            stream.close();
            // Receive ack back
            if (ackSocket == null) ackSocket  = new ServerSocket(Utils.TCP_ACK_PORT);
            Socket ack = ackSocket.accept();
            ack.close();
            Long endTime = System.currentTimeMillis();
            Log.d("Sender", "Data has been sent: " + jsonObjectString);
            Log.d("TCP_TIME", "It took " + (endTime - startTime) + "ms");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (socket != null)
                try {
                    socket.close();
                    socket = null;
                    Log.d("Sender", "Socket closed.");
                } catch (IOException e) {
                }
        }
    }
}
