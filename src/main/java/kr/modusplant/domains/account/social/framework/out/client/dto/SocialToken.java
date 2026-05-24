package kr.modusplant.domains.account.social.framework.out.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record SocialToken(
        @JsonProperty("access_token")
        String accessToken,

        @JsonProperty("id_token")
        String idToken
) {
}
