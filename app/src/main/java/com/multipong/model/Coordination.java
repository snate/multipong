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
        message.forCoordination(false);
        MultiplayerGame multiplayerGame = (MultiplayerGame) activity.getGame();
        MultiplayerStateManager msm = multiplayerGame.getMSM();
        // Forward message to local MSM
        msm.receive(MultiplayerStateManager.MessageType.BALL_INFO, json, sender);
        // Send message to participants
        Collection<Integer> ids = msm.getActivePlayers();
        Log.d("a", ids.toString());
        for (Integer id : ids) {
            InetAddress address = NameResolutor.INSTANCE.getNodeByHash(id);
            Log.d("a", address.getHostAddress());
            AddressedContent content = new AddressedContent(message, address);
            // Avoid sending ball info to local MSM
            // && Avoid sending ball info to sender too (not necessary)
            if (!id.equals(DeviceIdUtility.getId()) && !(address.equals(sender)))
                activity.addMessageToQueue(content);
        }
    }

    public class MessageType {
        public static final String AYA = "AYA";
    }
}
