package com.multipong.model;

import org.json.JSONObject;

import java.net.InetAddress;

public interface Actor {
    void receive(String type, JSONObject message, final InetAddress sender);

    class MessageType {
        public static final String POISON_PILL = "POISON_PILL";
    }
}
