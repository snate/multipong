package com.multipong.model.coordination;

import android.util.Log;

import com.multipong.activity.GameActivity;
import com.multipong.model.Actor;
import com.multipong.model.game.MultiplayerGame;
import com.multipong.model.multiplayer.MultiplayerStateManager;
import com.multipong.model.multiplayer.MultiplayerStateManager.BallInfo;
import com.multipong.model.multiplayer.MultiplayerStateManager.Player;
import com.multipong.net.NameResolutor;
import com.multipong.net.Utils;
import com.multipong.net.messages.game.BallInfoMessage;
import com.multipong.net.messages.game.DeathMessage;
import com.multipong.net.send.Sender.AddressedContent;
import com.multipong.utility.DeviceIdUtility;
import com.multipong.utility.GOUtility;

import org.json.JSONObject;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Timer;

public class Coordination implements Actor {

    private GameActivity activity;
    private static Timer pinger = new Timer();
    private BallInfo lastInfo;

    public Coordination(GameActivity activity) {
        this.activity = activity;
        synchronized (pinger) {
            cancelDiscovery();
        }
        pinger = new Timer();
        if (GOUtility.imTheGo())
            pinger.scheduleAtFixedRate(new NGOPinger(activity), 10000, 2500);
        else
            pinger.scheduleAtFixedRate(new GOPinger(activity), 10000 + (long) (Math.random()*8500), 8500);
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
        saveLastBallInfo(message);
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

    private void saveLastBallInfo(BallInfoMessage message) {
        Map<String, Object> fields = message.decode();
        double speedX = (double) fields.get(BallInfoMessage.SPEED_X_FIELD);
        double speedY = (double) fields.get(BallInfoMessage.SPEED_Y_FIELD);
        double position = (double) fields.get(BallInfoMessage.POSITION_FIELD);
        int nextPlayer = (Integer) fields.get(BallInfoMessage.NEXT_FIELD);
        boolean stillInGame = (boolean) fields.get(BallInfoMessage.STILL_IN_GAME_FIELD);
        lastInfo = MultiplayerStateManager.createBallInfo(speedX, speedY, position)
                                          .withNextPlayer(nextPlayer)
                                          .tellIfStillInGame(stillInGame);
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
            boolean wasRemoved = msm.removePlayer(currentPlayer);
            if (wasRemoved) {
                Collection<Integer> players = msm.getActivePlayers();
                for (Integer player : players) {
                    // Notify other players of player's death
                    DeathMessage deathMessage = new DeathMessage().withDead(currentPlayer.getId());
                    InetAddress address = NameResolutor.INSTANCE.getNodeByHash(player);
                    AddressedContent content = new AddressedContent(deathMessage, address);
                    activity.addMessageToQueue(content);
                }
                // Send ball to next player after `currentPlayer died by using last BallInfo msg
                BallInfoMessage message = new BallInfoMessage();
                if (lastInfo == null) {
                    // What if current coordinator has just been elected? (lastBallInfo == null)
                    //      => compute ballInfo randomly
                    // (first player died)
                    BallInfo info = MultiplayerStateManager.createBallInfo(Math.random(), Math.random(), Math.random())
                            .tellIfStillInGame(true).withNextPlayer(oldPlayers.get(0).getId());
                }
                message.addBallInfo(lastInfo);
                ArrayList<Integer> ids = new ArrayList<>(msm.getActivePlayers());
                Player nextPlayer = msm.getExtractor().getNext(oldPlayers, currentPlayer);
                InetAddress address = NameResolutor.INSTANCE.getNodeByHash(nextPlayer.getId());
                AddressedContent content = new AddressedContent(message, address);
                activity.addMessageToQueue(content);
            }
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
        public static final String DEATH = "DEA";
    }
}
