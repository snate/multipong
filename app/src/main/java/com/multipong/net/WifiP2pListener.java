package com.multipong.net;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.net.wifi.p2p.WifiP2pManager.PeerListListener;
import android.util.Log;

import com.multipong.activity.MultiplayerGameFormationActivity;

import java.net.InetAddress;

public class WifiP2pListener extends BroadcastReceiver {

    private WifiP2pManager mManager;
    private Channel mChannel;
    private PeerListListener mListener;
    private MultiplayerGameFormationActivity mActivity;

    public WifiP2pListener(WifiP2pManager manager, Channel channel,
                           MultiplayerGameFormationActivity activity) {
        mManager = manager;
        mChannel = channel;
        mListener = new PeerExplorer(activity);
        mActivity = activity;
        mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                mManager.requestPeers(mChannel, mListener);
            }

            @Override
            public void onFailure(int reason) {

            }
        });
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);

            if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                // Wifi P2P is enabled
            } else {
                // this check is for debug purposes
            }
            // Check to see if Wi-Fi is enabled and notify appropriate activity

        } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
            mManager.requestPeers(mChannel, mListener);

        } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
            if (mManager == null) { return; }
            NetworkInfo networkInfo = intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);
            if (networkInfo.isConnected()) {
                // Connection
                mManager.requestConnectionInfo(mChannel, new MyConnectionListener());
            } else {
                // Disconnection

            }

        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
            // Respond to this device's wifi state changing
        }
    }

    private class MyConnectionListener implements WifiP2pManager.ConnectionInfoListener {

        @Override
        public void onConnectionInfoAvailable(WifiP2pInfo info) {
            InetAddress address = info.groupOwnerAddress;
            Intent serviceIntent = new Intent(mActivity, Sender.class);
            serviceIntent.setAction(Sender.ACTION_SEND_FILE);
            serviceIntent.putExtra(Sender.EXTRAS_ADDRESS, address);
            serviceIntent.putExtra(Sender.EXTRAS_PORT, Utils.PORT);
            Log.d("MyConnectionLister", "Address: " + address + ":" + Utils.PORT);
        }
    }
}
