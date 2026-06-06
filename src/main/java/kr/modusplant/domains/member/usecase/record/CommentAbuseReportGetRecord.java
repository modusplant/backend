package kr.modusplant.domains.member.usecase.record;

import kr.modusplant.domains.member.domain.enums.AbuseReportStatus;

public record CommentAbuseReportGetRecord(AbuseReportStatus status, String lastPostUlid, String lastPath, Integer size) {
}
