package com.multipong.model;

import android.util.Log;

import com.multipong.activity.GameActivity;
import com.multipong.persistence.MultipongDatabase;

import com.multipong.model.MultiplayerStateManager.BallInfo;

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

        private Object forBallToComeBack = new Object();

        public MultiplayerGameThread(String playerName, GameActivity activity) {
            super(playerName, activity);
            MultiplayerStateManager msm = new MultiplayerStateManager();
        }

        /*
        TODO modify to implement multiplayer logic
         */
        @Override
        public void ballOnTopOfTheField() {
            myTurn = false;
            activity.makeBallDisappear();
            double newSpeedX = getXFactor() * - 1;
            double newSpeedY = getYFactor() * - 1;
            double newPos = 1 - getFinalX();
            BallInfo info = MultiplayerStateManager.createBallInfo(newSpeedX, newSpeedY, newPos);
            // TODO: Send ball out to MSM
            synchronized (forBallToComeBack) {
                try {
                    forBallToComeBack.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
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

        @Override
        public void ballBounced() {
            double posX = getX();
            double speedX = getXFactor();
            double speedY = getYFactor() * (-1.0);
            double range = getRange();
            int movements = (int) Math.ceil(range / speedY);
            double xFinalPosition = posX + movements * speedX / range;
            Log.d("Speed X", String.valueOf(speedX));
            Log.d("Speed Y", String.valueOf(speedY));
            Log.d("Movements", String.valueOf(movements));
            Log.d("X movement", String.valueOf(xFinalPosition));
            int leftEdge = (int) Math.floor(xFinalPosition);
            xFinalPosition = xFinalPosition - leftEdge;
            if (leftEdge % 2 != 0) xFinalPosition = 1 - xFinalPosition;
            if (0 <= xFinalPosition && xFinalPosition <= 1) {
                Log.d("Predictor", "Predicted: " + xFinalPosition);
            }
        }
    }
}
