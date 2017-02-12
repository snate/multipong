package com.multipong.model;

import android.util.Log;

import com.multipong.activity.GameActivity;
import com.multipong.model.game.MultiplayerGame;
import com.multipong.model.multiplayer.MultiplayerStateManager;
import com.multipong.net.NameResolutor;
import com.multipong.net.messages.game.BallInfoMessage;
import com.multipong.net.send.Sender.AddressedContent;
import com.multipong.utility.DeviceIdUtility;

import org.json.JSONObject;

import java.net.InetAddress;
import java.util.Collection;

public class Coordination implements Actor {

    private GameActivity activity;

    public Coordination(GameActivity activity) {
        this.activity = activity;
    }

    @Override
    public synchronized void receive(String type, JSONObject message, InetAddress sender) {
        Log.d("Coordination", "Message received");
        switch (type) {
            case MultiplayerStateManager.MessageType.BALL_INFO:
                spreadToParticipants(message, sender);
                break;
        }
    }

    private void spreadToParticipants(JSONObject json, InetAddress sender) {
        BallInfoMessage message = BallInfoMessage.createFromJson(json);
        message.forCoordinator(false);
        MultiplayerGame multiplayerGame = (MultiplayerGame) activity.getGame();
        MultiplayerStateManager msm = multiplayerGame.getMSM();
        // Send message to participants
        Collection<Integer> ids = msm.getActivePlayers();
        for (Integer id : ids) {
            InetAddress address = NameResolutor.INSTANCE.getNodeByHash(id);
            AddressedContent content = new AddressedContent(message, address);
            // Avoid sending ball info to local MSM
            // && Avoid sending ball info to sender too (not necessary)
            if (!id.equals(DeviceIdUtility.getId()) && !(address.equals(sender)))
                activity.addMessageToQueue(content);
        }
        // Forward message to local MSM
        Actor actor = activity.getActor();
        actor.receive(MultiplayerStateManager.MessageType.BALL_INFO, json, sender);
    }
}
