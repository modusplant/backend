package kr.modusplant.modules.jwt.app.http.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

public record TokenResponse (
        @Schema(description = "접근 토큰")
        @JsonProperty("access_token")
        String accessToken) {
}
