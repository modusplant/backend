package kr.modusplant.api.crud.model.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Metadata {
    private int status;
    private String message;
}
