package com.multipong.net.messages;

import com.multipong.model.formation.Participant;
import com.multipong.utility.DeviceIdUtility;

import org.json.JSONException;

import java.util.Map;

public class JoinMessage extends Message {

    public static final String ID_FIELD = "newId";

    // TODO: Precondition - here I assume that DeviceUtility.id has been set
    //       when creating a participant-side JOIN message
    public JoinMessage() {
        super();
        Integer myId = DeviceIdUtility.getId();
        try {
            object.put(ID_FIELD, myId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected String getMessageType() {
        return Participant.MessageType.JOIN;
    }

    @Override
    public Map<String, Object> decode() {
        // TODO: Add implementation
        return null;
    }
}
