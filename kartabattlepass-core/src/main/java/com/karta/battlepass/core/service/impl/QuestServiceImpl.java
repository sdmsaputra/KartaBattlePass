package com.karta.battlepass.core.service.impl;

import com.karta.battlepass.api.data.quest.Quest;
import com.karta.battlepass.api.data.quest.QuestCategory;
import com.karta.battlepass.api.data.quest.QuestProgress;
import com.karta.battlepass.api.data.reward.Reward;
import com.karta.battlepass.api.quest.QuestType;
import com.karta.battlepass.api.service.QuestService;
import com.karta.battlepass.core.config.ConfigManager;
import com.karta.battlepass.core.config.QuestConfig;
import com.karta.battlepass.core.quest.type.BreakBlockQuest;
import com.karta.battlepass.core.quest.type.KillMobQuest;
import com.karta.battlepass.core.quest.type.PlaytimeQuest;
import com.karta.battlepass.core.service.ServiceRegistry;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;

public class QuestServiceImpl implements QuestService {

    private final ServiceRegistry registry;
    private final ConfigManager configManager;
    private final Map<String, QuestType> questTypeRegistry = new ConcurrentHashMap<>();
    private final Map<String, Quest> questRegistry = new ConcurrentHashMap<>();

    public QuestServiceImpl(ServiceRegistry registry, ConfigManager configManager) {
        this.registry = registry;
        this.configManager = configManager;
        registerBuiltInQuestTypes();
        reloadQuests().join(); // Initial load
    }

    private void registerBuiltInQuestTypes() {
        registerQuestType("BREAK_BLOCK", new BreakBlockQuest());
        registerQuestType("KILL_MOB", new KillMobQuest());
        registerQuestType("PLAYTIME", new PlaytimeQuest());
    }

    @Override
    public @NotNull Map<String, QuestType> getRegisteredQuestTypes() {
        return Collections.unmodifiableMap(questTypeRegistry);
    }

    @Override
    public void registerQuestType(@NotNull String identifier, @NotNull QuestType questType) {
        questTypeRegistry.put(identifier.toLowerCase(), questType);
    }

    @Override
    public @NotNull Optional<Quest> getQuestById(@NotNull String questId) {
        return Optional.ofNullable(questRegistry.get(questId.toLowerCase()));
    }

    @Override
    public @NotNull CompletableFuture<List<Quest>> getAvailableQuests(
            @NotNull UUID playerUuid, @NotNull QuestCategory category) {
        // TODO: Filter out completed non-repeatable quests
        return CompletableFuture.completedFuture(
                questRegistry.values().stream()
                        .filter(q -> q.category() == category)
                        .collect(Collectors.toList()));
    }

    @Override
    public @NotNull CompletableFuture<QuestProgress> getQuestProgress(
            @NotNull UUID playerUuid, @NotNull String questId) {
        // TODO: Implement DAO call
        return null;
    }

    @Override
    public @NotNull CompletableFuture<Void> forceCompleteQuest(
            @NotNull UUID playerUuid, @NotNull String questId) {
        // TODO: Implement logic
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public @NotNull CompletableFuture<Void> resetQuestProgress(
            @NotNull UUID playerUuid, @NotNull String questId) {
        // TODO: Implement logic
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public @NotNull CompletableFuture<Void> reloadQuests() {
        return CompletableFuture.runAsync(
                () -> {
                    questRegistry.clear();
                    Map<String, QuestConfig> configs =
                            configManager.loadConfigsFromDirectory("quests", QuestConfig.class);
                    for (QuestConfig config : configs.values()) {
                        config.quests()
                                .forEach(
                                        (id, def) -> {
                                            Quest quest = toApi(id, def);
                                            questRegistry.put(id.toLowerCase(), quest);
                                        });
                    }
                });
    }

    @Override
    public void processQuestProgress(@NotNull UUID playerUuid, @NotNull Object event) {
        // This is a naive implementation. A better one would cache quests by event type.
        // This should also be run async.
        getAvailableQuests(playerUuid, null)
                .thenAccept(
                        quests -> {
                            for (Quest quest : quests) {
                                QuestType questType =
                                        questTypeRegistry.get(quest.type().toLowerCase());
                                if (questType != null
                                        && questType.getListeningEvent().isInstance(event)) {
                                    // This part needs a proper implementation with the QuestContext
                                    // and database updates. It's a placeholder for now.
                                    System.out.println(
                                            "Processing quest "
                                                    + quest.id()
                                                    + " for event "
                                                    + event.getClass().getSimpleName());
                                }
                            }
                        });
    }

    @Override
    public @NotNull CompletableFuture<Void> incrementQuestProgress(
            @NotNull UUID playerUuid, @NotNull String questId, int amount) {
        // TODO: Implement the logic to get current progress, add amount, and save to DB.
        // This will be similar to the logic inside processQuestProgress but more direct.
        return CompletableFuture.completedFuture(null);
    }

    private Quest toApi(String id, QuestConfig.QuestDefinition def) {
        List<Reward> rewards =
                def.rewards().stream()
                        .map(
                                r ->
                                        new Reward(
                                                id + "_" + r.type(), // Generate a unique ID for
                                                // the reward
                                                r.type(),
                                                r.data(),
                                                r.description()))
                        .collect(Collectors.toList());

        return new Quest(
                id,
                def.name(),
                def.description(),
                def.category(),
                def.type(),
                def.objectives(),
                def.points(),
                rewards,
                def.repeatable());
    }
}
