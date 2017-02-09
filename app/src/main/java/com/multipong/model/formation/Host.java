package com.multipong.model.formation;

import com.multipong.model.Actor;

import org.json.JSONObject;

public class Host implements Actor {

    @Override
    public void receiveMessage(String type, JSONObject message) {
        // TODO: Check if host has received either `discover or `join Participant messages
    }

    public enum MessageType {
        AVAILABLE("AVAILABLE"),
        STARTED("STARTED");

        private final String description;

        private MessageType(String value) { description = value; }

        @Override
        public String toString() {
            return description;
        }
    }
}
