package com.multipong.model.formation;

import com.multipong.model.Actor;

import org.json.JSONObject;

public class Participant implements Actor {
    @Override
    public void receive(String type, JSONObject message) {
        switch (type) {
            case Host.MessageType.AVAILABLE: break;
            case Host.MessageType.STARTING: break;
        }
    }

    public class MessageType {
        public static final String DISCOVER = "DISCOVER";
        public static final String JOIN = "JOIN";
        public static final String CANCEL = "CANCEL";
    }
}
