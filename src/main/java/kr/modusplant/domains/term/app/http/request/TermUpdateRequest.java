package kr.modusplant.domains.term.app.http.request;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

public record TermUpdateRequest(
        @Schema(description = "약관 식별을 위한 약관 식별자", example = "cad0f836-22f0-4913-8eac-d0507ec7218b")
        UUID uuid,

        @Schema(description = "갱신을 위한 약관 컨텐츠", example = "개인정보처리방침 컨텐츠")
        String content,

        @Schema(description = "갱신을 위한 약관 버전", example = "v1.0.5")
        String version) {
}