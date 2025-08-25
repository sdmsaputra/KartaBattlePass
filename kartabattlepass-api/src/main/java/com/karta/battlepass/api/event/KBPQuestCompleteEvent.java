package com.karta.battlepass.api.event;

import com.karta.battlepass.api.data.quest.Quest;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Called when a player completes the objectives for a quest.
 */
public class KBPQuestCompleteEvent extends KartaBattlePassEvent {
    private final Player player;
    private final Quest quest;

    public KBPQuestCompleteEvent(@NotNull Player player, @NotNull Quest quest) {
        super(true);
        this.player = player;
        this.quest = quest;
    }

    /**
     * Gets the player who completed the quest.
     * @return The player.
     */
    @NotNull
    public Player getPlayer() {
        return player;
    }

    /**
     * Gets the quest that was completed.
     * @return The quest.
     */
    @NotNull
    public Quest getQuest() {
        return quest;
    }
}
