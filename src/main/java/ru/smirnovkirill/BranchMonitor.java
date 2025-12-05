package ru.smirnovkirill;

public class BranchMonitor {
    private int sitting = 0;
    private final int capacity;

    public BranchMonitor(int capacity) {
        this.capacity = capacity;
    }

    public synchronized boolean trySit() {
        if(sitting < capacity) {
            sitting++;
            return true;
        }
        return false;
    }

    public synchronized void leave() {
        if(sitting <= 0) throw new IllegalStateException("leave() called when sitting <= 0");
        sitting--;
        this.notifyAll();
    }

    public synchronized int getSittingCount() {
        return sitting;
    }

    public int getCapacity() {
        return capacity;
    }

}
