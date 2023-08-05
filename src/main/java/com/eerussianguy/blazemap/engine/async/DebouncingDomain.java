package com.eerussianguy.blazemap.engine.async;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class DebouncingDomain<T> {
    private final Map<T, Delay> queue = new ConcurrentHashMap<>();
    private final Consumer<T> callback;
    private final int step;
    private final int max;
    private Thread thread;
    private long next = Long.MAX_VALUE;

    public DebouncingDomain(Consumer<T> callback, int step, int max) {
        this.callback = callback;
        this.step = step;
        this.max = max;
    }

    public void push(T object) {
        synchronized(queue) {
            Delay d = queue.computeIfAbsent(object, $ -> new Delay(step, max)).touch();
            if(d.next < this.next) {
                thread.interrupt();
            }
        }
    }

    public void clear() {
        synchronized(queue) {
            queue.clear();
        }
    }

    public boolean remove(T key) {
        synchronized(queue) {
            return queue.remove(key) != null;
        }
    }

    public void finish() {
        pop(Long.MAX_VALUE);
    }

    private long pop(long curr) {
        Set<T> pop = new HashSet<>();
        synchronized(queue) {
            this.next = Long.MAX_VALUE;
            Iterator<Map.Entry<T, Delay>> iter = queue.entrySet().iterator();
            while(iter.hasNext()) {
                Map.Entry<T, Delay> e = iter.next();
                Delay d = e.getValue();
                if(d.next <= curr) {
                    pop.add(e.getKey());
                    iter.remove();
                }
                else if(d.next < this.next) {
                    this.next = d.next;
                }
            }
        }
        for(T obj : pop) {
            try {
                callback.accept(obj);
            }
            catch(Throwable t) {
                t.printStackTrace();
            }
        }
        return this.next;
    }

    public long pop() {
        long curr = System.currentTimeMillis();
        return pop(curr);
    }

    public void setThread(Thread thread) {
        this.thread = thread;
    }

    private static class Delay {
        private final int step;
        private final long max;
        private long next;

        Delay(int step, int limit) {
            long curr = System.currentTimeMillis();
            this.step = step;
            this.next = curr + step;
            this.max = curr + limit;
        }

        Delay touch() {
            long curr = System.currentTimeMillis();
            next = Math.min(curr + step, max);
            return this;
        }
    }
}
