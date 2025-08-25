package com.karta.battlepass.bukkit.command;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface SubCommand {

    /**
     * Gets the name of the subcommand.
     * @return The name.
     */
    @NotNull
    String getName();

    /**
     * Gets a list of aliases for the subcommand.
     * @return The list of aliases.
     */
    @NotNull
    List<String> getAliases();

    /**
     * Gets the permission required to use this subcommand.
     * @return The permission node, or null if no permission is required.
     */
    String getPermission();

    /**
     * Executes the subcommand.
     *
     * @param sender The command sender.
     * @param args The arguments passed to the subcommand.
     */
    void execute(@NotNull CommandSender sender, @NotNull String[] args);

    /**
     * Provides tab completions for the subcommand.
     *
     * @param sender The command sender.
     * @param args The arguments currently typed.
     * @return A list of suggested completions.
     */
    @NotNull
    List<String> onTabComplete(@NotNull CommandSender sender, @NotNull String[] args);
}
