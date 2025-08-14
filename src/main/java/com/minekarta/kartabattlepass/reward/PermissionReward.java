package com.minekarta.kartabattlepass.reward;

import com.minekarta.kartabattlepass.KartaBattlePass;
import org.bukkit.entity.Player;

public class PermissionReward extends Reward {
    private final String permission;
    private final String duration;

    public PermissionReward(String track, String permission, String duration) {
        super(track);
        this.permission = permission;
        this.duration = duration;
    }

    @Override
    public void give(Player player) {
        KartaBattlePass plugin = KartaBattlePass.getInstance();
        if (plugin.getVaultHook() != null && plugin.getVaultHook().getPermission() != null) {
            if (duration != null && !duration.isEmpty()) {
                plugin.getLogger().warning("Attempted to grant a temporary permission ('" + permission + "' for " + duration + ") but this feature is not supported by the Vault API. The permission will be granted permanently. For temporary permissions, a direct integration with a permissions plugin like LuckPerms is required.");
            }
            plugin.getVaultHook().getPermission().playerAdd(null, player, permission);
        } else {
            plugin.getLogger().warning("Could not grant permission '" + permission + "' because Vault or a permissions plugin is not hooked.");
        }
    }
}
