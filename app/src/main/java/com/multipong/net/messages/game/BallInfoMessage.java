package com.multipong.net.messages.game;

import com.multipong.model.multiplayer.MultiplayerStateManager;
import com.multipong.model.multiplayer.MultiplayerStateManager.BallInfo;
import com.multipong.net.messages.Message;

import org.json.JSONException;

public class BallInfoMessage extends Message {

    public static final String SPEED_X_FIELD  = "speedX";
    public static final String SPEED_Y_FIELD  = "speedY";
    public static final String POSITION_FIELD = "position";

    @Override
    protected String getMessageType() {
        return MultiplayerStateManager.MessageType.BALL_INFO;
    }

    public void addBallInfo(BallInfo ballInfo) {
        double speedX   = ballInfo.getBallSpeedX();
        double speedY   = ballInfo.getBallSpeedY();
        double position = ballInfo.getPosition();
        try {
            object.put(SPEED_X_FIELD, speedX);
            object.put(SPEED_Y_FIELD, speedY);
            object.put(POSITION_FIELD, position);
            // TODO: Add implementation
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
