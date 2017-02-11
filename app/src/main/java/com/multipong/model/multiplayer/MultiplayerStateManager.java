package com.multipong.model.multiplayer;

import com.multipong.model.game.MultiplayerGame;

import java.util.ArrayList;

public class MultiplayerStateManager {

    private MultiplayerGame game;
    private State state;

    public MultiplayerStateManager(MultiplayerGame multiplayerGameThread) {
        game = multiplayerGameThread;
        state = new State();
    }

    public void sendBallToNext(BallInfo ballInfo){
        PlayerExtractor extractor = new ConsecutivePlayerExtractor();
        extractor.getNext(state.activePlayers, state.me);
        // TODO: Send ball info to coordinator via net
        // TODO: Remove the following stub call
        receiveData(ballInfo);
    }

    public void receiveData(BallInfo info) {
        game.newTurn(info);
    }

    public static BallInfo createBallInfo(double speedX, double speedY, double position) {
        return new BallInfo().withBallSpeedX(speedX)
                             .withBallSpeedY(speedY)
                             .withPosition(position);
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

        Player(Integer id) {
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
    }

    public static class BallInfo {
        private double ballSpeedX;
        private double ballSpeedY;
        private double position;

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
    }
}
