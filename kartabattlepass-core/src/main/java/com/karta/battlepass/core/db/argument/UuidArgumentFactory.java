package com.karta.battlepass.core.db.argument;

import org.jdbi.v3.core.argument.AbstractArgumentFactory;
import org.jdbi.v3.core.argument.Argument;
import org.jdbi.v3.core.config.ConfigRegistry;

import java.nio.ByteBuffer;
import java.sql.Types;
import java.util.UUID;

/**
 * Jdbi argument factory for binding UUIDs.
 * Binds as BINARY for MySQL/Postgres and TEXT for SQLite.
 */
public class UuidArgumentFactory extends AbstractArgumentFactory<UUID> {

    public UuidArgumentFactory() {
        super(Types.OTHER);
    }

    @Override
    protected Argument build(UUID value, ConfigRegistry config) {
        return (position, statement, ctx) -> {
            String dbName = ctx.getConnection().getMetaData().getDatabaseProductName().toLowerCase();
            if (dbName.contains("sqlite")) {
                statement.setString(position, value.toString());
            } else {
                // For MySQL and PostgreSQL
                byte[] bytes = toBytes(value);
                statement.setBytes(position, bytes);
            }
        };
    }

    private byte[] toBytes(UUID uuid) {
        ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
        bb.putLong(uuid.getMostSignificantBits());
        bb.putLong(uuid.getLeastSignificantBits());
        return bb.array();
    }
}
