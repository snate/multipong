package com.multipong.net.messages;

import com.multipong.model.formation.Participant;

import org.json.JSONObject;

import java.util.HashMap;

public class JoinMessage extends Message {
    @Override
    protected String getMessageType() {
        return Participant.MessageType.JOIN;
    }

    @Override
    public HashMap<String, Object> decodeJson(JSONObject jsonObject) {
        // TODO: Add implementation
        return null;
    }
}
