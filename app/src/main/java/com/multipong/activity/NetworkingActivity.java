package com.multipong.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.multipong.model.Actor;
import com.multipong.net.NameResolutor;
import com.multipong.net.receive.Receiver;
import com.multipong.net.send.Sender;
import com.multipong.utility.DeviceIdUtility;

import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public abstract class NetworkingActivity extends AppCompatActivity implements ActivityWithActor {

    private BlockingQueue<Sender.AddressedContent> messagesQueue = new ArrayBlockingQueue<>(10);
    private Receiver receiver;
    private Sender sender;
    private Actor actor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Integer myID = DeviceIdUtility.getId();
        if (myID == null) {
            String uniqueID = UUID.randomUUID().toString();
            myID = NameResolutor.hashOf(uniqueID);
            DeviceIdUtility.setId(myID);
        }

        sender = getSender().withQueue(messagesQueue);
        new Thread(sender).start();

        receiver = getReceiver();
        new Thread(receiver).start();
    }

    protected abstract Sender getSender();
    protected abstract Receiver getReceiver();

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(receiver != null) receiver.stop();
        if(sender != null) {
            sender.stop();
            // add new null content to make the sender interrupt abruptly
            try {
                messagesQueue.put(new Sender.AddressedContent(null, null));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void addMessageToQueue(Sender.AddressedContent content) {
        try {
            messagesQueue.put(content);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    public void setActor(Actor actor) {
        this.actor = actor;
    }

    public Actor getActor() {
        return actor;
    }
}
