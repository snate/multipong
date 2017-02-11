package com.multipong.net.messages.gameformation;

import com.multipong.model.formation.Participant;
import com.multipong.net.messages.Message;

import org.json.JSONObject;

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
}
