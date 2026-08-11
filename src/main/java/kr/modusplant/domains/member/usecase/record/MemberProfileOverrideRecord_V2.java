package kr.modusplant.domains.member.usecase.record;

import java.util.UUID;

public record MemberProfileOverrideRecord_V2(UUID id, String introduction, String fileKey, String nickname) {
}
