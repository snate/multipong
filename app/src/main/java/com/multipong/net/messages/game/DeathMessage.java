package com.multipong.net.messages.game;

import com.multipong.model.coordination.Coordination;
import com.multipong.net.messages.gameformation.DiscoverMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class DeathMessage extends GameMessage {

    public static final String DEAD_FIELD = "dead";

    @Override
    public Map<String, Object> decode() {
        Map<String, Object> result = super.decode();
        try {
            result.put(DEAD_FIELD, object.getString(DEAD_FIELD));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    public DeathMessage withDead(Integer id) {
        try {
            object.put(DEAD_FIELD, id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return this;
    }

    public static DeathMessage createFromJson(JSONObject jsonObject) {
        DeathMessage message = new DeathMessage();
        message.object = jsonObject;
        return message;
    }

    @Override
    protected String getMessageType() {
        return Coordination.MessageType.DEATH;
    }

}
