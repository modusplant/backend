package kr.modusplant.domains.notification.usecase.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.UUID;

public record NotificationResponse(
        @Schema(description = "알림 식별자", example = "01JXEDF9SNSMAVBY8Z3P5YXK5J")
        @JsonProperty("notificationId")
        String ulid,

        @Schema(description = "알림 행위자의 ID", example = "550e8400-e29b-41d4-a716-446655440000")
        UUID actorId,

        @Schema(description = "알림 행위자의 닉네임", example = "홍길동")
        String actorNickname,

        @Schema(description = "알림 발생 액션", example = "COMMENT_ADDED")
        String action,

        @Schema(description = "알림 상태", example = "unread")
        String status,

        @Schema(description = "게시글 식별자", example = "01ARZ3NDEKTSV4RRFFQ69G5FAV")
        @JsonProperty("postId")
        String postUlid,

        @Schema(description = "댓글 경로", example = "4.8.12")
        String commentPath,

        @Schema(description = "컨텐츠 종류", example = "comment")
        String contentType,

        @Schema(description = "컨텐츠 미리보기", example = "저도 동의해요! 좋은 글 감사합니다.")
        String contentPreview,

        @Schema(description = "알림 생성 날짜 및 시간", example = "2023-01-01T00:00:00.000")
        LocalDateTime createdAt

) {
}
