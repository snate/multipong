package com.multipong.model.multiplayer;

import android.util.Log;

import com.multipong.activity.GameActivity;
import com.multipong.model.Actor;
import com.multipong.model.coordination.Coordination;
import com.multipong.model.game.MultiplayerGame;
import com.multipong.net.messages.PoisonPillMessage;
import com.multipong.net.messages.game.GameMessage;
import com.multipong.net.send.Sender.AddressedContent;

import org.json.JSONObject;

import java.net.InetAddress;

public class GameRouter implements Actor {

    private Coordination coordinationRef;
    private GameActivity activity;

    public GameRouter(GameActivity activity) {
        this.activity = activity;
        coordinationRef = new Coordination(activity);
    }

    @Override
    public void receive(String type, JSONObject message, InetAddress sender) {
        if (type.equals(MessageType.POISON_PILL)) {
            coordinationRef.cancelDiscovery();
            AddressedContent content = new AddressedContent(new PoisonPillMessage(), null);
            activity.addMessageToQueue(content);
            Log.d("POISON", "PILLED");
            return;
        }
        if (GameMessage.isForCoordination(message))
            coordinationRef.receive(type, message, sender);
        else {
            MultiplayerGame game = (MultiplayerGame) activity.getGame();
            game.getMSM().receive(type, message, sender);
        }
    }
}
