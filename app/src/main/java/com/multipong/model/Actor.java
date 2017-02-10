package com.multipong.model;

import org.json.JSONObject;

import java.net.InetAddress;

public interface Actor {
    public void receive(String type, JSONObject message, final InetAddress sender);
}
