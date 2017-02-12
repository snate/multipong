package com.multipong.model;

import com.multipong.activity.NetworkingActivity;
import com.multipong.model.multiplayer.MultiplayerStateManager;
import com.multipong.net.NameResolutor;
import com.multipong.net.messages.game.BallInfoMessage;
import com.multipong.net.send.Sender.AddressedContent;
import com.multipong.utility.DeviceIdUtility;

import org.json.JSONObject;

import java.net.InetAddress;
import java.util.Collection;

public class Coordination implements Actor {

    private NetworkingActivity activity;

    @Override
    public synchronized void receive(String type, JSONObject message, InetAddress sender) {
        switch (type) {
            case MultiplayerStateManager.MessageType.BALL_INFO:
                spreadToParticipants(message, sender);
                break;
        }
    }

    private void spreadToParticipants(JSONObject json, InetAddress sender) {
        BallInfoMessage message = BallInfoMessage.createFromJson(json);
        if (message.isForCoordinator()) {
            message = message.forCoordinator(false);
            MultiplayerStateManager msm = (MultiplayerStateManager) activity.getActor();
            // Send message to participants
            Collection<Integer> ids = msm.getActivePlayers();
            for (Integer id : ids) {
                InetAddress address = NameResolutor.INSTANCE.getNodeByHash(id);
                AddressedContent content = new AddressedContent(message, address);
                // Avoid sending ball info to local MSM
                // && Avoid sending ball info to sender too (not necessary)
                if (!id.equals(DeviceIdUtility.getId()) && !(address.equals(sender))
                    activity.addMessageToQueue(content);
            }
            // Forward message to local MSM
            Actor actor = activity.getActor();
            actor.receive(MultiplayerStateManager.MessageType.BALL_INFO, json, sender);
        } else {
            Actor actor = activity.getActor();
            actor.receive(MultiplayerStateManager.MessageType.BALL_INFO, json, sender);
        }
    }

    public void setActivity(NetworkingActivity activity) {
        this.activity = activity;
    }
}
