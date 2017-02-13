package com.multipong.model.coordination;

import android.util.Log;

import com.multipong.activity.GameActivity;
import com.multipong.activity.NetworkingActivity;
import com.multipong.model.Actor;
import com.multipong.model.game.MultiplayerGame;
import com.multipong.model.multiplayer.MultiplayerStateManager;
import com.multipong.model.multiplayer.MultiplayerStateManager.Player;
import com.multipong.net.NameResolutor;
import com.multipong.net.Utils;
import com.multipong.net.messages.game.AreYouAliveMessage;
import com.multipong.net.messages.game.BallInfoMessage;
import com.multipong.net.send.AckUDPSender.ReliablyDeliverableAddressedContent;
import com.multipong.net.send.Sender.AddressedContent;
import com.multipong.utility.DeviceIdUtility;

import org.json.JSONObject;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.Timer;
import java.util.TimerTask;

public class Coordination implements Actor {

    private GameActivity activity;
    private static Timer pinger = new Timer();

    public Coordination(GameActivity activity) {
        this.activity = activity;
        synchronized (pinger) {
            cancelDiscovery();
        }
        pinger = new Timer();
        if (Math.random() > 0.5) // TODO: if(imTheGO) { ... }
            pinger.scheduleAtFixedRate(new NGOPinger(activity), 0, 2500);
        else
            pinger.scheduleAtFixedRate(new GOPinger(), (long) (Math.random()*8500), 8500);
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

    public void cancelDiscovery() {
        pinger.cancel();
    }

    private class NGOPinger extends Pinger {

        Player currentPlayer;

        NGOPinger(GameActivity activity) {
            super(activity);
        }

        @Override
        protected int getAttempts() {
            return 2;
        }

        @Override
        protected InetAddress getPinged() {
            currentPlayer = msm.getCurrentPlayer();
            return NameResolutor.INSTANCE.getNodeByHash(currentPlayer.getId());
        }

        @Override
        protected void pingedIsNotAlive() {
            msm.removePlayer(currentPlayer);
        }
    }

    private class GOPinger extends Pinger {

        GOPinger(GameActivity activity) {
            super(activity);
        }

        @Override
        protected int getAttempts() {
            return 5;
        }

        @Override
        protected InetAddress getPinged() {
            try {
                return InetAddress.getByName(Utils.WIFI_P2P_GROUP_OWNER_ADDRESS);
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void pingedIsNotAlive() {
            // TODO: GO has crashed
        }
    }

    public class MessageType {
        public static final String AYA = "AYA";
    }
}
