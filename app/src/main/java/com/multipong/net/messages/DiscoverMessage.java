package com.multipong.net.messages;

import com.multipong.model.formation.Participant;

import org.json.JSONObject;

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

    public static DiscoverMessage createMessageFromJSON(JSONObject json) {
        DiscoverMessage msg = new DiscoverMessage();
        msg.object = json;
        return msg;
    }
}
