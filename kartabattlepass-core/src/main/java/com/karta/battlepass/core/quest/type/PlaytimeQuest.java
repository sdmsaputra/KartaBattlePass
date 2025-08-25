package com.karta.battlepass.core.quest.type;

import com.karta.battlepass.api.quest.QuestContext;
import com.karta.battlepass.api.quest.QuestType;
import java.util.Map;
import java.util.Optional;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** A quest type that is not driven by events, but by a repeating task. */
public class PlaytimeQuest implements QuestType {
    @Nullable
    @Override
    public Class<? extends Event> getListeningEvent() {
        return null; // This quest type is not driven by a standard Bukkit event
    }

    @Override
    public @NotNull Optional<Map<String, Object>> processEvent(@NotNull QuestContext context) {
        // This method will not be called by the MasterQuestListener.
        // Progress will be updated by a dedicated scheduler.
        return Optional.empty();
    }
}
