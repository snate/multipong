package com.multipong.model;

import org.json.JSONObject;

import java.net.InetAddress;
import java.util.ArrayList;

public interface Actor {
    void receive(String type, JSONObject message, final InetAddress sender);
}
