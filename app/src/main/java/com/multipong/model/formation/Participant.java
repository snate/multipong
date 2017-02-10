package com.multipong.model.formation;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import com.multipong.activity.MultiplayerGameFormationActivity;
import com.multipong.activity.MultiplayerGameJoinActivity;
import com.multipong.model.Actor;
import com.multipong.net.NameResolutor;
import com.multipong.net.messages.AvailableMessage;
import com.multipong.net.messages.Message;
import com.multipong.net.messages.StartingMessage;
import com.multipong.utility.DeviceIdUtility;

import org.json.JSONObject;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;

public class Participant implements Actor {

    private MultiplayerGameFormationActivity activity;
    private InetAddress hostAddress;
    private ArrayList<String> partecipants;

    public Participant(MultiplayerGameFormationActivity activity) {
        this.activity = activity;
        Integer myID = DeviceIdUtility.getId();
        if (myID == null) {
            String uniqueID = UUID.randomUUID().toString();
            myID = NameResolutor.hashOf(uniqueID);
            DeviceIdUtility.setId(myID);
        }
    }

    @Override
    public synchronized void receive(String type, JSONObject message, InetAddress sender) {
        switch (type) {
            case Host.MessageType.AVAILABLE:
                onAvailableMessageReceived(message, sender);
                break;
            case Host.MessageType.STARTING:
                onStartingMessageReceived(message, sender);
                break;
        }
    }

    public class MessageType {
        public static final String DISCOVER = "DISCOVER";
        public static final String JOIN = "JOIN";
        public static final String CANCEL = "CANCEL";
    }

    private void onAvailableMessageReceived(JSONObject message, InetAddress sender) {
        AvailableMessage msg = AvailableMessage.createMessageFromJSON(message);
        Map<String, Object> msgInfo = msg.decode();
        partecipants = (ArrayList<String>) msgInfo.get(AvailableMessage.PARTICIPANTS_FIELD);
        hostAddress = sender;
        ((MultiplayerGameJoinActivity)activity).receiveList(
                (Integer)msgInfo.get(Message.ID_FIELD),
                (String)msgInfo.get(Message.NAME_FIELD),
                partecipants);
    }

    private void onStartingMessageReceived(JSONObject message, InetAddress sender) {
        StartingMessage msg = StartingMessage.createMessageFromJSON(message);
        Map<String, Object> msgInfo = msg.decode();
        //TODO -> do something with msgInfo
        //probably start the game
    }
}
