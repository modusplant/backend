package kr.modusplant.domains.account.social.framework.out.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import kr.modusplant.domains.account.social.usecase.port.client.dto.SocialUserInfo;
import lombok.Getter;

@Getter
public class GoogleUserInfo implements SocialUserInfo {
    private String id;
    private String email;

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
