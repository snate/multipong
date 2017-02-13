package com.multipong.model.game;

import android.util.Log;

import com.multipong.activity.GameActivity;
import com.multipong.model.multiplayer.MultiplayerStateManager;
import com.multipong.model.multiplayer.MultiplayerStateManager.BallInfo;
import com.multipong.persistence.MultipongDatabase;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Marco Zanella
 * @version 0.01
 * @since 0.01
 */
public class MultiplayerGame extends Game {
    private GameActivity activity;
    private String playerName;
    private MultipongDatabase database;
    private MultiplayerStateManager msm;
    private volatile Boolean started;

    public MultiplayerGame(GameActivity activity) {
        this.activity = activity;
        msm = new MultiplayerStateManager(this, activity);
    }

    public MultiplayerStateManager getMSM() {
        return msm;
    }

    public void setStartingPlayer(boolean isStarting) {
        this.started = isStarting;
    }

    public void setAllPlayers(List<Integer> ids) {
        msm.addPlayers(ids);
    }

    public void increaseSpeed() {
        currentGame.decrementDelay();
    }

    @Override
    public void start(String playerName) {
        this.playerName = playerName;
        if (currentGame == null) {
            currentGame = new MultiplayerGameThread(playerName, activity);
            Log.e("START", started + "");
            new Thread(currentGame).start();
        }

    }

    public void newTurn(BallInfo info){
        ((MultiplayerGameThread)currentGame).newPlayerTurn(info);
    }

    private class MultiplayerGameThread extends AbsGameThread {
        private double newX;
        private double newY = -100; // big enough to make it wait

        private final AtomicInteger forBallToComeBack = new AtomicInteger(1);

        public MultiplayerGameThread(String playerName, GameActivity activity) {
            super(playerName, activity);
        }

        @Override
        public void ballOnTopOfTheField() {
            activity.makeBallDisappear();
            waitForBallToComeBack();
        }

        // TODO: Before starting the game, be careful to set newY to 0.0 in the starting node
        //       (See #21)
        @Override
        public void initialBallPosition() {
            if(started) return;
            waitForBallToComeBack();
            started = true;
        }

        public void newPlayerTurn(BallInfo info) {
            newX = info.getPosition();
            newY = 0.0;
            setX(newX);
            setY(newY);
            setXFactor(info.getBallSpeedX());
            setYFactor(info.getBallSpeedY());
            synchronized (forBallToComeBack) {
                forBallToComeBack.set(1);
                forBallToComeBack.notifyAll();
            }
        }

        @Override
        public void ballBounced(boolean bounced) {
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
            if (0 <= xFinalPosition && xFinalPosition <= 1)
                Log.d("Predictor", "Predicted: " + xFinalPosition);

            // now send info to other players
            double newSpeedX = speedX * -1;
            double newSpeedY = getYFactor() * - 1;
            double newPos = 1 - xFinalPosition;
            boolean stillAlive = getNumberOfLives() > 0;
            if (!bounced) {
                newSpeedX = Math.random();
                newSpeedY = Math.random();
                newPos    = Math.random();
            }
            BallInfo info = MultiplayerStateManager.createBallInfo(newSpeedX, newSpeedY, newPos);
            info = info.tellIfStillInGame(stillAlive);
            msm.sendBallToNext(info);
        }

        private void waitForBallToComeBack() {
            Log.d("MULTI", "Waiting...");
            forBallToComeBack.decrementAndGet();
            synchronized(forBallToComeBack) {
                try {
                    while (forBallToComeBack.get() == 0)
                        forBallToComeBack.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
