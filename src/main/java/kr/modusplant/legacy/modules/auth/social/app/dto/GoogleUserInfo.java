package kr.modusplant.legacy.modules.auth.social.app.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import kr.modusplant.legacy.modules.auth.social.app.dto.supers.SocialUserInfo;
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
