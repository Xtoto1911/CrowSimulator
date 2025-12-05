package ru.smirnovkirill;

public class FeederMonitor {
    private int eating = 0;
    private final int capacity;

    public FeederMonitor(int capacity) {
        this.capacity = capacity;
    }

    public synchronized boolean tryImmediateFeed() {
        if (eating < capacity) {
            eating++;
            return true;
        }
        return false;
    }

    public synchronized boolean tryAcquireWithTimeout(long timeout) throws InterruptedException {
        if (eating < capacity) {
            eating++;
            return true;
        }

        long end = System.currentTimeMillis() + timeout;

        while (eating >= capacity) {
            long remaining = end - System.currentTimeMillis();
            if (remaining <= 0) {
                return false;
            }
            wait(remaining);
        }

        eating++;
        return true;
    }

    public synchronized void leave() {
        synchronized (this) {
            if(eating <= 0) throw new IllegalStateException("No one is eating!");
            else eating--;
            this.notifyAll();
        }
    }

    public synchronized int getEatingCount() {
        return eating;
    }

    public  int getCapacity() {
        return capacity;
    }


}
