package com.multipong.net.messages.game;

import com.multipong.model.Coordination;

public class AreYouAlive extends GameMessage {

    public AreYouAlive() {
        super();
        forCoordination(true);
    }

    @Override
    protected String getMessageType() {
        return Coordination.MessageType.AYA;
    }
}
