package com.multipong.net.send;

import android.util.Log;

import com.multipong.net.Utils;
import com.multipong.net.messages.Message;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;

public class AckUDPSender extends Sender {

    private static final int UDP_TIMEOUT = 500;

    @Override
    public void send(AddressedContent content) {
        int attempts = 0;
        if (content instanceof ReliablyDeliverableAddressedContent)
            ((ReliablyDeliverableAddressedContent) content).setB(false);
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
                Long startTime = System.currentTimeMillis();
                clientSocket.send(sendPacket);

                clientSocket.setSoTimeout(UDP_TIMEOUT);

                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                clientSocket.receive(receivePacket);
                Long endTime = System.currentTimeMillis();
                attempts = 4;
                Log.d("UDP_TIME", "It took " + (endTime - startTime) + "ms");
                if (content instanceof ReliablyDeliverableAddressedContent)
                    ((ReliablyDeliverableAddressedContent) content).setB(true);
                String ack = new String(receivePacket.getData());
                Log.d("AckUDPSender", "Received " + ack + " as a response");
                clientSocket.close();
            } catch (SocketTimeoutException exc) { } catch (IOException e) { } finally {
                attempts++;
            }
        }
        if (content instanceof ReliablyDeliverableAddressedContent)
            synchronized (content) {
                content.notifyAll();
            }
    }

    public static class ReliablyDeliverableAddressedContent extends AddressedContent {

        private volatile Boolean b = true;

        public ReliablyDeliverableAddressedContent(Message message, InetAddress address) {
            super(message, address);
        }

        public Boolean getB() {
            return b;
        }

        public void setB(boolean newB) {
            b = newB;
        }
    }
}
