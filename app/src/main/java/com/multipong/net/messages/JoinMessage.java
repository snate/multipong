package com.multipong.net.messages;

import com.multipong.model.formation.Participant;

public class JoinMessage extends Message {
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
