package kr.modusplant.api.auth.email.model.response;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class SingleDataResponse<T> {
    private Map<String, Object> metadata;
    private Map<String, Object> data;
}
