package com.karta.battlepass.api.service;

import com.karta.battlepass.api.data.quest.Quest;
import com.karta.battlepass.api.data.quest.QuestCategory;
import com.karta.battlepass.api.data.quest.QuestProgress;
import com.karta.battlepass.api.quest.QuestType;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * A service for managing quest definitions, progress, and types.
 */
public interface QuestService {

    /**
     * Registers a new custom quest type.
     * This should be called by other plugins in their onEnable method.
     *
     * @param identifier The unique identifier for the quest type (e.g., "myplugin:custom_task").
     * @param questType The implementation of the {@link QuestType}.
     */
    void registerQuestType(@NotNull String identifier, @NotNull QuestType questType);

    /**
     * Gets a map of all registered quest types.
     *
     * @return An unmodifiable map of quest type identifiers to their implementations.
     */
    @NotNull
    Map<String, QuestType> getRegisteredQuestTypes();

    /**
     * Gets a specific quest definition by its ID.
     *
     * @param questId The ID of the quest to retrieve.
     * @return An {@link Optional} containing the {@link Quest} if found, otherwise empty.
     */
    @NotNull
    Optional<Quest> getQuestById(@NotNull String questId);

    /**
     * Gets all available quests for a player in a specific category for the current period.
     * This takes into account any quests the player may have already completed if they are not repeatable.
     *
     * @param playerUuid The UUID of the player.
     * @param category The category of quests to retrieve (DAILY, WEEKLY, SEASONAL).
     * @return A {@link CompletableFuture} that completes with a list of available quests.
     */
    @NotNull
    CompletableFuture<List<Quest>> getAvailableQuests(@NotNull UUID playerUuid, @NotNull QuestCategory category);

    /**
     * Gets a player's progress for a specific quest.
     *
     * @param playerUuid The UUID of the player.
     * @param questId The ID of the quest.
     * @return A {@link CompletableFuture} that completes with the player's {@link QuestProgress}.
     */
    @NotNull
    CompletableFuture<QuestProgress> getQuestProgress(@NotNull UUID playerUuid, @NotNull String questId);

    /**
     * Forces a player to complete a quest.
     * This is an admin command and bypasses normal quest logic.
     *
     * @param playerUuid The UUID of the player.
     * @param questId The ID of the quest to complete.
     * @return A {@link CompletableFuture} that completes when the operation is finished.
     */
    @NotNull
    CompletableFuture<Void> forceCompleteQuest(@NotNull UUID playerUuid, @NotNull String questId);

    /**
     * Resets a player's progress on a specific quest.
     *
     * @param playerUuid The UUID of the player.
     * @param questId The ID of the quest to reset.
     * @return A {@link CompletableFuture} that completes when the operation is finished.
     */
    @NotNull
    CompletableFuture<Void> resetQuestProgress(@NotNull UUID playerUuid, @NotNull String questId);

    /**
     * Forces a reload of all quest definitions from the configuration files.
     *
     * @return A {@link CompletableFuture} that completes when the reload is finished.
     */
    @NotNull
    CompletableFuture<Void> reloadQuests();

    /**
     * Processes a Bukkit event for a player to update their quest progress.
     * This is intended to be called by the quest listeners in the Bukkit module.
     *
     * @param playerUuid The UUID of the player who triggered the event.
     * @param event The event that was triggered.
     */
    void processQuestProgress(@NotNull java.util.UUID playerUuid, @NotNull Object event);

    /**
     * Directly increments the progress of a quest for a player.
     * This is intended for non-event-driven quests like playtime.
     *
     * @param playerUuid The UUID of the player.
     * @param questId The ID of the quest.
     * @param amount The amount to increment the progress by.
     * @return A {@link CompletableFuture} that completes when the operation is finished.
     */
    @NotNull
    CompletableFuture<Void> incrementQuestProgress(@NotNull java.util.UUID playerUuid, @NotNull String questId, int amount);
}
