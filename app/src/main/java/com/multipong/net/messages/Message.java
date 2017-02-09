package com.multipong.net.messages;

import org.json.JSONObject;

import java.util.HashMap;

public abstract class Message {

    protected JSONObject object;

    public Message() {
        this.object = new JSONObject();
        // TODO: Add here basic stuff like   { "application": "multipong" }
        // TODO: Add message type like       { "type": getMessageType() }
        String messageType = getMessageType();
    }

    public JSONObject getMsg() {
        return object;
    }

    protected abstract String getMessageType();

    public abstract HashMap<String, Object> decodeJson(JSONObject jsonObject);
}
