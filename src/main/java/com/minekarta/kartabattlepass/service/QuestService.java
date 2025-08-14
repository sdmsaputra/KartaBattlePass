package com.minekarta.kartabattlepass.service;

import com.minekarta.kartabattlepass.KartaBattlePass;
import com.minekarta.kartabattlepass.model.BattlePassPlayer;
import com.minekarta.kartabattlepass.quest.PlayerQuestProgress;
import com.minekarta.kartabattlepass.quest.Quest;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Map;

/**
 * Service for managing battle pass quests.
 */
public class QuestService {

    private final KartaBattlePass plugin;
    private final Map<String, Quest> availableQuests;

    public QuestService(KartaBattlePass plugin) {
        this.plugin = plugin;
        // This assumes QuestConfig is initialized in the main class before this service.
        this.availableQuests = plugin.getQuestConfig().getQuests();
    }

    /**
     * Handles the progression of a quest for a player.
     *
     * @param player The player performing the action.
     * @param questType The type of action (e.g., "block-break").
     * @param target The specific target of the action (e.g., "STONE"), can be null.
     * @param amount The amount to increment by.
     */
    public void progressQuest(Player player, String questType, String target, int amount) {
        BattlePassPlayer bpPlayer = plugin.getBattlePassStorage().getPlayerData(player.getUniqueId());
        if (bpPlayer == null) return;

        // In a real implementation, we would assign quests to players.
        // For now, let's assume the player has all quests active.
        // We will iterate through all available quests.
        for (Quest quest : availableQuests.values()) {
            // Check if the quest type matches the action
            if (!quest.getType().equalsIgnoreCase(questType)) {
                continue;
            }

            // Check if the quest target matches the action target
            // If quest target is null, it's a generic action type (e.g., "fish" anything)
            if (quest.getTarget() != null && !quest.getTarget().equalsIgnoreCase(target)) {
                continue;
            }

            // Get the player's progress for this quest
            PlayerQuestProgress progress = bpPlayer.getQuestProgress(quest.getId());

            // If the player doesn't have this quest yet, give it to them.
            // This is a simplification. A real system would have daily/weekly assignments.
            if (progress == null) {
                progress = new PlayerQuestProgress(quest.getId());
                bpPlayer.addQuestProgress(progress);
            }

            // If the quest is already completed, do nothing.
            if (progress.isCompleted()) {
                continue;
            }

            // Increment the progress
            progress.incrementAmount(amount);

            // Check if the quest is now completed
            if (progress.getCurrentAmount() >= quest.getAmount()) {
                completeQuest(player, quest, progress);
            }
        }
    }

    private void completeQuest(Player player, Quest quest, PlayerQuestProgress progress) {
        progress.setCompleted(true);

        // Announce completion
        player.sendMessage("§aQuest Completed: §f" + quest.getId());

        // Give rewards
        for (String command : quest.getRewards()) {
            String processedCommand = command.replace("%player%", player.getName());
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), processedCommand);
        }
    }
}
