package com.karta.battlepass.core.scheduler;

import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

/**
 * An abstraction over the server's scheduler (Bukkit or Folia) to allow
 * for safe task execution on the appropriate threads.
 */
public interface KartaScheduler {

    /**
     * Executes a task asynchronously.
     *
     * @param task The task to execute.
     */
    void runAsync(@NotNull Runnable task);

    /**
     * Executes a task asynchronously and returns a CompletableFuture.
     *
     * @param task The task to execute.
     * @return A CompletableFuture that completes with the result of the supplier.
     */
    <T> CompletableFuture<T> supplyAsync(@NotNull Supplier<T> task);

    /**
     * Executes a task on the main server thread.
     *
     * @param task The task to execute.
     */
    void runSync(@NotNull Runnable task);

    /**
     * Executes a task asynchronously after a specified delay.
     *
     * @param task The task to execute.
     * @param delay The delay before execution.
     * @return A {@link KartaTask} representing the scheduled task.
     */
    @NotNull
    KartaTask runLaterAsync(@NotNull Runnable task, @NotNull Duration delay);

    /**
     * Executes a task on the main server thread after a specified delay.
     *
     * @param task The task to execute.
     * @param delay The delay before execution.
     * @return A {@link KartaTask} representing the scheduled task.
     */
    @NotNull
    KartaTask runLaterSync(@NotNull Runnable task, @NotNull Duration delay);

    /**
     * Executes a task asynchronously on a timer.
     *
     * @param task The task to execute.
     * @param delay The initial delay before the first execution.
     * @param period The period between subsequent executions.
     * @return A {@link KartaTask} representing the scheduled task.
     */
    @NotNull
    KartaTask runTimerAsync(@NotNull Runnable task, @NotNull Duration delay, @NotNull Duration period);

    /**
     * Executes a task on the main server thread on a timer.
     *
     * @param task The task to execute.
     * @param delay The initial delay before the first execution.
     * @param period The period between subsequent executions.
     * @return A {@link KartaTask} representing the scheduled task.
     */
    @NotNull
    KartaTask runTimerSync(@NotNull Runnable task, @NotNull Duration delay, @NotNull Duration period);
}
