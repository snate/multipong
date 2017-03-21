package com.multipong.activity;

import android.content.Context;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;

import com.multipong.net.Utils;
import com.multipong.net.WifiP2pListener;
import com.multipong.net.messages.PoisonPillMessage;
import com.multipong.net.receive.Receiver;
import com.multipong.net.receive.TCPReceiver;
import com.multipong.net.send.Sender;
import com.multipong.net.send.TCPSender;
import com.multipong.utility.TimeLoggingUtility;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Marco Zanella
 * @version 0.01
 * @since 0.01
 */
public abstract class MultiplayerGameFormationActivity extends NetworkingActivity {

    private WifiP2pListener listener;
    private AtomicBoolean semafor = new AtomicBoolean(false);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TimeLoggingUtility.setStartFor(TCPSender.TCP_LOGS_KEY);
        Utils.setGameFormationActivity(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        final WifiP2pManager manager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        WifiP2pManager.Channel channel = manager.initialize(this, getMainLooper(), null);
        listener = new WifiP2pListener(manager, channel, this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        listener.cancelDiscovery();
    }

    public abstract boolean isHost();

    @Override
    public Sender getSender() {
        return new TCPSender();
    }

    @Override
    public Receiver getReceiver() {
        return new TCPReceiver(this);
    }

    public void waitForEmptyMessageQueue() {
        getReceiver().stop();
        if (semafor.getAndSet(true)) return;
        try {
            PoisonPillMessage die = new PoisonPillMessage();
            Sender.AddressedContent content = new Sender.AddressedContent(die, null);
            addMessageToQueue(content);
            synchronized (die) {
                die.wait();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
