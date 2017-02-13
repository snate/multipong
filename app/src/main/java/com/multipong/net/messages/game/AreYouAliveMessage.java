package com.multipong.net.messages.game;

import com.multipong.model.Coordination;

public class AreYouAliveMessage extends GameMessage {

    public AreYouAliveMessage() {
        super();
        forCoordination(true);
    }

    @Override
    protected String getMessageType() {
        return Coordination.MessageType.AYA;
    }
}
