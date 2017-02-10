package com.multipong.net.messages;

import com.multipong.model.formation.Host;

public class StartingMessage extends Message {
    @Override
    protected String getMessageType() {
        return Host.MessageType.STARTING;
    }

    @Override
    public Map<String, Object> decode() {
        // TODO: Add implementation
        return null;
    }
}
