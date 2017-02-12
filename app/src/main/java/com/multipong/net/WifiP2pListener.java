package com.multipong.net;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.net.wifi.p2p.WifiP2pManager.PeerListListener;
import android.util.Log;

import com.multipong.activity.MultiplayerGameFormationActivity;

import java.util.Timer;
import java.util.TimerTask;

public class WifiP2pListener extends BroadcastReceiver {

    private WifiP2pManager mManager;
    private Channel mChannel;
    private PeerListListener mListener;
    private MultiplayerGameFormationActivity mActivity;
    private static Timer timer = new Timer();

    public WifiP2pListener(WifiP2pManager manager, Channel channel,
                           MultiplayerGameFormationActivity activity) {
        mManager = manager;
        mChannel = channel;
        mListener = new PeerExplorer(activity).withManager(manager).withChannel(channel);
        mActivity = activity;

        synchronized (timer) {
            if (timer != null) {
                timer.cancel();
            }
        }
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                Log.d("DISCOVER", "scheduled discover of peers");
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
        };
        timer.scheduleAtFixedRate(timerTask, 0, 10000);
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
            } else {
                // Disconnection

            }

        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
            // Respond to this device's wifi state changing
        }
    }
}
