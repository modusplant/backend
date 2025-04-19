package kr.modusplant.modules.auth.social.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.util.UUID;

@Getter
public class SocialLoginRequest {
    @Schema(description = "소셜 로그인 인가 코드", example = "BPAlKjanydCLdDnYdib6MQpDRwPG7hgqgWwECDwlr_jVWR6WpNeIbGlpBKIKKiVOAAABjE6Zt5qBPKUF0hG4dQ")
    @NotNull(message = "code는 필수 값입니다")
    private String code;
    @Schema(description = "기기 id", example = "241b327b-30ed-4f06-ba6a-cc0c3cf3b5a4")
    @NotNull(message = "deviceId는 필수 값입니다.")
    @JsonProperty("device_id")
    private UUID deviceId;
}
