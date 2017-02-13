package com.multipong.net.messages.game;

import com.multipong.net.messages.Message;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public abstract class GameMessage extends Message {

    public static final String TO_COORDINATOR_FIELD = "tCoo";

    public GameMessage() {
        super();
    }

    @Override
    public Map<String, Object> decode() {
        Map<String, Object> result = super.decode();
        try {
            result.put(TO_COORDINATOR_FIELD, object.getBoolean(TO_COORDINATOR_FIELD));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }


    public void forCoordination(boolean forCoordination) {
        try {
            object.put(TO_COORDINATOR_FIELD, forCoordination);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public boolean isForCoordination() {
        boolean answer = true;
        try {
            answer = object.getBoolean(TO_COORDINATOR_FIELD);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return answer;
    }

    public static boolean isForCoordination(JSONObject jsonObject) {
        boolean answer = true;
        try {
            answer = jsonObject.getBoolean(TO_COORDINATOR_FIELD);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return answer;
    }
}
