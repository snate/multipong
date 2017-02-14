package com.multipong.model.multiplayer;

import android.util.Log;

import com.multipong.activity.GameActivity;
import com.multipong.model.Actor;
import com.multipong.model.game.MultiplayerGame;
import com.multipong.net.Utils;
import com.multipong.net.messages.game.BallInfoMessage;
import com.multipong.net.send.Sender.AddressedContent;
import com.multipong.utility.DeviceIdUtility;

import org.json.JSONObject;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class MultiplayerStateManager implements Actor {

    private GameActivity activity;
    private MultiplayerGame game;
    private State state;
    private PlayerExtractor extractor;

    public MultiplayerStateManager(MultiplayerGame multiplayerGameThread, Integer hostId, GameActivity activity) {
        this.activity = activity;
        game = multiplayerGameThread;
        state = new State();
        state.setCurrentActivePlayer(new Player(hostId));
        state.setMe(new Player(DeviceIdUtility.getId()));
        extractor = new ConsecutivePlayerExtractor();
    }

    public PlayerExtractor getExtractor() {
        return extractor;
    }

    public void sendBallToNext(BallInfo ballInfo){
        Player next = extractor.getNext(state.activePlayers, state.me);
        // Send ball info to coordinator via net
        BallInfoMessage ballInfoMessage = new BallInfoMessage()
                                             .addBallInfo(ballInfo)
                                             .addNextPlayerInfo(next.id);
        ballInfoMessage.forCoordination(true);
        try {
            InetAddress address = InetAddress.getByName(Utils.WIFI_P2P_GROUP_OWNER_ADDRESS);
            AddressedContent content = new AddressedContent(ballInfoMessage, address);
            activity.addMessageToQueue(content);
            // TODO: check if next player received the ball
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void receive(String type, JSONObject message, InetAddress sender) {
        switch (type) {
            case MessageType.BALL_INFO:
                handleBallInfo(message);
                break;
        }
    }

    private synchronized void handleBallInfo(JSONObject json) {
        BallInfoMessage message = BallInfoMessage.createFromJson(json);
        Map<String, Object> fields = message.decode();
        Player nextPlayer = new Player((Integer) fields.get(BallInfoMessage.NEXT_FIELD));
        Log.d("Next", nextPlayer.toString());
        Log.d("Me", state.me.toString());

        // Update state
        boolean previousIsStillInGame = (boolean) fields.get(BallInfoMessage.STILL_IN_GAME_FIELD);
        if (!previousIsStillInGame)
            state.removePlayer(new Player((Integer) fields.get(BallInfoMessage.ID_FIELD)));
        state.currentActivePlayer = new Player((Integer) fields.get(BallInfoMessage.NEXT_FIELD));

        //If I'm the last player in the game
        if (state.activePlayers.size() == 1) {
            activity.endGame(game.getScore(), true);
        } else { //If there is (at least) one another player

            // If next player is me, invoke receiveData method
            if (nextPlayer.equals(state.me)) {
                Log.d("Ball", "Incoming");
                double speedX = (double) fields.get(BallInfoMessage.SPEED_X_FIELD);
                double speedY = (double) fields.get(BallInfoMessage.SPEED_Y_FIELD);
                double startingPosition = (double) fields.get(BallInfoMessage.POSITION_FIELD);
                BallInfo ballInfo = createBallInfo(speedX, speedY, startingPosition);
                game.increaseSpeed();
                game.newTurn(ballInfo);
            }
        }
    }

    public Collection<Integer> getActivePlayers() {
        Collection<Integer> ids = new ArrayList<>();
        for (Player player : state.activePlayers)
            ids.add(player.id);
        return ids;
    }

    public void addPlayers(List<Integer> ids) {
        for (int i : ids)
            state.addPlayer(new Player(i));
    }

    public synchronized Player getCurrentPlayer() {
        return state.currentActivePlayer;
    }

    public synchronized boolean removePlayer(Player player) {
        if (!player.equals(state.currentActivePlayer))
            return false;
        state.removePlayer(player);
        return true;
    }

    public synchronized void setInitialPlayer(Integer initialPlayer) {
        if (state.currentActivePlayer == null)
            state.setCurrentActivePlayer(new Player(initialPlayer));
    }

    private class State {
        public Player me;
        public Player currentActivePlayer;
        public ArrayList<Player> activePlayers = new ArrayList<>();

        public void setMe(Player me) {
            this.me = me;
        }

        public void setCurrentActivePlayer(Player currentActivePlayer) {
            this.currentActivePlayer = currentActivePlayer;
        }

        public void addPlayer(Player player) {
            activePlayers.add(player);
        }

        public void removePlayer(Player player) {
            activePlayers.remove(player);
        }
    }

    // this wrapper avoids possible weird behaviour of removing an integer from an ArrayList
    public static class Player {
        Integer id;

        public Player(Integer id) {
            this.id = id;
        }

        // TODO: Enforce the fact that ids of different players are effectively different
        @Override
        public boolean equals(Object obj) {
            if(!(obj instanceof Player)) return false;
            return id.equals(((Player) obj).id);
        }

        @Override
        public String toString() {
            return String.valueOf(id.intValue());
        }

        public Integer getId() {
            return id;
        }
    }

    public static class BallInfo {
        private double ballSpeedX;
        private double ballSpeedY;
        private double position;
        private boolean stillInGame;
        private int nextPlayer;

        public double getBallSpeedX() {
            return ballSpeedX;
        }

        public BallInfo withBallSpeedX(double ballSpeedX) {
            this.ballSpeedX = ballSpeedX;
            return this;
        }

        public double getBallSpeedY() {
            return ballSpeedY;
        }

        public BallInfo withBallSpeedY(double ballSpeedY) {
            this.ballSpeedY = ballSpeedY;
            return this;
        }

        public double getPosition() {
            return position;
        }

        public BallInfo withPosition(double position) {
            this.position = position;
            return this;
        }

        public int getNextPlayer() {
            return nextPlayer;
        }

        public BallInfo withNextPlayer(int nextPlayer) {
            this.nextPlayer = nextPlayer;
            return this;
        }

        public boolean getStillInGame() {
            return stillInGame;
        }

        public BallInfo tellIfStillInGame(boolean yesOrNo) {
            stillInGame = yesOrNo;
            return this;
        }
    }

    public static BallInfo createBallInfo(double speedX, double speedY, double position) {
        return new BallInfo().withBallSpeedX(speedX)
                .withBallSpeedY(speedY)
                .withPosition(position);
    }

    public class MessageType {
        public static final String BALL_INFO = "BALL_INFO";
    }
}
