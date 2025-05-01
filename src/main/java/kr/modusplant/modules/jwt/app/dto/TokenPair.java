package kr.modusplant.modules.jwt.app.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TokenPair {
    private String accessToken;
    private String refreshToken;
}
