package com.multipong.net.messages.gameformation;

import com.multipong.model.formation.Participant;
import com.multipong.net.messages.Message;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class DiscoverMessage extends Message {

    public static final String YOUR_IP = "yourIp";

    @Override
    public Map<String, Object> decode() {
        Map<String, Object> result = super.decode();
        try {
            result.put(YOUR_IP, object.getString(YOUR_IP));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

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
}
