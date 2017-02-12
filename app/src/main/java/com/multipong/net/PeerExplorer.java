package com.multipong.net;

import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.util.Log;

import com.multipong.activity.MultiplayerGameFormationActivity;

import java.util.ArrayList;
import java.util.Collection;

/**
 * PeerExplorer discovers new peers.
 */
public class PeerExplorer implements WifiP2pManager.PeerListListener {

    private MultiplayerGameFormationActivity mActivity;
    private WifiP2pManager mManager;
    private Channel mChannel;
    private volatile Collection<WifiP2pDevice> peers = new ArrayList<WifiP2pDevice>();

    public PeerExplorer(MultiplayerGameFormationActivity activity) {
        mActivity = activity;
    }

    public PeerExplorer withManager(WifiP2pManager manager) {
        mManager = manager;
        return this;
    }

    public PeerExplorer withChannel(Channel channel) {
        mChannel = channel;
        return this;
    }

    @Override
    public void onPeersAvailable(WifiP2pDeviceList peerList) {
        Collection<WifiP2pDevice> refreshedPeers = peerList.getDeviceList();
        peers.clear();
        peers.addAll(refreshedPeers);
        for (WifiP2pDevice device : peers)
            Utils.connectTo(device, mManager, mChannel);

        if (peers.size() == 0)
            Log.d("PEER_EXPLORER", "No devices found");
    }
}
