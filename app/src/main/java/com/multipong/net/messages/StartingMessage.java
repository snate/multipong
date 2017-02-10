package com.multipong.net.messages;

import com.multipong.model.formation.Host;
import com.multipong.net.NameResolutor;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

public class StartingMessage extends Message {

    public static String PARTICIPANTS_FIELD = "participants";

    @Override
    protected String getMessageType() {
        return Host.MessageType.STARTING;
    }

    @Override
    public Map<String, Object> decode() {
        Map<String, Object> result = super.decode();
        try {
            ArrayList<StartingGameInfo> gameInfos = new ArrayList<>();
            JSONArray array = object.getJSONArray(PARTICIPANTS_FIELD);
            for (int i = 0; i < array.length(); i++)
                gameInfos.add((StartingGameInfo) array.get(i));
            result.put(PARTICIPANTS_FIELD, gameInfos);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static StartingMessage createMessageFromJSON(JSONObject json) {
        StartingMessage msg = new StartingMessage();
        msg.object = json;
        return msg;
    }

    public void addParticipants(Map<Integer, String> values) {
        ArrayList<StartingGameInfo> gameInfos = new ArrayList();
        for (Integer id : values.keySet())
            gameInfos.add( new StartingGameInfo(id, values.get(id),
                    NameResolutor.INSTANCE.getNodeByHash(id)));
        try {
            JSONArray jsonArray = new JSONArray(Arrays.asList(gameInfos));
            object.put(PARTICIPANTS_FIELD, jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public class StartingGameInfo {
        private final Integer id;
        private final String name;
        private final InetAddress address;

        private final String ID   = "id";
        private final String NAME = "name";
        private final String ADDR = "address";

        StartingGameInfo(Integer id, String name, InetAddress address) {
            this.id = id;
            this.name = name;
            this.address = address;
        }

        public JSONObject toJson() {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put(ID, id);
                jsonObject.put(NAME, name);
                jsonObject.put(ADDR, address);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return jsonObject;
        }
    }
}
