package com.multipong.net.messages;

import com.multipong.model.formation.Participant;

public class AreYouTheHostMessage extends Message {
    @Override
    protected String getMessageType() {
        return Participant.MessageType.ARE_YOU_THE_HOST;
    }
}
