package kr.modusplant.domains.member.usecase.record;

import java.util.UUID;

public record PostAbuseReportRecord(UUID memberId, String postUlid) {
}