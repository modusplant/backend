package kr.modusplant.modules.auth.social.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import kr.modusplant.modules.auth.social.dto.supers.SocialUserInfo;
import lombok.Getter;

@Getter
public class GoogleUserInfo implements SocialUserInfo {
    private String id;
    private String email;
    @JsonProperty("verified_email")
    private Boolean verifiedEmail;
    @JsonProperty("name")
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
