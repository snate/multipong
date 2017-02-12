package com.multipong.model;

import com.multipong.activity.MultiplayerGameFormationActivity;
import com.multipong.model.multiplayer.MultiplayerStateManager;
import com.multipong.net.messages.Message;
import com.multipong.net.send.Sender.AddressedContent;

import org.json.JSONObject;

import java.net.InetAddress;
import java.net.UnknownHostException;

public enum Coordination implements Actor {

    INSTANCE;

    // TODO: Refactor to give proper type to activity
    private MultiplayerGameFormationActivity activity;
    private static final String WIFI_P2P_GROUP_OWNER_ADDRESS = "192.168.49.1";
    private static final String WIFI_P2P_BROADCAST = "192.168.49.255";

    @Override
    public synchronized void receive(String type, JSONObject message, InetAddress sender) {
        switch (type) {
            case MultiplayerStateManager.MessageType.BALL_INFO:
                spreadToParticipants(message, sender);
                break;
        }
    }

    private void spreadToParticipants(JSONObject message, InetAddress sender) {
        // TODO: Add implementation
        // TODO: Avoid sending ball info to sender too (not necessary)
    }

    public void setActivity(MultiplayerGameFormationActivity activity) {
        this.activity = activity;
    }

    public void sendToCoordinator(Message message) {
        try {
            InetAddress address = InetAddress.getByName(WIFI_P2P_GROUP_OWNER_ADDRESS);
            AddressedContent content = new AddressedContent(message, address);
            activity.addMessageToQueue(content);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }
}
