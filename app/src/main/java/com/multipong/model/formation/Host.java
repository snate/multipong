package com.multipong.model.formation;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import com.multipong.activity.MultiplayerGameHostActivity;
import com.multipong.model.Actor;
import com.multipong.net.NameResolutor;
import com.multipong.net.Sender;
import com.multipong.net.messages.AvailableMessage;
import com.multipong.net.messages.CancelMessage;
import com.multipong.net.messages.DiscoverMessage;
import com.multipong.net.messages.JoinMessage;
import com.multipong.net.messages.Message;
import com.multipong.net.Sender.AddressedContent;
import com.multipong.utility.DeviceIdUtility;
import com.multipong.utility.PlayerNameUtility;

import org.json.JSONObject;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class Host implements Actor {

    private MultiplayerGameHostActivity activity;

    private Map<Integer, String> participants;

    public Host(MultiplayerGameHostActivity activity) {
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

    public void startGame() {
        // TODO: Add implementation
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
        activity.receiveList(participants.values());
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
        activity.receiveList(participants.values());
    }

    private void sendParticipantsListTo(Collection<InetAddress> addresses) {
        for (InetAddress recipient : addresses) {
            AvailableMessage response = new AvailableMessage();
            response.addParticipants(participants.values());
            AddressedContent content = new Sender.AddressedContent(response, recipient);
            activity.addMessageToQueue(content);
        }
    }

    public class MessageType {
        public static final String AVAILABLE = "AVAILABLE";
        public static final String STARTING = "STARTING";
    }
}
