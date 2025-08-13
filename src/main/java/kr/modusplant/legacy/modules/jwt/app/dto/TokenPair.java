package kr.modusplant.legacy.modules.jwt.app.dto;

public record TokenPair (
        String accessToken,
        String refreshToken
) {
}