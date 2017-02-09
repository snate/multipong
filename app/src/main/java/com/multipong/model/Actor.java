package com.multipong.model;

import org.json.JSONObject;

public interface Actor {
    public void receiveMessage(String type, JSONObject message);
}
