package kr.modusplant.domains.account.social.usecase.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

import static kr.modusplant.shared.util.MaskUtils.maskAnyString;

public record SocialAuthRequest(
    @Schema(description = "인가 코드", example = "BPAlKjanydCLdDnYdib6MQpDRwPG7hgqgWwECDwlr_jVWR6WpNeIbGlpBKIKKiVOAAABjE6Zt5qBPKUF0hG4dQ")
    @NotBlank(message = "코드가 비어 있습니다.")
    String code ) {
    @Override
    public String toString() {
        return "SocialAuthRequest[code=" + maskAnyString(code) +
                "]";
    }
}
