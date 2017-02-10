package com.multipong.net.messages;

import com.multipong.model.formation.Participant;

import org.json.JSONException;
import org.json.JSONObject;

public class DiscoverMessage extends Message {

    public final String YOUR_IP = "yourIp";

    public DiscoverMessage withIp(String address) {
        try {
            object.put(YOUR_IP, address);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return this;
    }

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
