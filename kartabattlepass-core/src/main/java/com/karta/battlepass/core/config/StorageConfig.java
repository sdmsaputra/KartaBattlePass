package com.karta.battlepass.core.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.NotNull;

/**
 * Represents the storage configuration from config.yml.
 */
public record StorageConfig(
    @NotNull Type type,
    @NotNull String url,
    @NotNull String username,
    @NotNull String password,
    @NotNull PoolOptions pool,
    @NotNull String schema
) {
    public enum Type {
        MYSQL,
        POSTGRESQL, // Renamed from POSTGRES for clarity
        SQLITE
    }

    public record PoolOptions(
        @JsonProperty("max-pool-size") int maxPoolSize,
        @JsonProperty("connection-timeout") long connectionTimeout,
        @JsonProperty("idle-timeout") long idleTimeout,
        @JsonProperty("max-lifetime") long maxLifetime
    ) {}
}
