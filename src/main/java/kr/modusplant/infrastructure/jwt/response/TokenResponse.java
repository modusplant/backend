package kr.modusplant.infrastructure.jwt.response;

import jakarta.validation.constraints.NotBlank;

public record TokenResponse(
        @NotBlank(message = "토큰이 비어 있습니다.")
        String accessToken) {
}
