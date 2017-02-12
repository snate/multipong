package com.multipong.net.messages.game;

import com.multipong.model.multiplayer.MultiplayerStateManager;
import com.multipong.model.multiplayer.MultiplayerStateManager.BallInfo;
import com.multipong.net.messages.Message;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class BallInfoMessage extends Message {

    // shorter strings so they are lighter when sent with UDP
    public static final String SPEED_X_FIELD  = "sX";
    public static final String SPEED_Y_FIELD  = "sY";
    public static final String POSITION_FIELD = "pos";
    public static final String STILL_IN_GAME_FIELD = "sIG";
    public static final String TO_COORDINATOR_FIELD = "tCoo";

    @Override
    protected String getMessageType() {
        return MultiplayerStateManager.MessageType.BALL_INFO;
    }

    @Override
    public Map<String, Object> decode() {
        Map<String, Object> result = super.decode();
        try {
            result.put(SPEED_X_FIELD, object.getDouble(SPEED_X_FIELD));
            result.put(SPEED_Y_FIELD, object.getDouble(SPEED_Y_FIELD));
            result.put(POSITION_FIELD, object.getDouble(POSITION_FIELD));
            result.put(STILL_IN_GAME_FIELD, object.getBoolean(STILL_IN_GAME_FIELD));
            result.put(TO_COORDINATOR_FIELD, object.getBoolean(TO_COORDINATOR_FIELD));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    public BallInfoMessage addBallInfo(BallInfo ballInfo) {
        double speedX   = ballInfo.getBallSpeedX();
        double speedY   = ballInfo.getBallSpeedY();
        double position = ballInfo.getPosition();
        boolean stillInGame = ballInfo.getStillInGame();
        try {
            object.put(SPEED_X_FIELD, speedX);
            object.put(SPEED_Y_FIELD, speedY);
            object.put(POSITION_FIELD, position);
            object.put(STILL_IN_GAME_FIELD, stillInGame);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return this;
    }

    public BallInfoMessage forCoordinator(boolean forCoordinator) {
        try {
            object.put(TO_COORDINATOR_FIELD, forCoordinator);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return this;
    }

    public boolean isForCoordinator() {
        boolean answer = true;
        try {
            answer = object.getBoolean(TO_COORDINATOR_FIELD);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return answer;
    }

    public static BallInfoMessage createFromJson(JSONObject jsonObject) {
        BallInfoMessage ballInfoMessage = new BallInfoMessage();
        ballInfoMessage.object = jsonObject;
        return ballInfoMessage;
    }

}
