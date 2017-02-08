package com.multipong.net;

import android.net.wifi.p2p.WifiP2pDevice;

/**
 * PeerExplorer discovers new peers.
 */
public class PeerExplorer {

    private Integer getIdForNode(WifiP2pDevice device) {
        String name = device.deviceName;
        String mac = device.deviceAddress;
        int nameHash = name.hashCode();
        int macHash = mac.hashCode();
        return nameHash * 31 + macHash;
    }
}
