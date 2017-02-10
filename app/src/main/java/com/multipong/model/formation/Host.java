package com.multipong.model.formation;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import com.multipong.activity.MultiplayerGameFormationActivity;
import com.multipong.model.Actor;
import com.multipong.net.NameResolutor;
import com.multipong.net.Sender;
import com.multipong.net.Utils;
import com.multipong.net.messages.AvailableMessage;
import com.multipong.net.messages.CancelMessage;
import com.multipong.net.messages.JoinMessage;
import com.multipong.net.messages.Message;
import com.multipong.utility.DeviceIdUtility;
import com.multipong.utility.PlayerNameUtility;

import org.json.JSONObject;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class Host implements Actor {

    private MultiplayerGameFormationActivity activity;

    private Map<Integer, String> participants;

    public Host(MultiplayerGameFormationActivity activity) {
        this.activity = activity;
        participants = new HashMap<>();
        Integer myID = DeviceIdUtility.getId();
        if (myID == null) {
            WifiManager manager = (WifiManager) activity.getSystemService(Context.WIFI_SERVICE);
            WifiInfo wifiInfo = manager.getConnectionInfo();
            String mac = wifiInfo.getMacAddress();
            myID = NameResolutor.hashOf(mac);
            DeviceIdUtility.setId(myID);
        }
        participants.put(myID, PlayerNameUtility.getPlayerName());
    }

    @Override
    public synchronized void receive(String type, JSONObject message, InetAddress sender) {
        switch (type) {
            case Participant.MessageType.DISCOVER: discover(sender);
            case Participant.MessageType.JOIN: join(message, sender);
            case Participant.MessageType.CANCEL: cancel(message);
        }
    }

    private void discover(InetAddress sender) {
        Collection<InetAddress> listWithSenderOnly = new ArrayList<>();
        listWithSenderOnly.add(sender);
        sendParticipantsListTo(listWithSenderOnly);
    }

    private void join(JSONObject json, InetAddress sender) {
        JoinMessage message = JoinMessage.createFromJson(json);
        Map<String, Object> object = message.decode();
        Integer newId = (Integer) object.get(JoinMessage.ID_FIELD);
        String name = (String) object.get(Message.NAME_FIELD);
        if (!participants.keySet().contains(newId)) {
            participants.put(newId, name);
            NameResolutor.INSTANCE.addNode(newId, sender);
        }
        Collection<InetAddress> addresses = new ArrayList<>();
        for (Integer id : participants.keySet())
            addresses.add(NameResolutor.INSTANCE.getNodeByHash(id));
        sendParticipantsListTo(addresses);
        // TODO: Display updated participants' list on (here host) screen
    }

    private void cancel(JSONObject json) {
        CancelMessage message = CancelMessage.createFromJson(json);
        Map<String, Object> object = message.decode();
        Integer newId = (Integer) object.get(JoinMessage.ID_FIELD);
        if (participants.keySet().contains(newId))
            participants.remove(newId);
        Collection<InetAddress> addresses = new ArrayList<>();
        for (Integer id : participants.keySet())
            addresses.add(NameResolutor.INSTANCE.getNodeByHash(id));
        sendParticipantsListTo(addresses);
    }

    // TODO: Ensure FIFO ordering for available messages which are sent out
    //       (to prevent inconsistencies)
    private void sendParticipantsListTo(Collection<InetAddress> addresses) {
        for (InetAddress recipient : addresses) {
            Intent sendResponse = new Intent(activity, Sender.class);
            sendResponse.setAction(Sender.ACTION_SEND_MESSAGE);
            sendResponse.putExtra(Sender.EXTRAS_ADDRESS, recipient.toString());
            sendResponse.putExtra(Sender.EXTRAS_PORT, Utils.PORT);
            AvailableMessage response = new AvailableMessage();
            response.addParticipants(participants.values());
            sendResponse.putExtra(Sender.EXTRAS_CONTENT, response.getMsg().toString());
            activity.startService(sendResponse);
        }
    }

    public class MessageType {
        public static final String AVAILABLE = "AVAILABLE";
        public static final String STARTING = "STARTING";
    }
}
