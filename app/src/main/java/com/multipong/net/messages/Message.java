package com.multipong.net.messages;

import com.multipong.utility.DeviceIdUtility;
import com.multipong.utility.PlayerNameUtility;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public abstract class Message {

    public static final String APP_FIELD = "app";
    public static final String APP_VALUE = "multipong";
    public static final String NAME_FIELD = "pl";
    public static final String MESSAGE_TYPE_FIELD = "t";
    public static final String ID_FIELD = "id";

    protected JSONObject object;

    // TODO: Precondition - here I assume that DeviceUtility.id has been set
    //       when creating a participant-side JOIN message
    public Message() {
        try {
            object = new JSONObject();
            object.put(APP_FIELD, APP_VALUE);
            object.put(NAME_FIELD, PlayerNameUtility.getPlayerName());
            object.put(MESSAGE_TYPE_FIELD, getMessageType());
            object.put(ID_FIELD, DeviceIdUtility.getId());
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
            result.put(ID_FIELD, object.getInt(ID_FIELD));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }
}
