package kr.modusplant.domains.member.usecase.response;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.modusplant.domains.member.usecase.model.read.PostAbuseReportDashboardReadModel;

import java.util.List;

public record PostAbuseReportDashboardResponse(
        @Schema(description = "조회된 읽기 모델")
        List<PostAbuseReportDashboardReadModel> readModels,

        @Schema(description = "마지막 게시글 ID", example = "01JY3PPG5YJ41H7BPD0DSQW2RA")
        String lastPostUlid,

        @Schema(description = "다음 게시글 존재 여부", example = "true")
        boolean hasNext,

        @Schema(description = "페이지 크기", example = "10")
        int size) {
    public static PostAbuseReportDashboardResponse of(
            List<PostAbuseReportDashboardReadModel> readModels,
            String lastPostUlid,
            boolean hasNext) {
        return new PostAbuseReportDashboardResponse(readModels, lastPostUlid, hasNext, readModels.size());
    }
}
