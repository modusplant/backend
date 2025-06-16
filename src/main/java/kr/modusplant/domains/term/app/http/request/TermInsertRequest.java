package kr.modusplant.domains.term.app.http.request;

import io.swagger.v3.oas.annotations.media.Schema;

public record TermInsertRequest(
        @Schema(description = "약관 이름", example = "이용약관")
        String name,

        @Schema(description = "약관 컨텐츠", example = "이용약관 컨텐츠")
        String content,

        @Schema(description = "약관 버전", example = "v1.0.4")
        String version) {
}