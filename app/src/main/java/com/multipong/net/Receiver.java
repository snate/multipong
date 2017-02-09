package com.multipong.net;

import android.content.Context;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Receiver implements Runnable {

    private Context context;
    private ServerSocket serverSocket;

    public Receiver(Context context) {
        this.context = context;
    }

    @Override
    public void run() {
        Log.d("RECEIVER", "Started");
        try {
            serverSocket = new ServerSocket(Utils.PORT);
            while(true) {
                Socket client = serverSocket.accept();

                try {
                    InputStream input = client.getInputStream();
                    Scanner scanner = new Scanner(input).useDelimiter("\\A");
                    String json = scanner.hasNext() ? scanner.next() : "";
                    JSONObject jsonObject = new JSONObject(json);
                    String app = jsonObject.getString(Utils.JsonGameFormation.APP_FIELD);
                    String name = jsonObject.getString(Utils.JsonGameFormation.NAME_FIELD);
                    Log.d("Receiver", "'Application' field: " + app + " with name " + name);
                } catch (IOException e) {
                    System.out.println(e);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Log.d("Receiver", "Request accepted");
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
