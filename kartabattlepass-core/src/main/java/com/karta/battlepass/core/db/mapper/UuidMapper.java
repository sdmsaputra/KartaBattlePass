package com.karta.battlepass.core.db.mapper;

import org.jdbi.v3.core.mapper.ColumnMapper;
import org.jdbi.v3.core.statement.StatementContext;

import java.nio.ByteBuffer;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

/**
 * Maps UUIDs between Java and JDBC.
 * Handles BINARY(16) for MySQL/Postgres and TEXT for SQLite.
 */
public class UuidMapper implements ColumnMapper<UUID> {

    @Override
    public UUID map(ResultSet r, int columnNumber, StatementContext ctx) throws SQLException {
        Object obj = r.getObject(columnNumber);
        if (obj instanceof byte[] bytes) {
            if (bytes.length != 16) {
                return null;
            }
            ByteBuffer bb = ByteBuffer.wrap(bytes);
            long high = bb.getLong();
            long low = bb.getLong();
            return new UUID(high, low);
        }
        if (obj instanceof String str) {
            return UUID.fromString(str);
        }
        return (UUID) obj;
    }
}
