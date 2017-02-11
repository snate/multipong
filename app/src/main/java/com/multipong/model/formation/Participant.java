package com.multipong.model.formation;

import com.multipong.activity.MultiplayerGameFormationActivity;
import com.multipong.activity.MultiplayerGameJoinActivity;
import com.multipong.model.Actor;
import com.multipong.net.NameResolutor;
import com.multipong.net.messages.AvailableMessage;
import com.multipong.net.messages.CancelMessage;
import com.multipong.net.messages.DiscoverMessage;
import com.multipong.net.messages.JoinMessage;
import com.multipong.net.messages.KnownHostsMessage;
import com.multipong.net.messages.Message;
import com.multipong.net.messages.StartingMessage;
import com.multipong.net.send.Sender.AddressedContent;
import com.multipong.utility.DeviceIdUtility;

import org.json.JSONObject;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Participant implements Actor {

    private MultiplayerGameFormationActivity activity;
    private ArrayList<String> participants;
    private Map<Integer, String> hosts = new HashMap<>();
    private Collection<InetAddress> known_hosts = new ArrayList<>();
    private Integer currentHost;

    public Participant(MultiplayerGameFormationActivity activity) {
        this.activity = activity;
        Integer myID = DeviceIdUtility.getId();
        if (myID == null) {
            String uniqueID = UUID.randomUUID().toString();
            myID = NameResolutor.hashOf(uniqueID);
            DeviceIdUtility.setId(myID);
        }
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
                onStartingMessageReceived(message, sender);
                break;
        }
    }

    private void tellKnownHostsTo(InetAddress sender) {
        KnownHostsMessage message = new KnownHostsMessage();
        message.addHosts(known_hosts);
        AddressedContent reply = new AddressedContent(message, sender);
        activity.addMessageToQueue(reply);
    }

    private void pingHosts(JSONObject json) {
        KnownHostsMessage knownHostsMessage = KnownHostsMessage.createMessageFromJSON(json);
        Map<String, Object> fields= knownHostsMessage.decode();
        Collection<InetAddress> addresses = (Collection<InetAddress>) fields.get(KnownHostsMessage.KNOWN_HOSTS_FIELD);
        for (InetAddress address : addresses) {
            DiscoverMessage message = new DiscoverMessage().withIp(address.getHostAddress());
            AddressedContent content = new AddressedContent(message, address);
            activity.addMessageToQueue(content);
        }
    }

    private void onReceivingHostIp(InetAddress sender) {
        if (!known_hosts.contains(sender)) known_hosts.add(sender);
        DiscoverMessage message = new DiscoverMessage().withIp(sender.getHostAddress());
        AddressedContent content = new AddressedContent(message, sender);
        activity.addMessageToQueue(content);
    }

    private void onAvailableMessageReceived(JSONObject message, InetAddress sender) {
        if (!known_hosts.contains(sender)) known_hosts.add(sender);
        AvailableMessage msg = AvailableMessage.createMessageFromJSON(message);
        Map<String, Object> msgInfo = msg.decode();
        participants = (ArrayList<String>) msgInfo.get(AvailableMessage.PARTICIPANTS_FIELD);
        Integer id = (Integer) msgInfo.get(Message.ID_FIELD);
        String hostName = (String) msgInfo.get(Message.NAME_FIELD);
        hosts.put(id, hostName);
        NameResolutor.INSTANCE.addNode(id, sender);
        ((MultiplayerGameJoinActivity)activity).receiveList(id, hostName, participants);
    }

    private void onStartingMessageReceived(JSONObject message, InetAddress sender) {
        StartingMessage msg = StartingMessage.createMessageFromJSON(message);
        Map<String, Object> msgInfo = msg.decode();
        //TODO -> do something with msgInfo
        //probably start the game
    }

    public class MessageType {
        public static final String ARE_YOU_THE_HOST = "ARE_U_HOST";
        public static final String KNOWN_HOSTS = "KNOWN_HOSTS";
        public static final String DISCOVER = "DISCOVER";
        public static final String JOIN = "JOIN";
        public static final String CANCEL = "CANCEL";
    }
}
