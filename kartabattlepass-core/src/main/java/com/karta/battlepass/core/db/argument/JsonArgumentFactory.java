package com.karta.battlepass.core.db.argument;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.karta.battlepass.core.db.mapper.JsonMapper;
import java.sql.Types;
import java.util.Map;
import org.jdbi.v3.core.argument.AbstractArgumentFactory;
import org.jdbi.v3.core.argument.Argument;
import org.jdbi.v3.core.config.ConfigRegistry;

public class JsonArgumentFactory extends AbstractArgumentFactory<Map<String, Object>> {

    private final ObjectMapper objectMapper;

    public JsonArgumentFactory() {
        super(Types.VARCHAR); // Treat as a string
        this.objectMapper = new ObjectMapper();
    }

    @Override
    protected Argument build(Map<String, Object> value, ConfigRegistry config) {
        return (position, statement, ctx) -> {
            String json = JsonMapper.toJson(value, objectMapper);
            statement.setString(position, json);
        };
    }
}
