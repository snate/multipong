package com.multipong.net.send;

import com.multipong.net.messages.Message;
import com.multipong.net.messages.PoisonPillMessage;

import java.net.InetAddress;
import java.util.concurrent.BlockingQueue;

/**
 * Abstract class for worker thread that sends content.
 */
public abstract class Sender implements Runnable {

    protected static final int SOCKET_TIMEOUT = 5000;

    private BlockingQueue<AddressedContent> messages;
    private volatile boolean stop = false;

    public Sender withQueue(BlockingQueue<AddressedContent> queue) {
        this.messages = queue;
        return this;
    }

    @Override
    public void run() {
        while (!stop) {
            AddressedContent content = null;
            try {
                content = messages.take();
                if (content.getMessage() instanceof PoisonPillMessage) {
                    stop = true;
                    Message msg = content.getMessage();
                    synchronized (msg) {
                        msg.notifyAll();
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (content.address == null || content.message == null) continue;
            send(content);
        }
    }

    public void stop() {
        //stop = true;
    }

    public abstract void send(AddressedContent content);

    public static class AddressedContent {
        private final Message message;
        private final InetAddress address;

        public AddressedContent(Message message, InetAddress address) {
            this.message = message;
            this.address = address;
        }

        public InetAddress getAddress() {
            return address;
        }

        public Message getMessage() {
            return message;
        }
    }
}
