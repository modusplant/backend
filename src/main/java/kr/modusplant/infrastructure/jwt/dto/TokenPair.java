package kr.modusplant.infrastructure.jwt.dto;

import org.jspecify.annotations.NonNull;

import static kr.modusplant.shared.util.MaskUtils.maskAnyString;

public record TokenPair(
        String accessToken,
        String refreshToken
) {
    @Override
    public @NonNull String toString() {
        return "TokenPair[accessToken=" + maskAnyString(accessToken) +
                ", refreshToken=" + maskAnyString(refreshToken) +
                "]";
    }
}