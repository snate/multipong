package com.multipong.net.messages;

import com.multipong.model.formation.Participant;
import com.multipong.utility.DeviceIdUtility;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import org.json.JSONObject;

import java.util.Map;

public class JoinMessage extends Message {

    public static JoinMessage createFromJson(JSONObject object) {
        JoinMessage message = new JoinMessage();
        message.object = object;
        return message;
    }

    @Override
    protected String getMessageType() {
        return Participant.MessageType.JOIN;
    }

    @Override
    public Map<String, Object> decode() {
        Map<String, Object> result = super.decode();
        try {
            Integer participantId = object.getInt(ID_FIELD);
            result.put(ID_FIELD, participantId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static JoinMessage createMessageFromJSON(JSONObject json) {
        JoinMessage msg = new JoinMessage();
        msg.object = json;
        return msg;
    }
}
