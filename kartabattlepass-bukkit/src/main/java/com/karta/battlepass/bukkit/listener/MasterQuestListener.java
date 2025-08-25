package com.karta.battlepass.bukkit.listener;

import com.karta.battlepass.api.service.QuestService;
import java.lang.reflect.Method;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

public class MasterQuestListener implements Listener {

    private final QuestService questService;
    private final Plugin plugin;

    public MasterQuestListener(QuestService questService, Plugin plugin) {
        this.questService = questService;
        this.plugin = plugin;
        registerQuestListeners();
    }

    private void registerQuestListeners() {
        questService.getRegisteredQuestTypes().values().stream()
                .map(qt -> qt.getListeningEvent())
                .filter(java.util.Objects::nonNull) // Ignore non-event-driven quests
                .distinct()
                .forEach(this::registerGenericListener);
    }

    private <T extends Event> void registerGenericListener(Class<T> eventClass) {
        Bukkit.getPluginManager()
                .registerEvent(
                        eventClass,
                        this,
                        EventPriority.MONITOR,
                        (listener, event) -> {
                            if (!eventClass.isInstance(event)) {
                                return;
                            }
                            // A bit of reflection to get the player from the event.
                            // A better implementation might use a map of event_class ->
                            // player_getter_function.
                            Player player = getPlayerFromEvent(event);
                            if (player != null) {
                                questService.processQuestProgress(player.getUniqueId(), event);
                            }
                        },
                        plugin,
                        true); // ignoreCancelled = true
    }

    private Player getPlayerFromEvent(Event event) {
        try {
            Method getPlayerMethod = event.getClass().getMethod("getPlayer");
            if (Player.class.isAssignableFrom(getPlayerMethod.getReturnType())) {
                return (Player) getPlayerMethod.invoke(event);
            }
        } catch (Exception e) {
            // Method might not exist, e.g. EntityDeathEvent -> getEntity()
            if (event instanceof org.bukkit.event.entity.EntityDeathEvent e) {
                return e.getEntity().getKiller();
            }
        }
        return null;
    }
}
