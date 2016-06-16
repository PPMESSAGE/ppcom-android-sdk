package com.ppmessage.ppcomlib.utils;

import com.ppmessage.sdk.core.L;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Created by ppmessage on 5/13/16.
 */
public final class PollingControl {

    private static final int DEFAULT_DELAY = 7; // Seconds

    private static final String RUNNABLE_START_LOG = "[PollingControl] start task";
    private static final String RUNNABLE_RUN_LOG = "[PollingControl] schedule task";
    private static final String RUNNABLE_STOP_LOG = "[PollingControl] stop task";

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private ScheduledFuture scheduledFuture;

    public void run(final Runnable task) {
        run(DEFAULT_DELAY, task);
    }

    public void run(int delayInSeconds, final Runnable task) {
        L.d(RUNNABLE_START_LOG);
        scheduledFuture = scheduler.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                L.d(RUNNABLE_RUN_LOG);
                task.run();
            }
        }, 0, delayInSeconds, TimeUnit.SECONDS);
    }

    public void cancel() {
        L.d(RUNNABLE_STOP_LOG);
        if (scheduledFuture != null) {
            scheduledFuture.cancel(true);
        }
    }

}
