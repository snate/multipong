package com.multipong.net;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.net.wifi.p2p.WifiP2pManager.ConnectionInfoListener;
import android.util.Log;

import com.multipong.activity.MultiplayerGameFormationActivity;
import com.multipong.net.messages.DiscoverMessage;
import com.multipong.net.messages.Message;
import com.multipong.net.Sender.AddressedContent;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.InetAddress;

public class Utils {

    public static final int PORT = 8888;

    private static MultiplayerGameFormationActivity activity;

    public static void setActivity(MultiplayerGameFormationActivity activity) {
        Utils.activity = activity;
    }

    public static void connectTo(final WifiP2pDevice device, final WifiP2pManager manager,
                                 final Channel channel) {
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

        private MultiplayerGameFormationActivity mActivity;

        public MyConnectionListener(MultiplayerGameFormationActivity context) {
            mActivity = context;
        }

        @Override
        public void onConnectionInfoAvailable(WifiP2pInfo info) {
            InetAddress address = info.groupOwnerAddress;
            AddressedContent content = new AddressedContent(new DiscoverMessage(), address);
            mActivity.addMessageToQueue(content);
            Log.d("MyConnectionLister", "Address: " + address + ":" + Utils.PORT);
        }
    }

    public static boolean isJsonObject (String test) {
        try {
            new JSONObject(test);
        } catch (JSONException ex) {
            return false;
        }
        return true;
    }
}
