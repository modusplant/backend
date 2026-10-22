package kr.modusplant.infrastructure.jwt.response;

import jakarta.validation.constraints.NotBlank;
import org.jspecify.annotations.NonNull;

import static kr.modusplant.shared.util.MaskUtils.maskAnyString;

public record TokenResponse(
        @NotBlank(message = "토큰이 비어 있습니다.")
        String accessToken) {
    @Override
    public @NonNull String toString() {
        return "TokenResponse[accessToken=" + maskAnyString(accessToken) + "]";
    }
}
