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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.setGameFormationActivity(this);

        WifiP2pManager manager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        WifiP2pManager.Channel channel = manager.initialize(this, getMainLooper(), null);
        new WifiP2pListener(manager, channel, this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public abstract boolean isHost();
}
