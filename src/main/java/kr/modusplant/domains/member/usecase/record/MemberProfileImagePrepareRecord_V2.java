package kr.modusplant.domains.member.usecase.record;

import java.util.UUID;

public record MemberProfileImagePrepareRecord_V2(UUID id, String filename, String contentType) {
}
