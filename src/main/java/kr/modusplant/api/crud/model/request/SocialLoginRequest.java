package kr.modusplant.api.crud.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class SocialLoginRequest {
    @Schema(description = "소셜 로그인 인가 코드", example = "BPAlKjanydCLdDnYdib6MQpDRwPG7hgqgWwECDwlr_jVWR6WpNeIbGlpBKIKKiVOAAABjE6Zt5qBPKUF0hG4dQ")
    private String code;
}
