package org.darcro.messageproc.util;

import java.util.ArrayList;
import java.util.PriorityQueue;

public class BufferedInputQueue<T> implements Shutdownable, Runnable {

    public interface QueueListener<T> {
        void process(T msg);
    }

    private final PriorityQueue<QueueItem<T>> priorityQueue;
    private final ArrayList<QueueListener<T>> queueListeners;
    private final long bufferTime;
    private final long maxCapacity;

    public BufferedInputQueue(long bufferTime, int maxCapacity) {
        this.priorityQueue = new PriorityQueue<>(maxCapacity);
        this.queueListeners = new ArrayList<>(10);
        this.maxCapacity = maxCapacity;
        this.bufferTime = bufferTime;
        this.running = true;
    }
    private boolean running;

    public void enqueue(long timestamp, T msg) throws QueueOverflowException {
        if(priorityQueue.size() >= maxCapacity) throw new QueueOverflowException("Queue already at maximum size: " + maxCapacity);
        priorityQueue.add(new QueueItem<>(timestamp, msg));
    }

    public void registerListener(QueueListener<T> listener) {
        this.queueListeners.add(listener);
    }

    public void removeListener(QueueListener<T> listener) {
        this.queueListeners.remove(listener);
    }

    @Override
    public void shutdown() {
        running = false;
    }

    private void publish(T message) {
        queueListeners.forEach(listener -> listener.process(message));
    }

    @Override
    public void run() {
        while (running) {
            //Wait for queued data
            if (priorityQueue.isEmpty()) {
                try {
                    Thread.sleep(100); //sleep for 100ms
                } catch (InterruptedException e) {
                    System.err.println("Internal error: " + e.getMessage());
                    e.printStackTrace();
                    running = false;
                }
            } else {
                //Check if we have buffered long enough;
                final long currentTime = System.currentTimeMillis();
                final long cutoff = currentTime - bufferTime;

                long nextItemTime = priorityQueue.peek().timestamp;

                while(!priorityQueue.isEmpty() && nextItemTime <= cutoff) {
                    final QueueItem<T> item = priorityQueue.remove();
                    publish(item.value);
                    if(!priorityQueue.isEmpty()) {
                        nextItemTime = priorityQueue.peek().timestamp;
                    }
                }
            }
        }
    }

    private static class QueueItem<T> implements Comparable<QueueItem<T>> {
        private final long timestamp;
        private final T value;

        private QueueItem(long timestamp, T value) {
            this.timestamp = timestamp;
            this.value = value;
        }

        @Override
        public int compareTo(QueueItem<T> o) {
            return Long.compare(this.timestamp, o.timestamp);
        }
    }



}
