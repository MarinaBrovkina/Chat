package ru.shift.io;

import ru.shift.messages.Message;

import java.util.LinkedList;
import java.util.Queue;

class BoundedMessageQueue {
    private static final int SOFT_LIMIT = 1000;
    private final Queue<Message> queue = new LinkedList<>();

    synchronized void add(Message message) {
        if (queue.size() >= SOFT_LIMIT) {
            queue.poll();
        }
        queue.offer(message);
        notifyAll();
    }

    synchronized Message take() throws InterruptedException {
        while (queue.isEmpty()) {
            wait();
        }
        return queue.poll();
    }
}