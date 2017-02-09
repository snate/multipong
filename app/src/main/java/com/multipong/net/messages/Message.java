package com.multipong.net.messages;

import org.json.JSONObject;

import java.util.HashMap;

public abstract class Message {

    protected JSONObject object;

    public Message(JSONObject object) {
        this.object = new JSONObject();
        // TODO: Add here basic stuff like   { "application": "multipong" }
    }

    public abstract JSONObject getMsg();
    public abstract HashMap<String, Object> decodeJson();
}
