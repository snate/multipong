package com.multipong.net.messages;

import com.multipong.model.formation.Host;

import org.json.JSONObject;

import java.util.HashMap;

public class AvailableMessage extends Message {

    @Override
    protected String getMessageType() {
        return Host.MessageType.AVAILABLE;
    }

    @Override
    public HashMap<String, Object> decodeJson(JSONObject jsonObject) {
        // TODO: Add implementation
        return null;
    }
}
