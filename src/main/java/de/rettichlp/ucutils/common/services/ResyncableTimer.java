package de.rettichlp.ucutils.common.services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import static de.rettichlp.ucutils.UCUtils.LOGGER;
import static java.time.LocalDateTime.now;
import static java.util.concurrent.Executors.newSingleThreadScheduledExecutor;

public class ResyncableTimer {

    private final ScheduledExecutorService scheduler = newSingleThreadScheduledExecutor();
    private final Collection<Consumer<Integer>> onTick = new ArrayList<>();
    private final long initialDelay;
    private final long period;
    private final TimeUnit unit;

    private int currentTick = 0;
    private ScheduledFuture<?> currentTask;

    public ResyncableTimer(long initialDelay, long period, TimeUnit unit) {
        this.initialDelay = initialDelay;
        this.period = period;
        this.unit = unit;
    }

    public void start() {
        schedule();
    }

    public void synchronize() {
        schedule();
        LOGGER.info("Synchronized timer to {}", now());
    }

    public void add(Consumer<Integer> onTick) {
        this.onTick.add(onTick);
    }

    private void schedule() {
        if (this.currentTask != null) {
            this.currentTask.cancel(false);
        }

        this.currentTask = this.scheduler.scheduleAtFixedRate(() -> {
            this.currentTick++;
            this.onTick.forEach(onTick -> onTick.accept(this.currentTick));
        }, this.initialDelay, this.period, this.unit);
    }
}
