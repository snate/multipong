package com.multipong.net.send;

import android.util.Log;

import com.multipong.net.Utils;
import com.multipong.net.messages.Message;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

public class TCPSender extends Sender {

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
            socket.connect(new InetSocketAddress(host, Utils.PORT), SOCKET_TIMEOUT);
            Log.d("TCP Sender", "Sending " + jsonObjectString);
            Log.d("TCP Sender", "To " + host);
            OutputStream stream = socket.getOutputStream();
            stream.write(jsonObjectString.getBytes(), 0, jsonObjectString.length());
            stream.close();
            Log.d("Sender", "Data has been sent: " + jsonObjectString);
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
