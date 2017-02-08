package com.multipong.net;

import android.net.wifi.p2p.WifiP2pDevice;

import java.util.HashMap;
import java.util.Iterator;
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
        nodes.put(hashOf(newNode.deviceAddress+newNode.deviceName), newNode);
    }

    public WifiP2pDevice getNodeByHash (int hash) {
        return nodes.get(hash);
    }

    public WifiP2pDevice getNodeByName (String deviceName) {
        Iterator it = nodes.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            if ((((WifiP2pDevice)pair.getValue()).deviceName).equals(deviceName))
                return ((WifiP2pDevice)pair.getValue());
        }
        return null;
    }

    public WifiP2pDevice getNodeByAddress (String deviceAddress) {
        Iterator it = nodes.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            if ((((WifiP2pDevice)pair.getValue()).deviceAddress).equals(deviceAddress))
                return ((WifiP2pDevice)pair.getValue());
        }
        return null;
    }

    private Integer hashOf(String string) {
        int hash = 7;
        for (int i = 0; i < string.length(); i++) {
            hash = hash*31 + string.charAt(i);
        }
        return hash;
    }
}
