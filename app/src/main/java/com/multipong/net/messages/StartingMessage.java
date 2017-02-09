package com.multipong.net.messages;

import com.multipong.model.formation.Host;

import org.json.JSONObject;

import java.util.HashMap;

public class StartingMessage extends Message {
    @Override
    protected String getMessageType() {
        return Host.MessageType.STARTING;
    }

    @Override
    public HashMap<String, Object> decodeJson(JSONObject jsonObject) {
        // TODO: Add implementation
        return null;
    }
}
