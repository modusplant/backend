package kr.modusplant.domains.member.usecase.record;

import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public record MemberProfileOverrideRecord_V1(UUID id, String introduction, MultipartFile image, String nickname) {
}
