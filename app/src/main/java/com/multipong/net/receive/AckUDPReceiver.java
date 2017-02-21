package com.multipong.net.receive;

import android.util.Log;

import com.multipong.activity.NetworkingActivity;
import com.multipong.net.Utils;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class AckUDPReceiver extends Receiver {

    private DatagramSocket serverSocket;
    private volatile boolean stop = false;

    public AckUDPReceiver(NetworkingActivity activity) {
        super(activity);
    }

    @Override
    public void run() {
        super.run();
        try {
            serverSocket = new DatagramSocket(Utils.UDP_PORT);
            Log.d("AckUDPReceiver", "Waiting for data...");
            while (!stop) {
                byte[] receiveData = new byte[Utils.MTU];
                byte[] ack = new byte[Utils.MTU];
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                serverSocket.receive(receivePacket);
                String incomingData = new String(receivePacket.getData());
                Log.d("AckUDPReceiver", "RECEIVED: " + incomingData);
                InetAddress client = receivePacket.getAddress();
                process(incomingData, client);
                int port = receivePacket.getPort();
                ack = Utils.UDP_ACK.getBytes();
                DatagramPacket sendPacket = new DatagramPacket(ack, ack.length, client, port);
                serverSocket.send(sendPacket);
            }
        } catch(IOException e) {

        }
    }

    @Override
    public void stop() {
        if (serverSocket != null)
            serverSocket.close();
        stop = true;
    }
}
