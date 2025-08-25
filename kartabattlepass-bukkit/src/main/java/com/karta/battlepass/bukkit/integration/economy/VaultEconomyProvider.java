package com.karta.battlepass.bukkit.integration.economy;

import com.karta.battlepass.api.economy.EconomyProvider;
import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.jetbrains.annotations.NotNull;

public class VaultEconomyProvider implements EconomyProvider {

    private Economy economy;

    public boolean setup() {
        if (Bukkit.getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp =
                Bukkit.getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        economy = rsp.getProvider();
        return economy != null;
    }

    @Override
    public @NotNull String getName() {
        return "Vault";
    }

    @Override
    public @NotNull CompletableFuture<BigDecimal> getBalance(@NotNull UUID playerUuid) {
        return CompletableFuture.completedFuture(
                BigDecimal.valueOf(economy.getBalance(Bukkit.getOfflinePlayer(playerUuid))));
    }

    @Override
    public @NotNull CompletableFuture<Boolean> withdraw(
            @NotNull UUID playerUuid, @NotNull BigDecimal amount) {
        OfflinePlayer player = Bukkit.getOfflinePlayer(playerUuid);
        return CompletableFuture.completedFuture(
                economy.withdrawPlayer(player, amount.doubleValue()).transactionSuccess());
    }

    @Override
    public @NotNull CompletableFuture<Boolean> deposit(
            @NotNull UUID playerUuid, @NotNull BigDecimal amount) {
        OfflinePlayer player = Bukkit.getOfflinePlayer(playerUuid);
        return CompletableFuture.completedFuture(
                economy.depositPlayer(player, amount.doubleValue()).transactionSuccess());
    }

    @Override
    public @NotNull CompletableFuture<Boolean> hasFunds(
            @NotNull UUID playerUuid, @NotNull BigDecimal amount) {
        OfflinePlayer player = Bukkit.getOfflinePlayer(playerUuid);
        return CompletableFuture.completedFuture(economy.has(player, amount.doubleValue()));
    }
}
