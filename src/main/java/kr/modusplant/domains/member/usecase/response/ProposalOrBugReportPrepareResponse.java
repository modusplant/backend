package kr.modusplant.domains.member.usecase.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record ProposalOrBugReportPrepareResponse(
        @Schema(description = "건의 및 버그 제보 식별자", example = "01ARZ3NDEKTSV4RRFFQ69G5FAV")
        String reportId,

        @Schema(description = "건의 및 버그 제보 이미지 준비 응답")
        List<ProposalOrBugReportImagePrepareResponse> imagePrepareResponse) {

    public record ProposalOrBugReportImagePrepareResponse(
            @Schema(description = "건의 및 버그 제보 이미지 파일 키",
                    example = "member/2ca57394-03ba-4eb8-a63c-74ae0771cd4a/report/proposal-or-bug/01ARZ3NDEKTSV4RRFFQ69G5FAV/image_0.png")
            String fileKey,

            @Schema(description = "건의 및 버그 제보 이미지를 업로드할 스토리지 URL")
            String storageUrl) {
    }

    public static ProposalOrBugReportPrepareResponse of(
            String reportId,
            List<ProposalOrBugReportImagePrepareResponse> imagePrepareResponse) {
        return new ProposalOrBugReportPrepareResponse(
                reportId, imagePrepareResponse);
    }
}
