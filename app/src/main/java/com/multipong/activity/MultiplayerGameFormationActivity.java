package com.multipong.activity;

import android.content.Context;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;

import com.multipong.net.Utils;
import com.multipong.net.WifiP2pListener;

/**
 * @author Marco Zanella
 * @version 0.01
 * @since 0.01
 */
public abstract class MultiplayerGameFormationActivity extends NetworkingActivity {

    private WifiP2pListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.setGameFormationActivity(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        WifiP2pManager manager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
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
}
