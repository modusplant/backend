package kr.modusplant.modules.jwt.dto;

import lombok.*;

@Getter
@Builder
public class TokenPair {
    private String accessToken;
    private String refreshToken;
}
