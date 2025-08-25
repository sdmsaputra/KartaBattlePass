package com.karta.battlepass.bukkit.command.sub;

import com.karta.battlepass.bukkit.command.SubCommand;
import com.karta.battlepass.core.service.ServiceRegistry;
import java.util.Collections;
import java.util.List;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class ReloadCommand implements SubCommand {

    private final ServiceRegistry registry;

    public ReloadCommand(ServiceRegistry registry) {
        this.registry = registry;
    }

    @Override
    public @NotNull String getName() {
        return "reload";
    }

    @Override
    public @NotNull List<String> getAliases() {
        return Collections.emptyList();
    }

    @Override
    public String getPermission() {
        return "kbp.admin.reload";
    }

    @Override
    public void execute(@NotNull CommandSender sender, @NotNull String[] args) {
        // TODO: Call config manager reload logic
        sender.sendMessage("Configuration reloaded (Not yet implemented).");
    }

    @Override
    public @NotNull List<String> onTabComplete(
            @NotNull CommandSender sender, @NotNull String[] args) {
        return Collections.emptyList();
    }
}
