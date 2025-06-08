package kr.modusplant.modules.auth.social.app.http.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class SocialLoginRequest {
    @Schema(description = "인가 코드", example = "BPAlKjanydCLdDnYdib6MQpDRwPG7hgqgWwECDwlr_jVWR6WpNeIbGlpBKIKKiVOAAABjE6Zt5qBPKUF0hG4dQ")
    @NotNull(message = "code는 필수 값입니다")
    private String code;
}
