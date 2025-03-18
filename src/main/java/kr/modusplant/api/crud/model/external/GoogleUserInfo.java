package kr.modusplant.api.crud.model.external;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class GoogleUserInfo {
    private String id;
    private String email;
    @JsonProperty("verified_email")
    private Boolean verifiedEmail;
    @JsonProperty("name")
    private String nickname;
}
