package com.multipong.net.send;

import android.util.Log;

import com.multipong.net.Utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.concurrent.BlockingQueue;

public class AckUDPSender extends Sender {

    public AckUDPSender(BlockingQueue<AddressedContent> queue) {
        super(queue);
    }

    @Override
    public void send(AddressedContent content) {
        try {
            DatagramSocket clientSocket = new DatagramSocket();
            InetAddress addr = content.getAddress();
            String message = content.getMessage().getMsg().toString();
            byte[] data = new byte[message.length()];
            byte[] receiveData = new byte[message.length()];
            data = message.getBytes();
            DatagramPacket sendPacket = new DatagramPacket(data, data.length, addr, Utils.UDP_PORT);
            clientSocket.send(sendPacket);
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            clientSocket.receive(receivePacket);
            String ack = new String(receivePacket.getData());
            Log.d("AckUDPSender", "Received " + ack + " as a response");
            clientSocket.close();
        } catch (IOException e) { }
    }
}
