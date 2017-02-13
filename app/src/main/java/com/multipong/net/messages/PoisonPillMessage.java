package com.multipong.net.messages;

import com.multipong.model.Actor;

/**
 * @author Marco Zanella
 * @version 0.01
 * @since 0.01
 */
public class PoisonPillMessage extends Message {
    public String getMessageType(){
        return Actor.MessageType.POISON_PILL;
    }
}
