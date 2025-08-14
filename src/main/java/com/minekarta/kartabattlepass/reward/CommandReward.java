package com.minekarta.kartabattlepass.reward;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class CommandReward extends Reward {
    private final String command;
    private final String executor;

    public CommandReward(String track, String command, String executor) {
        super(track);
        this.command = command;
        this.executor = executor != null ? executor : "console";
    }

    @Override
    public void give(Player player) {
        String processedCommand = command.replace("%player%", player.getName());
        if ("player".equalsIgnoreCase(executor)) {
            player.performCommand(processedCommand);
        } else {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), processedCommand);
        }
    }
}
