package com.multipong.model;

import org.json.JSONObject;

public interface Actor {
    public void receive(String type, JSONObject message);
}
