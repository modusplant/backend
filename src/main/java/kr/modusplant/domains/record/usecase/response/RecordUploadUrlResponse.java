package kr.modusplant.domains.record.usecase.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.Map;

public record RecordUploadUrlResponse(
        @Schema(description = "미리 생성된 기록 ULID", example = "01JY3PPG5YJ41H7BPD0DSQW2RD")
        String recordId,

        @Schema(description = "영상 업로드 Presigned URL (10분간 유효)", example = "https://storage.example.com/presigned-put")
        String uploadUrl,

        @Schema(description = "등록 API에 전달할 서명 토큰 (10분간 유효)", example = "server-signed-upload-proof")
        String uploadToken,

        @Schema(description = "업로드 요청 시 반드시 포함해야 할 헤더")
        Map<String, String> requiredHeaders,

        @Schema(description = "업로드 URL 및 토큰 만료 시각", example = "2026-11-07T14:10:00Z")
        Instant expiresAt
) {
}
