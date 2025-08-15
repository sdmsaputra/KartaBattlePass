package com.minekarta.kartabattlepass.service;

import com.minekarta.kartabattlepass.KartaBattlePass;
import com.minekarta.kartabattlepass.model.BattlePassPlayer;
import com.minekarta.kartabattlepass.quest.PlayerQuestProgress;
import com.minekarta.kartabattlepass.quest.Quest;
import com.minekarta.kartabattlepass.quest.QuestCategory;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Map;

/**
 * Service for managing battle pass quests with sequential categories.
 */
public class QuestService {

    private final KartaBattlePass plugin;

    public QuestService(KartaBattlePass plugin) {
        this.plugin = plugin;
    }

    /**
     * Handles the progression of a quest for a player based on an action.
     *
     * @param player The player performing the action.
     * @param actionType The type of action (e.g., "block-break").
     * @param target The specific target of the action (e.g., "STONE"), can be null.
     * @param amount The amount to increment by.
     */
    public void progressQuest(Player player, String actionType, String target, int amount) {
        BattlePassPlayer bpPlayer = plugin.getBattlePassStorage().getPlayerData(player.getUniqueId());
        if (bpPlayer == null) return;

        Map<String, QuestCategory> categories = plugin.getQuestConfig().getQuestCategories();

        // Iterate through each quest category
        for (QuestCategory category : categories.values()) {
            int progressIndex = bpPlayer.getCategoryProgress(category.getId());

            // Check if the category is already completed
            if (category.isCompleted(progressIndex)) {
                continue;
            }

            // Get the current active quest for the player in this category
            String currentQuestId = category.getQuestId(progressIndex);
            if (currentQuestId == null) continue; // Should not happen if isCompleted is false, but good practice

            Quest currentQuest = plugin.getQuestConfig().getQuest(currentQuestId);
            if (currentQuest == null) {
                plugin.getLogger().warning("Player " + player.getName() + " is on an invalid quest '" + currentQuestId + "' in category '" + category.getId() + "'.");
                continue;
            }

            // Check if the action matches the current quest's requirements
            if (!currentQuest.getType().equalsIgnoreCase(actionType)) {
                continue;
            }
            if (currentQuest.getTarget() != null && !currentQuest.getTarget().equalsIgnoreCase(target)) {
                continue;
            }

            // The action matches the player's current quest. Let's update their progress.
            PlayerQuestProgress progress = bpPlayer.getQuestProgress(currentQuest.getId());
            if (progress == null) {
                progress = new PlayerQuestProgress(currentQuest.getId());
                bpPlayer.addQuestProgress(progress);
            }

            // Don't progress already completed quests (e.g., if server lags and sends event twice)
            if (progress.isCompleted()) {
                continue;
            }

            progress.incrementAmount(amount);

            // Check if the quest is now completed
            if (progress.getCurrentAmount() >= currentQuest.getAmount()) {
                completeQuest(player, bpPlayer, currentQuest, category, progress);
            }
        }
    }

    private void completeQuest(Player player, BattlePassPlayer bpPlayer, Quest quest, QuestCategory category, PlayerQuestProgress progress) {
        progress.setCompleted(true);

        // Announce completion
        player.sendMessage(plugin.getMiniMessage().deserialize("<green>Quest Completed: <white>" + quest.getDisplayName() + "</white></green>"));

        // Give EXP reward
        if (quest.getExp() > 0) {
            plugin.getExperienceService().addXP(player, quest.getExp());
        }

        // Give command rewards
        for (String command : quest.getRewards()) {
            String processedCommand = command.replace("%player%", player.getName());
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), processedCommand);
        }

        // Advance the player to the next quest in the category
        bpPlayer.advanceCategoryProgress(category.getId());

        // Announce if the whole category is complete
        int newProgressIndex = bpPlayer.getCategoryProgress(category.getId());
        if (category.isCompleted(newProgressIndex)) {
            player.sendMessage(plugin.getMiniMessage().deserialize("<gold>Quest Category Completed: <white>" + category.getDisplayName() + "</white></gold>"));
        }
    }
}
