package com.multipong.model.formation;

import com.multipong.model.Actor;

import org.json.JSONObject;

public class Participant implements Actor {
    @Override
    public void receiveMessage(String type, JSONObject message) {
        // TODO: Check if host has received either `available or `started Host messages
    }

    public enum MessageType {
        DISCOVER("DISCOVER"),
        JOIN("JOIN");

        private final String description;

        private MessageType(String value) { description = value; }

        @Override
        public String toString() {
            return description;
        }
    }
}
