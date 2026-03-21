package kr.modusplant.framework.jooq.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.modusplant.framework.jackson.holder.ObjectMapperHolder;
import org.jetbrains.annotations.NotNull;
import org.jooq.Converter;
import org.jooq.JSONB;
import org.jooq.exception.DataAccessException;

import java.io.IOException;


public class JsonbJsonNodeConverter implements Converter<JSONB, JsonNode> {
    @Override
    public JsonNode from(JSONB db) {
        if (db == null) return null;
        try {
            return getObjectMapper().readTree(db.data());
        } catch (IOException e) {
            throw new DataAccessException("Failed to parse JSONB", e);
        }
    }

    @Override
    public JSONB to(JsonNode obj) {
        if (obj == null) return null;
        try {
            return JSONB.valueOf(getObjectMapper().writeValueAsString(obj));
        } catch (JsonProcessingException e) {
            throw new DataAccessException("Failed to serialize JsonNode", e);
        }
    }

    @NotNull
    @Override
    public Class<JSONB> fromType() {
        return JSONB.class;
    }

    @NotNull
    @Override
    public Class<JsonNode> toType() {
        return JsonNode.class;
    }

    private ObjectMapper getObjectMapper() {
        return ObjectMapperHolder.getStaticObjectMapper();
    }
}
