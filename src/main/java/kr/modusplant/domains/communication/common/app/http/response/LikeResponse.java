package kr.modusplant.domains.communication.common.app.http.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record LikeResponse(
        @Schema(description = "현재 조회 수", example = "32")
        int likeCount,

        @Schema(description = "해당 요청의 결과로서의 좋아요 상태", example = "true")
        boolean liked) {
    public static LikeResponse of(int likeCount, boolean liked) {
        return new LikeResponse(likeCount, liked);
    }
}