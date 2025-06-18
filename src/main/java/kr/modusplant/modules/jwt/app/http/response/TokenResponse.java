package kr.modusplant.modules.jwt.app.http.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record TokenResponse (
        @Schema(description = "접근 토큰")
        @JsonProperty("access_token")
        @NotBlank(message = "토큰이 비어 있습니다.")
        String accessToken) {
}
