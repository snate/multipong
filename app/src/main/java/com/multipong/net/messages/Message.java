package com.multipong.net.messages;

import com.multipong.utility.PlayerNameUtility;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public abstract class Message {

    public static final String APP_FIELD = "application";
    public static final String APP_VALUE = "multipong";
    public static final String NAME_FIELD = "playerName";
    public static final String MESSAGE_TYPE_FIELD = "type";

    protected JSONObject object;

    public Message() {
        try {
            object = new JSONObject();
            object.put(APP_FIELD, APP_VALUE);
            object.put(NAME_FIELD, PlayerNameUtility.getPlayerName());
            object.put(MESSAGE_TYPE_FIELD, getMessageType());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public JSONObject getMsg() {
        return object;
    }

    protected abstract String getMessageType();

    public abstract HashMap<String, Object> decodeJson(JSONObject jsonObject);
}
