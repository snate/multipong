package com.multipong.activity;

import android.content.Context;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.multipong.R;

public class MultiplayerGameFormationActivity extends AppCompatActivity {

    WifiP2pManager mManager;
    Channel mChannel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiplayer_game_formation);

        mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(this, getMainLooper(), null);
    }
}
