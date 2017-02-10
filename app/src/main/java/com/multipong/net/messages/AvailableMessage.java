package com.multipong.net.messages;

import com.multipong.model.formation.Host;

import org.json.JSONArray;
import org.json.JSONException;

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

    public void addParticipants(Collection<Integer> participants) {
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
            ArrayList<Integer> ids = new ArrayList<>();
            JSONArray array = object.getJSONArray(PARTICIPANTS_FIELD);
            for (int i = 0; i < array.length(); i++)
                ids.add(array.getInt(i));
            result.put(PARTICIPANTS_FIELD, ids);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }
}
