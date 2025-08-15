package com.minekarta.kartabattlepass.quest;

import java.util.List;

/**
 * Represents a category of quests, containing a sequence of individual quests.
 */
public class QuestCategory {

    private final String id;
    private final String displayName;
    private final String displayItem;
    private final List<String> questIds;

    public QuestCategory(String id, String displayName, String displayItem, List<String> questIds) {
        this.id = id;
        this.displayName = displayName;
        this.displayItem = displayItem;
        this.questIds = questIds;
    }

    public String getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDisplayItem() {
        return displayItem;
    }

    public List<String> getQuestIds() {
        return questIds;
    }

    /**
     * Gets the quest ID at a specific index in the sequence.
     *
     * @param index The index.
     * @return The quest ID, or null if the index is out of bounds.
     */
    public String getQuestId(int index) {
        if (index >= 0 && index < questIds.size()) {
            return questIds.get(index);
        }
        return null;
    }

    /**
     * Checks if a category has been completed (i.e., the player has progressed past the last quest).
     *
     * @param progressIndex The player's current progress index for this category.
     * @return true if the category is completed, false otherwise.
     */
    public boolean isCompleted(int progressIndex) {
        return progressIndex >= questIds.size();
    }
}
