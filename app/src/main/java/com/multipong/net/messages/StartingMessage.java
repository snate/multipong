package com.multipong.net.messages;

import com.multipong.model.formation.Host;

import org.json.JSONObject;

import java.util.Map;

public class StartingMessage extends Message {
    @Override
    protected String getMessageType() {
        return Host.MessageType.STARTING;
    }

    @Override
    public Map<String, Object> decode() {
        // TODO: Add implementation
        return null;
    }

    public static StartingMessage createMessageFromJSON(JSONObject json) {
        StartingMessage msg = new StartingMessage();
        msg.object = json;
        return msg;
    }
}
