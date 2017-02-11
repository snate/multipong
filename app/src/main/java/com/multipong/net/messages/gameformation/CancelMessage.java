package com.multipong.net.messages.gameformation;

import com.multipong.model.formation.Participant;
import com.multipong.net.messages.Message;

import org.json.JSONObject;

public class CancelMessage extends Message {

    public static CancelMessage createFromJson(JSONObject object) {
        CancelMessage message = new CancelMessage();
        message.object = object;
        return message;
    }

    @Override
    protected String getMessageType() {
        return Participant.MessageType.CANCEL;
    }
}
