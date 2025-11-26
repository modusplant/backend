package kr.modusplant.infrastructure.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jooq.Converter;
import org.jooq.JSONB;
import org.jooq.exception.DataAccessException;

import java.io.IOException;


public class JsonNodeConverter implements Converter<JSONB, JsonNode> {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Override
    public JsonNode from(JSONB db) {
        if (db == null) return null;
        try {
            return MAPPER.readTree(db.data());
        } catch (IOException e) {
            throw new DataAccessException("Failed to parse JSONB", e);
        }
    }

    @Override
    public JSONB to(JsonNode obj) {
        if (obj == null) return null;
        try {
            return JSONB.valueOf(MAPPER.writeValueAsString(obj));
        } catch (JsonProcessingException e) {
            throw new DataAccessException("Failed to serialize JsonNode", e);
        }
    }

    @Override
    public Class<JSONB> fromType() {
        return JSONB.class;
    }

    @Override
    public Class<JsonNode> toType() {
        return JsonNode.class;
    }
}
