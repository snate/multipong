package com.multipong.net.messages;

import com.multipong.utility.PlayerNameUtility;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

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

    public Map<String, Object> decode() {
        Map<String, Object> result = new HashMap<String, Object>();
        try {
            result.put(APP_FIELD, object.getString(APP_FIELD));
            result.put(NAME_FIELD, object.getString(NAME_FIELD));
            result.put(MESSAGE_TYPE_FIELD, object.getString(MESSAGE_TYPE_FIELD));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }
}
