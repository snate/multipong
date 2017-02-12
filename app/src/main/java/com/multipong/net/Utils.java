package com.multipong.net;

import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.net.wifi.p2p.WifiP2pManager.ConnectionInfoListener;
import android.util.Log;

import com.multipong.activity.MultiplayerGameFormationActivity;
import com.multipong.net.send.Sender.AddressedContent;
import com.multipong.net.messages.gameformation.AreYouTheHostMessage;
import com.multipong.net.messages.gameformation.TellIPMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.InetAddress;

public class Utils {

    public static final int PORT = 8888;
    public static final int UDP_PORT = 9054;
    public static final String UDP_ACK = "ACK";
    public static final int MTU = 1500;

    public static final String WIFI_P2P_GROUP_OWNER_ADDRESS = "192.168.49.1";
    public static final String WIFI_P2P_BROADCAST = "192.168.49.255";

    private static MultiplayerGameFormationActivity gameFormationActivity;

    public static void setGameFormationActivity(MultiplayerGameFormationActivity activity) {
        Utils.gameFormationActivity = activity;
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
                manager.requestConnectionInfo(channel, new FormationListener(gameFormationActivity));
            }
            @Override
            public void onFailure(int reason) { }
        });
    }

    private static class FormationListener implements ConnectionInfoListener {

        private MultiplayerGameFormationActivity mActivity;


        public FormationListener(MultiplayerGameFormationActivity context) {
            mActivity = context;
        }

        @Override
        public void onConnectionInfoAvailable(WifiP2pInfo info) {
            InetAddress address = info.groupOwnerAddress;
            Log.d("MyConnectionLister", "Address: " + address + ":" + Utils.PORT);
            // GO does not know other IP: do nothing
            if (info.isGroupOwner) {
                Log.d("Hello", " world");
                return;
            }
            // Participant sends discovery message to host because knows its IP
            if (!mActivity.isHost()) {
                AreYouTheHostMessage message = new AreYouTheHostMessage();
                AddressedContent content = new AddressedContent(message, address);
                mActivity.addMessageToQueue(content);
                return;
            }
            // Otw, Host has to tell its IP to participant
            TellIPMessage message = new TellIPMessage();
            AddressedContent content = new AddressedContent(message, address);
            mActivity.addMessageToQueue(content);
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
