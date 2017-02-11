package com.multipong.net.messages.gameformation;

import com.multipong.model.formation.Host;
import com.multipong.net.messages.Message;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

public class AvailableMessage extends Message {

    public static String PARTICIPANTS_FIELD = "participants";

    @Override
    protected String getMessageType() {
        return Host.MessageType.AVAILABLE;
    }

    public void addParticipants(Collection<String> participants) {
        try {
            JSONArray jsonArray = new JSONArray(Arrays.asList(participants));
            object.put(PARTICIPANTS_FIELD, jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Map<String, Object> decode() {
        Map<String, Object> result = super.decode();
        try {
            ArrayList<String> ids = new ArrayList<>();
            JSONArray array = object.getJSONArray(PARTICIPANTS_FIELD);
            for (int i = 0; i < array.length(); i++)
                ids.add(array.getString(i));
            result.put(PARTICIPANTS_FIELD, ids);
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
