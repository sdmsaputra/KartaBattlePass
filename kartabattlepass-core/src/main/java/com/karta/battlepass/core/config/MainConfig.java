package com.karta.battlepass.core.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.karta.battlepass.api.data.booster.BoosterStackingStrategy;
import org.jetbrains.annotations.NotNull;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

/**
 * Maps to the main config.yml file.
 */
public record MainConfig(
    @NotNull StorageConfig storage,
    @NotNull RedisConfig redis,
    @NotNull EconomyConfig economy,
    @NotNull SeasonConfig season,
    @NotNull DailyResetConfig daily,
    @NotNull WeeklyResetConfig weekly,
    @NotNull LeaderboardConfig leaderboard,
    @NotNull BoosterConfig boosters,
    @NotNull GuiConfig gui,
    @NotNull PlaceholderConfig placeholders,
    @NotNull MetricsConfig metrics,
    @NotNull FoliaConfig folia,
    boolean debug,
    @JsonProperty("config-version") int configVersion
) {
    public record RedisConfig(
        boolean enabled,
        @NotNull String uri,
        @NotNull Channels channels
    ) {
        public record Channels(
            @NotNull String boosters,
            @NotNull String leaderboard
        ) {}
    }

    public record EconomyConfig(
        @NotNull String provider,
        @JsonProperty("currency-name") @NotNull String currencyName,
        @JsonProperty("price-premium") double pricePremium
    ) {}

    public record SeasonConfig(
        @NotNull String name,
        @NotNull ZonedDateTime start,
        @NotNull ZonedDateTime end,
        @NotNull ZoneId timezone,
        @JsonProperty("auto-reset") boolean autoReset
    ) {}

    public record DailyResetConfig(
        @JsonProperty("reset-time") @NotNull LocalTime resetTime,
        @NotNull ZoneId timezone
    ) {}

    public record WeeklyResetConfig(
        @JsonProperty("reset-day") @NotNull DayOfWeek resetDay,
        @JsonProperty("reset-time") @NotNull LocalTime resetTime,
        @NotNull ZoneId timezone
    ) {}

    public record LeaderboardConfig(
        int size,
        @JsonProperty("snapshot-interval-minutes") int snapshotIntervalMinutes,
        @JsonProperty("network-mode") boolean networkMode
    ) {}

    public record BoosterConfig(
        @JsonProperty("default-stacking") @NotNull BoosterStackingStrategy defaultStacking
    ) {}

    public record GuiConfig(
        @NotNull String theme,
        boolean sounds,
        boolean titles
    ) {}

    public record PlaceholderConfig(
        @JsonProperty("cache-ms") long cacheMs
    ) {}

    public record MetricsConfig(
        boolean bstats,
        @JsonProperty("log-performance") boolean logPerformance
    ) {}

    public record FoliaConfig(
        boolean enabled // "auto" will be handled in logic
    ) {}
}
