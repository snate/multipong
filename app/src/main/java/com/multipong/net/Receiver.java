package com.multipong.net;

import android.util.Log;

import com.multipong.activity.MultiplayerGameFormationActivity;
import com.multipong.net.messages.Message;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Receiver implements Runnable {

    private MultiplayerGameFormationActivity activity;
    private ServerSocket serverSocket;
    private ExecutorService executor;

    public Receiver(MultiplayerGameFormationActivity activity) {
        this.activity = activity;
    }

    @Override
    public void run() {
        int cores = Runtime.getRuntime().availableProcessors();
        executor = Executors.newFixedThreadPool(cores * 2);
        Log.d("RECEIVER", "Started");
        try {
            serverSocket = new ServerSocket(Utils.PORT);
            while(true) {
                final Socket client = serverSocket.accept();

                try {
                    InputStream input = client.getInputStream();
                    Scanner scanner = new Scanner(input).useDelimiter("\\A");
                    String json = scanner.hasNext() ? scanner.next() : "";
                    if (!Utils.isJsonObject(json)) continue;
                    final JSONObject jsonObject = new JSONObject(json);
                    if(!jsonObject.has(Message.APP_FIELD)) continue;
                    String app = jsonObject.getString(Message.APP_FIELD);
                    if(!app.equals(Message.APP_VALUE)) continue;
                    String name = jsonObject.getString(Message.NAME_FIELD);
                    final String type = jsonObject.getString(Message.MESSAGE_TYPE_FIELD);
                    executor.submit(new Runnable() {
                        @Override
                        public void run() {
                            activity.getActor().receive(type, jsonObject, client.getInetAddress());
                        }
                    });
                    Log.d("Receiver", "'Application' field: " + app + " with name " + name);
                } catch (IOException e) {
                    System.out.println(e);
                } catch (JSONException e) {
                    Log.d("Receiver", "Not understandable Json object");
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        try {
            if (serverSocket != null)
                serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
