package com.multipong.net.messages;

import com.multipong.model.formation.Participant;

import org.json.JSONArray;
import org.json.JSONException;

import java.net.InetAddress;
import java.util.Arrays;
import java.util.Collection;

public class KnownHostsMessage extends Message {

    public static final String KNOWN_HOSTS_FIELD = "knownHosts";

    public void addHosts(Collection<InetAddress> hosts) {
        JSONArray jsonArray = new JSONArray(Arrays.asList(hosts));
        try {
            object.put(KNOWN_HOSTS_FIELD, jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected String getMessageType() {
        return Participant.MessageType.KNOWN_HOSTS;
    }
}
