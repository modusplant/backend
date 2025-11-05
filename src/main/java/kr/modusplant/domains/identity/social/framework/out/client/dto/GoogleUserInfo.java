package kr.modusplant.domains.identity.social.framework.out.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import kr.modusplant.domains.identity.social.usecase.port.client.dto.SocialUserInfo;
import lombok.Getter;

import static kr.modusplant.infrastructure.persistence.constant.EntityFieldName.NAME;

@Getter
public class GoogleUserInfo implements SocialUserInfo {
    private String id;
    private String email;

    private Boolean verifiedEmail;

    @JsonProperty(NAME)
    private String nickname;

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getEmail() {
        return email;
    }

    @Override
    public String getNickname() {
        return nickname;
    }
}
