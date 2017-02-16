package com.multipong.net;

import android.net.wifi.p2p.WifiP2pDevice;

import java.net.InetAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * NameResolutor binds logical names to physical addresses/objects.
 */
public enum NameResolutor {

    INSTANCE;

    private Map<Integer, InetAddress> nodes = new ConcurrentHashMap<>();
    private volatile boolean starting = false;

    public void addNode (WifiP2pDevice device, InetAddress address) {
        if (starting) return;
        nodes.put(hashOf(device.deviceAddress), address);
    }

    public void addNode (String macAddress, InetAddress address) {
        if (starting) return;
        nodes.put(hashOf(macAddress), address);
    }

    public void addNode (Integer id, InetAddress address) {
        if (starting) return;
        nodes.put(id, address);
    }

    public InetAddress getNodeByHash (Integer hash) {
        return nodes.get(hash);
    }

    public InetAddress getNodeByAddress (String macAddress) {
        return nodes.get(hashOf(macAddress));
    }

    public void removeNode(Integer entry) {
        nodes.remove(entry);
    }

    public static Integer hashOf(String mac) {
        int hash = 7;
        for (int i = 0; i < mac.length(); i++)
            hash = hash*31 + mac.charAt(i);
        return hash;
    }
}
