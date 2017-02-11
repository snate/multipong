package com.multipong.net.messages;

import com.multipong.model.formation.Participant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

public class KnownHostsMessage extends Message {

    public static final String KNOWN_HOSTS_FIELD = "knownHosts";

    public void addHosts(Collection<InetAddress> hosts) {
        ArrayList<String> hostsStrings = new ArrayList<>();
        for (InetAddress host : hosts)
        hostsStrings.add(host.getCanonicalHostName());
        JSONArray jsonArray = new JSONArray(Arrays.asList(hosts));
        try {
            object.put(KNOWN_HOSTS_FIELD, jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Map<String, Object> decode() {
        Map<String, Object> result = super.decode();
        try {
            Collection<InetAddress> hosts = new ArrayList<>();
            JSONArray array = object.getJSONArray(KNOWN_HOSTS_FIELD);
            for (int i = 0; i < array.length(); i++)
                try {
                    hosts.add(InetAddress.getByName(array.getString(i)));
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
            result.put(KNOWN_HOSTS_FIELD, hosts);
        } catch (JSONException e) {
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
