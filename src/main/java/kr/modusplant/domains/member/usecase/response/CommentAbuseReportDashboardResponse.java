package kr.modusplant.domains.member.usecase.response;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.modusplant.domains.member.usecase.model.read.CommentAbuseReportDashboardReadModel;

import java.util.List;

public record CommentAbuseReportDashboardResponse(
        @Schema(description = "조회된 읽기 모델")
        List<CommentAbuseReportDashboardReadModel> readModels,

        @Schema(description = "마지막 게시글 ID", example = "01JY3PPG5YJ41H7BPD0DSQW2RA")
        String lastPostUlid,

        @Schema(description = "마지막 댓글 경로", example = "1.4.5")
        String lastPath,

        @Schema(description = "다음 댓글 존재 여부", example = "true")
        boolean hasNext,

        @Schema(description = "페이지 크기", example = "10")
        int size) {
    public static CommentAbuseReportDashboardResponse of(
            List<CommentAbuseReportDashboardReadModel> readModels,
            String lastPostUlid,
            String nextPath,
            boolean hasNext) {
        return new CommentAbuseReportDashboardResponse(
                readModels, lastPostUlid, nextPath, hasNext, readModels.size());
    }
}
