package com.multipong.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.multipong.model.Actor;
import com.multipong.net.NameResolutor;
import com.multipong.net.messages.PoisonPillMessage;
import com.multipong.net.receive.Receiver;
import com.multipong.net.send.Sender;
import com.multipong.utility.DeviceIdUtility;

import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public abstract class NetworkingActivity extends AppCompatActivity implements ActivityWithActor {

    private BlockingQueue<Sender.AddressedContent> messagesQueue;
    private Receiver receiver;
    private Sender sender;
    private volatile Actor actor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Integer myID = DeviceIdUtility.getId();
        if (myID == null) {
            String uniqueID = UUID.randomUUID().toString();
            myID = NameResolutor.hashOf(uniqueID);
            DeviceIdUtility.setId(myID);
        }

        messagesQueue =  new ArrayBlockingQueue<>(10);
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
        if(receiver != null) {
            receiver.stop();
            receiver = null;
        }
        else Log.d("iugbuigbgil", "asafoesuuigs");
        if(sender != null) {
            sender.stop();
            // add new null content to make the sender interrupt abruptly
            try {
                messagesQueue.put(new Sender.AddressedContent(new PoisonPillMessage(), null));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            sender = null;
        }
        actor = null;
    }

    public void addMessageToQueue(Sender.AddressedContent content) {
        Log.d("Adding", content.getMessage().getMsg().toString());
        Log.d("MQ cap 1", String.valueOf(messagesQueue.remainingCapacity()));
        try {
            messagesQueue.put(content);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Log.d("MQ cap 2", String.valueOf(messagesQueue.remainingCapacity()));
        Log.d("Added", content.getMessage().getMsg().toString());
    }

    public void setActor(Actor actor) {
        this.actor = actor;
    }

    public Actor getActor() {
        return actor;
    }
}
