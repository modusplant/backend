package kr.modusplant.domains.member.usecase.record;

import kr.modusplant.domains.member.domain.enums.AbuseReportStatus;

public record PostAbuseReportGetRecord(AbuseReportStatus status, String lastPostUlid, Integer size) {
}
