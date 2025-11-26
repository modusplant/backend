package kr.modusplant.framework.jackson.holder;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import org.springframework.stereotype.Component;

@Component
public class ObjectMapperHolder {
    @Getter
    private static ObjectMapper objectMapper;

    public ObjectMapperHolder(ObjectMapper objectMapper) {
        ObjectMapperHolder.objectMapper = objectMapper;
    }
}