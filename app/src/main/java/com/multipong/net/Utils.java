package com.multipong.net;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.net.wifi.p2p.WifiP2pManager.ConnectionInfoListener;
import android.util.Log;

import java.net.InetAddress;

public class Utils {

    public static final int PORT = 8888;

    public static void connectTo(final WifiP2pDevice device, final WifiP2pManager manager,
                                 final Channel channel, final Context activity) {
        WifiP2pConfig config = new WifiP2pConfig();
        config.deviceAddress = device.deviceAddress;
        manager.connect(channel, config, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Log.d("Utils", "Connected to " + device.deviceName + " that has address "
                        + device.deviceAddress);
                manager.requestConnectionInfo(channel, new MyConnectionListener(activity));
            }
            @Override
            public void onFailure(int reason) { }
        });
    }

    private static class MyConnectionListener implements ConnectionInfoListener {

        private Context mActivity;

        public MyConnectionListener(Context context) {
            mActivity = context;
        }

        @Override
        public void onConnectionInfoAvailable(WifiP2pInfo info) {
            if (info.isGroupOwner) {
                Log.d("Utils", "I'm GO");
                return;
            }
            Log.d("Utils", "Not GO");
            InetAddress address = info.groupOwnerAddress;
            Intent serviceIntent = new Intent(mActivity, Sender.class);
            serviceIntent.setAction(Sender.ACTION_SEND_FILE);
            serviceIntent.putExtra(Sender.EXTRAS_ADDRESS, address.toString());
            serviceIntent.putExtra(Sender.EXTRAS_PORT, Utils.PORT);
            Log.d("MyConnectionLister", "Address: " + address + ":" + Utils.PORT);
            mActivity.startService(serviceIntent);
        }
    }
}
