package com.karta.battlepass.core.scheduler;

/** Represents a task that has been scheduled for execution. */
public interface KartaTask {

    /** Cancels the task if it has not already been executed. */
    void cancel();

    /**
     * Checks if the task has been cancelled.
     *
     * @return true if the task has been cancelled.
     */
    boolean isCancelled();
}
