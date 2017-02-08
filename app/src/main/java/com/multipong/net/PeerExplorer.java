package com.multipong.net;

import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;

import java.util.ArrayList;

/**
 * PeerExplorer discovers new peers.
 */
public class PeerExplorer {

    WifiP2pManager manager;
    Channel channel;

    public PeerExplorer(WifiP2pManager manager1, Channel channel1) {
        manager = manager1;
        channel = channel1;
    }

    public ArrayList<WifiP2pDevice> discover() {
        return null;
    }

    private static Integer getIdForNode(WifiP2pDevice device) {
        String name = device.deviceName;
        String mac = device.deviceAddress;
        int nameHash = name.hashCode();
        int macHash = mac.hashCode();
        return nameHash * 31 + macHash;
    }
}
