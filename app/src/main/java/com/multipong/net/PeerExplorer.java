package com.multipong.net;

import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;

import com.multipong.activity.MultiplayerGameFormationActivity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * PeerExplorer discovers new peers.
 */
public class PeerExplorer implements WifiP2pManager.PeerListListener {

    MultiplayerGameFormationActivity mActivity;
    private List<WifiP2pDevice> peers = new ArrayList<WifiP2pDevice>();

    public PeerExplorer(MultiplayerGameFormationActivity activity) {
        mActivity = activity;
    }

    @Override
    public void onPeersAvailable(WifiP2pDeviceList peerList) {
        Collection<WifiP2pDevice> refreshedPeers = peerList.getDeviceList();
        if (!refreshedPeers.equals(peers)) {
            peers.clear();
            peers.addAll(refreshedPeers);
            mActivity.receiveList(peers);

            // If an AdapterView is backed by this data, notify it
            // of the change.  For instance, if you have a ListView of
            // available peers, trigger an update.
            // ((WiFiPeerListAdapter) getListAdapter()).notifyDataSetChanged();

            // Perform any other updates needed based on the new list of
            // peers connected to the Wi-Fi P2P network.
        }

        if (peers.size() == 0) {
            Log.d("PEER_EXPLORER", "No devices found");
            return;
        }
    }
}
