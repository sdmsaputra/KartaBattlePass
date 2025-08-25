package com.karta.battlepass.api.quest;

import java.util.Map;
import java.util.Optional;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

/**
 * A service provider interface for defining custom quest logic.
 *
 * <p>Implement this interface to create a new type of quest that can be registered with the {@link
 * com.karta.battlepass.api.service.QuestService}.
 */
public interface QuestType {

    /**
     * Gets the Bukkit event class that this quest type listens to. The quest processing logic will
     * only be triggered for this event.
     *
     * @return The class of the event to listen for.
     */
    @NotNull
    Class<? extends Event> getListeningEvent();

    /**
     * Processes an event to determine if it should increment a player's quest progress.
     *
     * @param context The context of the quest processing, containing the player, quest, current
     *     progress, and the triggering event.
     * @return An {@link Optional} containing the new progress value if the event was relevant, or
     *     an empty Optional if the event should not affect the quest progress. The progress value
     *     is an abstract object, often a number, whose meaning is defined by the implementation.
     *     For simple quests, this is the new total count.
     */
    @NotNull
    Optional<Map<String, Object>> processEvent(@NotNull QuestContext context);
}
