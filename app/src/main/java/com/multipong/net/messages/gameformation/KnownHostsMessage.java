package com.multipong.net.messages.gameformation;

import com.multipong.model.formation.Participant;
import com.multipong.net.messages.Message;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;

public class KnownHostsMessage extends Message {

    public static final String KNOWN_HOST_FIELD = "knownHost";

    public void addHost(InetAddress host) {
        try {
            object.put(KNOWN_HOST_FIELD, host.getHostAddress());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Map<String, Object> decode() {
        Map<String, Object> result = super.decode();
        try {
            InetAddress host;
            String hostString = object.getString(KNOWN_HOST_FIELD);
            host = InetAddress.getByName(hostString);
            result.put(KNOWN_HOST_FIELD, host);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return result;
    }


    public static KnownHostsMessage createMessageFromJSON(JSONObject json) {
        KnownHostsMessage msg = new KnownHostsMessage();
        msg.object = json;
        return msg;
    }

    @Override
    protected String getMessageType() {
        return Participant.MessageType.KNOWN_HOSTS;
    }
}
