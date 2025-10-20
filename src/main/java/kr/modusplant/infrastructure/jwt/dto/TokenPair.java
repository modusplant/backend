package kr.modusplant.infrastructure.jwt.dto;

public record TokenPair(
        String accessToken,
        String refreshToken
) {
}