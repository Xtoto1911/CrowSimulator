package ru.smirnovkirill;


import java.io.IOException;

public class App
{
    public static void main( String[] args ) throws IOException, InterruptedException {
        FeederMonitor feeder = new FeederMonitor(5);
        BranchMonitor branch = new BranchMonitor(10);

        Spawner spawner = new Spawner(feeder, branch, 1000);
        Thread spawnerThread = new Thread(spawner, "Spawner");
        spawnerThread.start();

        System.out.println("Симуляция запущена. Нажмите Enter для остановки.");
        System.in.read();

        System.out.println("Остановка симуляции...");
        spawner.stop();
        spawnerThread.interrupt();
        spawnerThread.join();

        spawner.waitForCrows();
        System.out.println("Симуляция остановлена.");


    }
}
