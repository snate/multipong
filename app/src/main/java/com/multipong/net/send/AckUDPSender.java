package com.multipong.net.send;

import android.util.Log;

import com.multipong.net.Utils;
import com.multipong.net.messages.Message;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class AckUDPSender extends Sender {

    private static final int UDP_TIMEOUT = 500;

    @Override
    public void send(AddressedContent content) {
        int attempts = 0;
        while (attempts < 4) {
            try {
                DatagramSocket clientSocket = new DatagramSocket();
                InetAddress addr = content.getAddress();
                String message = content.getMessage().getMsg().toString();
                byte[] data = new byte[message.length()];
                byte[] receiveData = new byte[message.length()];
                Log.d("AckUDPSender", "Sending " + message);
                Log.d("AckUDPSender", "To " + addr.getHostAddress());
                data = message.getBytes();
                DatagramPacket sendPacket = new DatagramPacket(data, data.length, addr, Utils.UDP_PORT);
                clientSocket.send(sendPacket);

                clientSocket.setSoTimeout(UDP_TIMEOUT);

                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                clientSocket.receive(receivePacket);
                attempts = 4;
                // TODO: if instance of RDAC ...
                String ack = new String(receivePacket.getData());
                Log.d("AckUDPSender", "Received " + ack + " as a response");
                clientSocket.close();
            } catch (SocketTimeoutException exc) {
                attempts++;
            } catch (IOException e) {
            }
        }
    }

    public static class ReliablyDeliverableAddressedContent extends AddressedContent {

        private volatile Boolean b;

        public ReliablyDeliverableAddressedContent(Message message, InetAddress address) {
            super(message, address);
            b = false;
        }

        public Boolean getB() {
            return b;
        }
    }
}
