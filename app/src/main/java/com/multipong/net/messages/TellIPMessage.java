package com.multipong.net.messages;

import com.multipong.model.formation.Host;

public class TellIPMessage extends Message {
    @Override
    protected String getMessageType() {
        return Host.MessageType.TELL_IP;
    }
}
