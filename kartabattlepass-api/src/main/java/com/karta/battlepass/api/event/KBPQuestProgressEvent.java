package com.karta.battlepass.api.event;

import com.karta.battlepass.api.data.quest.Quest;
import java.util.Map;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.jetbrains.annotations.NotNull;

/** Called when a player's progress on a quest is about to be updated. This event is cancellable. */
public class KBPQuestProgressEvent extends KartaBattlePassEvent implements Cancellable {
    private final Player player;
    private final Quest quest;
    private final Map<String, Object> oldProgress;
    private Map<String, Object> newProgress;
    private boolean cancelled;

    public KBPQuestProgressEvent(
            @NotNull final Player player,
            @NotNull final Quest quest,
            @NotNull final Map<String, Object> oldProgress,
            @NotNull final Map<String, Object> newProgress) {
        super(true); // This can be fired from async listeners
        this.player = player;
        this.quest = quest;
        this.oldProgress = oldProgress;
        this.newProgress = newProgress;
    }

    /**
     * Gets the player whose quest progress is changing.
     *
     * @return The player.
     */
    @NotNull
    public Player getPlayer() {
        return player;
    }

    /**
     * Gets the quest definition.
     *
     * @return The quest.
     */
    @NotNull
    public Quest getQuest() {
        return quest;
    }

    /**
     * Gets the player's progress value before this event.
     *
     * @return The old progress.
     */
    @NotNull
    public Map<String, Object> getOldProgress() {
        return oldProgress;
    }

    /**
     * Gets the player's progress value that will be set if the event is not cancelled.
     *
     * @return The new progress.
     */
    @NotNull
    public Map<String, Object> getNewProgress() {
        return newProgress;
    }

    /**
     * Sets the new progress value.
     *
     * @param newProgress The new progress to set.
     */
    public void setNewProgress(@NotNull final Map<String, Object> newProgress) {
        this.newProgress = newProgress;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(final boolean cancel) {
        this.cancelled = cancel;
    }
}
