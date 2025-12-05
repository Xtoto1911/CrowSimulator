package ru.smirnovkirill;

import java.util.HashSet;
import java.util.Set;

import static java.util.Collections.synchronizedSet;


public class Spawner implements Runnable {
    private final FeederMonitor feeder;
    private final BranchMonitor branch;
    private final long spawnIntervalMs;
    private volatile boolean isRunning = true;
    private int idCounter = 1;

    private final Set<Thread> activeCrows = synchronizedSet(new HashSet<>());

    public Spawner(FeederMonitor feeder, BranchMonitor branch, long spawnIntervalMs) {
        this.feeder = feeder;
        this.branch = branch;
        this.spawnIntervalMs = spawnIntervalMs;
    }

    @Override
    public void run() {
        while (isRunning && !Thread.currentThread().isInterrupted()) {
            String crowName = "Crow " + idCounter++;
            Thread t = new Thread(new Crow(feeder, branch, crowName, this), crowName + " thread");
            t.start();
            try{
                Thread.sleep(spawnIntervalMs);
            }catch (InterruptedException e){
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    public void stop() {
        isRunning = false;
    }

    public void registerCrow(Thread t) {
        activeCrows.add(t);
    }

    public void unregisterCrow(Thread t) {
        activeCrows.remove(t);
    }

    public void waitForCrows() {
        while(true) {
            synchronized (activeCrows) {
                if(activeCrows.isEmpty()) break;
            }
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
}
