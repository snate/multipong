package com.multipong.net.messages.gameformation;

import com.multipong.model.formation.Host;
import com.multipong.net.NameResolutor;
import com.multipong.net.messages.Message;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
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
                gameInfos.add(StartingGameInfo.fromJson(array.getJSONObject(i)));
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
        ArrayList<StartingGameInfo> gameInfos = new ArrayList<>();
        for (Integer id : values.keySet())
            gameInfos.add( new StartingGameInfo(id, values.get(id),
                    NameResolutor.INSTANCE.getNodeByHash(id)));
        try {
            JSONArray jsonArray = new JSONArray();
            for (StartingGameInfo gameInfo : gameInfos)
                jsonArray.put(gameInfo.toJson());
            object.put(PARTICIPANTS_FIELD, jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return object.toString();
    }

    public static class StartingGameInfo {
        private final Integer id;
        private final String name;
        private final InetAddress address;

        private static final String ID   = "id";
        private static final String NAME = "name";
        private static final String ADDR = "address";

        StartingGameInfo(Integer id, String name, InetAddress address) {
            this.id = id;
            this.name = name;
            this.address = address;
        }

        public InetAddress getAddress() {
            return address;
        }

        public Integer getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        JSONObject toJson() {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put(ID, id);
                jsonObject.put(NAME, name);
                jsonObject.put(ADDR, address.getHostAddress());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return jsonObject;
        }

        static StartingGameInfo fromJson(JSONObject jsonObject) {
            StartingGameInfo result = null;
            try {
                Integer id = jsonObject.getInt(ID);
                String name = jsonObject.getString(NAME);
                InetAddress address = null;
                try {
                    address = InetAddress.getByName(jsonObject.getString(ADDR));
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
                result = new StartingGameInfo(id, name, address);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return result;
        }
    }
}
