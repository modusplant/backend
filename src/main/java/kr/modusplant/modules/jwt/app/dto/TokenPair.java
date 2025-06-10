package kr.modusplant.modules.jwt.app.dto;

public record TokenPair (
        String accessToken,
        String refreshToken
) {
}