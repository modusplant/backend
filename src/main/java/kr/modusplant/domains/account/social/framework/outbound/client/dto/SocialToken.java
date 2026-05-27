package kr.modusplant.domains.account.social.framework.outbound.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record SocialToken(
        @JsonProperty("access_token")
        String accessToken,

        @JsonProperty("id_token")
        String idToken
) {
}
