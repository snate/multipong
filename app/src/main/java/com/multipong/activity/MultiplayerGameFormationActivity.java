package com.multipong.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.multipong.model.Actor;
import com.multipong.net.Receiver;
import com.multipong.net.send.Sender;
import com.multipong.net.Utils;
import com.multipong.net.send.Sender.AddressedContent;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * @author Marco Zanella
 * @version 0.01
 * @since 0.01
 */
public abstract class MultiplayerGameFormationActivity extends AppCompatActivity{

    private Receiver receiver;
    private Sender sender;
    private BlockingQueue<AddressedContent> messagesQueue = new ArrayBlockingQueue<>(10);
    private Actor actor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.setActivity(this);
        sender = new Sender(messagesQueue);
        new Thread(sender).start();

        receiver = new Receiver(this);
        new Thread(receiver).start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(receiver != null) receiver.stop();
        if(sender != null) {
            sender.stop();
            // add new null content to make the sender interrupt abruptly
            try {
                messagesQueue.put(new AddressedContent(null, null));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void addMessageToQueue(AddressedContent content) {
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

    public abstract boolean isHost();
}
