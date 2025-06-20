package kr.modusplant.domains.term.app.http.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record TermUpdateRequest(
        @Schema(description = "약관 식별을 위한 약관 식별자", example = "cad0f836-22f0-4913-8eac-d0507ec7218b")
        @NotNull(message = "식별자가 비어 있습니다.")
        UUID uuid,

        @Schema(description = "갱신을 위한 약관 컨텐츠", example = "개인정보처리방침 컨텐츠")
        @NotBlank(message = "컨텐츠가 비어 있습니다.")
        String content,

        @Schema(description = "갱신을 위한 약관 버전", example = "v1.0.5")
        @NotBlank(message = "버전이 비어 있습니다.")
        String version) {
}