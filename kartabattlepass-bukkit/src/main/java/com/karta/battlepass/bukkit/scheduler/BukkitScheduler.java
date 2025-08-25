package com.karta.battlepass.bukkit.scheduler;

import com.karta.battlepass.core.scheduler.KartaScheduler;
import com.karta.battlepass.core.scheduler.KartaTask;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class BukkitScheduler implements KartaScheduler {

    private final Plugin plugin;

    public BukkitScheduler(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void runAsync(@NotNull Runnable task) {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, task);
    }

    @Override
    public <T> CompletableFuture<T> supplyAsync(@NotNull Supplier<T> task) {
        CompletableFuture<T> future = new CompletableFuture<>();
        runAsync(() -> {
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
        plugin.getServer().getScheduler().runTask(plugin, task);
    }

    @Override
    public @NotNull KartaTask runLaterAsync(@NotNull Runnable task, @NotNull Duration delay) {
        return toKartaTask(plugin.getServer().getScheduler().runTaskLaterAsynchronously(plugin, task, delay.toSeconds() * 20));
    }

    @Override
    public @NotNull KartaTask runLaterSync(@NotNull Runnable task, @NotNull Duration delay) {
        return toKartaTask(plugin.getServer().getScheduler().runTaskLater(plugin, task, delay.toSeconds() * 20));
    }

    @Override
    public @NotNull KartaTask runTimerAsync(@NotNull Runnable task, @NotNull Duration delay, @NotNull Duration period) {
        return toKartaTask(plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, task, delay.toSeconds() * 20, period.toSeconds() * 20));
    }

    @Override
    public @NotNull KartaTask runTimerSync(@NotNull Runnable task, @NotNull Duration delay, @NotNull Duration period) {
        return toKartaTask(plugin.getServer().getScheduler().runTaskTimer(plugin, task, delay.toSeconds() * 20, period.toSeconds() * 20));
    }

    private KartaTask toKartaTask(BukkitTask bukkitTask) {
        return new KartaTask() {
            @Override
            public void cancel() {
                bukkitTask.cancel();
            }

            @Override
            public boolean isCancelled() {
                return bukkitTask.isCancelled();
            }
        };
    }
}
