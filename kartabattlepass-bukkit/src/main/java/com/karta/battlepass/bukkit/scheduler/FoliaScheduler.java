package com.karta.battlepass.bukkit.scheduler;

import com.karta.battlepass.core.scheduler.KartaScheduler;
import com.karta.battlepass.core.scheduler.KartaTask;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public class FoliaScheduler implements KartaScheduler {

    private final Plugin plugin;

    public FoliaScheduler(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void runAsync(@NotNull Runnable task) {
        plugin.getServer().getAsyncScheduler().runNow(plugin, t -> task.run());
    }

    @Override
    public <T> CompletableFuture<T> supplyAsync(@NotNull Supplier<T> task) {
        CompletableFuture<T> future = new CompletableFuture<>();
        runAsync(
                () -> {
                    try {
                        future.complete(task.get());
                    } catch (Throwable t) {
                        future.completeExceptionally(t);
                    }
                });
        return future;
    }

    @Override
    public void runSync(@NotNull Runnable task) {
        // On Folia, "sync" means on the global region thread.
        plugin.getServer().getGlobalRegionScheduler().run(plugin, t -> task.run());
    }

    @Override
    public @NotNull KartaTask runLaterAsync(@NotNull Runnable task, @NotNull Duration delay) {
        return toKartaTask(
                plugin.getServer()
                        .getAsyncScheduler()
                        .runDelayed(
                                plugin, t -> task.run(), delay.toMillis(), TimeUnit.MILLISECONDS));
    }

    @Override
    public @NotNull KartaTask runLaterSync(@NotNull Runnable task, @NotNull Duration delay) {
        return toKartaTask(
                plugin.getServer()
                        .getGlobalRegionScheduler()
                        .runDelayed(plugin, t -> task.run(), delay.toSeconds() * 20));
    }

    @Override
    public @NotNull KartaTask runTimerAsync(
            @NotNull Runnable task, @NotNull Duration delay, @NotNull Duration period) {
        return toKartaTask(
                plugin.getServer()
                        .getAsyncScheduler()
                        .runAtFixedRate(
                                plugin,
                                t -> task.run(),
                                delay.toMillis(),
                                period.toMillis(),
                                TimeUnit.MILLISECONDS));
    }

    @Override
    public @NotNull KartaTask runTimerSync(
            @NotNull Runnable task, @NotNull Duration delay, @NotNull Duration period) {
        return toKartaTask(
                plugin.getServer()
                        .getGlobalRegionScheduler()
                        .runAtFixedRate(
                                plugin,
                                t -> task.run(),
                                delay.toSeconds() * 20,
                                period.toSeconds() * 20));
    }

    private KartaTask toKartaTask(ScheduledTask scheduledTask) {
        return new KartaTask() {
            @Override
            public void cancel() {
                scheduledTask.cancel();
            }

            @Override
            public boolean isCancelled() {
                return scheduledTask.isCancelled();
            }
        };
    }
}
