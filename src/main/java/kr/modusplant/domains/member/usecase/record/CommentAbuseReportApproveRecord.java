package kr.modusplant.domains.member.usecase.record;

import java.util.UUID;

public record CommentAbuseReportApproveRecord(String postUlid, String path, UUID memberId) {
}
