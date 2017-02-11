package com.multipong.net.send;

import com.multipong.net.Utils;
import com.multipong.net.messages.Message;

import java.net.InetAddress;
import java.util.concurrent.BlockingQueue;

/**
 * Abstract class for worker thread that sends content.
 */
public abstract class Sender implements Runnable {

    protected static final int SOCKET_TIMEOUT = 5000;

    private final BlockingQueue<AddressedContent> messages;
    private volatile boolean stop = false;

    public Sender(BlockingQueue<AddressedContent> queue) {
        messages = queue;
    }

    @Override
    public void run() {
        while (!stop) {
            AddressedContent content = null;
            try {
                content = messages.take();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (content.address == null || content.message == null) continue;
            send(content);
        }
    }

    public void stop() {
        stop = true;
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
