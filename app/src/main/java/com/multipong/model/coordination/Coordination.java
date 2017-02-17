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
import com.multipong.net.send.AckUDPSender.ReliablyDeliverableAddressedContent;
import com.multipong.net.send.Sender.AddressedContent;
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
        sendToNext(message, lastInfo.getNextPlayer());
    }

    private void saveLastBallInfo(BallInfoMessage message) {
        Map<String, Object> fields = message.decode();
        lastInfo = (BallInfo) fields.get(BallInfoMessage.DECODED_BALL);
    }

    public void cancelDiscovery() {
        pinger.cancel();
    }

    private void sendToNext(BallInfoMessage ballInfoMsg, Integer nextPlayerId) {
        MultiplayerGame multiplayerGame = (MultiplayerGame) activity.getGame();
        MultiplayerStateManager msm = multiplayerGame.getMSM();
        Player nextPlayer = new Player(nextPlayerId);
        // save current list of players
        ArrayList<Player> oldPlayers = new ArrayList<>();
        for (Integer id : msm.getActivePlayers())
            oldPlayers.add(new Player(id));
        // Send to everyone
        ReliablyDeliverableAddressedContent rdac = null;
        for (Integer playerId : msm.getActivePlayers()) {
            InetAddress address = NameResolutor.INSTANCE.getNodeByHash(playerId);
            AddressedContent addressedContent = new AddressedContent(ballInfoMsg, address);
            // Try to send rdac to next one
            if (playerId.equals(nextPlayer.getId())) {
                rdac = new ReliablyDeliverableAddressedContent(ballInfoMsg, address);
                addressedContent = rdac;
            }
            activity.addMessageToQueue(addressedContent);
        }
        // Wait for rdac to be notified
        synchronized (rdac) {
            try {
                rdac.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        // Find out if rdac was delivered
        Boolean delivered = rdac.getB();
        // if tt, do nothing
        if (delivered) return;
        // otherwise, we are sending the ball to a dead player!
        // send death information to everyone
        sendDeath(nextPlayer);
        // take next
        Player nextNextPlayer = msm.getExtractor().getNext(oldPlayers, nextPlayer);
        Map<String, Object> fields = ballInfoMsg.decode();
        BallInfo ballInfo = (BallInfo) fields.get(BallInfoMessage.DECODED_BALL);
        ballInfo.withNextPlayer(nextNextPlayer.getId());
        BallInfoMessage bim = new BallInfoMessage().addBallInfo(ballInfo)
                                                   .addNextPlayerInfo(nextNextPlayer.getId());
        sendToNext(bim, nextNextPlayer.getId());
    }

    private boolean sendDeath(Player deadOne) {
        MultiplayerGame multiplayerGame = (MultiplayerGame) activity.getGame();
        MultiplayerStateManager msm = multiplayerGame.getMSM();
        boolean wasRemoved = msm.removePlayer(deadOne);
        if (!wasRemoved) return false;
        Collection<Integer> players = msm.getActivePlayers();
        for (Integer player : players) {
            // Notify other players of player's death
            DeathMessage deathMessage = new DeathMessage().withDead(deadOne.getId());
            InetAddress address = NameResolutor.INSTANCE.getNodeByHash(player);
            deathMessage.forCoordination(false);
            AddressedContent content = new AddressedContent(deathMessage, address);
            activity.addMessageToQueue(content);
        }
        return true;
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
            boolean wasRemoved = sendDeath(currentPlayer);
            if (!wasRemoved) return;
            // Send ball to next player after `currentPlayer died by using last BallInfo msg
            Player nextPlayer = msm.getExtractor().getNext(oldPlayers, currentPlayer);
            Log.e("GOMORTO", nextPlayer.toString());
            if (lastInfo == null) {
                // What if current coordinator has just been elected? (lastBallInfo == null)
                //      => compute ballInfo randomly
                // (first player died)
                lastInfo = MultiplayerStateManager.createBallInfo(Math.random(), Math.random(), Math.random())
                        .tellIfStillInGame(true);
                nextPlayer = new Player(oldPlayers.get(1).getId());
            }
            lastInfo = lastInfo.withNextPlayer(nextPlayer.getId());
            BallInfoMessage message = new BallInfoMessage();
            message.addBallInfo(lastInfo);
            message.forCoordination(false);
            sendToNext(message, nextPlayer.getId());
            Log.e("PROVAMORTOMORTO", "GO MORTO");
            /*int score = ((MultiplayerGame)activity.getGame()).getScore();
            activity.endGame(score, false);*/
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
            Log.e("MORTOMORTO", "GO MORTO");
            int score = ((MultiplayerGame)activity.getGame()).getScore();
            activity.endGame(score,false);
            //activity.onBackPressed();
        }
    }

    public class MessageType {
        public static final String AYA = "AYA";
        public static final String DEATH = "DEA";
    }}
