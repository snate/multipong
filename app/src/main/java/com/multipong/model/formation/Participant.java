package com.multipong.model.formation;

import android.content.Intent;
import android.util.Log;

import com.multipong.activity.GameActivity;
import com.multipong.activity.MainActivity;
import com.multipong.activity.MultiplayerGameFormationActivity;
import com.multipong.activity.MultiplayerGameJoinActivity;
import com.multipong.model.Actor;
import com.multipong.net.NameResolutor;
import com.multipong.net.messages.Message;
import com.multipong.net.messages.gameformation.AvailableMessage;
import com.multipong.net.messages.gameformation.CancelMessage;
import com.multipong.net.messages.gameformation.DiscoverMessage;
import com.multipong.net.messages.gameformation.JoinMessage;
import com.multipong.net.messages.gameformation.KnownHostsMessage;
import com.multipong.net.messages.gameformation.StartingMessage;
import com.multipong.net.messages.gameformation.StartingMessage.StartingGameInfo;
import com.multipong.net.send.Sender;
import com.multipong.net.send.Sender.AddressedContent;
import com.multipong.utility.PlayerNameUtility;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Participant implements Actor {

    private MultiplayerGameFormationActivity activity;
    private Map<Integer, String> participants = new HashMap<>();
    private Map<Integer, String> hosts = new HashMap<>();
    private InetAddress known_host;
    private Integer currentHost;

    public Participant(MultiplayerGameFormationActivity activity) {
        this.activity = activity;
    }

    public synchronized void join(Integer hostId) {
        if (currentHost != null) {
            InetAddress oldHost = NameResolutor.INSTANCE.getNodeByHash(currentHost);
            CancelMessage cancelMessage = new CancelMessage();
            AddressedContent cancellation = new AddressedContent(cancelMessage, oldHost);
            activity.addMessageToQueue(cancellation);
        }
        currentHost = hostId;
        InetAddress address = NameResolutor.INSTANCE.getNodeByHash(hostId);
        JoinMessage joinMessage = new JoinMessage();
        AddressedContent joinRequest = new AddressedContent(joinMessage, address);
        activity.addMessageToQueue(joinRequest);
    }

    public void cancelGame() {
        if (currentHost == null) return;
        InetAddress oldHost = NameResolutor.INSTANCE.getNodeByHash(currentHost);
        CancelMessage cancelMessage = new CancelMessage();
        AddressedContent cancellation = new AddressedContent(cancelMessage, oldHost);
        activity.addMessageToQueue(cancellation);
    }

    @Override
    public synchronized void receive(String type, JSONObject message, InetAddress sender) {
        switch (type) {
            case MessageType.ARE_YOU_THE_HOST:
                tellKnownHostsTo(sender);
                break;
            case MessageType.KNOWN_HOSTS:
                pingHosts(message);
                break;
            case Host.MessageType.TELL_IP:
                onReceivingHostIp(sender);
                break;
            case Host.MessageType.AVAILABLE:
                onAvailableMessageReceived(message, sender);
                break;
            case Host.MessageType.STARTING:
                Log.d("startingmsg", "received");
                onStartingMessageReceived(message, sender);
                break;
        }
    }

    private void tellKnownHostsTo(InetAddress sender) {
        KnownHostsMessage message = new KnownHostsMessage();
        message.addHost(known_host);
        AddressedContent reply = new AddressedContent(message, sender);
        activity.addMessageToQueue(reply);
    }

    private void pingHosts(JSONObject json) {
        KnownHostsMessage knownHostsMessage = KnownHostsMessage.createMessageFromJSON(json);
        Map<String, Object> fields= knownHostsMessage.decode();
        InetAddress address = (InetAddress) fields.get(KnownHostsMessage.KNOWN_HOST_FIELD);
        DiscoverMessage message = new DiscoverMessage().withIp(address.getHostAddress());
        AddressedContent content = new AddressedContent(message, address);
        activity.addMessageToQueue(content);
    }

    private void onReceivingHostIp(InetAddress sender) {
        if (known_host != sender) known_host = sender;
        DiscoverMessage message = new DiscoverMessage().withIp(sender.getHostAddress());
        AddressedContent content = new AddressedContent(message, sender);
        activity.addMessageToQueue(content);
    }

    private void onAvailableMessageReceived(JSONObject message, InetAddress sender) {
        if (known_host != sender) known_host = sender;
        AvailableMessage msg = AvailableMessage.createMessageFromJSON(message);
        Map<String, Object> msgInfo = msg.decode();
        participants = (Map<Integer, String>) msgInfo.get(AvailableMessage.PARTICIPANTS_FIELD);
        Log.d("Participant - 1", message.toString());
        Log.d("Participant - 2", msgInfo.get(AvailableMessage.PARTICIPANTS_FIELD).toString());
        Log.d("Participant - 3", participants.values().toString());
        Integer id = (Integer) msgInfo.get(Message.ID_FIELD);
        String hostName = (String) msgInfo.get(Message.NAME_FIELD);
        hosts.put(id, hostName);
        NameResolutor.INSTANCE.addNode(id, sender);

        List<String> names = new ArrayList<>(participants.values());
        ((MultiplayerGameJoinActivity)activity).receiveList(id, hostName, names);
    }

    private void onStartingMessageReceived(JSONObject message, InetAddress sender) {
        StartingMessage msg = StartingMessage.createMessageFromJSON(message);
        Map<String, Object> msgInfo = msg.decode();
        ArrayList<StartingGameInfo> gameInfos =
                (ArrayList<StartingGameInfo>) msgInfo.get(StartingMessage.PARTICIPANTS_FIELD);
        participants = new HashMap<>();
        for (StartingGameInfo sgi : gameInfos) {
            NameResolutor.INSTANCE.addNode(sgi.getId(), sgi.getAddress());
            participants.put(sgi.getId(), sgi.getName());
        }
        Log.d("Participant", "Starting...");
        Intent intent = new Intent(activity.getApplicationContext(), GameActivity.class)
                .putExtra(PLAYER_NAME, PlayerNameUtility.getPlayerName())
                .putExtra(MainActivity.IS_MULTI, true)
                .putIntegerArrayListExtra(PLAYERS, getPlayerIDs());
        activity.startActivity(intent);
    }

    private void forwardDiscover(InetAddress sender) {
        AvailableMessage response = new AvailableMessage();
        response.addParticipants(participants);
        AddressedContent content = new Sender.AddressedContent(response, sender);
        activity.addMessageToQueue(content);
    }

    private void forwardJoin(JSONObject json, InetAddress sender) {
        if (known_host == null) return;
        // forward request
        JoinMessage joinMessage = JoinMessage.createFromJson(json);
        AddressedContent joinRequest = new AddressedContent(joinMessage, known_host);
        activity.addMessageToQueue(joinRequest);
        // send available information back to other participant
        Map<String, Object> object = joinMessage.decode();
        Integer newId = (Integer) object.get(JoinMessage.ID_FIELD);
        String name = (String) object.get(Message.NAME_FIELD);
        NameResolutor.INSTANCE.addNode(newId, sender);
        AvailableMessage response = new AvailableMessage();
        participants.put(newId, name);
        response.addParticipants(participants);
        AddressedContent content = new Sender.AddressedContent(response, sender);
        activity.addMessageToQueue(content);
        Log.d("ASIHAISFOAGD","SDFSDFDSBFUS");
        Log.d("IP address", sender.getHostAddress().toString());
    }

    public ArrayList<Integer> getPlayerIDs() {
        ArrayList<Integer> ids = new ArrayList<>(participants.keySet());
        Collections.sort(ids);
        return ids;
    }

    public static final String PLAYER_NAME = "com.multipong.PLAYER_NAME";
    public static final String PLAYERS = "player_list";

    public class MessageType {
        public static final String ARE_YOU_THE_HOST = "ARE_U_HOST";
        public static final String KNOWN_HOSTS = "KNOWN_HOSTS";
        public static final String DISCOVER = "DISCOVER";
        public static final String JOIN = "JOIN";
        public static final String CANCEL = "CANCEL";
    }
}
