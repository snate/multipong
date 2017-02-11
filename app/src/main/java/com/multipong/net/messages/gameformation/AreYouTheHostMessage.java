package com.multipong.net.messages.gameformation;

import com.multipong.model.formation.Participant;
import com.multipong.net.messages.Message;

public class AreYouTheHostMessage extends Message {
    @Override
    protected String getMessageType() {
        return Participant.MessageType.ARE_YOU_THE_HOST;
    }
}
