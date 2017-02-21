package com.multipong.net.messages.gameformation;

import com.multipong.model.formation.Host;
import com.multipong.net.messages.Message;

public class TellIPMessage extends Message {
    @Override
    protected String getMessageType() {
        return Host.MessageType.TELL_IP;
    }
}
