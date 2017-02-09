package com.multipong.model.formation;

import com.multipong.model.Actor;
import com.multipong.model.formation.Participant.MessageType;

import org.json.JSONObject;

public class Host implements Actor {

    @Override
    public void receive(String type, JSONObject message) {
        switch (type) {
            case Participant.MessageType.DISCOVER: break;
            case Participant.MessageType.JOIN: break;
        }
    }

    public class MessageType {
        public static final String AVAILABLE = "AVAILABLE";
        public static final String STARTING = "STARTING";
    }
}
