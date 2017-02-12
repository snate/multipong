package com.multipong.net.messages.game;

import com.multipong.model.multiplayer.MultiplayerStateManager;
import com.multipong.model.multiplayer.MultiplayerStateManager.BallInfo;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class BallInfoMessage extends GameMessage {

    // shorter strings so they are lighter when sent with UDP
    public static final String SPEED_X_FIELD  = "sX";
    public static final String SPEED_Y_FIELD  = "sY";
    public static final String POSITION_FIELD = "pos";
    public static final String STILL_IN_GAME_FIELD = "sIG";
    public static final String NEXT_FIELD = "nP";

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
            result.put(NEXT_FIELD, object.getInt(NEXT_FIELD));
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
        int nextPlayer = ballInfo.getNextPlayer();
        try {
            object.put(SPEED_X_FIELD, speedX);
            object.put(SPEED_Y_FIELD, speedY);
            object.put(POSITION_FIELD, position);
            object.put(STILL_IN_GAME_FIELD, stillInGame);
            object.put(NEXT_FIELD, nextPlayer);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return this;
    }

    public BallInfoMessage addNextPlayerInfo(int playerId) {
        try {
            object.put(NEXT_FIELD, playerId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return this;
    }

    public static BallInfoMessage createFromJson(JSONObject jsonObject) {
        BallInfoMessage ballInfoMessage = new BallInfoMessage();
        ballInfoMessage.object = jsonObject;
        return ballInfoMessage;
    }

}
