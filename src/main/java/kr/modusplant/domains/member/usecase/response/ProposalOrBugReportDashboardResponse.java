package kr.modusplant.domains.member.usecase.response;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.modusplant.domains.member.usecase.model.read.ProposalOrBugReportDashboardReadModel;

import java.util.List;

public record ProposalOrBugReportDashboardResponse (
        @Schema(description = "조회된 읽기 모델")
        List<ProposalOrBugReportDashboardReadModel> readModels,

        @Schema(description = "마지막 보고서 ID", example = "01JY3PPG5YJ41H7BPD0DSQW2RA")
        String lastReportUlid,

        @Schema(description = "다음 보고서 존재 여부", example = "true")
        boolean hasNext,

        @Schema(description = "페이지 크기", example = "10")
        int size) {
    public static ProposalOrBugReportDashboardResponse of(
            List<ProposalOrBugReportDashboardReadModel> readModels,
            String lastReportUlid,
            boolean hasNext) {
        return new ProposalOrBugReportDashboardResponse(
                readModels,
                lastReportUlid,
                hasNext,
                readModels.size()
        );
    }
}
