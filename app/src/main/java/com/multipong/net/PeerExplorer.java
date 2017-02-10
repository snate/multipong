package com.multipong.net;

import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;

import com.multipong.activity.MultiplayerGameFormationActivity;

import java.util.ArrayList;
import java.util.Collection;

/**
 * PeerExplorer discovers new peers.
 */
public class PeerExplorer implements WifiP2pManager.PeerListListener {

    MultiplayerGameFormationActivity mActivity;
    private volatile Collection<WifiP2pDevice> peers = new ArrayList<WifiP2pDevice>();

    public PeerExplorer(MultiplayerGameFormationActivity activity) {
        mActivity = activity;
    }

    @Override
    public void onPeersAvailable(WifiP2pDeviceList peerList) {
        Collection<WifiP2pDevice> refreshedPeers = peerList.getDeviceList();
        if (!peers.containsAll(refreshedPeers) ||
                !refreshedPeers.containsAll(peers)) {
            peers.clear();
            peers.addAll(refreshedPeers);
            mActivity.receiveList(peers);
        }

        if (peers.size() == 0)
            Log.d("PEER_EXPLORER", "No devices found");
    }
}
