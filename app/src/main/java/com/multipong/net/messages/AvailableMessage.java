package com.multipong.net.messages;

import com.multipong.model.formation.Host;

public class AvailableMessage extends Message {

    @Override
    protected String getMessageType() {
        return Host.MessageType.AVAILABLE;
    }

    @Override
    public Map<String, Object> decode() {
        // TODO: Add implementation
        return null;
    }
}
