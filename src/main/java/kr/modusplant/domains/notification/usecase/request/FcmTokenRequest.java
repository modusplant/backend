package kr.modusplant.domains.notification.usecase.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import kr.modusplant.shared.enums.Platform;

public record FcmTokenRequest(
        @Schema(description = "Fcm 토큰")
        @NotBlank(message = "Fcm 토큰이 비어 있습니다.")
        String token,

        @Schema(description = "플랫폼 종류 (대문자)", example = "WEB")
        @NotNull
        Platform platform
) {
}
