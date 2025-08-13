package com.minekarta.kartabattlepass.expansion;

import com.minekarta.kartabattlepass.KartaBattlePass;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

public class BattlePassExpansion extends PlaceholderExpansion {

    private final KartaBattlePass plugin;

    public BattlePassExpansion(KartaBattlePass plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull String getIdentifier() {
        // This will be the prefix for the placeholders, e.g., %battlepass_version%
        return "battlepass";
    }

    @Override
    public @NotNull String getAuthor() {
        return plugin.getDescription().getAuthors().toString();
    }

    @Override
    public @NotNull String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public boolean persist() {
        return true; // Required for PAPI to keep the expansion loaded
    }

    @Override
    public String onRequest(OfflinePlayer player, @NotNull String params) {
        if (params.equalsIgnoreCase("version")) {
            return getVersion();
        }

        return null; // Placeholder is unknown
    }
}
