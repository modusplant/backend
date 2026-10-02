package kr.modusplant.shared.framework.jackson.holder;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.modusplant.shared.exception.NotInitializedException;
import lombok.Getter;
import org.springframework.stereotype.Component;

import static kr.modusplant.shared.exception.enums.GeneralErrorCode.HOLDER_NOT_INITIALIZED;

@Component
public class ObjectMapperHolder {
    @Getter
    private final ObjectMapper objectMapper;

    private static ObjectMapper staticObjectMapper;

    public ObjectMapperHolder(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        ObjectMapperHolder.staticObjectMapper = objectMapper;
    }

    // 주의: 최소한으로만 사용할 것.
    public static ObjectMapper getStaticObjectMapper() {
        if (staticObjectMapper == null) {
            throw new NotInitializedException(HOLDER_NOT_INITIALIZED, "staticObjectMapper");
        }
        return staticObjectMapper;
    }
}