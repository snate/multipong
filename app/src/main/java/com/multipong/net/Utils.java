package com.multipong.net;

import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.util.Log;

import com.multipong.activity.MultiplayerGameFormationActivity;

public class Utils {

    public static void connectTo(final WifiP2pDevice device, WifiP2pManager manager, Channel channel) {
        WifiP2pConfig config = new WifiP2pConfig();
        config.deviceAddress = device.deviceAddress;
        manager.connect(channel, config, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Log.d("Utils", "Connected to " + device.deviceName + " that has address "
                        + device.deviceAddress);
            }
            @Override
            public void onFailure(int reason) { }
        });
    }
}
