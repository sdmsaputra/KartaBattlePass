package com.karta.battlepass.bukkit.integration.papi;

import com.karta.battlepass.api.KartaBattlePassAPI;
import com.karta.battlepass.api.data.player.PlayerProfile;
import java.util.Optional;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

public class KartaBattlePassExpansion extends PlaceholderExpansion {

    private final KartaBattlePassAPI api;

    public KartaBattlePassExpansion(KartaBattlePassAPI api) {
        this.api = api;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "kartabp";
    }

    @Override
    public @NotNull String getAuthor() {
        return "Karta Team";
    }

    @Override
    public @NotNull String getVersion() {
        return api.getApiVersion();
    }

    @Override
    public boolean persist() {
        return true; // We can persist as we don't rely on a specific player instance
    }

    @Override
    public String onRequest(OfflinePlayer player, @NotNull String params) {
        if (player == null) {
            return "";
        }

        Optional<PlayerProfile> profileOpt =
                api.getPlayerService().getPlayerProfile(player.getUniqueId()).join();
        if (profileOpt.isEmpty()) {
            return "0"; // Default value
        }
        PlayerProfile profile = profileOpt.get();

        return switch (params) {
            case "points" -> String.valueOf(profile.points());
            case "tier" -> String.valueOf(profile.tier());
            case "pass_type" -> profile.passType().name();
                // TODO: Add other placeholders like quest progress, time to reset, etc.
            default -> null; // Let PAPI handle unknown placeholders
        };
    }
}
