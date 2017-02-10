package com.multipong.net.messages;

import com.multipong.model.formation.Participant;

import org.json.JSONObject;

import java.util.Map;

public class DiscoverMessage extends Message {

    public static DiscoverMessage createFromJson(JSONObject jsonObject) {
        DiscoverMessage message = new DiscoverMessage();
        message.object = jsonObject;
        return message;
    }

    @Override
    protected String getMessageType() {
        return Participant.MessageType.DISCOVER;
    }
}
