package kr.modusplant.framework.jackson.holder;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import org.springframework.stereotype.Component;

@Component
public class ObjectMapperHolder {
    @Getter
    private final ObjectMapper objectMapper;

    @Getter
    private static ObjectMapper staticObjectMapper;

    public ObjectMapperHolder(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        ObjectMapperHolder.staticObjectMapper = objectMapper;
    }
}