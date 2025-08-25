package com.karta.battlepass.core.quest.type;

import com.karta.battlepass.api.quest.QuestContext;
import com.karta.battlepass.api.quest.QuestType;
import java.util.Map;
import java.util.Optional;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockBreakEvent;
import org.jetbrains.annotations.NotNull;

public class BreakBlockQuest implements QuestType {
    @Override
    public @NotNull Class<? extends Event> getListeningEvent() {
        return BlockBreakEvent.class;
    }

    @Override
    public @NotNull Optional<Map<String, Object>> processEvent(@NotNull QuestContext context) {
        if (!(context.event() instanceof BlockBreakEvent event)) {
            return Optional.empty();
        }

        Map<String, Object> objectives = context.quest().objectives();
        String targetBlock = (String) objectives.get("block");

        if (targetBlock != null
                && event.getBlock().getType().name().equalsIgnoreCase(targetBlock)) {
            int currentProgress =
                    ((Number) context.progress().progress().getOrDefault("value", 0)).intValue();
            return Optional.of(Map.of("value", currentProgress + 1));
        }

        return Optional.empty();
    }
}
