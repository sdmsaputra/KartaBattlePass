package com.karta.battlepass.bukkit.quest;

import com.karta.battlepass.api.service.QuestService;
import com.karta.battlepass.core.scheduler.KartaScheduler;
import java.time.Duration;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class PlaytimeTracker {

    private final QuestService questService;
    private final KartaScheduler scheduler;

    public PlaytimeTracker(QuestService questService, KartaScheduler scheduler) {
        this.questService = questService;
        this.scheduler = scheduler;
    }

    public void start() {
        scheduler.runTimerAsync(this::track, Duration.ofMinutes(1), Duration.ofMinutes(1));
    }

    private void track() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            // This is a simplified version. A real implementation would be more optimized.
            questService
                    .getAvailableQuests(player.getUniqueId(), null)
                    .thenAccept(
                            quests -> {
                                quests.stream()
                                        .filter(q -> q.type().equalsIgnoreCase("PLAYTIME"))
                                        .forEach(
                                                quest -> {
                                                    questService.incrementQuestProgress(
                                                            player.getUniqueId(),
                                                            quest.id(),
                                                            60); // 60 seconds
                                                });
                            });
        }
    }
}
