package com.multipong.net.receive;

import android.util.Log;

import com.multipong.activity.NetworkingActivity;
import com.multipong.net.Utils;
import com.multipong.net.messages.Message;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.InetAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class Receiver implements Runnable {

    private NetworkingActivity activity;
    private ExecutorService executor;

    Receiver(NetworkingActivity activity) {
        this.activity = activity;
    }

    @Override
    public void run() {
        Log.d("RECEIVER", "Started");
        int cores = Runtime.getRuntime().availableProcessors();
        executor = Executors.newFixedThreadPool(cores * 2);
    }

    void process(String json, final InetAddress address) {
        try {
            if (!Utils.isJsonObject(json)) return;
            final JSONObject jsonObject = new JSONObject(json);
            if(!jsonObject.has(Message.APP_FIELD)) return;
            String app = jsonObject.getString(Message.APP_FIELD);
            if(!app.equals(Message.APP_VALUE)) return;
            final String type = jsonObject.getString(Message.MESSAGE_TYPE_FIELD);
            Log.d("Rec_JSON", json);
            Log.d("Rec_INFO", type + ": " + String.valueOf(json.getBytes().length));
            executor.submit(new Runnable() {
                @Override
                public void run() {
                    activity.getActor().receive(type, jsonObject, address);
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("Receiver", "Message processed");
    }

    public abstract void stop();
}
