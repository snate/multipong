package com.multipong.net.messages.game;

import android.util.Log;

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
    public static final int DECIMALS = 4;

    public static final String DECODED_BALL = "BALL_OBJ";

    @Override
    protected String getMessageType() {
        return MultiplayerStateManager.MessageType.BALL_INFO;
    }

    @Override
    public Map<String, Object> decode() {
        Map<String, Object> result = super.decode();
        try {
            double speedX = Double.parseDouble(object.getString(SPEED_X_FIELD));
            double speedY = Double.parseDouble(object.getString(SPEED_Y_FIELD));
            double position = Double.parseDouble(object.getString(POSITION_FIELD));
            boolean still_in_game = object.getString(STILL_IN_GAME_FIELD).equals("t");
            int nextPlayer = object.getInt(NEXT_FIELD);
            BallInfo ballInfo = MultiplayerStateManager.createBallInfo(speedX, speedY, position)
                                                       .tellIfStillInGame(still_in_game)
                                                       .withNextPlayer(nextPlayer);
            result.put(DECODED_BALL, ballInfo);
            Log.d("DECODING______","_______");
            Log.d("speedX________",object.getString(SPEED_X_FIELD));
            Log.d("speedY________",object.getString(SPEED_Y_FIELD));
            Log.d("position______",object.getString(POSITION_FIELD));
            Log.d("stillInGameInt",object.getString(STILL_IN_GAME_FIELD));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    public BallInfoMessage addBallInfo(BallInfo ballInfo) {
        String strSpeedX = String.valueOf(ballInfo.getBallSpeedX());
        String speedX    = (strSpeedX.substring(0, Math.min(2 + DECIMALS, strSpeedX.length())));
        String strSpeedY = String.valueOf(ballInfo.getBallSpeedY());
        String speedY    = (strSpeedY.substring(0, Math.min(2 + DECIMALS, strSpeedY.length())));
        String strPos    = String.valueOf(ballInfo.getPosition());
        String position  = (strPos.substring(0, Math.min(2 + DECIMALS, strPos.length())));
        String stillInGame = "f";
        if (ballInfo.getStillInGame()) stillInGame = "t";
        int nextPlayer = ballInfo.getNextPlayer();
        try {
            object.put(SPEED_X_FIELD, speedX);
            object.put(SPEED_Y_FIELD, speedY);
            object.put(POSITION_FIELD, position);
            object.put(STILL_IN_GAME_FIELD, stillInGame);
            Log.d("ENCODING______","_______");
            Log.d("speedX________",""+speedY);
            Log.d("speedY________",""+speedX);
            Log.d("position______",""+position);
            Log.d("stillInGameInt",""+stillInGame);
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
