package com.multipong.net.messages;

import com.multipong.model.formation.Participant;
import com.multipong.utility.DeviceIdUtility;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

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
}
