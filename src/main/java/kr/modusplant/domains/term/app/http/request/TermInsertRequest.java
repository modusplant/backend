package kr.modusplant.domains.term.app.http.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record TermInsertRequest(
        @Schema(description = "약관 이름", example = "이용약관")
        @NotBlank(message = "이름이 비어 있습니다.")
        String name,

        @Schema(description = "약관 컨텐츠", example = "이용약관 컨텐츠")
        @NotBlank(message = "컨텐츠가 비어 있습니다.")
        String content,

        @Schema(description = "약관 버전", example = "v1.0.4")
        @NotBlank(message = "버전이 비어 있습니다.")
        String version) {
}