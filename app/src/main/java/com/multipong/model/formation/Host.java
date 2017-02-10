package com.multipong.model.formation;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import com.multipong.activity.MultiplayerGameFormationActivity;
import com.multipong.model.Actor;
import com.multipong.net.NameResolutor;
import com.multipong.net.messages.DiscoverMessage;
import com.multipong.utility.DeviceIdUtility;

import org.json.JSONObject;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collection;

public class Host implements Actor {

    private MultiplayerGameFormationActivity activity;

    private Collection<Integer> participants;

    public Host(MultiplayerGameFormationActivity activity) {
        this.activity = activity;
        participants = new ArrayList<>();
        Integer myID = DeviceIdUtility.getId();
        if (myID == null) {
            WifiManager manager = (WifiManager) activity.getSystemService(Context.WIFI_SERVICE);
            WifiInfo wifiInfo = manager.getConnectionInfo();
            String mac = wifiInfo.getMacAddress();
            myID = NameResolutor.hashOf(mac);
            DeviceIdUtility.setId(myID);
        }
        participants.add(myID);
    }

    @Override
    public synchronized void receive(String type, JSONObject message, InetAddress sender) {
        switch (type) {
            case Participant.MessageType.DISCOVER: discover(message, sender);
            case Participant.MessageType.JOIN: break;
            case Participant.MessageType.CANCEL: break;
        }
    }

    private void discover(JSONObject jsonObject, InetAddress sender) {
        DiscoverMessage message = DiscoverMessage.createFromJson(jsonObject);
        // TODO: Add implementation
    }

    public class MessageType {
        public static final String AVAILABLE = "AVAILABLE";
        public static final String STARTING = "STARTING";
    }
}
