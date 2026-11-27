package kr.modusplant.domains.record.usecase.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RecordCreateRequest(
        @Schema(
                description = "업로드 준비 API에서 발급받은 서명 토큰",
                example = "eyJhbGciOiJFUzI1NiJ9..."
        )
        @NotBlank(message = "업로드 토큰이 비어 있습니다.")
        String uploadToken,

        @Schema(
                description = "기록 설명 (선택). 최대 60자. 생략 시 빈 문자열 또는 null 허용.",
                example = "오늘 새 잎이 나왔어요"
        )
        @Size(max = 60, message = "설명은 최대 60자까지 허용됩니다.")
        String description
) {
}
