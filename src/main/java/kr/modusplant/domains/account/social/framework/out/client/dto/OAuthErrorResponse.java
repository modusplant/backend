package kr.modusplant.domains.account.social.framework.out.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record OAuthErrorResponse(
        String error,

        @JsonProperty("error_description")
        String errorDescription
) {
}
