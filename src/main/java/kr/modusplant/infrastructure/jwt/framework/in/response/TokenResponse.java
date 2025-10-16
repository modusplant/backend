package kr.modusplant.infrastructure.jwt.framework.in.response;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record TokenResponse(
        @Schema(description = "접근 토큰")
        @NotBlank(message = "토큰이 비어 있습니다.")
        String accessToken) {
}
