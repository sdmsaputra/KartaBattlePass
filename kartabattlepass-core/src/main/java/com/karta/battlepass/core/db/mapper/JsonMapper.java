package com.karta.battlepass.core.db.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jdbi.v3.core.mapper.ColumnMapper;
import org.jdbi.v3.core.statement.StatementContext;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Map;

/**
 * Maps a JSON string column to a Map<String, Object>.
 */
public class JsonMapper implements ColumnMapper<Map<String, Object>> {

    private final ObjectMapper objectMapper;

    public JsonMapper() {
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public Map<String, Object> map(ResultSet r, int columnNumber, StatementContext ctx) throws SQLException {
        String json = r.getString(columnNumber);
        if (json == null || json.isEmpty()) {
            return Collections.emptyMap();
        }
        try {
            return objectMapper.readValue(json, new TypeReference<>() {});
        } catch (IOException e) {
            throw new SQLException("Failed to parse JSON from database", e);
        }
    }

    public static String toJson(Map<String, Object> map, ObjectMapper objectMapper) {
        try {
            return objectMapper.writeValueAsString(map);
        } catch (JsonProcessingException e) {
            // This should not happen with a valid map
            throw new RuntimeException("Failed to serialize map to JSON", e);
        }
    }
}
