package com.multipong.net;

import android.net.wifi.p2p.WifiP2pDevice;

import java.util.HashMap;
import java.util.Map;

/**
 * NameResolutor binds logical names to physical addresses/objects.
 */
public class NameResolutor {
    private Map<Integer, WifiP2pDevice> nodes;

    public NameResolutor() {
        nodes = new HashMap<>();
    }

    public void addNode (WifiP2pDevice newNode) {
        nodes.put(hashOf(newNode.deviceAddress), newNode);
    }

    public WifiP2pDevice getNodeByHash (int hash) {
        return nodes.get(hash);
    }

    public WifiP2pDevice getNodeByName (String name) {
        for (WifiP2pDevice device : nodes.values())
            if (device.deviceName.equals(name))
                return device;
        return null;
    }

    public WifiP2pDevice getNodeByAddress (String address) {
        for (WifiP2pDevice device : nodes.values())
            if (device.deviceAddress.equals(address))
                return device;
        return null;
    }

    public static Integer hashOf(String mac) {
        int hash = 7;
        for (int i = 0; i < mac.length(); i++)
            hash = hash*31 + mac.charAt(i);
        return hash;
    }
}
