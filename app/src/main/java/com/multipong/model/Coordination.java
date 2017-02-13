package com.multipong.model;

import android.util.Log;

import com.multipong.activity.GameActivity;
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
            pinger.scheduleAtFixedRate(new NGOPinger(), 0, 2000);
        else
            pinger.scheduleAtFixedRate(new GOPinger(), 0, 6000);
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

    private class NGOPinger extends TimerTask {
        @Override
        public void run() {
            MultiplayerGame multiplayerGame = (MultiplayerGame) activity.getGame();
            MultiplayerStateManager msm = multiplayerGame.getMSM();
            Player currentPlayer = msm.getCurrentPlayer();
            AreYouAliveMessage ayaMessage = new AreYouAliveMessage();
            InetAddress address = NameResolutor.INSTANCE.getNodeByHash(currentPlayer.getId());
            ReliablyDeliverableAddressedContent rdac =
                    new ReliablyDeliverableAddressedContent(ayaMessage, address);
            activity.addMessageToQueue(rdac);
            Boolean messagehasBeenSent = rdac.getB();
            synchronized (messagehasBeenSent) {
                try {
                    messagehasBeenSent.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            if (!messagehasBeenSent)
                msm.removePlayer(currentPlayer);
        }
    }

    private class GOPinger extends TimerTask {
        @Override
        public void run() {
            AreYouAliveMessage ayaMessage = new AreYouAliveMessage();
            InetAddress address = null;
            try {
                address = InetAddress.getByName(Utils.WIFI_P2P_GROUP_OWNER_ADDRESS);
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
            ReliablyDeliverableAddressedContent rdac =
                    new ReliablyDeliverableAddressedContent(ayaMessage, address);
            Boolean messagehasBeenSent = rdac.getB();
            activity.addMessageToQueue(rdac);
            synchronized (messagehasBeenSent) {
                try {
                    messagehasBeenSent.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            if (!messagehasBeenSent)
                // TODO: GO has crashed
                return;
        }
    }

    public class MessageType {
        public static final String AYA = "AYA";
    }
}
