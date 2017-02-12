package com.multipong.net.messages.game;

import com.multipong.net.messages.Message;

import org.json.JSONException;

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


    public void forCoordinator(boolean forCoordinator) {
        try {
            object.put(TO_COORDINATOR_FIELD, forCoordinator);
        } catch (JSONException e) {
            e.printStackTrace();
        }
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
}
