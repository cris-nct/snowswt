package org.herbshouse.logic;

import java.util.concurrent.LinkedBlockingDeque;

public class CircularQueue<E> extends LinkedBlockingDeque<E> {
    private final int capacity;

    public CircularQueue(int capacity) {
        super(capacity);
        this.capacity = capacity;
    }

    @Override
    public boolean offer(E e) {
        if (size() == capacity) {
            poll(); // Remove the oldest element
        }
        return super.offer(e);
    }

}
