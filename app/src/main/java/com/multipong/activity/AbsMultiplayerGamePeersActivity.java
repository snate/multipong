package com.multipong.activity;

import android.net.wifi.p2p.WifiP2pDevice;
import android.support.v7.app.AppCompatActivity;

import java.util.Collection;

/**
 * @author Marco Zanella
 * @version 0.01
 * @since 0.01
 */
public abstract class AbsMultiplayerGamePeersActivity extends AppCompatActivity{
    public abstract void receiveList(Collection<WifiP2pDevice> list);
}
