package ru.smirnovkirill;

public class Crow implements Runnable{

    private static final long WAIT_ON_BRANCH_MS = 10000L;
    private static final long EATING_TIME_MS = 6000L;

    private final FeederMonitor feeder;
    private final BranchMonitor branch;
    private final String name;
    private final Spawner spawner;

    public Crow(FeederMonitor feeder, BranchMonitor branch, String name, Spawner spawner) {
        this.feeder = feeder;
        this.branch = branch;
        this.name = name;
        this.spawner = spawner;
    }

    private boolean sittingOnBranch = false;
    @Override
    public void run() {
        spawner.registerCrow(Thread.currentThread());
        try {
            log("Прилетела");
            if(feeder.tryImmediateFeed()) {
                try {
                    log("Села сразу на кормушку");
                    eatAndLeaveFeeder();
                    log("Поела и улетела");
                    return;
                } finally {

                }
            }

            if (!branch.trySit()) {
                log("Не смогла сесть на ветку - улетает");
                return;
            }

            sittingOnBranch = true;
            log("Села на ветку. Ожидает кормушку до " + (WAIT_ON_BRANCH_MS / 1000) + " секунд");

            boolean acquired = false;

            try {
                acquired = feeder.tryAcquireWithTimeout(WAIT_ON_BRANCH_MS);
                if(acquired) {
                    branch.leave();
                    sittingOnBranch = false;
                    log("Переходит с ветки в кормушку и начинает есть");
                    eatAndLeaveFeeder();
                    log("Поела и улетела");
                } else {
                    branch.leave();
                    sittingOnBranch = false;
                    log("Не смогла взять кормушку - улетает");
                }
            } catch (InterruptedException e) {
                if(sittingOnBranch) {
                    branch.leave();
                    sittingOnBranch = false;
                }
                log("Прервано во время ожидания кормушки - улетает");
                Thread.currentThread().interrupt();
            }
        } catch (Exception e) {
            System.err.println("Ошибка в вороне " + name + ": " + e.getMessage() + "");
            e.printStackTrace();
        } finally {
            spawner.unregisterCrow(Thread.currentThread());
        }
    }

    private void eatAndLeaveFeeder() {
        try {
            Thread.sleep(EATING_TIME_MS);
        }catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }finally {
            feeder.leave();
        }
    }

    private void log(String msg) {
        System.out.printf("[%s] %s - %s | (на ветке %d/%d, в кормушке %d/%d)\n",
                name,
                msg,
                Thread.currentThread().getName(),
                branch.getSittingCount(), branch.getCapacity(),
                feeder.getEatingCount(), feeder.getCapacity());
    }
}
