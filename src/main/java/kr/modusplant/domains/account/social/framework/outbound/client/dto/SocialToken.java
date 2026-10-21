package kr.modusplant.domains.account.social.framework.outbound.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import static kr.modusplant.shared.util.MaskUtils.maskAnyString;

public record SocialToken(
        @JsonProperty("access_token")
        String accessToken,

        @JsonProperty("id_token")
        String idToken
) {
    @Override
    public String toString() {
        return "SocialToken[accessToken=" + maskAnyString(accessToken) +
                ", idToken=" + maskAnyString(idToken) +
                "]";
    }
}
