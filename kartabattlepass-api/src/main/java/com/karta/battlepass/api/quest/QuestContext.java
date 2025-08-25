package com.karta.battlepass.api.quest;

import com.karta.battlepass.api.data.quest.Quest;
import com.karta.battlepass.api.data.quest.QuestProgress;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

/**
 * Provides context for a quest type's logic processing.
 * <p>
 * This object is passed to {@link QuestType} methods to provide access to the
 * relevant player, quest definition, and progress state.
 *
 * @param player The player involved in the quest event.
 * @param quest The static definition of the quest being processed.
 * @param progress The player's current progress on the quest.
 * @param event The Bukkit event that triggered the quest check.
 */
public record QuestContext(
    @NotNull Player player,
    @NotNull Quest quest,
    @NotNull QuestProgress progress,
    @NotNull Event event
) {
}
