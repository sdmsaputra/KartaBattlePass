package com.karta.battlepass.core.db;

import com.karta.battlepass.core.config.StorageConfig;
import com.karta.battlepass.core.db.argument.JsonArgumentFactory;
import com.karta.battlepass.core.db.argument.UuidArgumentFactory;
import com.karta.battlepass.core.db.mapper.JsonMapper;
import com.karta.battlepass.core.db.mapper.UuidMapper;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.util.concurrent.TimeUnit;
import org.flywaydb.core.Flyway;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;
import org.jetbrains.annotations.NotNull;

public class DatabaseManager implements AutoCloseable {

    private final HikariDataSource dataSource;
    private final Jdbi jdbi;

    public DatabaseManager(@NotNull StorageConfig config) {
        this.dataSource = createDataSource(config);
        runMigrations(config);
        this.jdbi =
                Jdbi.create(dataSource)
                        .installPlugin(new SqlObjectPlugin())
                        .registerArgument(new UuidArgumentFactory())
                        .registerColumnMapper(new UuidMapper())
                        .registerArgument(new JsonArgumentFactory())
                        .registerColumnMapper(new JsonMapper());
    }

    private HikariDataSource createDataSource(@NotNull StorageConfig config) {
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setPoolName("KartaBattlePass-Pool");

        switch (config.type()) {
            case MYSQL:
                hikariConfig.setDriverClassName("com.mysql.cj.jdbc.Driver");
                break;
            case POSTGRESQL:
                hikariConfig.setDriverClassName("org.postgresql.Driver");
                break;
            case SQLITE:
                hikariConfig.setDriverClassName("org.sqlite.JDBC");
                break;
            default:
                throw new IllegalArgumentException("Unsupported database type: " + config.type());
        }

        hikariConfig.setJdbcUrl(config.url());
        hikariConfig.setUsername(config.username());
        hikariConfig.setPassword(config.password());

        StorageConfig.PoolOptions poolOptions = config.pool();
        hikariConfig.setMaximumPoolSize(poolOptions.maxPoolSize());
        hikariConfig.setConnectionTimeout(
                TimeUnit.SECONDS.toMillis(poolOptions.connectionTimeout()));
        hikariConfig.setIdleTimeout(TimeUnit.MINUTES.toMillis(poolOptions.idleTimeout()));
        hikariConfig.setMaxLifetime(TimeUnit.MINUTES.toMillis(poolOptions.maxLifetime()));

        // Sensible defaults
        hikariConfig.addDataSourceProperty("cachePrepStmts", "true");
        hikariConfig.addDataSourceProperty("prepStmtCacheSize", "250");
        hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

        return new HikariDataSource(hikariConfig);
    }

    private void runMigrations(@NotNull StorageConfig config) {
        Flyway flyway =
                Flyway.configure()
                        .dataSource(dataSource)
                        .locations("db/migration/" + config.type().name().toLowerCase())
                        .defaultSchema(config.schema())
                        .createSchemas(true)
                        .load();
        flyway.migrate();
    }

    @NotNull
    public Jdbi getJdbi() {
        return jdbi;
    }

    @Override
    public void close() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }
}
