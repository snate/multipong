package com.multipong.model;

import com.multipong.activity.GameActivity;
import com.multipong.persistence.MultipongDatabase;

/**
 * @author Marco Zanella
 * @version 0.01
 * @since 0.01
 */
public class MultiplayerGame extends Game {
    private GameActivity activity;
    private String playerName;
    private MultipongDatabase database;

    //TODO invoke setNumberOfLives before start on MultiGame to have a game with more than one live
    @Override
    public void start(String playerName) {
        this.playerName = playerName;
        if (currentGame == null) {
            currentGame = new MultiplayerGameThread(playerName, activity);
            new Thread(currentGame).start();
        }

    }

    /*
    TODO call this method when is the turn of the player
     */
    public void newTurn(double x){
        ((MultiplayerGameThread)currentGame).newPlayerTurn(x);
    }

    private class MultiplayerGameThread extends AbsGameThread {
        private volatile boolean myTurn = false;
        private double newX;

        public MultiplayerGameThread(String playerName, GameActivity activity) {
            super(playerName, activity);
        }

        /*
        TODO modify to implement multiplayer logic
         */
        @Override
        public void ballOnTopOfTheField() {
            myTurn = false;
        }

        @Override
        public void initialBallPosition() {
            if (myTurn)
                setX(newX);
        }

        public void newPlayerTurn(double x) {
            newX = x;
            myTurn = true;
        }
    }
}
