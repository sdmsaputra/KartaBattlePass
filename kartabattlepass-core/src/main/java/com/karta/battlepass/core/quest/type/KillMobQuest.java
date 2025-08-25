package com.karta.battlepass.core.quest.type;

import com.karta.battlepass.api.quest.QuestContext;
import com.karta.battlepass.api.quest.QuestType;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDeathEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Optional;

public class KillMobQuest implements QuestType {
    @Override
    public @NotNull Class<? extends Event> getListeningEvent() {
        return EntityDeathEvent.class;
    }

    @Override
    public @NotNull Optional<Map<String, Object>> processEvent(@NotNull QuestContext context) {
        if (!(context.event() instanceof EntityDeathEvent event)) {
            return Optional.empty();
        }
        if (event.getEntity().getKiller() == null || !event.getEntity().getKiller().equals(context.player())) {
            return Optional.empty();
        }

        Map<String, Object> objectives = context.quest().objectives();
        String targetMob = (String) objectives.get("mob");

        if (targetMob != null && event.getEntityType().name().equalsIgnoreCase(targetMob)) {
            int currentProgress = ((Number) context.progress().progress().getOrDefault("value", 0)).intValue();
            return Optional.of(Map.of("value", currentProgress + 1));
        }

        return Optional.empty();
    }
}
