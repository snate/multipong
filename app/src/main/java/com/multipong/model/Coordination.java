package com.multipong.model;

import com.multipong.activity.MultiplayerGameFormationActivity;
import com.multipong.model.multiplayer.MultiplayerStateManager;
import com.multipong.net.messages.Message;
import com.multipong.net.messages.game.BallInfoMessage;
import com.multipong.net.send.Sender.AddressedContent;

import org.json.JSONObject;

import java.net.InetAddress;

public class Coordination implements Actor {

    // TODO: Refactor to give proper type to activity
    private MultiplayerGameFormationActivity activity;

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
            // TODO: Send message to peers
            // TODO: Avoid sending ball info to sender too (not necessary)
            // TODO: Avoid sending ball info to coordinator node (but deliver the message to MSM!)
        } else {
            // TODO: Forward message to current node's MSM
        }
    }

    public void setActivity(MultiplayerGameFormationActivity activity) {
        this.activity = activity;
    }
}
