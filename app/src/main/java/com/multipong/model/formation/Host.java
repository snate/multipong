package com.multipong.model.formation;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.AsyncTask;
import android.text.format.Formatter;
import android.util.Log;

import com.multipong.activity.MultiplayerGameHostActivity;
import com.multipong.model.Actor;
import com.multipong.net.NameResolutor;
import com.multipong.net.Sender;
import com.multipong.net.Utils;
import com.multipong.net.messages.AvailableMessage;
import com.multipong.net.messages.CancelMessage;
import com.multipong.net.messages.DiscoverMessage;
import com.multipong.net.messages.JoinMessage;
import com.multipong.net.messages.Message;
import com.multipong.net.Sender.AddressedContent;
import com.multipong.net.messages.StartingMessage;
import com.multipong.net.messages.TellIPMessage;
import com.multipong.utility.DeviceIdUtility;
import com.multipong.utility.PlayerNameUtility;

import org.json.JSONObject;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static android.content.Context.WIFI_SERVICE;

public class Host implements Actor {

    private MultiplayerGameHostActivity activity;

    private Map<Integer, String> participants;

    public Host(MultiplayerGameHostActivity activity) {
        this.activity = activity;
        participants = new HashMap<>();
        Integer myID = DeviceIdUtility.getId();
        if (myID == null) {
            String uniqueID = UUID.randomUUID().toString();
            myID = NameResolutor.hashOf(uniqueID);
            DeviceIdUtility.setId(myID);
        }
        participants.put(myID, PlayerNameUtility.getPlayerName());
    }

    public void startGame() {
        Collection<InetAddress> addresses = new ArrayList<>();
        for (Integer id : participants.keySet())
            addresses.add(NameResolutor.INSTANCE.getNodeByHash(id));
        StartingMessage response = new StartingMessage();
        Log.d("Host", addresses.toString());
        response.addParticipants(participants);
        Log.d("Host", response.toString());
        for (InetAddress recipient : addresses) {
            AddressedContent content = new Sender.AddressedContent(response, recipient);
            activity.addMessageToQueue(content);
        }
    }

    @Override
    public synchronized void receive(String type, JSONObject message, InetAddress sender) {
        switch (type) {
            case Participant.MessageType.DISCOVER: discover(message, sender); break;
            case Participant.MessageType.JOIN: join(message, sender); break;
            case Participant.MessageType.CANCEL: cancel(message); break;
            case Participant.MessageType.ARE_YOU_THE_HOST: tell_ip(sender);
        }
    }

    private void discover(JSONObject json, InetAddress sender) {
        DiscoverMessage message = DiscoverMessage.createFromJson(json);
        Map<String, Object> fields = message.decode();
        String address = (String) fields.get(DiscoverMessage.YOUR_IP);
        try {
            NameResolutor.INSTANCE.addNode(DeviceIdUtility.getId(), InetAddress.getByName(address));
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
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
        Integer hasLeftId = (Integer) object.get(JoinMessage.ID_FIELD);
        if (participants.keySet().contains(hasLeftId))
            participants.remove(hasLeftId);
        NameResolutor.INSTANCE.removeNode(hasLeftId);
        Collection<InetAddress> addresses = new ArrayList<>();
        for (Integer id : participants.keySet())
            addresses.add(NameResolutor.INSTANCE.getNodeByHash(id));
        sendParticipantsListTo(addresses);
        activity.receiveList(participants.values());
    }

    private void sendParticipantsListTo(Collection<InetAddress> addresses) {
        AvailableMessage response = new AvailableMessage();
        response.addParticipants(participants.values());
        for (InetAddress recipient : addresses) {
            AddressedContent content = new Sender.AddressedContent(response, recipient);
            activity.addMessageToQueue(content);
        }
    }

    private void tell_ip(InetAddress sender) {
        Log.d("Telling","IP");
        TellIPMessage message = new TellIPMessage();
        AddressedContent content = new AddressedContent(message, sender);
        activity.addMessageToQueue(content);
    }

    public class MessageType {
        public static final String AVAILABLE = "AVAILABLE";
        public static final String STARTING = "STARTING";
        public static final String TELL_IP = "TELL_IP";
    }
}
