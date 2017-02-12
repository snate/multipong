package com.multipong.net.messages.gameformation;

import com.multipong.model.formation.Host;
import com.multipong.net.messages.Message;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class AvailableMessage extends Message {

    public static String PARTICIPANTS_FIELD = "participants";
    public static String P_NAME_FIELD = "n";
    public static String P_ID_FIELD = "n";

    @Override
    protected String getMessageType() {
        return Host.MessageType.AVAILABLE;
    }

    public void addParticipants(Map<Integer, String> participants) {
        try {
            JSONArray jsonArray = new JSONArray();
            for (Integer key : participants.keySet()) {
                jsonArray.put(new JSONObject()
                                  .put(P_NAME_FIELD, participants.get(key))
                                  .put(P_ID_FIELD, key));
            }
            object.put(PARTICIPANTS_FIELD, jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Map<String, Object> decode() {
        Map<String, Object> result = super.decode();
        try {
            Map<Integer, String> participants = new HashMap<>();
            JSONArray array = object.getJSONArray(PARTICIPANTS_FIELD);
            for (int i = 0; i < array.length(); i++) {
                Integer id = array.getJSONObject(i).getInt(P_ID_FIELD);
                String name = array.getJSONObject(i).getString(P_NAME_FIELD);
                participants.put(id, name);
            }
            result.put(PARTICIPANTS_FIELD, participants);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static AvailableMessage createMessageFromJSON(JSONObject json) {
        AvailableMessage msg = new AvailableMessage();
        msg.object = json;
        return msg;
    }
}
