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

    public static JoinMessage createMessageFromJSON(JSONObject json) {
        JoinMessage msg = new JoinMessage();
        msg.object = json;
        return msg;
    }
}
